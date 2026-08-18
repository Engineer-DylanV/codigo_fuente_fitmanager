package servlet;

import Controlador.ProductosDAO;
import Controlador.ProveedoresDAO;
import Controlador.SedesDAO;
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

@WebServlet(name = "PanelAdministradorServlet", urlPatterns = {"/PanelAdministradorServlet"})
public class PanelAdministradorServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://" + System.getenv().getOrDefault("MYSQLHOST","fitmanager-mysqldb.mysql.database.azure.com") + ":" + System.getenv().getOrDefault("MYSQLPORT","3306") + "/" + System.getenv().getOrDefault("MYSQLDATABASE","railway") + "?useSSL=true&requireSSL=true&verifyServerCertificate=false&serverTimezone=UTC&autoReconnect=true";
    private static final String DB_USER = System.getenv().getOrDefault("MYSQLUSER","fitmanager_admin");
    private static final String DB_PASS = System.getenv().getOrDefault("MYSQLPASSWORD","Fitmanagerpassword#");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        List<Map<String, Object>> usuariosAdmin = new ArrayList<>();
        List<Map<String, Object>> cabeceras = new ArrayList<>();
        List<Map<String, Object>> detalladas = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                usuariosAdmin = listarUsuariosAdmin(con);
                cabeceras = listarFacturasCabecera(con);
                detalladas = listarFacturasDetalladas(con);

                request.setAttribute("tiposDocumento", listarCatalogo(con, "tipo_documento", "id_tipo_documento", "descripcion"));
                request.setAttribute("rolesAdmin", listarCatalogo(con, "roles", "id_roles", "descripcion"));
                request.setAttribute("metodosPago", listarCatalogo(con, "metodo_pago", "id_metodo_pago", "descripcion"));
                request.setAttribute("membresias", listarMembresias(con));
                request.setAttribute("evaluacionesAdmin", listarEvaluacionesFisicas(con));
                request.setAttribute("productos", new ProductosDAO(con).listar());
                request.setAttribute("proveedores", new ProveedoresDAO(con).listar());
                request.setAttribute("sedes", new SedesDAO(con).listar());
            }

        } catch (Exception e) {
            request.setAttribute("errorFacturas", e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

        request.setAttribute("datosAdminCargados", true);
        request.setAttribute("usuariosAdmin", usuariosAdmin);
        request.setAttribute("facturasCabecera", cabeceras);
        request.setAttribute("facturasDetalladas", detalladas);

        request.getRequestDispatcher("/Vista/Admin.jsp").forward(request, response);
    }

    List<Map<String, Object>> listarUsuariosAdmin(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT u.id_usuarios, u.nombre, u.apellido, u.documento, u.email, "
                + "u.id_roles, r.descripcion AS rol, td.descripcion AS tipoDoc, "
                + "m.tipo AS membresia, u.vencimiento, u.activo "
                + "FROM usuarios u "
                + "LEFT JOIN roles r ON u.id_roles = r.id_roles "
                + "LEFT JOIN tipo_documento td ON u.id_tipo_documento = td.id_tipo_documento "
                + "LEFT JOIN membresias m ON u.id_membresias = m.id_membresias "
                + "ORDER BY u.id_usuarios DESC";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");

                fila.put("id", rs.getInt("id_usuarios"));
                fila.put("nombre", (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : ""));
                fila.put("tipoDoc", rs.getString("tipoDoc"));
                fila.put("documento", rs.getString("documento"));
                fila.put("correo", rs.getString("email"));
                fila.put("rol", rs.getString("rol"));
                fila.put("idRol", rs.getInt("id_roles"));
                fila.put("membresia", rs.getString("membresia"));
                fila.put("vencimiento", rs.getDate("vencimiento"));
                fila.put("activo", rs.getInt("activo") == 1);
                lista.add(fila);
            }
        }

        return lista;
    }

    private List<Map<String, Object>> listarCatalogo(Connection con, String tabla, String idCampo, String descripcionCampo)
            throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT " + idCampo + " AS id, " + descripcionCampo + " AS descripcion FROM " + tabla + " ORDER BY " + idCampo;

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id"));
                fila.put("descripcion", rs.getString("descripcion"));
                lista.add(fila);
            }
        }

        return lista;
    }

    private List<Map<String, Object>> listarMembresias(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_membresias, tipo, precio, duracion_dias FROM membresias ORDER BY id_membresias";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_membresias"));
                fila.put("tipo", rs.getString("tipo"));
                fila.put("precio", rs.getDouble("precio"));
                fila.put("duracionDias", rs.getInt("duracion_dias"));
                lista.add(fila);
            }
        }

        return lista;
    }

    private List<Map<String, Object>> listarEvaluacionesFisicas(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT e.id_evaluaciones_fisicas, e.fecha, e.peso, e.edad, e.condicion, e.pruebas, "
                + "u.email, u.nombre, u.apellido "
                + "FROM evaluaciones_fisicas e "
                + "LEFT JOIN usuarios u ON e.id_usuarios = u.id_usuarios "
                + "ORDER BY e.fecha DESC, e.id_evaluaciones_fisicas DESC";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");

                fila.put("id", rs.getInt("id_evaluaciones_fisicas"));
                fila.put("fecha", rs.getDate("fecha"));
                fila.put("peso", rs.getString("peso"));
                fila.put("edad", rs.getString("edad"));
                fila.put("condicion", rs.getString("condicion"));
                fila.put("pruebas", rs.getString("pruebas"));
                fila.put("correo", rs.getString("email"));
                fila.put("usuario", (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : ""));
                lista.add(fila);
            }
        }

        return lista;
    }

    private List<Map<String, Object>> listarFacturasCabecera(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT fc.id_fact_cabecera, fc.fecha_fact, fc.n_factura, fc.valor_total, fc.concepto, "
                + "mp.descripcion AS metodo_pago, u.nombre, u.apellido, u.email "
                + "FROM fact_cabecera fc "
                + "LEFT JOIN metodo_pago mp ON fc.id_metodo_pago = mp.id_metodo_pago "
                + "LEFT JOIN usuarios u ON fc.id_usuarios = u.id_usuarios "
                + "ORDER BY fc.id_fact_cabecera";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String concepto = rs.getString("concepto");

                fila.put("id", rs.getInt("id_fact_cabecera"));
                fila.put("fecha", rs.getDate("fecha_fact"));
                fila.put("numero", rs.getString("n_factura"));
                fila.put("valor_total", rs.getString("valor_total"));
                fila.put("concepto", concepto != null && !concepto.trim().isEmpty() ? concepto : "Membresía FitManager");
                fila.put("metodoPago", rs.getString("metodo_pago"));
                fila.put("usuario", (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : ""));
                fila.put("correo", rs.getString("email"));
                lista.add(fila);
            }
        }

        return lista;
    }

    private List<Map<String, Object>> listarFacturasDetalladas(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT fd.id_fact_detallada, fd.cantidad, p.nombre AS producto, p.precio, "
                + "fc.n_factura, fc.fecha_fact, u.nombre, u.apellido "
                + "FROM fact_detallada fd "
                + "LEFT JOIN productos p ON fd.id_productos = p.id_productos "
                + "LEFT JOIN fact_cabecera fc ON fd.id_fact_cabecera = fc.id_fact_cabecera "
                + "LEFT JOIN usuarios u ON fc.id_usuarios = u.id_usuarios "
                + "ORDER BY fd.id_fact_detallada";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");

                fila.put("id", rs.getInt("id_fact_detallada"));
                fila.put("numeroFactura", rs.getString("n_factura"));
                fila.put("producto", rs.getString("producto"));
                fila.put("precio", rs.getBigDecimal("precio"));
                fila.put("cantidad", rs.getString("cantidad"));
                fila.put("fecha", rs.getDate("fecha_fact"));
                fila.put("usuario", (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : ""));
                lista.add(fila);
            }
        }

        return lista;
    }
}

