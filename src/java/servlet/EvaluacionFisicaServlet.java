package servlet;

import Controlador.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/EvaluacionFisicaServlet")
public class EvaluacionFisicaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        int idUsuario = obtenerIdUsuario(request);

        if (idUsuario <= 0) {
            escribirJson(response, "[]");
            return;
        }

        String sql = "SELECT id_evaluaciones_fisicas, fecha, peso, edad, condicion, pruebas "
                + "FROM evaluaciones_fisicas WHERE id_usuarios = ? "
                + "ORDER BY fecha DESC, id_evaluaciones_fisicas DESC";

        StringBuilder json = new StringBuilder("[");

        try (Connection con = Conexion.getConnect();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                boolean primero = true;

                while (rs.next()) {
                    if (!primero) {
                        json.append(",");
                    }

                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id_evaluaciones_fisicas")).append(",")
                            .append("\"fecha\":\"").append(escaparJson(rs.getString("fecha"))).append("\",")
                            .append("\"peso\":\"").append(escaparJson(rs.getString("peso"))).append("\",")
                            .append("\"edad\":\"").append(escaparJson(rs.getString("edad"))).append("\",")
                            .append("\"condicion\":\"").append(escaparJson(rs.getString("condicion"))).append("\",")
                            .append("\"pruebas\":\"").append(escaparJson(rs.getString("pruebas"))).append("\"")
                            .append("}");

                    primero = false;
                }
            }

        } catch (Exception e) {
            escribirJson(response, "[]");
            return;
        }

        json.append("]");
        escribirJson(response, json.toString());
    }

    private int obtenerIdUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            return 0;
        }

        try {
            return Integer.parseInt(String.valueOf(session.getAttribute("idUsuario")));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void escribirJson(HttpServletResponse response, String json) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
        }
    }

    private String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
