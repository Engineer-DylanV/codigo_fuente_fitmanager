package servlet;

import Controlador.RutinasDAO;
import Modelo.Rutinas;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/GestionRutinasServlet")
public class GestionRutinasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        int idUsuario = obtenerIdUsuario(request);
        RutinasDAO dao = new RutinasDAO();

        if ("listar".equals(request.getParameter("accion"))) {
            escribirJson(response, rutinasJson(dao.listarPorUsuario(idUsuario)));
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
        RutinasDAO dao = new RutinasDAO();

        if ("crear".equals(accion)) {
            escribirJson(response, "{\"ok\":false,\"mensaje\":\"Las rutinas solo las asigna el administrador después de la evaluación física.\"}");
            return;
        }

        if ("actualizar".equals(accion)) {
            Rutinas r = new Rutinas();
            r.setId_rutinas(parseInt(request.getParameter("idRutina")));
            r.setNombre(request.getParameter("nombreRutina"));
            r.setDescripcion(request.getParameter("descRutina"));
            r.setId_usuarios(idUsuario);
            escribirResultado(response, dao.actualizar(r), "No se pudo actualizar la rutina");
            return;
        }

        if ("eliminar".equals(accion)) {
            escribirJson(response, "{\"ok\":false,\"mensaje\":\"Las rutinas asignadas no se pueden eliminar desde la cuenta del cliente.\"}");
            return;
        }

        escribirJson(response, "{\"ok\":false,\"mensaje\":\"Accion no valida\"}");
    }

    String rutinasJson(List<Rutinas> rutinas) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < rutinas.size(); i++) {
            Rutinas r = rutinas.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{")
                    .append("\"id_Rutinas\":").append(r.getId_rutinas()).append(",")
                    .append("\"nombre\":\"").append(escaparJson(r.getNombre())).append("\",")
                    .append("\"descripcion\":\"").append(escaparJson(r.getDescripcion())).append("\",")
                    .append("\"objetivo\":\"").append(escaparJson(r.getObjetivo())).append("\",")
                    .append("\"programa\":\"").append(escaparJson(r.getPrograma())).append("\",")
                    .append("\"enlaceDrive\":\"").append(escaparJson(r.getEnlaceDrive())).append("\"")
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

    private String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
