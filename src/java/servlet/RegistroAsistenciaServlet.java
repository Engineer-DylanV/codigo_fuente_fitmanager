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

@WebServlet("/RegistroAsistenciaServlet")
public class RegistroAsistenciaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        int idUsuario = obtenerIdUsuario(request);

        if ("totalMes".equals(request.getParameter("accion"))) {
            escribirJson(response, totalMesJson(idUsuario));
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

        if ("registrar".equals(accion)) {
            escribirJson(response, registrarJson(idUsuario));
            return;
        }

        escribirJson(response, "{\"ok\":false,\"mensaje\":\"Accion no valida\"}");
    }

    /** Consulta el total de asistencias de la semana actual y la racha, ya usado por doGet. */
    String totalMesJson(int idUsuario) {
        AsistenciasDAO dao = new AsistenciasDAO();
        int totalMes = dao.contarSemanaActual(idUsuario);
        int racha = dao.calcularRachaActual(idUsuario);
        return "{\"ok\":true,\"totalMes\":" + totalMes + ",\"racha\":" + racha + "}";
    }

    /** Registra la asistencia de hoy para el usuario y devuelve el mismo JSON que usa doPost. */
    String registrarJson(int idUsuario) {
        if (idUsuario <= 0) {
            return "{\"ok\":false,\"mensaje\":\"Sesion no valida\",\"totalMes\":0}";
        }

        AsistenciasDAO dao = new AsistenciasDAO();
        boolean ok = dao.registrarHoy(idUsuario);
        int totalMes = dao.contarSemanaActual(idUsuario);
        int racha = dao.calcularRachaActual(idUsuario);

        if (ok) {
            return "{\"ok\":true,\"totalMes\":" + totalMes + ",\"racha\":" + racha + "}";
        }
        return "{\"ok\":false,\"mensaje\":\"Ya registraste asistencia hoy\",\"totalMes\":" + totalMes + ",\"racha\":" + racha + "}";
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

