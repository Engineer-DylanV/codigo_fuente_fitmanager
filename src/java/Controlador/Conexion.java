package Controlador;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class Conexion {
    public static Connection getConnect() {
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String host = variableRequerida("MYSQLHOST");
            String port = variableRequerida("MYSQLPORT");
            String db   = variableRequerida("MYSQLDATABASE");
            String user = variableRequerida("MYSQLUSER");
            String pass = variableRequerida("MYSQLPASSWORD");
            String url  = "jdbc:mysql://" + host + ":" + port + "/" + db
                        + "?useSSL=true&requireSSL=true&verifyServerCertificate=false"
                        + "&useUnicode=true&characterEncoding=UTF-8"
                        + "&useTimezone=true&serverTimezone=UTC"
                        + "&socketTimeout=30000&connectTimeout=10000";
            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexión a base de datos establecida.");
            return con;
        } catch (ClassNotFoundException | SQLException | IllegalStateException ex) {
            System.err.println("No se pudo conectar a la base de datos: " + ex.getMessage());
            return null;
        }
    }

    private static String variableRequerida(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Falta configurar la variable de entorno " + nombre + ".");
        }
        return valor;
    }
}
 
