package servlet;

import Controlador.EmailService;
import Controlador.PerfilDAO;
import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Acciones autenticadas de "Mi perfil", persistidas en la base de datos. */
@WebServlet(name = "PerfilClienteServlet", urlPatterns = {"/PerfilClienteServlet"})
@MultipartConfig(maxFileSize = 3 * 1024 * 1024, maxRequestSize = 4 * 1024 * 1024)
public class PerfilClienteServlet extends HttpServlet {

    private static final long MAX_FOTO_BYTES = 3L * 1024L * 1024L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = sesionCliente(request, response);
        if (session == null) return;
        int idUsuario = idUsuario(session);
        if (idUsuario <= 0) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String accion = valor(request.getParameter("accion"));
        if ("foto".equals(accion)) enviarFoto(response, idUsuario);
        else if ("estado".equals(accion)) enviarEstado(response, idUsuario);
        else response.sendRedirect(request.getContextPath() + "/PanelClienteServlet?seccion=perfil");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = sesionCliente(request, response);
        if (session == null) return;

        int idUsuario = idUsuario(session);
        Usuarios usuario = (Usuarios) session.getAttribute("usuario");
        if (idUsuario <= 0 || usuario == null || usuario.getEmail() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        switch (valor(request.getParameter("accion"))) {
            case "solicitar-codigo": solicitarCodigo(session, usuario); break;
            case "cambiar-password": cambiarPassword(request, session, usuario); break;
            case "foto": guardarFoto(request, session, idUsuario); break;
            case "avatar": guardarAvatar(request, session, idUsuario); break;
            default: mensaje(session, "La acción solicitada no es válida.", true);
        }
        response.sendRedirect(request.getContextPath() + "/PanelClienteServlet?seccion=perfil");
    }

