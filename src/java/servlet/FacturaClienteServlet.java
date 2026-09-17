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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/FacturaClienteServlet")
public class FacturaClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("idUsuario") == null
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
        int idFactura = parseInt(request.getParameter("id"));

        try (Connection con = Conexion.getConnect()) {
            Map<String, Object> factura = consultarFactura(con, idUsuario, idFactura);

            if (factura == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Factura no encontrada.");
                return;
            }

            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");

            try (PrintWriter out = response.getWriter()) {
                escribirFactura(out, factura);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No se pudo cargar la factura.");
        }
    }

    Map<String, Object> consultarFactura(Connection con, int idUsuario, int idFactura) throws Exception {
        String sql = "SELECT fc.id_fact_cabecera, fc.fecha_fact, fc.n_factura, fc.valor_total, fc.concepto, "
                + "fc.id_transaccion_wompi, fc.referencia_pago, "
                + "mp.descripcion AS metodo_pago, u.nombre, u.apellido, u.email, u.telefono "
                + "FROM fact_cabecera fc "
                + "LEFT JOIN metodo_pago mp ON fc.id_metodo_pago = mp.id_metodo_pago "
                + "LEFT JOIN usuarios u ON fc.id_usuarios = u.id_usuarios "
                + "WHERE fc.id_usuarios = ? ";

        if (idFactura > 0) {
            sql += "AND fc.id_fact_cabecera = ? ";
        }

        sql += "ORDER BY fc.id_fact_cabecera DESC LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            if (idFactura > 0) {
                ps.setInt(2, idFactura);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Map<String, Object> factura = new HashMap<>();
                factura.put("id", rs.getInt("id_fact_cabecera"));
                factura.put("fecha", rs.getDate("fecha_fact"));
                factura.put("numero", rs.getString("n_factura"));
                factura.put("valor_total", rs.getString("valor_total"));
                factura.put("metodoPago", rs.getString("metodo_pago"));
                factura.put("cliente", valorSeguro(rs.getString("nombre")) + " " + valorSeguro(rs.getString("apellido")));
                factura.put("correo", rs.getString("email"));
                factura.put("telefono", valorSeguro(rs.getString("telefono")));
                factura.put("conceptoCabecera", rs.getString("concepto"));
                factura.put("idTransaccion", rs.getString("id_transaccion_wompi"));
                factura.put("referenciaPago", rs.getString("referencia_pago"));

                cargarDetalle(con, factura);
                cargarInfoCompra(con, factura);
                return factura;
            }
        }
    }

    /**
     * A partir de la referencia de pago (formato FM-{tipo}-{idItem}-{idUsuario}-{timestamp})
     * determina si la compra fue una membresia o un producto, y si fue
     * membresia consulta su nombre y duracion exacta en la tabla membresias.
     */
    private void cargarInfoCompra(Connection con, Map<String, Object> factura) {
        String tipoCompra = "";
        String nombreMembresia = "";
        int duracionDias = 0;
        String referenciaPago = (String) factura.get("referenciaPago");

        if (referenciaPago != null) {
            String[] partes = referenciaPago.split("-");
            if (partes.length >= 3 && "FM".equals(partes[0])) {
                String tipo = partes[1];
                try {
                    int idItem = Integer.parseInt(partes[2]);
                    if ("membresia".equals(tipo)) {
                        tipoCompra = "Membresía";
                        try (PreparedStatement ps = con.prepareStatement(
                                "SELECT tipo, duracion_dias FROM membresias WHERE id_membresias = ?")) {
                            ps.setInt(1, idItem);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    nombreMembresia = rs.getString("tipo");
                                    duracionDias = rs.getInt("duracion_dias");
                                }
                            }
                        }
                    } else if ("producto".equals(tipo)) {
                        tipoCompra = "Producto";
                    }
                } catch (NumberFormatException | java.sql.SQLException ignored) { }
            }
        }
        if (tipoCompra.isEmpty()) {
            tipoCompra = "Membresía";
        }
        factura.put("tipoCompra", tipoCompra);
        factura.put("nombreMembresia", nombreMembresia);
        factura.put("duracionDias", duracionDias);
    }

    private void cargarDetalle(Connection con, Map<String, Object> factura) throws Exception {
        String sql = "SELECT fd.cantidad, p.nombre AS producto, p.precio "
                + "FROM fact_detallada fd "
                + "LEFT JOIN productos p ON fd.id_productos = p.id_productos "
                + "WHERE fd.id_fact_cabecera = ? "
                + "ORDER BY fd.id_fact_detallada LIMIT 1";

        String conceptoCabecera = String.valueOf(factura.get("conceptoCabecera"));
        boolean hayConceptoCabecera = factura.get("conceptoCabecera") != null && !conceptoCabecera.trim().isEmpty();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(String.valueOf(factura.get("id"))));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // El concepto guardado en fact_cabecera es la fuente de verdad;
                    // el nombre del producto solo se usa como respaldo si no hay concepto.
                    factura.put("concepto", hayConceptoCabecera ? conceptoCabecera : rs.getString("producto"));
                    factura.put("cantidad", rs.getString("cantidad"));
                    factura.put("precioUnitario", rs.getBigDecimal("precio"));
                } else {
                    factura.put("concepto", hayConceptoCabecera ? conceptoCabecera : "Membresia FitManager");
                    factura.put("cantidad", "1");
                    factura.put("precioUnitario", new BigDecimal(String.valueOf(factura.get("valor_total"))));
                }
            }
        }
    }

    private void escribirFactura(PrintWriter out, Map<String, Object> f) {
        String numero = escapeHtml(String.valueOf(f.get("numero")));
        String archivo = "comprobante-" + numero.replaceAll("[^A-Za-z0-9_-]", "") + ".html";
        String tipoCompra = escapeHtml(String.valueOf(f.get("tipoCompra")));
        String nombreMembresia = escapeHtml(String.valueOf(f.get("nombreMembresia")));
        int duracionDias = f.get("duracionDias") != null ? (Integer) f.get("duracionDias") : 0;
        String idTransaccion = valorSeguro((String) f.get("idTransaccion"));
        String referenciaPago = valorSeguro((String) f.get("referenciaPago"));

        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Comprobante " + numero + "</title>");
        out.println("<style>");
        out.println("body{margin:0;background:#f3f4f6;color:#111827;font-family:Arial,sans-serif;}");
        out.println(".wrap{max-width:820px;margin:32px auto;padding:0 16px;}");
        out.println(".factura{background:#fff;border:1px solid #d1d5db;padding:34px;border-radius:8px;}");
        out.println(".top{display:flex;justify-content:space-between;gap:20px;border-bottom:2px solid #111827;padding-bottom:18px;}");
        out.println("h1{margin:0;font-size:30px;} h2{margin:0 0 6px;font-size:18px;} p{margin:4px 0;color:#374151;}");
        out.println(".comprado{background:#eef2ff;border:1px solid #c7d2fe;border-radius:8px;padding:16px 20px;margin:22px 0;}");
        out.println(".comprado .label{font-size:12px;text-transform:uppercase;letter-spacing:.06em;color:#4338ca;margin:0;}");
        out.println(".comprado .valor{font-size:20px;font-weight:700;color:#111827;margin:4px 0 0;}");
        out.println(".comprado .duracion{font-size:13px;color:#4338ca;margin:4px 0 0;font-weight:600;}");
        out.println(".grid{display:grid;grid-template-columns:1fr 1fr;gap:22px;margin:24px 0;}");
        out.println("table{width:100%;border-collapse:collapse;margin-top:16px;} th,td{border-bottom:1px solid #e5e7eb;padding:12px;text-align:left;} th{background:#f9fafb;}");
        out.println(".total{text-align:right;font-size:22px;font-weight:700;margin-top:22px;}");
        out.println(".actions{display:flex;gap:10px;justify-content:flex-end;margin:18px 0;}");
        out.println("button{border:0;background:#111827;color:#fff;border-radius:6px;padding:10px 14px;cursor:pointer;} button.sec{background:#4b5563;}");
        out.println(".wompi{margin-top:22px;padding-top:14px;border-top:1px solid #e5e7eb;font-size:12px;color:#6b7280;}");
        out.println("@media print{body{background:#fff}.actions{display:none}.wrap{margin:0;max-width:none}.factura{border:0;border-radius:0}}");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='wrap'>");
        out.println("<div class='actions'>");
        out.println("<button onclick='window.print()'>Imprimir / guardar PDF</button>");
        out.println("<button class='sec' onclick='descargarFactura()'>Descargar comprobante</button>");
        out.println("</div>");
        out.println("<main class='factura' id='factura'>");
        out.println("<div class='top'>");
        out.println("<div><h1>FITMANAGER</h1><p>Comprobante de pago</p></div>");
        out.println("<div><h2>N. " + numero + "</h2><p>Fecha: " + escapeHtml(String.valueOf(f.get("fecha"))) + "</p></div>");
        out.println("</div>");
        out.println("<div class='comprado'>");
        out.println("<p class='label'>" + (tipoCompra.isEmpty() ? "Comprado" : tipoCompra) + "</p>");
        out.println("<p class='valor'>" + escapeHtml(String.valueOf(f.get("concepto"))) + "</p>");
        if ("Membresía".equals(tipoCompra) && duracionDias > 0) {
            out.println("<p class='duracion'>Duración: " + duracionDias + " días"
                    + (!nombreMembresia.isEmpty() && !nombreMembresia.equalsIgnoreCase(String.valueOf(f.get("concepto")))
                            ? " &middot; Tipo: " + nombreMembresia : "") + "</p>");
        }
        out.println("</div>");
        out.println("<section class='grid'>");
        out.println("<div><h2>Cliente</h2><p>" + escapeHtml(String.valueOf(f.get("cliente"))) + "</p><p>" + escapeHtml(String.valueOf(f.get("correo"))) + "</p><p>Tel: " + escapeHtml(String.valueOf(f.get("telefono"))) + "</p></div>");
        out.println("<div><h2>Pago</h2><p>Metodo: " + escapeHtml(String.valueOf(f.get("metodoPago"))) + "</p><p>Estado: Confirmado</p></div>");
        out.println("</section>");
        out.println("<table>");
        out.println("<thead><tr><th>Concepto</th><th>Cantidad</th><th>Valor unitario</th><th>Total</th></tr></thead>");
        out.println("<tbody><tr>");
        out.println("<td>" + escapeHtml(String.valueOf(f.get("concepto"))) + "</td>");
        out.println("<td>" + escapeHtml(String.valueOf(f.get("cantidad"))) + "</td>");
        out.println("<td>$ " + escapeHtml(String.valueOf(f.get("precioUnitario"))) + "</td>");
        out.println("<td>$ " + escapeHtml(String.valueOf(f.get("valor_total"))) + "</td>");
        out.println("</tr></tbody>");
        out.println("</table>");
        out.println("<div class='total'>Total: $ " + escapeHtml(String.valueOf(f.get("valor_total"))) + "</div>");
        if (!idTransaccion.isEmpty() || !referenciaPago.isEmpty()) {
            out.println("<div class='wompi'>");
            if (!idTransaccion.isEmpty()) {
                out.println("<p>ID transacción Wompi: " + escapeHtml(idTransaccion) + "</p>");
            }
            if (!referenciaPago.isEmpty()) {
                out.println("<p>Referencia de pago: " + escapeHtml(referenciaPago) + "</p>");
            }
            out.println("</div>");
        }
        out.println("</main>");
        out.println("</div>");
        out.println("<script>");
        out.println("function descargarFactura(){const html='<!DOCTYPE html>'+document.documentElement.outerHTML;const blob=new Blob([html],{type:'text/html;charset=utf-8'});const a=document.createElement('a');a.href=URL.createObjectURL(blob);a.download='" + archivo + "';a.click();URL.revokeObjectURL(a.href);}");
        out.println("</script>");
        out.println("</body>");
        out.println("</html>");
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private String valorSeguro(String value) {
        return value == null ? "" : value.trim();
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
