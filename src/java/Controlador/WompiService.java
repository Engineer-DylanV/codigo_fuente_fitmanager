package Controlador;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Integracion con la pasarela de pagos Wompi (Web Checkout).
 *
 * Flujo:
 *  1) CompraServlet genera una referencia unica y una firma de integridad
 *     y muestra el boton "Pagar con Wompi" (formulario hacia checkout.wompi.co).
 *  2) El usuario paga en Wompi (tarjeta, PSE, Nequi, etc).
 *  3) Wompi redirige de vuelta a WompiRedirectServlet con el id de la transaccion.
 *  4) WompiRedirectServlet usa consultarTransaccion(id) para confirmar el pago
 *     directamente contra el servidor de Wompi antes de activar la compra.
 *     Nunca se confia en los parametros que llegan por la URL.
 */
public class WompiService {

    /**
     * Genera la firma de integridad exigida por el Web Checkout de Wompi.
     * Formula oficial (el orden importa):
     *   SHA256(referencia + montoEnCentavos + moneda + secretoDeIntegridad)
     */
    public static String generarFirmaIntegridad(String referencia, long montoEnCentavos, String moneda) {
        try {
            String cadena = referencia + montoEnCentavos + moneda + WompiConfig.getSecretoIntegridad();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(cadena.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar la firma de integridad de Wompi", e);
        }
    }

    /**
     * Consulta el estado real de una transaccion en la API de Wompi
     * (GET /v1/transactions/{id} con la llave publica como Bearer token).
     * Devuelve un mapa con: id, reference, status, statusMessage, currency,
     * amountInCents y paymentMethodType.
     */
    public static Map<String, String> consultarTransaccion(String idTransaccion) throws Exception {
        URL url = new URL(WompiConfig.getUrlBaseApi() + "/transactions/" + idTransaccion);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + WompiConfig.getLlavePublica());
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int codigo = conn.getResponseCode();
        boolean ok = codigo >= 200 && codigo < 300;

        StringBuilder cuerpo = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                ok ? conn.getInputStream() : conn.getErrorStream(), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                cuerpo.append(linea);
            }
        }

        if (!ok) {
            throw new Exception("Wompi respondio HTTP " + codigo + ": " + cuerpo);
        }

        String json = cuerpo.toString();
        Map<String, String> datos = new HashMap<>();
        datos.put("id", extraerTexto(json, "id"));
        datos.put("reference", extraerTexto(json, "reference"));
        datos.put("status", extraerTexto(json, "status"));
        datos.put("statusMessage", extraerTexto(json, "status_message"));
        datos.put("currency", extraerTexto(json, "currency"));
        datos.put("amountInCents", extraerNumero(json, "amount_in_cents"));
        datos.put("paymentMethodType", extraerTexto(json, "payment_method_type"));
        return datos;
    }

    private static String extraerTexto(String json, String campo) {
        Matcher m = Pattern.compile("\"" + campo + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private static String extraerNumero(String json, String campo) {
        Matcher m = Pattern.compile("\"" + campo + "\"\\s*:\\s*(-?\\d+)").matcher(json);
        return m.find() ? m.group(1) : "0";
    }
}
