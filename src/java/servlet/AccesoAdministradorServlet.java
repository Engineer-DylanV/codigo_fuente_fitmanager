package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AccesoAdministradorServlet", urlPatterns = {"/AccesoAdministradorServlet"})
public class AccesoAdministradorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession(false);

            if (session == null
                    || session.getAttribute("usuario") == null
                    || !"admin".equals(session.getAttribute("rol"))) {
                response.sendRedirect(request.getContextPath() + "/Vista/Login.jsp");
                return;
            }

            request.getRequestDispatcher("/Vista/Admin.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorAdmin", "No se pudo cargar el acceso del administrador.");
            request.getRequestDispatcher("/Vista/Login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
