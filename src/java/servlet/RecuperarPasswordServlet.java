package servlet;

import Controlador.EmailService;
import Controlador.UsuariosDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Flujo "olvidé mi contraseña" para la web, en 3 pasos:
 *   1) el usuario ingresa su correo y se le envía un código de 6 dígitos.
 *   2) ingresa el código para validarlo (sin marcar la cuenta como verificada).
 *   3) ingresa la nueva contraseña (+confirmación) y se valida el código otra vez
 *      antes de guardarla, para que no pueda reutilizarse el enlace del paso 2.
 *
 * Reutiliza el mismo sistema de códigos de 6 dígitos que ya existe para la
 * verificación de cuentas nuevas (UsuariosDAO.generarCodigoRecuperacion /
 * verificarCodigoSinConsumir), pero sin tocar la columna "verificado": si la
 * cuenta ya estaba verificada, sigue estándolo después de cambiar la
 * contraseña.
 */
@WebServlet(name = "RecuperarPasswordServlet", urlPatterns = {"/RecuperarPasswordServlet"})
public class RecuperarPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String paso = param(request, "paso");
        if (paso.isEmpty()) paso = "correo";
        request.setAttribute("paso", paso);
        request.setAttribute("correo", param(request, "correo"));
        request.getRequestDispatcher("/recuperar-password").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String paso = param(request, "paso");

        switch (paso) {
            case "codigo":
                procesarPasoCodigo(request, response);
                break;
            case "nueva":
                procesarPasoNuevaPassword(request, response);
                break;
            default:
                procesarPasoCorreo(request, response);
        }
    }

    // Paso 1: pide el correo y envía el código.
    private void procesarPasoCorreo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String correo = param(request, "correo");
        if (correo.isEmpty()) {
            request.setAttribute("paso", "correo");
            request.setAttribute("error", "Ingresa tu correo.");
            request.getRequestDispatcher("/recuperar-password").forward(request, response);
            return;
        }

        UsuariosDAO dao = new UsuariosDAO();
        if (dao.existeEmail(correo)) {
            String codigo = dao.generarCodigoRecuperacion(correo, 10);
            if (codigo != null) {
                String nombre = dao.obtenerNombrePorCorreo(correo);
                EmailService.enviarCodigoRecuperacion(correo, nombre == null ? "" : nombre, codigo);
            }
        }
        // Por seguridad, se avanza al paso 2 exista o no la cuenta, para no
        // revelar si un correo está registrado.
        request.setAttribute("paso", "codigo");
        request.setAttribute("correo", correo);
        request.setAttribute("info", "Si el correo está registrado, te enviamos un código de verificación.");
        request.getRequestDispatcher("/recuperar-password").forward(request, response);
    }

    // Paso 2: valida el código sin consumirlo.
    private void procesarPasoCodigo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String correo = param(request, "correo");
        String codigo = param(request, "codigo");

        if (correo.isEmpty() || codigo.isEmpty()) {
            request.setAttribute("paso", "codigo");
            request.setAttribute("correo", correo);
            request.setAttribute("error", "Ingresa el código que recibiste por correo.");
            request.getRequestDispatcher("/recuperar-password").forward(request, response);
            return;
        }

        UsuariosDAO.ResultadoVerificacion resultado = new UsuariosDAO().verificarCodigoSinConsumir(correo, codigo);
        if (resultado == UsuariosDAO.ResultadoVerificacion.OK) {
            request.setAttribute("paso", "nueva");
            request.setAttribute("correo", correo);
            request.setAttribute("codigo", codigo);
        } else {
            request.setAttribute("paso", "codigo");
            request.setAttribute("correo", correo);
            request.setAttribute("error", mensajeError(resultado));
        }
        request.getRequestDispatcher("/recuperar-password").forward(request, response);
    }

    // Paso 3: revalida el código y guarda la nueva contraseña.
    private void procesarPasoNuevaPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String correo = param(request, "correo");
        String codigo = param(request, "codigo");
        String password = param(request, "password");
        String confirmPassword = param(request, "confirmPassword");

        if (correo.isEmpty() || codigo.isEmpty() || password.isEmpty()) {
            request.setAttribute("paso", "codigo");
            request.setAttribute("correo", correo);
            request.setAttribute("error", "Datos incompletos, ingresa el código de nuevo.");
            request.getRequestDispatcher("/recuperar-password").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("paso", "nueva");
            request.setAttribute("correo", correo);
            request.setAttribute("codigo", codigo);
            request.setAttribute("error", "Las contraseñas no coinciden.");
            request.getRequestDispatcher("/recuperar-password").forward(request, response);
            return;
        }

        UsuariosDAO dao = new UsuariosDAO();
        UsuariosDAO.ResultadoVerificacion resultado = dao.verificarCodigoSinConsumir(correo, codigo);
        if (resultado != UsuariosDAO.ResultadoVerificacion.OK) {
            request.setAttribute("paso", "codigo");
            request.setAttribute("correo", correo);
            request.setAttribute("error", mensajeError(resultado));
            request.getRequestDispatcher("/recuperar-password").forward(request, response);
            return;
        }

        if (dao.actualizarPassword(correo, password)) {
            response.sendRedirect(request.getContextPath() + "/login?passwordActualizada=true");
        } else {
            request.setAttribute("paso", "nueva");
            request.setAttribute("correo", correo);
            request.setAttribute("codigo", codigo);
            request.setAttribute("error", "No se pudo actualizar la contraseña. Intenta de nuevo.");
            request.getRequestDispatcher("/recuperar-password").forward(request, response);
        }
    }

    private String mensajeError(UsuariosDAO.ResultadoVerificacion resultado) {
        switch (resultado) {
            case CODIGO_INCORRECTO:
                return "El código ingresado no coincide con el que enviamos. Verifícalo e inténtalo de nuevo.";
            case CODIGO_VENCIDO:
                return "El código venció. Solicita uno nuevo.";
            case SIN_CODIGO_PENDIENTE:
                return "No hay un código pendiente para este correo. Solicita uno nuevo.";
            default:
                return "No encontramos una cuenta con ese correo.";
        }
    }

    private String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
}
