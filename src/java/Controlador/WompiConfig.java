package Controlador;

/**
 * Configuracion de la pasarela de pagos Wompi.
 *
 * IMPORTANTE: reemplaza los valores por defecto (marcados como
 * "TU_..._AQUI") por tus llaves reales del dashboard de comercios de Wompi
 * (https://comercios.wompi.co -> Desarrolladores -> Llaves API / Secretos
 * para integracion tecnica).
 *
 * Para no dejar las llaves reales escritas en el codigo fuente, tambien
 * puedes definirlas como variables de entorno (igual que se hace con la
 * base de datos en Conexion.java):
 *   WOMPI_PUBLIC_KEY, WOMPI_PRIVATE_KEY, WOMPI_INTEGRITY_SECRET, WOMPI_ENV
 */
public class WompiConfig {

    // ---- Llaves de SANDBOX (pruebas) ----
    private static final String LLAVE_PUBLICA_DEFECTO = "pub_test_Z9NJAcizzZcgB1rhaCyGGPgLsJbfRB0q";
    private static final String LLAVE_PRIVADA_DEFECTO = "prv_test_6OKTJMK03fOEKcWn60Ei7p0515iFXw8H";
    private static final String SECRETO_INTEGRIDAD_DEFECTO = "test_integrity_4mKB5nkAg9wu2tGzbTBiV1tIZbZTAydQ";

    // "sandbox" mientras pruebas, "produccion" cuando vayas a cobrar de verdad
    private static final String AMBIENTE_DEFECTO = "sandbox";

    public static final String MONEDA = "COP";
    public static final String URL_CHECKOUT = "https://checkout.wompi.co/p/";

    public static String getLlavePublica() {
        return valorOEnvDefecto("WOMPI_PUBLIC_KEY", LLAVE_PUBLICA_DEFECTO);
    }

    public static String getLlavePrivada() {
        return valorOEnvDefecto("WOMPI_PRIVATE_KEY", LLAVE_PRIVADA_DEFECTO);
    }

    public static String getSecretoIntegridad() {
        return valorOEnvDefecto("WOMPI_INTEGRITY_SECRET", SECRETO_INTEGRIDAD_DEFECTO);
    }

    public static boolean esProduccion() {
        String ambiente = valorOEnvDefecto("WOMPI_ENV", AMBIENTE_DEFECTO);
        return "produccion".equalsIgnoreCase(ambiente) || "production".equalsIgnoreCase(ambiente);
    }

    /**
     * URL base de la API de Wompi para consultar transacciones.
     * Sandbox: https://sandbox.wompi.co/v1
     * Produccion: https://production.wompi.co/v1
     */
    public static String getUrlBaseApi() {
        return esProduccion() ? "https://production.wompi.co/v1" : "https://sandbox.wompi.co/v1";
    }

    private static String valorOEnvDefecto(String variableEntorno, String valorDefecto) {
        String valor = System.getenv(variableEntorno);
        return (valor != null && !valor.isBlank()) ? valor : valorDefecto;
    }
}
