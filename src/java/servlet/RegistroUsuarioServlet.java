package servlet;

import Controlador.EmailService;
import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegistroUsuarioServlet", urlPatterns = {"/RegistroUsuarioServlet"})
public class RegistroUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nombres = request.getParameter("nombres");
        String tipoDoc = request.getParameter("tipoDoc");
        String documento = request.getParameter("documento");
        String fechaNacimiento = request.getParameter("fechaNacimiento");
        String genero = request.getParameter("genero");
        String correo = request.getParameter("correo");
        String telefono = request.getParameter("telefono");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String aceptaPolitica = request.getParameter("aceptaPolitica");
        String tipoCompra = request.getParameter("tipoCompra");
        String idCompra = request.getParameter("idCompra");

        if (nombres == null || nombres.trim().isEmpty()
                || tipoDoc == null || tipoDoc.trim().isEmpty()
                || documento == null || documento.trim().isEmpty()
                || correo == null || correo.trim().isEmpty()
                || telefono == null || telefono.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()) {

            reenviarRegistro(request, response, "Por favor completa todos los campos.");
            return;
        }

        if (!nombres.trim().matches("[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\\s]+")) {
            reenviarRegistro(request, response, "El nombre completo solo puede contener letras.");
            return;
        }

        if (!documento.trim().matches("\\d{1,10}")) {
            reenviarRegistro(request, response, "El documento solo puede contener numeros (maximo 10 digitos).");
            return;
        }

        if (!telefono.trim().matches("\\d{10}")) {
            reenviarRegistro(request, response, "El telefono debe tener exactamente 10 numeros.");
            return;
        }

        if (!correo.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            reenviarRegistro(request, response, "Ingresa un correo electronico valido.");
            return;
        }

        if (fechaNacimiento == null || fechaNacimiento.trim().isEmpty()) {
            reenviarRegistro(request, response, "La fecha de nacimiento es obligatoria.");
            return;
        }

        if (genero == null || !(genero.equals("M") || genero.equals("F"))) {
            reenviarRegistro(request, response, "Selecciona tu género.");
            return;
        }

        try {
            int anioNacimiento = Integer.parseInt(fechaNacimiento.trim().split("-")[0]);
            if (anioNacimiento > 2011) {
                reenviarRegistro(request, response, "La fecha de nacimiento no puede ser posterior al 2011.");
                return;
            }
        } catch (Exception e) {
            reenviarRegistro(request, response, "La fecha de nacimiento no es valida.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            reenviarRegistro(request, response, "Las contrasenas no coinciden.");
            return;
        }

        if (aceptaPolitica == null) {
            reenviarRegistro(request, response, "Debes aceptar la politica de tratamiento de datos.");
            return;
        }

        int idTipoDocumento;

        try {
            idTipoDocumento = Integer.parseInt(tipoDoc);
        } catch (NumberFormatException e) {
            reenviarRegistro(request, response, "Selecciona un tipo de documento valido.");
            return;
        }

        String[] partes = nombres.trim().split(" ", 2);
        String nombre = partes[0];
        String apellido = "";

        if (partes.length > 1) {
            apellido = partes[1];
        }

        Usuarios usuario = new Usuarios();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setDocumento(documento.trim());
        usuario.setEmail(correo.trim());
        usuario.setTelefono(telefono == null ? "" : telefono.trim());
        usuario.setPassword(password.trim());
        usuario.setId_tipo_documento(idTipoDocumento);
        usuario.setId_roles(2);
        usuario.setId_membresias(0);
        usuario.setGenero(genero);

        try {
            UsuariosDAO dao = new UsuariosDAO();

            if (dao.existeEmail(usuario.getEmail())) {
                reenviarRegistro(request, response, "Ya existe una cuenta registrada con ese correo.");
                return;
            }

            boolean registrado = dao.insertar(usuario);

            if (registrado) {
                String codigo = dao.generarYGuardarCodigo(usuario.getEmail(), 10);
                String avisoCorreo = "";

                if (codigo != null) {
                    boolean enviado = EmailService.enviarCodigoVerificacion(usuario.getEmail(), usuario.getNombre(), codigo);
                    if (!enviado) {
                        // No se pudo enviar el correo: lo dejamos visible en el redirect
                        // para no dejar al usuario esperando un código que nunca llegará.
                        avisoCorreo = "&mailError=" + java.net.URLEncoder.encode(
                                "No se pudo enviar el correo (" + EmailService.getUltimoError() + "). "
                                + "Usa 'Reenviar código' o contacta al administrador.", "UTF-8");
                    }
                }

                String infoCompra = "";
                if (tipoCompra != null && !tipoCompra.trim().isEmpty()
                        && idCompra != null && !idCompra.trim().isEmpty()) {
                    infoCompra = "&tipoCompra=" + java.net.URLEncoder.encode(tipoCompra.trim(), "UTF-8")
                            + "&idCompra=" + java.net.URLEncoder.encode(idCompra.trim(), "UTF-8");
                }

                response.sendRedirect(request.getContextPath()
                        + "/VerificacionServlet?correo=" + java.net.URLEncoder.encode(usuario.getEmail(), "UTF-8")
                        + avisoCorreo + infoCompra);
            } else {
                reenviarRegistro(request, response, "Error al registrar. El correo o documento ya puede estar en uso.");
            }
        } catch (Exception e) {
            reenviarRegistro(request, response, "No se pudo completar el registro. Verifica los datos e intenta de nuevo.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/FormularioRegistroServlet");
    }

    private void reenviarRegistro(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {

        request.setAttribute("error", error);
        request.getRequestDispatcher("/FormularioRegistroServlet").forward(request, response);
    }
}


