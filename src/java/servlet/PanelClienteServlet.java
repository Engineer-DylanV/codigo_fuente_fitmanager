package servlet;

import Controlador.Conexion;
import Controlador.PerfilDAO;
import Controlador.RedesSocialesDAO;
import Modelo.Usuarios;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "PanelClienteServlet", urlPatterns = {"/PanelClienteServlet"})
public class PanelClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null
                || !"cliente".equals(session.getAttribute("rol"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int idUsuario;
        try {
            idUsuario = Integer.parseInt(String.valueOf(session.getAttribute("idUsuario")));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        List<Map<String, Object>> membresias = new ArrayList<>();
        List<Map<String, Object>> productos = new ArrayList<>();
        Map<String, Object> ultimaFactura = null;
        List<Map<String, Object>> todasFacturas = new ArrayList<>();
        boolean cuentaActiva = false;

        try (Connection con = Conexion.getConnect()) {
            cuentaActiva = cargarEstadoUsuario(con, session, idUsuario);
            membresias = listarMembresias(con);
            productos = listarProductos(con);
            ultimaFactura = consultarUltimaFactura(con, idUsuario);
            todasFacturas = listarTodasFacturas(con, idUsuario);
        } catch (Exception e) {
            request.setAttribute("errorCliente", "No se pudo cargar el panel de cliente: " + e.getMessage());
        }

        request.setAttribute("clienteDatosCargados", true);
        request.setAttribute("cuentaActiva", cuentaActiva);
        request.setAttribute("membresias", membresias);
        request.setAttribute("productos", productos);
        request.setAttribute("ultimaFactura", ultimaFactura);
        request.setAttribute("todasFacturas", todasFacturas);
        PerfilDAO perfilDAO = new PerfilDAO();
        request.setAttribute("perfilCuenta", perfilDAO.obtenerDatosCuenta(idUsuario));
        request.setAttribute("avataresPerfil", perfilDAO.listarAvataresActivos());
        request.setAttribute("redesSociales", new RedesSocialesDAO().listarActivas());

        // Mensajes de acciones realizadas desde "Mi perfil". Se muestran una
        // sola vez después de la redirección para evitar repetir formularios.
        Object perfilMensaje = session.getAttribute("perfilMensaje");
        Object perfilError = session.getAttribute("perfilError");
        if (perfilMensaje != null) {
            request.setAttribute("perfilMensaje", perfilMensaje);
            request.setAttribute("perfilError", Boolean.TRUE.equals(perfilError));
            session.removeAttribute("perfilMensaje");
            session.removeAttribute("perfilError");
        }
        request.getRequestDispatcher("/Vista/Cliente.jsp").forward(request, response);
    }

    private boolean cargarEstadoUsuario(Connection con, HttpSession session, int idUsuario) throws SQLException {
        String sql = "SELECT id_membresias, vencimiento FROM usuarios WHERE id_usuarios = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                int idMembresia = rs.getInt("id_membresias");
                Date vencimiento = rs.getDate("vencimiento");

                Usuarios usuario = (Usuarios) session.getAttribute("usuario");
                usuario.setId_membresias(rs.wasNull() ? 0 : idMembresia);
                usuario.setVencimiento(vencimiento == null ? null : vencimiento.toString());
                session.setAttribute("usuario", usuario);

                return idMembresia > 0
                        && vencimiento != null
                        && !vencimiento.toLocalDate().isBefore(LocalDate.now());
            }
        }
    }

    private List<Map<String, Object>> listarMembresias(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_membresias, tipo, precio, duracion_dias FROM membresias ORDER BY id_membresias";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_membresias"));
                fila.put("nombre", rs.getString("tipo"));
                fila.put("precio", rs.getDouble("precio"));
                fila.put("duracion_dias", rs.getInt("duracion_dias"));
                lista.add(fila);
            }
        }

        return lista;
    }

    private List<Map<String, Object>> listarProductos(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_productos, nombre, precio FROM productos ORDER BY id_productos";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_productos"));
                fila.put("nombre", rs.getString("nombre"));
                fila.put("precio", rs.getDouble("precio"));
                lista.add(fila);
            }
        }

        return lista;
    }

    private List<Map<String, Object>> listarTodasFacturas(Connection con, int idUsuario) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_fact_cabecera, fecha_fact, n_factura, valor_total, concepto "
                + "FROM fact_cabecera "
                + "WHERE id_usuarios = ? "
                + "ORDER BY id_fact_cabecera DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> factura = new HashMap<>();
                    factura.put("id", rs.getInt("id_fact_cabecera"));
                    factura.put("fecha", rs.getDate("fecha_fact"));
                    factura.put("numero", rs.getString("n_factura"));
                    factura.put("valorTotal", rs.getString("valor_total"));
                    String concepto = rs.getString("concepto");
                    factura.put("concepto", concepto != null && !concepto.trim().isEmpty() ? concepto : "Membresía FitManager");
                    lista.add(factura);
                }
            }
        }

        return lista;
    }

    private Map<String, Object> consultarUltimaFactura(Connection con, int idUsuario) throws SQLException {
        String sql = "SELECT id_fact_cabecera, fecha_fact, n_factura, valor_total, concepto "
                + "FROM fact_cabecera "
                + "WHERE id_usuarios = ? "
                + "ORDER BY id_fact_cabecera DESC LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Map<String, Object> factura = new HashMap<>();
                factura.put("id", rs.getInt("id_fact_cabecera"));
                factura.put("fecha", rs.getDate("fecha_fact"));
                factura.put("numero", rs.getString("n_factura"));
                factura.put("valorTotal", rs.getString("valor_total"));
                String concepto = rs.getString("concepto");
                factura.put("concepto", concepto != null && !concepto.trim().isEmpty() ? concepto : "Membresía FitManager");
                return factura;
            }
        }
    }
}
