package servlet;

import Controlador.Conexion;
import Controlador.RedesSocialesDAO;
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

@WebServlet("/InicioServlet")
public class InicioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarDatosYMostrar(request, response);
    }

    private void cargarDatosYMostrar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Map<String, Object>> membresias = new ArrayList<>();
        List<Map<String, Object>> proveedores = new ArrayList<>();
        List<Map<String, Object>> productos = new ArrayList<>();
        List<Map<String, Object>> sedes = new ArrayList<>();

        try {
            try (Connection con = Conexion.getConnect()) {
                if (con == null) throw new SQLException("No hay conexión disponible.");
                membresias = listarMembresias(con);
                proveedores = listarProveedores(con);
                productos = listarProductosConProveedor(con, proveedores);
                sedes = listarSedes(con);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Throwable raiz = e;
            while (raiz.getCause() != null && raiz.getCause() != raiz) {
                raiz = raiz.getCause();
            }
            String detalle = raiz.getClass().getSimpleName() + ": " + raiz.getMessage();
            request.setAttribute("errorCatalogo",
                    "No se pudo cargar el catálogo: " + e.getMessage() + " | Causa raíz: " + detalle);
        }

        request.setAttribute("catalogoCargado", true);
        request.setAttribute("membresias", membresias);
        request.setAttribute("proveedores", proveedores);
        request.setAttribute("productos", productos);
        request.setAttribute("sedes", sedes);
        request.setAttribute("redesSociales", new RedesSocialesDAO().listarActivas());

        request.getRequestDispatcher("/Index.jsp").forward(request, response);
    }

    /**
     * Trae los productos y les agrega el nombre del proveedor que los surte.
     * No hay relacion (FK) entre las tablas en la base de datos, asi que el
     * cruce se hace por nombre: proveedores.tipo_producto == productos.nombre.
     */
    private List<Map<String, Object>> listarProductosConProveedor(Connection con,
            List<Map<String, Object>> proveedores) throws SQLException {

        List<Map<String, Object>> lista;

        // Se intenta primero con la columna descripcion_consumo (ver
        // migraciones/migracion_descripciones.sql). Si esa columna todavia
        // no existe en la base de datos, se cae automaticamente a la
        // consulta antigua para que el sitio no se rompa mientras se aplica
        // la migracion.
        try {
            lista = consultarProductos(con, proveedores,
                    "SELECT id_productos, nombre, precio, descripcion_consumo FROM productos ORDER BY id_productos",
                    true);
        } catch (SQLException e) {
            System.out.println("Columna descripcion_consumo aun no existe, usando consulta basica: " + e.getMessage());
            lista = consultarProductos(con, proveedores,
                    "SELECT id_productos, nombre, precio FROM productos ORDER BY id_productos",
                    false);
        }

        return lista;
    }

    private List<Map<String, Object>> consultarProductos(Connection con,
            List<Map<String, Object>> proveedores, String sql, boolean conDescripcion) throws SQLException {

        List<Map<String, Object>> lista = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                String nombreProducto = rs.getString("nombre");
                fila.put("id", rs.getInt("id_productos"));
                fila.put("nombre", nombreProducto);
                fila.put("precio", rs.getDouble("precio"));
                fila.put("descripcionConsumo", conDescripcion ? rs.getString("descripcion_consumo") : null);

                String proveedor = null;
                for (Map<String, Object> pr : proveedores) {
                    String tipoProducto = String.valueOf(pr.get("tipo_producto"));
                    if (tipoProducto.equalsIgnoreCase(nombreProducto)) {
                        proveedor = String.valueOf(pr.get("nombre"));
                        break;
                    }
                }
                fila.put("proveedor", proveedor);

                lista.add(fila);
            }
        }

        return lista;
    }

    private List<Map<String, Object>> listarMembresias(Connection con) throws SQLException {
        List<Map<String, Object>> lista;

        // Igual que con productos: se intenta con beneficio_extra (ver
        // migraciones/migracion_descripciones.sql) y si la columna aun no
        // existe, se cae a la consulta basica sin romper el sitio.
        try {
            lista = consultarMembresias(con,
                    "SELECT id_membresias, tipo, precio, duracion_dias, beneficio_extra FROM membresias ORDER BY id_membresias",
                    true);
        } catch (SQLException e) {
            System.out.println("Columna beneficio_extra aun no existe, usando consulta basica: " + e.getMessage());
            lista = consultarMembresias(con,
                    "SELECT id_membresias, tipo, precio, duracion_dias FROM membresias ORDER BY id_membresias",
                    false);
        }

        marcarMayorAhorro(lista);
        marcarMasComprada(con, lista);

        return lista;
    }

    private List<Map<String, Object>> consultarMembresias(Connection con, String sql, boolean conBeneficio)
            throws SQLException {

        List<Map<String, Object>> lista = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_membresias"));
                fila.put("nombre", rs.getString("tipo"));
                fila.put("precio", rs.getDouble("precio"));
                fila.put("duracion_dias", rs.getInt("duracion_dias"));
                fila.put("beneficioExtra", conBeneficio ? rs.getString("beneficio_extra") : null);
                fila.put("esMayorAhorro", false);
                fila.put("esMasComprada", false);
                lista.add(fila);
            }
        }

        return lista;
    }

    /**
     * Marca la membresia con el mejor costo por dia (precio / duracion_dias)
     * como "Mayor ahorro". Se excluyen los pases de un solo dia porque no
     * representan un ahorro a largo plazo, solo se comparan membresias con
     * duracion mayor a 1 dia.
     */
    private void marcarMayorAhorro(List<Map<String, Object>> lista) {
        Map<String, Object> mejor = null;
        double mejorCostoPorDia = Double.MAX_VALUE;

        for (Map<String, Object> fila : lista) {
            int duracion = (int) fila.get("duracion_dias");
            double precio = (double) fila.get("precio");
            if (duracion <= 1) continue;

            double costoPorDia = precio / duracion;
            if (costoPorDia < mejorCostoPorDia) {
                mejorCostoPorDia = costoPorDia;
                mejor = fila;
            }
        }

        if (mejor != null) {
            mejor.put("esMayorAhorro", true);
        }
    }

    /**
     * Marca la membresia mas comprada consultando el historico de facturas.
     * El concepto de la factura se guarda con el mismo texto que el tipo de
     * la membresia (ver CompraServlet), asi que se cuentan coincidencias por
     * ese texto. Si la consulta falla o no hay compras registradas, no se
     * marca ninguna membresia (evita mostrar una insignia sin datos reales).
     */
    private void marcarMasComprada(Connection con, List<Map<String, Object>> lista) {
        String sql = "SELECT concepto, COUNT(*) AS total FROM fact_cabecera GROUP BY concepto";
        Map<String, Integer> conteoPorConcepto = new HashMap<>();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                conteoPorConcepto.put(rs.getString("concepto"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.out.println("No se pudo calcular la membresia mas comprada: " + e.getMessage());
            return;
        }

        Map<String, Object> mejor = null;
        int mejorConteo = 0;

        for (Map<String, Object> fila : lista) {
            String nombre = String.valueOf(fila.get("nombre"));
            Integer conteo = conteoPorConcepto.get(nombre);
            if (conteo != null && conteo > mejorConteo) {
                mejorConteo = conteo;
                mejor = fila;
            }
        }

        if (mejor != null) {
            mejor.put("esMasComprada", true);
        }
    }

    private List<Map<String, Object>> listarProveedores(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT id_Proveedores, Nombre, tipo_producto FROM proveedores ORDER BY id_Proveedores";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_Proveedores"));
                fila.put("nombre", rs.getString("nombre"));
                fila.put("tipo_producto", rs.getString("tipo_producto"));
                lista.add(fila);
            }
        }

        return lista;
    }

    private List<Map<String, Object>> listarSedes(Connection con) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT id_sedes, nombre, direccion, horario, telefono, descripcion FROM sedes ORDER BY id_sedes";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id", rs.getInt("id_sedes"));
                fila.put("nombre", rs.getString("nombre"));
                fila.put("direccion", rs.getString("direccion"));
                fila.put("horario", rs.getString("horario"));
                fila.put("telefono", rs.getString("telefono"));
                fila.put("descripcion", rs.getString("descripcion"));
                lista.add(fila);
            }
        }

        return lista;
    }
}
