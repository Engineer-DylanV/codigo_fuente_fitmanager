package servlet;

import Controlador.AsistenciasDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/AsistenciaServlet")
public class AsistenciaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        int idUsuario = obtenerIdUsuario(request);
        AsistenciasDAO dao = new AsistenciasDAO();

        if ("totalMes".equals(request.getParameter("accion"))) {
            int totalMes = dao.contarSemanaActual(idUsuario);
            escribirJson(response, "{\"ok\":true,\"totalMes\":" + totalMes + "}");
            return;
        }

        escribirJson(response, "{\"ok\":false,\"mensaje\":\"Accion no valida\"}");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String accion = request.getParameter("accion");
        int idUsuario = obtenerIdUsuario(request);
        AsistenciasDAO dao = new AsistenciasDAO();

        if ("registrar".equals(accion)) {
            if (idUsuario <= 0) {
                escribirJson(response, "{\"ok\":false,\"mensaje\":\"Sesion no valida\",\"totalMes\":0}");
                return;
            }

            boolean ok = dao.registrarHoy(idUsuario);
            int totalMes = dao.contarSemanaActual(idUsuario);

            if (ok) {
                escribirJson(response, "{\"ok\":true,\"totalMes\":" + totalMes + "}");
            } else {
                escribirJson(response, "{\"ok\":false,\"mensaje\":\"Ya registraste asistencia hoy\",\"totalMes\":" + totalMes + "}");
            }
            return;
        }

        escribirJson(response, "{\"ok\":false,\"mensaje\":\"Accion no valida\"}");
    }

    private int obtenerIdUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            return 0;
        }
        return parseInt(String.valueOf(session.getAttribute("idUsuario")));
    }

    private void escribirJson(HttpServletResponse response, String json) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
        }
    }

    private int parseInt(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }
}
