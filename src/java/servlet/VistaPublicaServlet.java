package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Sirve las páginas públicas (login, registro, verificación, recuperar
 * contraseña) a través de una URL amigable, sin exponer el .jsp real en
 * la barra de direcciones. Usa forward(), así que la URL que ve el
 * usuario nunca cambia al Vista/*.jsp interno.
 */
@WebServlet(name = "VistaPublicaServlet", urlPatterns = {
    "/login", "/registro", "/verificacion", "/recuperar-password"
})
public class VistaPublicaServlet extends HttpServlet {

    private static final Map<String, String> RUTAS = Map.of(
            "/login", "/Vista/Login.jsp",
            "/registro", "/Vista/Register.jsp",
            "/verificacion", "/Vista/Verificacion.jsp",
            "/recuperar-password", "/Vista/RecuperarPassword.jsp"
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String destino = RUTAS.get(request.getServletPath());
        if (destino == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.getRequestDispatcher(destino).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