    private HttpSession sesionCliente(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null || !"cliente".equals(session.getAttribute("rol"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return session;
    }

    private int idUsuario(HttpSession session) {
        try { return Integer.parseInt(String.valueOf(session.getAttribute("idUsuario"))); }
        catch (NumberFormatException e) { return -1; }
    }

    private void solicitarCodigo(HttpSession session, Usuarios usuario) {
        UsuariosDAO dao = new UsuariosDAO();
        String codigo = dao.generarCodigoCambioPassword(usuario.getEmail(), 10);
        if (codigo == null) {
            mensaje(session, "No fue posible preparar el código. Intenta nuevamente.", true);
        } else if (EmailService.enviarCodigoCambioPassword(usuario.getEmail(), usuario.getNombre(), codigo)) {
            mensaje(session, "Enviamos un código de verificación a tu correo. Vence en 10 minutos.", false);
        } else {
            mensaje(session, "No se pudo enviar el correo. Revisa la configuración de correo del servidor.", true);
        }
    }

    private void cambiarPassword(HttpServletRequest request, HttpSession session, Usuarios usuario) {
        String codigo = valor(request.getParameter("codigo"));
        String password = request.getParameter("password");
        String confirmacion = request.getParameter("confirmPassword");
        if (!codigo.matches("[0-9]{6}")) {
            mensaje(session, "Ingresa el código de seis dígitos enviado a tu correo.", true); return;
        }
        if (password == null || password.length() < 8 || password.length() > 100) {
            mensaje(session, "La nueva contraseña debe tener entre 8 y 100 caracteres.", true); return;
        }
        if (!password.equals(confirmacion)) {
            mensaje(session, "Las contraseñas no coinciden.", true); return;
        }
        UsuariosDAO dao = new UsuariosDAO();
        UsuariosDAO.ResultadoVerificacion resultado = dao.verificarCodigoSinConsumir(usuario.getEmail(), codigo);
        if (resultado != UsuariosDAO.ResultadoVerificacion.OK) {
            mensaje(session, mensajeCodigo(resultado), true); return;
        }
        boolean actualizada = dao.actualizarPassword(usuario.getEmail(), password);
        mensaje(session, actualizada ? "Tu contraseña fue actualizada correctamente."
                : "No se pudo actualizar la contraseña. Intenta nuevamente.", !actualizada);
    }

    private void guardarFoto(HttpServletRequest request, HttpSession session, int idUsuario) throws IOException, ServletException {
        Part foto = request.getPart("fotoPerfil");
        if (foto == null || foto.getSize() == 0) { mensaje(session, "Selecciona una imagen para continuar.", true); return; }
        if (foto.getSize() > MAX_FOTO_BYTES) { mensaje(session, "La foto no puede superar los 3 MB.", true); return; }
        String mime = foto.getContentType();
        if (!"image/png".equals(mime) && !"image/jpeg".equals(mime)) {
            mensaje(session, "Solo puedes subir imágenes PNG o JPG.", true); return;
        }
        byte[] datos = leerYValidarImagen(foto);
        if (datos == null) { mensaje(session, "El archivo seleccionado no es una imagen válida.", true); return; }
        boolean ok = new PerfilDAO().guardarFoto(idUsuario, mime, datos);
        mensaje(session, ok ? "Tu foto de perfil fue actualizada." : "No se pudo guardar la foto en la base de datos.", !ok);
    }

    private byte[] leerYValidarImagen(Part foto) throws IOException {
        try (InputStream entrada = foto.getInputStream()) {
            BufferedImage imagen = ImageIO.read(entrada);
            if (imagen == null || imagen.getWidth() < 1 || imagen.getHeight() < 1) return null;
        }
        try (InputStream entrada = foto.getInputStream(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192]; int leidos;
            while ((leidos = entrada.read(buffer)) != -1) salida.write(buffer, 0, leidos);
            return salida.toByteArray();
        }
    }

    private void guardarAvatar(HttpServletRequest request, HttpSession session, int idUsuario) {
        String avatar = valor(request.getParameter("avatar"));
        PerfilDAO dao = new PerfilDAO();
        if (!dao.existeAvatarActivo(avatar)) {
            mensaje(session, "El avatar seleccionado no está disponible.", true); return;
        }
        boolean ok = dao.guardarAvatar(idUsuario, avatar);
        mensaje(session, ok ? "Tu avatar fue actualizado." : "No se pudo guardar el avatar en la base de datos.", !ok);
    }

    private void enviarEstado(HttpServletResponse response, int idUsuario) throws IOException {
        PerfilDAO.PerfilEstado estado = new PerfilDAO().obtenerEstado(idUsuario);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        String avatar = estado.getAvatarCodigo() == null ? "" : estado.getAvatarCodigo().replace("\\", "\\\\").replace("\"", "\\\"");
        response.getWriter().write("{\"avatarCodigo\":\"" + avatar + "\",\"tieneFoto\":" + estado.isTieneFoto() + "}");
    }

    private void enviarFoto(HttpServletResponse response, int idUsuario) throws IOException {
        PerfilDAO.FotoPerfil foto = new PerfilDAO().obtenerFoto(idUsuario);
        if (foto == null || foto.getDatos() == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        response.setContentType(foto.getMime());
        response.setHeader("Cache-Control", "private, no-store, max-age=0");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.getOutputStream().write(foto.getDatos());
    }

    private String mensajeCodigo(UsuariosDAO.ResultadoVerificacion resultado) {
        switch (resultado) {
            case CODIGO_INCORRECTO: return "El código ingresado no es correcto.";
            case CODIGO_VENCIDO: return "El código venció. Solicita uno nuevo.";
            case SIN_CODIGO_PENDIENTE: return "Solicita un código nuevo antes de actualizar la contraseña.";
            default: return "No fue posible validar el código.";
        }
    }
    private void mensaje(HttpSession session, String texto, boolean esError) { session.setAttribute("perfilMensaje", texto); session.setAttribute("perfilError", esError); }
    private String valor(String texto) { return texto == null ? "" : texto.trim(); }
}
