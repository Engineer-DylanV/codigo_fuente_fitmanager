package Controlador;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmailService {

    // Secretos y datos operativos se configuran fuera del código fuente.
    private static final String API_KEY = System.getenv("BREVO_API_KEY");
    private static final String EMAIL_FROM = System.getenv("FITMANAGER_EMAIL_FROM");
    private static final String NOMBRE_REMITENTE = System.getenv("FITMANAGER_EMAIL_NAME");
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";

    private static String ultimoError;

    public static String getUltimoError() {
        return ultimoError;
    }

    public static boolean enviarCorreo(String destinatario, String asunto, String cuerpoHtml) {
        if (API_KEY == null || API_KEY.isBlank() || EMAIL_FROM == null || EMAIL_FROM.isBlank()
                || NOMBRE_REMITENTE == null || NOMBRE_REMITENTE.isBlank()) {
            ultimoError = "Faltan BREVO_API_KEY, FITMANAGER_EMAIL_FROM o FITMANAGER_EMAIL_NAME en la configuración del servidor.";
            System.out.println("Configuración de correo incompleta.");
            return false;
        }
        try {
            String json = "{"
                    + "\"sender\":{\"name\":\"" + escaparJson(NOMBRE_REMITENTE) + "\",\"email\":\"" + escaparJson(EMAIL_FROM) + "\"},"
                    + "\"to\":[{\"email\":\"" + escaparJson(destinatario) + "\"}],"
                    + "\"subject\":\"" + escaparJson(asunto) + "\","
                    + "\"htmlContent\":\"" + escaparJson(cuerpoHtml) + "\""
                    + "}";

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("api-key", API_KEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                ultimoError = null;
                return true;
            } else {
                String cuerpoError = leerStream(conn.getErrorStream());
                ultimoError = "HTTP " + responseCode + (cuerpoError.isEmpty() ? "" : " - " + cuerpoError);
                System.out.println("Error Brevo API: " + ultimoError);
                return false;
            }
        } catch (Exception e) {
            ultimoError = e.getClass().getSimpleName() + ": " + e.getMessage();
            System.out.println("Error enviando correo: " + ultimoError);
            e.printStackTrace();
            return false;
        }
    }

    /** Lee por completo un InputStream (usado para el cuerpo de error de Brevo) sin lanzar si viene null. */
    private static String leerStream(InputStream is) {
        if (is == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
        } catch (Exception e) {
            // Si falla la lectura del error, seguimos con lo que se alcanzo a leer.
        }
        return sb.toString();
    }

    public static boolean enviarCodigoVerificacion(String destinatario, String nombre, String codigo) {
        String asunto = "Tu código de verificación - FitManager";
        String cuerpo = plantillaBase(
                "Verifica tu cuenta",
                "Hola " + escapar(nombre) + ", gracias por registrarte en FitManager. "
                        + "Usa el siguiente código para confirmar tu cuenta. Este código vence en 10 minutos.",
                codigo, null
        );
        return enviarCorreo(destinatario, asunto, cuerpo);
    }

    public static boolean enviarCodigoRecuperacion(String destinatario, String nombre, String codigo) {
        String asunto = "Recupera tu contraseña - FitManager";
        String cuerpo = plantillaBase(
                "Recupera tu contraseña",
                "Hola " + escapar(nombre) + ", recibimos una solicitud para restablecer tu contraseña. "
                        + "Usa el siguiente código para continuar. Este código vence en 10 minutos. "
                        + "Si tú no solicitaste esto, puedes ignorar este correo.",
                codigo, null
        );
        return enviarCorreo(destinatario, asunto, cuerpo);
    }

    /** Código solicitado desde el área autenticada "Mi perfil". */
    public static boolean enviarCodigoCambioPassword(String destinatario, String nombre, String codigo) {
        String asunto = "Confirma el cambio de contraseña - FitManager";
        String cuerpo = plantillaBase(
                "Confirma tu nueva contraseña",
                "Hola " + escapar(nombre) + ", recibimos una solicitud para cambiar la contraseña de tu cuenta. "
                        + "Usa el siguiente código para confirmar el cambio. Este código vence en 10 minutos. "
                        + "Si no fuiste tú, ignora este mensaje y conserva tu contraseña actual.",
                codigo, null
        );
        return enviarCorreo(destinatario, asunto, cuerpo);
    }

    public static boolean enviarAvisoVencimiento(String destinatario, String nombre, String fechaVencimiento, long diasRestantes) {
        String asunto = diasRestantes <= 0 ? "Tu membresía FitManager ha vencido" : "Tu membresía FitManager vence pronto";
        String mensajeDias;
        if (diasRestantes <= 0) {
            mensajeDias = "Tu membresía venció el " + escapar(fechaVencimiento) + ". "
                    + "Renueva tu plan para seguir disfrutando de todos los beneficios de FitManager.";
        } else if (diasRestantes == 1) {
            mensajeDias = "Tu membresía vence mañana, " + escapar(fechaVencimiento) + ". "
                    + "Renueva a tiempo para no perder el acceso.";
        } else {
            mensajeDias = "Tu membresía vence en " + diasRestantes + " días, el " + escapar(fechaVencimiento) + ". "
                    + "Renueva a tiempo para no perder el acceso.";
        }
        String cuerpo = plantillaBase(
                "Hola " + escapar(nombre),
                mensajeDias,
                null, "Renovar membresía"
        );
        return enviarCorreo(destinatario, asunto, cuerpo);
    }

    private static String plantillaBase(String titulo, String mensaje, String codigo, String boton) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'></head>")
                .append("<body style='margin:0;padding:0;background:#050505;font-family:Arial,Helvetica,sans-serif;'>")
                .append("<table role='presentation' width='100%' cellpadding='0' cellspacing='0' style='background:#050505;padding:32px 0;'>")
                .append("<tr><td align='center'>")
                .append("<table role='presentation' width='480' cellpadding='0' cellspacing='0' style='background:#121212;border:1px solid #383838;border-radius:14px;overflow:hidden;'>")
                .append("<tr><td style='background:linear-gradient(90deg,#c92f35,#651013);padding:22px 24px;text-align:center;'>")
                .append("<span style='font-size:22px;font-weight:bold;color:#ffffff;letter-spacing:1px;'>FitManager</span>")
                .append("</td></tr>")
                .append("<tr><td style='padding:28px 28px 8px 28px;'>")
                .append("<h2 style='color:#f2f2f2;margin:0 0 14px 0;font-size:20px;'>").append(escapar(titulo)).append("</h2>")
                .append("<p style='color:#b8b8b8;font-size:14px;line-height:1.6;margin:0 0 22px 0;'>").append(mensaje).append("</p>")
                .append("</td></tr>");

        if (codigo != null) {
            html.append("<tr><td style='padding:0 28px 28px 28px;text-align:center;'>")
                    .append("<div style='display:inline-block;background:#1d1d1d;border:1px solid #c92f35;border-radius:10px;")
                    .append("padding:16px 28px;font-size:32px;font-weight:bold;letter-spacing:10px;color:#ffffff;'>")
                    .append(escapar(codigo))
                    .append("</div></td></tr>");
        }

        if (boton != null) {
            html.append("<tr><td style='padding:0 28px 28px 28px;text-align:center;'>")
                    .append("<span style='display:inline-block;background:linear-gradient(90deg,#c92f35,#651013);")
                    .append("color:#ffffff;font-weight:bold;padding:12px 26px;border-radius:8px;font-size:14px;'>")
                    .append(escapar(boton))
                    .append("</span></td></tr>");
        }

        html.append("<tr><td style='padding:18px 28px;border-top:1px solid #383838;text-align:center;'>")
                .append("<span style='color:#9ca3af;font-size:11px;'>Si no realizaste esta solicitud, puedes ignorar este correo.</span>")
                .append("</td></tr>")
                .append("<tr><td style='padding:14px;text-align:center;background:#030303;'>")
                .append("<span style='color:#9ca3af;font-size:11px;'>&copy; 2026 FitManager - Todos los derechos reservados</span>")
                .append("</td></tr>")
                .append("</table>")
                .append("</td></tr></table>")
                .append("</body></html>");
        return html.toString();
    }

    private static String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
