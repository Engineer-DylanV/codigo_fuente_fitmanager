package servlet;

import Controlador.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "FormularioRegistroServlet", urlPatterns = {"/FormularioRegistroServlet"})
public class FormularioRegistroServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarDatosYMostrar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarDatosYMostrar(request, response);
    }

    private void cargarDatosYMostrar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tipoCompra = request.getAttribute("tipoCompra") != null
                ? String.valueOf(request.getAttribute("tipoCompra"))
                : request.getParameter("tipo");

        String idCompra = request.getAttribute("idCompra") != null
                ? String.valueOf(request.getAttribute("idCompra"))
                : request.getParameter("id");

        List<Map<String, Object>> tiposDocumento = new ArrayList<>();
        List<Map<String, Object>> membresias = new ArrayList<>();

        try {
            try (Connection con = Conexion.getConnect()) {
                if (con == null) throw new SQLException("No hay conexión disponible.");
                tiposDocumento = listarTiposDocumento(con);
                membresias = listarMembresias(con);
            }

        } catch (Exception e) {
            request.setAttribute("error", "No se pudieron cargar los datos del formulario: " + e.getMessage());
        }

        request.setAttribute("registroDatosCargados", true);
        request.setAttribute("tiposDocumento", tiposDocumento);
        request.setAttribute("membresias", membresias);
        request.setAttribute("tipoCompra", tipoCompra);
        request.setAttribute("idCompra", idCompra);

        request.getRequestDispatcher("/Vista/Register.jsp").forward(request, response);
    }

    private List<Map<String, Object>> listarTiposDocumento(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT id_tipo_documento, descripcion FROM tipo_documento ORDER BY id_tipo_documento";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_tipo_documento"));
                fila.put("descripcion", rs.getString("descripcion"));
                lista.add(fila);
            }
        }

        return lista;
    }

    private List<Map<String, Object>> listarMembresias(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT id_membresias, tipo FROM membresias ORDER BY id_membresias";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_membresias"));
                fila.put("tipo", rs.getString("tipo"));
                lista.add(fila);
            }
        }

        return lista;
    }
}
