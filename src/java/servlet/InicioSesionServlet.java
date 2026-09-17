package servlet;

import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "InicioSesionServlet", urlPatterns = {"/InicioSesionServlet"})
public class InicioSesionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String tipo = request.getParameter("tipo");
        String id = request.getParameter("id");

        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Por favor completa todos los campos.");
            request.getRequestDispatcher("/login").forward(request, response);
            return;
        }

        try {
            UsuariosDAO dao = new UsuariosDAO();
            Usuarios usuario = dao.login(email.trim(), password.trim());

            if (usuario != null) {

                if (!usuario.isVerificado()) {
                    String infoCompra = "";
                    if (tipo != null && !tipo.trim().isEmpty() && id != null && !id.trim().isEmpty()) {
                        infoCompra = "&tipoCompra=" + java.net.URLEncoder.encode(tipo.trim(), "UTF-8")
                                + "&idCompra=" + java.net.URLEncoder.encode(id.trim(), "UTF-8");
                    }
                    response.sendRedirect(request.getContextPath()
                            + "/VerificacionServlet?correo=" + java.net.URLEncoder.encode(usuario.getEmail(), "UTF-8")
                            + infoCompra);
                    return;
                }

                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);
                session.setAttribute("nombre", usuario.getNombre());
                session.setAttribute("idUsuario", usuario.getId_usuarios());

                // id_Roles = 1 Administrador | 2 Cliente
                if (usuario.getId_roles() == 1) {
                    session.setAttribute("rol", "admin");
                    response.sendRedirect(request.getContextPath() + "/AccesoAdministradorServlet");
                } else {
                    session.setAttribute("rol", "cliente");

                    boolean hayCompraPendiente = tipo != null && !tipo.trim().isEmpty()
                            && id != null && !id.trim().isEmpty();

                    if (hayCompraPendiente) {
                        response.sendRedirect(request.getContextPath() + "/CompraServlet"
                                + "?tipo=" + java.net.URLEncoder.encode(tipo.trim(), "UTF-8")
                                + "&id=" + java.net.URLEncoder.encode(id.trim(), "UTF-8"));
                    } else {
                        response.sendRedirect(request.getContextPath() + "/PanelClienteServlet");
                    }
                }

            } else {
                request.setAttribute("error", "Correo o contrasena incorrectos.");
                request.getRequestDispatcher("/login").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "No se pudo iniciar sesion. Verifica la conexion e intenta de nuevo.");
            request.getRequestDispatcher("/login").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login");
    }
}

