package servlet;

import Controlador.UsuariosDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        String accion = request.getParameter("accion");

        if ("listar".equals(accion)) {
            UsuariosDAO dao = new UsuariosDAO();
            try (ResultSet rs = dao.listarAdmin()) {
                escribirJson(response, usuariosJson(rs));
            } catch (SQLException e) {
                escribirJson(response, "[]");
            }
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
        UsuariosDAO dao = new UsuariosDAO();

        if ("editar".equals(accion)) {
            int idUsuario = parseInt(request.getParameter("idUsuario"));
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            int membresia = parseInt(request.getParameter("membresia"));

            boolean ok = dao.actualizarAdmin(idUsuario, nombre, correo, membresia);
            escribirResultado(response, ok, "No se pudo actualizar el usuario");
            return;
        }

        if ("eliminar".equals(accion)) {
            int idUsuario = parseInt(request.getParameter("idUsuario"));
            boolean ok = dao.eliminar(idUsuario);
            escribirResultado(response, ok, "No se pudo eliminar el usuario");
            return;
        }

        if ("vencimiento".equals(accion)) {
            String correo = request.getParameter("correoVencimiento");
            String fecha = request.getParameter("fechaVencimiento");
            boolean ok = dao.asignarVencimiento(correo, fecha);
            escribirResultado(response, ok, "No se pudo asignar el vencimiento");
            return;
        }

        escribirJson(response, "{\"ok\":false,\"mensaje\":\"Accion no valida\"}");
    }

    private String usuariosJson(ResultSet rs) throws SQLException {
        StringBuilder json = new StringBuilder("[");
        boolean primero = true;

        while (rs.next()) {
            if (!primero) {
                json.append(",");
            }
            primero = false;

            String vencimiento = rs.getDate("vencimiento") == null ? "" : rs.getDate("vencimiento").toString();

            json.append("{")
                    .append("\"id_Usuarios\":").append(rs.getInt("id_usuarios")).append(",")
                    .append("\"nombre\":\"").append(escaparJson(rs.getString("nombre"))).append("\",")
                    .append("\"apellido\":\"").append(escaparJson(rs.getString("apellido"))).append("\",")
                    .append("\"tipoDoc\":\"").append(escaparJson(rs.getString("tipoDoc"))).append("\",")
                    .append("\"documento\":\"").append(escaparJson(rs.getString("documento"))).append("\",")
                    .append("\"email\":\"").append(escaparJson(rs.getString("email"))).append("\",")
                    .append("\"membresia\":\"").append(escaparJson(rs.getString("membresia"))).append("\",")
                    .append("\"vencimiento\":\"").append(vencimiento).append("\"")
                    .append("}");
        }

        json.append("]");
        return json.toString();
    }

    private void escribirResultado(HttpServletResponse response, boolean ok, String mensaje)
            throws IOException {
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
