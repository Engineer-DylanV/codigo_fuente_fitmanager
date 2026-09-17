package servlet;

import Controlador.MetasDAO;
import Controlador.RegistrosDAO;
import Modelo.Metas;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/GestionMetasServlet")
public class GestionMetasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        String accion = request.getParameter("accion");
        int idUsuario = obtenerIdUsuario(request);
        MetasDAO dao = new MetasDAO();

        if ("listar".equals(accion)) {
            escribirJson(response, metasJson(dao.listarPorUsuario(idUsuario)));
            return;
        }

        if ("porcentaje".equals(accion)) {
            escribirJson(response, porcentajeJson(idUsuario, dao.listarPorUsuario(idUsuario)));
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
        MetasDAO dao = new MetasDAO();

        if ("crear".equals(accion)) {
            int idUsuario = obtenerIdUsuario(request);
            String ejercicio = request.getParameter("ejercicioMeta");
            int valor = parseInt(request.getParameter("valorMeta"));

            if (idUsuario <= 0) {
                escribirJson(response, "{\"ok\":false,\"mensaje\":\"Debes iniciar sesión\"}");
                return;
            }
            if (ejercicio == null || ejercicio.trim().isEmpty() || valor <= 0) {
                escribirJson(response, "{\"ok\":false,\"mensaje\":\"Datos invalidos\"}");
                return;
            }

            Metas m = new Metas();
            m.setEjercicio(ejercicio.trim());
            m.setMeta(valor);
            m.setId_usuarios(idUsuario);
            escribirResultado(response, dao.insertar(m), "No se pudo crear la meta");
            return;
        }

        if ("asignar".equals(accion)) {
            String correo = request.getParameter("correoMeta");
            String ejercicio = request.getParameter("ejercicioMeta");
            int valor = parseInt(request.getParameter("valorMeta"));
            int idUsuario = dao.buscarIdUsuarioPorCorreo(correo);

            if (idUsuario <= 0 || ejercicio == null || ejercicio.trim().isEmpty() || valor <= 0) {
                escribirJson(response, "{\"ok\":false,\"mensaje\":\"Usuario o datos invalidos\"}");
                return;
            }

            Metas m = new Metas();
            m.setEjercicio(ejercicio.trim());
            m.setMeta(valor);
            m.setId_usuarios(idUsuario);
            escribirResultado(response, dao.insertar(m), "No se pudo asignar la meta");
            return;
        }

        if ("actualizar".equals(accion)) {
            int idUsuario = obtenerIdUsuario(request);
            Metas m = new Metas();
            m.setId_metas(parseInt(request.getParameter("idMeta")));
            m.setEjercicio(request.getParameter("ejercicioMeta"));
            m.setMeta(parseInt(request.getParameter("valorMeta")));
            m.setId_usuarios(idUsuario);
            escribirResultado(response, dao.actualizar(m), "No se pudo actualizar la meta");
            return;
        }

        if ("eliminar".equals(accion)) {
            int idUsuario = obtenerIdUsuario(request);
            int idMeta = parseInt(request.getParameter("idMeta"));
            escribirResultado(response, dao.eliminar(idMeta, idUsuario), "No se pudo eliminar la meta");
            return;
        }

        escribirJson(response, "{\"ok\":false,\"mensaje\":\"Accion no valida\"}");
    }

    String metasJson(List<Metas> metas) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < metas.size(); i++) {
            Metas m = metas.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{")
                    .append("\"id_Metas\":").append(m.getId_metas()).append(",")
                    .append("\"ejercicio\":\"").append(escaparJson(m.getEjercicio())).append("\",")
                    .append("\"meta\":").append(m.getMeta())
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    String porcentajeJson(int idUsuario, List<Metas> metas) {
        RegistrosDAO registrosDAO = new RegistrosDAO();
        int total = metas.size();
        int cumplidas = 0;

        for (Metas m : metas) {
            int repeticiones = registrosDAO.sumarRepeticiones(idUsuario, m.getEjercicio());
            if (repeticiones >= m.getMeta()) {
                cumplidas++;
            }
        }

        int porcentaje = total == 0 ? 0 : (int) Math.round((cumplidas * 100.0) / total);
        return "{\"porcentaje\":" + porcentaje
                + ",\"cumplidas\":" + cumplidas
                + ",\"total\":" + total + "}";
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

