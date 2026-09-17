package servlet;

import Controlador.EmailService;
import Controlador.UsuariosDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "VerificacionServlet", urlPatterns = {"/VerificacionServlet"})
public class VerificacionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String accion = request.getParameter("accion");
        String mailError = request.getParameter("mailError");
        String tipoCompra = request.getParameter("tipoCompra");
        String idCompra = request.getParameter("idCompra");

        if (correo == null || correo.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if ("reenviar".equals(accion)) {
            boolean enviado = reenviarCodigo(correo.trim());
            if (enviado) {
                request.setAttribute("info", "Te enviamos un nuevo código a tu correo.");
            } else {
                request.setAttribute("error", "No se pudo enviar el código (" + EmailService.getUltimoError()
                        + "). Intenta de nuevo en un momento.");
            }
        } else if (mailError != null && !mailError.trim().isEmpty()) {
            request.setAttribute("error", mailError);
        } else {
            // Al entrar a la pantalla de verificación el código debe llegar solo.
            // Solo se envía si no hay uno pendiente y vigente, para no duplicar
            // el correo que ya se manda al terminar el registro.
            UsuariosDAO dao = new UsuariosDAO();

            if (!dao.tieneCodigoVigente(correo.trim())) {
                boolean enviado = reenviarCodigo(correo.trim());

                if (enviado) {
                    request.setAttribute("info", "Te enviamos un código de verificación a tu correo.");
                } else {
                    request.setAttribute("error", "No se pudo enviar el código (" + EmailService.getUltimoError()
                            + "). Usa 'Reenviar código' en un momento.");
                }
            } else {
                request.setAttribute("info", "Te enviamos un código de verificación a tu correo.");
            }
        }

        request.setAttribute("correo", correo.trim());
        request.setAttribute("tipoCompra", tipoCompra == null ? "" : tipoCompra);
        request.setAttribute("idCompra", idCompra == null ? "" : idCompra);
        request.getRequestDispatcher("/verificacion").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String correo = request.getParameter("correo");
        String codigo = request.getParameter("codigo");
        String tipoCompra = request.getParameter("tipoCompra");
        String idCompra = request.getParameter("idCompra");

        if (correo == null || correo.trim().isEmpty()
                || codigo == null || codigo.trim().isEmpty()) {

            request.setAttribute("correo", correo);
            request.setAttribute("tipoCompra", tipoCompra == null ? "" : tipoCompra);
            request.setAttribute("idCompra", idCompra == null ? "" : idCompra);
            request.setAttribute("error", "Ingresa el código que recibiste por correo.");
            request.getRequestDispatcher("/verificacion").forward(request, response);
            return;
        }

        UsuariosDAO dao = new UsuariosDAO();
        UsuariosDAO.ResultadoVerificacion resultado = dao.verificarCodigoDetallado(correo.trim(), codigo.trim());

        if (resultado == UsuariosDAO.ResultadoVerificacion.OK) {
            String infoCompra = "";
            if (tipoCompra != null && !tipoCompra.trim().isEmpty()
                    && idCompra != null && !idCompra.trim().isEmpty()) {
                infoCompra = "&tipo=" + java.net.URLEncoder.encode(tipoCompra.trim(), "UTF-8")
                        + "&id=" + java.net.URLEncoder.encode(idCompra.trim(), "UTF-8");
            }
            response.sendRedirect(request.getContextPath() + "/login?registrado=true" + infoCompra);
        } else {
            String mensaje;
            switch (resultado) {
                case CODIGO_INCORRECTO:
                    mensaje = "El código ingresado no coincide con el que enviamos. Verifícalo e inténtalo de nuevo.";
                    break;
                case CODIGO_VENCIDO:
                    mensaje = "El código venció. Usa 'Reenviar código' para recibir uno nuevo.";
                    break;
                case SIN_CODIGO_PENDIENTE:
                    mensaje = "No hay un código pendiente para este correo. Usa 'Reenviar código'.";
                    break;
                default:
                    mensaje = "No encontramos una cuenta con ese correo.";
            }
            request.setAttribute("correo", correo.trim());
            request.setAttribute("tipoCompra", tipoCompra == null ? "" : tipoCompra);
            request.setAttribute("idCompra", idCompra == null ? "" : idCompra);
            request.setAttribute("error", mensaje);
            request.getRequestDispatcher("/verificacion").forward(request, response);
        }
    }

    private boolean reenviarCodigo(String correo) {
        UsuariosDAO dao = new UsuariosDAO();
        String codigo = dao.generarYGuardarCodigo(correo, 10);

        if (codigo != null) {
            String nombre = dao.obtenerNombrePorCorreo(correo);
            return EmailService.enviarCodigoVerificacion(correo, nombre == null ? "" : nombre, codigo);
        }
        return false;
    }
}
