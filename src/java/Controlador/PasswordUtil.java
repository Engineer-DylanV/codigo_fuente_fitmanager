package Controlador;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Cifrado de contraseñas con PBKDF2WithHmacSHA256 (sal aleatoria por
 * usuario + múltiples iteraciones), usando únicamente clases incluidas
 * en el propio JDK — no requiere agregar ninguna librería nueva al
 * proyecto en NetBeans.
 *
 * Formato almacenado en la base de datos:
 *   $pbkdf2$<iteraciones>$<sal en base64>$<hash en base64>
 *
 * Las contraseñas antiguas (guardadas en texto plano antes de este
 * cambio) no tienen ese prefijo, así que se pueden distinguir con
 * {@link #estaCifrada(String)} y migrar de forma transparente la
 * primera vez que ese usuario inicia sesión (ver UsuariosDAO.login).
 */
public class PasswordUtil {

    private static final String PREFIJO = "$pbkdf2$";
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 65536;
    private static final int LARGO_LLAVE_BITS = 256;
    private static final int LARGO_SAL_BYTES = 16;

    private PasswordUtil() {}

    /** true si el valor ya viene en formato cifrado por este proyecto. */
    public static boolean estaCifrada(String valorGuardado) {
        return valorGuardado != null && valorGuardado.startsWith(PREFIJO);
    }

    /** Genera el hash cifrado (con sal nueva) a partir de una contraseña en texto plano. */
    public static String cifrar(String passwordPlano) {
        try {
            byte[] sal = new byte[LARGO_SAL_BYTES];
            new SecureRandom().nextBytes(sal);

            byte[] hash = pbkdf2(passwordPlano.toCharArray(), sal, ITERACIONES);

            return PREFIJO + ITERACIONES + "$"
                    + Base64.getEncoder().encodeToString(sal) + "$"
                    + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("No se pudo cifrar la contraseña", e);
        }
    }

    /**
     * Compara una contraseña en texto plano (la que escribe el usuario)
     * contra el valor cifrado guardado en la base de datos.
     */
    public static boolean verificar(String passwordPlano, String valorGuardado) {
        if (passwordPlano == null || !estaCifrada(valorGuardado)) {
            return false;
        }
        try {
            String[] partes = valorGuardado.split("\\$");
            // partes[0] = "" (antes del primer $), [1] = "pbkdf2", [2] = iteraciones, [3] = sal, [4] = hash
            int iteraciones = Integer.parseInt(partes[2]);
            byte[] sal = Base64.getDecoder().decode(partes[3]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[4]);

            byte[] hashCalculado = pbkdf2(passwordPlano.toCharArray(), sal, iteraciones);

            return comparacionSegura(hashEsperado, hashCalculado);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] passwordChars, byte[] sal, int iteraciones)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(passwordChars, sal, iteraciones, LARGO_LLAVE_BITS);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITMO);
        return skf.generateSecret(spec).getEncoded();
    }

    /** Comparación en tiempo constante para evitar ataques de temporización. */
    private static boolean comparacionSegura(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int resultado = 0;
        for (int i = 0; i < a.length; i++) {
            resultado |= a[i] ^ b[i];
        }
        return resultado == 0;
    }
}
