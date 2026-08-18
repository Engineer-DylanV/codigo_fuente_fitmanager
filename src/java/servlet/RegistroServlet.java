package servlet;

import Controlador.RegistrosDAO;
import Modelo.Registros;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

@WebServlet("/RegistroServlet")
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        int idUsuario = obtenerIdUsuario(request);
        RegistrosDAO dao = new RegistrosDAO();

        if ("listar".equals(request.getParameter("accion"))) {
            escribirJson(response, registrosJson(dao.listarPorUsuario(idUsuario)));
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
        RegistrosDAO dao = new RegistrosDAO();

        if ("guardar".equals(accion)) {
            Registros r = new Registros();
            r.setEjercicio(request.getParameter("ejercicio"));
            r.setRepeticiones(parseInt(request.getParameter("repeticiones")));
            r.setPeso(parseDouble(request.getParameter("peso")));
            r.setFecha(new Date(System.currentTimeMillis()));
            r.setId_usuarios(idUsuario);
            escribirResultado(response, dao.insertar(r), "No se pudo guardar el registro");
            return;
        }

        if ("actualizar".equals(accion)) {
            Registros r = new Registros();
            r.setId_registros(parseInt(request.getParameter("idRegistro")));
            r.setEjercicio(request.getParameter("ejercicio"));
            r.setRepeticiones(parseInt(request.getParameter("repeticiones")));
            r.setPeso(parseDouble(request.getParameter("peso")));
            r.setFecha(Date.valueOf(request.getParameter("fecha")));
            r.setId_usuarios(idUsuario);
            escribirResultado(response, dao.actualizar(r), "No se pudo actualizar el registro");
            return;
        }

        if ("eliminar".equals(accion)) {
            int idRegistro = parseInt(request.getParameter("idRegistro"));
            escribirResultado(response, dao.eliminar(idRegistro, idUsuario), "No se pudo eliminar el registro");
            return;
        }

        escribirJson(response, "{\"ok\":false,\"mensaje\":\"Accion no valida\"}");
    }

    private String registrosJson(List<Registros> registros) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < registros.size(); i++) {
            Registros r = registros.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{")
                    .append("\"id_Registros\":").append(r.getId_registros()).append(",")
                    .append("\"ejercicio\":\"").append(escaparJson(r.getEjercicio())).append("\",")
                    .append("\"repeticiones\":").append(r.getRepeticiones()).append(",")
                    .append("\"peso\":").append(r.getPeso()).append(",")
                    .append("\"fecha\":\"").append(r.getFecha()).append("\"")
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    private int obtenerIdUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            return 0;
        }
        return parseInt(String.valueOf(session.getAttribute("idUsuario")));
    }

    private void escribirResultado(HttpServletResponse response, boolean ok, String mensaje) throws IOException {
        escribirJson(response, ok ? "{\"ok\":true}" : "{\"ok\":false,\"mensaje\":\"" + escaparJson(mensaje) + "\"}");
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

    private double parseDouble(String valor) {
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    private String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
