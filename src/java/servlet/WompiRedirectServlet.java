package servlet;

import Controlador.Conexion;
import Controlador.UsuariosDAO;
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
import java.util.Map;

/**
 * Punto de retorno del Web Checkout de Wompi (parametro "redirect-url").
 * Wompi agrega automaticamente "?id=<idTransaccion>" a esta URL cuando el
 * usuario termina el proceso de pago.
 *
 * Aqui NUNCA se confia en los datos que llegan por la URL: se vuelve a
 * consultar la transaccion directamente en la API de Wompi (server to
 * server, usando la llave publica) y solo si el estado es APPROVED y el
 * monto coincide con el precio real del item, se registra la factura y se
 * activa la compra.
 */
@WebServlet("/WompiRedirectServlet")
public class WompiRedirectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        boolean tieneSesionWeb = session != null && session.getAttribute("idUsuario") != null
                && "cliente".equals(session.getAttribute("rol"));

        String idTransaccion = request.getParameter("id");

        if (idTransaccion == null || idTransaccion.isBlank()) {
            finalizar(request, response, session, tieneSesionWeb, "error", null);
            return;
        }

        CompraServlet compras = new CompraServlet();

        try (Connection con = Conexion.getConnect()) {

            if (yaRegistrada(con, idTransaccion)) {
                // El usuario recargo esta pagina o volvio atras: no duplicar la factura.
                finalizar(request, response, session, tieneSesionWeb, "ok", null);
                return;
            }

            Map<String, String> transaccion = WompiService.consultarTransaccion(idTransaccion);
            String[] datosReferencia = descomponerReferencia(transaccion.get("reference"));

            if (datosReferencia == null) {
                finalizar(request, response, session, tieneSesionWeb, "error", null);
                return;
            }

            int idUsuarioReferencia = Integer.parseInt(datosReferencia[2]);

            // Flujo web (navegador con sesion): la referencia debe pertenecer al
            // usuario que tiene la sesion abierta.
            // Flujo movil (app Flutter, que abre el Checkout en el navegador
            // externo del telefono y por lo tanto no tiene esta sesion): se
            // confia en el idUsuario codificado en la referencia, porque esta
            // ya fue verificada server-a-server contra la transaccion real de
            // Wompi (consultarTransaccion), no contra un parametro manipulable
            // de la URL.
            if (tieneSesionWeb) {
                int idUsuarioSesion = Integer.parseInt(String.valueOf(session.getAttribute("idUsuario")));
                if (idUsuarioReferencia != idUsuarioSesion) {
                    finalizar(request, response, session, tieneSesionWeb, "error", null);
                    return;
                }
            }

            int idUsuario = idUsuarioReferencia;
            String tipoCompra = datosReferencia[0];
            int idItem = Integer.parseInt(datosReferencia[1]);

            Map<String, Object> item = compras.consultarItem(con, tipoCompra, idItem);
            if (item == null) {
                finalizar(request, response, session, tieneSesionWeb, "error", null);
                return;
            }

            String estado = transaccion.getOrDefault("status", "");

            if (!"APPROVED".equals(estado)) {
                String motivo = "PENDING".equals(estado) ? "pendiente" : "declinado";
                if (tieneSesionWeb) {
                    response.sendRedirect(request.getContextPath()
                            + "/CompraServlet?tipo=" + tipoCompra + "&id=" + idItem + "&error=" + motivo);
                } else {
                    escribirPaginaMovil(response, false, motivo);
                }
                return;
            }

            BigDecimal precioEsperado = (BigDecimal) item.get("precio");
            long centavosEsperados = precioEsperado.multiply(new BigDecimal(100)).longValueExact();
            long centavosPagados = parseLongSeguro(transaccion.get("amountInCents"));

            if (centavosPagados != centavosEsperados) {
                // El precio del item cambio entre que se genero el checkout y se pago:
                // no se activa la compra para evitar cobrar/entregar un valor incorrecto.
                finalizar(request, response, session, tieneSesionWeb, "error", null);
                return;
            }

            int idMetodoPago = compras.obtenerOcrearMetodoPagoWompi(con);
            String tipoMetodo = transaccion.get("paymentMethodType");
            String descripcionMetodo = "Wompi" + (tipoMetodo != null && !tipoMetodo.isBlank()
                    ? " (" + tipoMetodo + ")" : "");

            Map<String, Object> factura = compras.registrarFactura(con, idUsuario, tipoCompra, idItem, item,
                    idMetodoPago, descripcionMetodo, idTransaccion, transaccion.get("reference"));

            if ("membresia".equals(tipoCompra)) {
                boolean ok = new UsuariosDAO().comprarMembresia(idUsuario, idItem);
                if (!ok) {
                    finalizar(request, response, session, tieneSesionWeb, "error", null);
                    return;
                }
                if (tieneSesionWeb) session.setAttribute("facturaResumen", factura);
                finalizar(request, response, session, tieneSesionWeb, "ok", factura.get("id"));
            } else {
                if (tieneSesionWeb) session.setAttribute("facturaResumen", factura);
                finalizar(request, response, session, tieneSesionWeb, "productoOk", factura.get("id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            finalizar(request, response, session, tieneSesionWeb, "error", null);
        }
    }

    /**
     * Termina la peticion segun el origen del pago: si viene del navegador
     * web con sesion activa, redirige a PanelClienteServlet como siempre; si
     * viene del navegador externo abierto por la app movil (sin sesion),
     * muestra una pagina HTML simple de confirmacion en vez de redirigir a
     * una vista que exige login.
     */
    private void finalizar(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            boolean tieneSesionWeb, String estado, Object idFactura) throws IOException {
        if (tieneSesionWeb) {
            String suffix = idFactura != null ? "&factura=" + idFactura : "";
            response.sendRedirect(request.getContextPath() + "/PanelClienteServlet?compra=" + estado + suffix);
        } else {
            escribirPaginaMovil(response, !"error".equals(estado), null);
        }
    }

    private void escribirPaginaMovil(HttpServletResponse response, boolean exito, String motivo) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        String titulo = exito ? "¡Pago exitoso!" : "Pago no completado";
        String mensaje = exito
                ? "Tu compra se registró correctamente. Ya puedes cerrar esta ventana y volver a la app Taurus Gym."
                : ("No se pudo confirmar el pago" + (motivo != null ? " (" + motivo + ")" : "")
                        + ". Cierra esta ventana, vuelve a la app e intenta de nuevo.");
        response.getWriter().print(
                "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<title>" + titulo + "</title>"
                + "<style>body{font-family:sans-serif;background:#111;color:#fff;text-align:center;padding:60px 20px}"
                + "h1{color:" + (exito ? "#4CAF50" : "#E53935") + "}</style></head><body>"
                + "<h1>" + titulo + "</h1><p>" + mensaje + "</p></body></html>");
    }

    private boolean yaRegistrada(Connection con, String idTransaccion) throws Exception {
        String sql = "SELECT 1 FROM fact_cabecera WHERE id_transaccion_wompi = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idTransaccion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Descompone la referencia generada en CompraServlet:
     * FM-{tipo}-{idItem}-{idUsuario}-{timestamp}
     * Devuelve {tipo, idItem, idUsuario} o null si no tiene el formato esperado.
     */
    private String[] descomponerReferencia(String referencia) {
        if (referencia == null) {
            return null;
        }
        String[] partes = referencia.split("-");
        if (partes.length < 5 || !"FM".equals(partes[0])) {
            return null;
        }
        String tipo = partes[1];
        if (!"membresia".equals(tipo) && !"producto".equals(tipo)) {
            return null;
        }
        try {
            Integer.parseInt(partes[2]);
            Integer.parseInt(partes[3]);
        } catch (NumberFormatException e) {
            return null;
        }
        return new String[]{tipo, partes[2], partes[3]};
    }

    private long parseLongSeguro(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return -1;
        }
    }
}
