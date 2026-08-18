package servlet;

import Controlador.ClasesDAO;
import Modelo.Clases;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/GestionClasesServlet")
public class GestionClasesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        String accion = request.getParameter("accion");
        ClasesDAO dao = new ClasesDAO();

        if ("disponibles".equals(accion)) {
            int idUsuario = obtenerIdUsuario(request);
            escribirJson(response, clasesConInscripcionJson(dao.listar(), dao, idUsuario));
            return;
        }

        if ("misClases".equals(accion)) {
            int idUsuario = obtenerIdUsuario(request);
            escribirJson(response, clasesJson(dao.listarMisClases(idUsuario)));
            return;
        }

        if ("todas".equals(accion) || accion == null) {
            escribirJson(response, clasesJson(dao.listar()));
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
        ClasesDAO dao = new ClasesDAO();

        if ("crear".equals(accion)) {
            String nombre = request.getParameter("nombre");
            String dia = request.getParameter("dia");
            String hora = request.getParameter("hora");
            String instructor = request.getParameter("instructor");

            if (estaVacio(nombre) || estaVacio(dia) || estaVacio(hora) || estaVacio(instructor)) {
                escribirJson(response, "{\"ok\":false,\"mensaje\":\"Nombre, dia, hora e instructor son obligatorios\"}");
                return;
            }

            Clases clase = new Clases();
            clase.setNombre(nombre.trim());
            clase.setDia(dia.trim());
            clase.setHora(hora.trim());
            clase.setInstructor(instructor.trim());
            escribirResultado(response, dao.insertar(clase), "No se pudo crear la clase");
            return;
        }

        if ("actualizar".equals(accion)) {
            int idClase = parseInt(request.getParameter("idClase"));
            String nombre = request.getParameter("nombre");
            String dia = request.getParameter("dia");
            String hora = request.getParameter("hora");
            String instructor = request.getParameter("instructor");

            if (idClase <= 0 || estaVacio(nombre) || estaVacio(dia) || estaVacio(hora) || estaVacio(instructor)) {
                escribirJson(response, "{\"ok\":false,\"mensaje\":\"Datos incompletos\"}");
                return;
            }

            Clases clase = new Clases(idClase, nombre.trim(), dia.trim(), hora.trim(), instructor.trim());
            boolean okActualizar = dao.actualizar(clase);
            String mensajeError = "No se pudo actualizar la clase"
                    + (dao.getUltimoError() != null ? " (" + dao.getUltimoError() + ")" : "");
            escribirResultado(response, okActualizar, mensajeError);
            return;
        }

        if ("eliminar".equals(accion)) {
            int idClase = parseInt(request.getParameter("idClase"));
            escribirResultado(response, dao.eliminar(idClase), "No se pudo eliminar la clase");
            return;
        }

        if ("inscribir".equals(accion)) {
            int idUsuario = obtenerIdUsuario(request);
            int idClase = parseInt(request.getParameter("idClase"));
            escribirResultado(response, dao.inscribir(idUsuario, idClase), "Ya estas inscrito o la clase no existe");
            return;
        }

        if ("desinscribir".equals(accion)) {
            int idUsuario = obtenerIdUsuario(request);
            int idClase = parseInt(request.getParameter("idClase"));
            escribirResultado(response, dao.desinscribir(idUsuario, idClase), "No se pudo salir de la clase");
            return;
        }

        escribirJson(response, "{\"ok\":false,\"mensaje\":\"Accion no valida\"}");
    }

    private int obtenerIdUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            return 0;
        }
        Object id = session.getAttribute("idUsuario");
        if (id instanceof Integer) {
            return (Integer) id;
        }
        return parseInt(String.valueOf(id));
    }

    private void escribirResultado(HttpServletResponse response, boolean ok, String mensaje)
            throws IOException {
        if (ok) {
            escribirJson(response, "{\"ok\":true}");
        } else {
            escribirJson(response, "{\"ok\":false,\"mensaje\":\"" + escaparJson(mensaje) + "\"}");
        }
    }

    private void escribirJson(HttpServletResponse response, String json) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
        }
    }

    String clasesJson(List<Clases> clases) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < clases.size(); i++) {
            Clases clase = clases.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{")
                    .append("\"id_Clases\":").append(clase.getId_clases()).append(",")
                    .append("\"nombre\":\"").append(escaparJson(clase.getNombre())).append("\",")
                    .append("\"dia\":\"").append(escaparJson(clase.getDia())).append("\",")
                    .append("\"hora\":\"").append(escaparJson(clase.getHora())).append("\",")
                    .append("\"instructor\":\"").append(escaparJson(clase.getInstructor())).append("\"")
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    String clasesConInscripcionJson(List<Clases> clases, ClasesDAO dao, int idUsuario) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < clases.size(); i++) {
            Clases clase = clases.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{")
                    .append("\"id_Clases\":").append(clase.getId_clases()).append(",")
                    .append("\"nombre\":\"").append(escaparJson(clase.getNombre())).append("\",")
                    .append("\"dia\":\"").append(escaparJson(clase.getDia())).append("\",")
                    .append("\"hora\":\"").append(escaparJson(clase.getHora())).append("\",")
                    .append("\"instructor\":\"").append(escaparJson(clase.getInstructor())).append("\",")
                    .append("\"inscrito\":").append(idUsuario > 0 && dao.estaInscrito(idUsuario, clase.getId_clases()))
                    .append("}");
        }
        json.append("]");
        return json.toString();
    }

    private int parseInt(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

