package servlet;

import Controlador.Conexion;
import Controlador.WompiConfig;
import Controlador.WompiService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Muestra el resumen de la compra y prepara los datos necesarios para que
 * Vista/Comprar.jsp abra el Web Checkout de Wompi (referencia, monto en
 * centavos y firma de integridad). El pago en si se procesa en Wompi; la
 * confirmacion final llega por WompiRedirectServlet.
 */
@WebServlet("/CompraServlet")
public class CompraServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("idUsuario") == null
                || !"cliente".equals(session.getAttribute("rol"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        session.removeAttribute("facturaResumen");

        int idUsuario;
        try {
            idUsuario = Integer.parseInt(String.valueOf(session.getAttribute("idUsuario")));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String tipoCompra = normalizarTipo(request.getParameter("tipo"));
        int idItem = parseInt(request.getParameter("id"));

        if (idItem <= 0 || tipoCompra.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/PanelClienteServlet?compra=error");
            return;
        }

        try (Connection con = Conexion.getConnect()) {
            Map<String, Object> item = consultarItem(con, tipoCompra, idItem);

            if (item == null) {
                response.sendRedirect(request.getContextPath() + "/PanelClienteServlet?compra=error");
                return;
            }

            BigDecimal precio = (BigDecimal) item.get("precio");
            long montoEnCentavos = precio.multiply(new BigDecimal(100)).longValueExact();

            // Referencia unica de pago. WompiRedirectServlet la decodifica para
            // saber que se compro, sin tener que confiar en parametros de la URL.
            String referencia = "FM-" + tipoCompra + "-" + idItem + "-" + idUsuario
                    + "-" + System.currentTimeMillis();
            String firma = WompiService.generarFirmaIntegridad(referencia, montoEnCentavos, WompiConfig.MONEDA);
            String urlRedireccion = urlAbsoluta(request, request.getContextPath() + "/WompiRedirectServlet");

            request.setAttribute("tipoCompra", tipoCompra);
            request.setAttribute("idItem", idItem);
            request.setAttribute("nombreItem", item.get("nombre"));
            request.setAttribute("precio", precio);
            request.setAttribute("duracionDias", item.get("duracionDias"));

            request.setAttribute("wompiPublicKey", WompiConfig.getLlavePublica());
            request.setAttribute("wompiMoneda", WompiConfig.MONEDA);
            request.setAttribute("wompiMontoCentavos", montoEnCentavos);
            request.setAttribute("wompiReferencia", referencia);
            request.setAttribute("wompiFirma", firma);
            request.setAttribute("wompiRedirectUrl", urlRedireccion);
            request.setAttribute("wompiCheckoutUrl", WompiConfig.URL_CHECKOUT);

            request.getRequestDispatcher("/Vista/Comprar.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/PanelClienteServlet?compra=error");
        }
    }

    private String urlAbsoluta(HttpServletRequest request, String path) {
        String esquema = request.getScheme();
        String host = request.getServerName();
        int puerto = request.getServerPort();
        boolean puertoEstandar = ("http".equals(esquema) && puerto == 80)
                || ("https".equals(esquema) && puerto == 443);
        return esquema + "://" + host + (puertoEstandar ? "" : ":" + puerto) + path;
    }

    /**
     * Inserta la factura (fact_cabecera / fact_detallada) de una compra ya
     * aprobada por Wompi. Visibilidad de paquete: la usa tambien
     * WompiRedirectServlet, que esta en el mismo paquete "servlet".
     */
    Map<String, Object> registrarFactura(Connection con, int idUsuario, String tipoCompra, int idItem,
            Map<String, Object> item, int idMetodoPago, String descripcionMetodo,
            String idTransaccionWompi, String referenciaPago) throws Exception {

        String numeroFactura = "FM-" + System.currentTimeMillis();
        BigDecimal precio = (BigDecimal) item.get("precio");
        // Concepto exacto de la compra (ej. "Membresía Anual" o el nombre del
        // producto) en vez de un texto genérico.
        String concepto = String.valueOf(item.get("nombre"));
        int idFactura;

        String sqlCabecera = "INSERT INTO fact_cabecera "
                + "(fecha_fact, n_factura, valor_total, concepto, id_metodo_pago, id_usuarios, "
                + "id_transaccion_wompi, referencia_pago) "
                + "VALUES (CURDATE(), ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sqlCabecera, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, numeroFactura);
            ps.setString(2, precio.toPlainString());
            ps.setString(3, concepto);
            ps.setInt(4, idMetodoPago);
            ps.setInt(5, idUsuario);
            ps.setString(6, idTransaccionWompi);
            ps.setString(7, referenciaPago);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new Exception("No se pudo obtener el ID de la factura.");
                }
                idFactura = keys.getInt(1);
            }
        }

        if ("producto".equals(tipoCompra)) {
            String sqlDetalle = "INSERT INTO fact_detallada "
                    + "(cantidad, id_productos, id_fact_cabecera) VALUES (?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                ps.setString(1, "1");
                ps.setInt(2, idItem);
                ps.setInt(3, idFactura);
                ps.executeUpdate();
            }
        }

        Map<String, Object> factura = new HashMap<>();
        factura.put("id", idFactura);
        factura.put("numero", numeroFactura);
        factura.put("fecha", java.time.LocalDate.now().toString());
        factura.put("tipoCompra", tipoCompra);
        factura.put("item", item.get("nombre"));
        factura.put("valor", precio);
        factura.put("metodoPago", descripcionMetodo);
        factura.put("cantidad", "producto".equals(tipoCompra) ? "1" : "1 membresia");
        return factura;
    }

    Map<String, Object> consultarItem(Connection con, String tipoCompra, int idItem) throws Exception {
        String sql;

        if ("membresia".equals(tipoCompra)) {
            sql = "SELECT id_membresias AS id, tipo AS nombre, precio, duracion_dias FROM membresias WHERE id_membresias = ?";
        } else if ("producto".equals(tipoCompra)) {
            sql = "SELECT id_productos AS id, nombre, precio, NULL AS duracion_dias FROM productos WHERE id_productos = ?";
        } else {
            return null;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idItem);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("nombre", rs.getString("nombre"));
                item.put("precio", rs.getBigDecimal("precio"));
                item.put("duracionDias", rs.getObject("duracion_dias"));
                return item;
            }
        }
    }

    /**
     * Devuelve el id_metodo_pago con descripcion "Wompi", creandolo si aun
     * no existe en la tabla metodo_pago.
     */
    int obtenerOcrearMetodoPagoWompi(Connection con) throws Exception {
        String sqlSelect = "SELECT id_metodo_pago FROM metodo_pago WHERE descripcion = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlSelect)) {
            ps.setString(1, "Wompi");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_metodo_pago");
                }
            }
        }

        String sqlInsert = "INSERT INTO metodo_pago (descripcion) VALUES (?)";
        try (PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Wompi");
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new Exception("No se pudo crear el metodo de pago Wompi.");
    }

    String normalizarTipo(String value) {
        String tipo = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return ("membresia".equals(tipo) || "producto".equals(tipo)) ? tipo : "";
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
