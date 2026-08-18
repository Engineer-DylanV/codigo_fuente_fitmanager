package Controlador;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class Conexion {
    public static Connection getConnect() {
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String host = System.getenv("MYSQLHOST") != null ? System.getenv("MYSQLHOST") : "fitmanager-mysqldb.mysql.database.azure.com";
            String port = System.getenv("MYSQLPORT") != null ? System.getenv("MYSQLPORT") : "3306";
            String db   = System.getenv("MYSQLDATABASE") != null ? System.getenv("MYSQLDATABASE") : "railway";
            String user = System.getenv("MYSQLUSER") != null ? System.getenv("MYSQLUSER") : "fitmanager_admin";
            String pass = System.getenv("MYSQLPASSWORD") != null ? System.getenv("MYSQLPASSWORD") : "Fitmanagerpassword#";
            String url  = "jdbc:mysql://" + host + ":" + port + "/" + db
                        + "?useSSL=true&requireSSL=true&verifyServerCertificate=false"
                        + "&useTimezone=true&serverTimezone=UTC"
                        + "&socketTimeout=30000&connectTimeout=10000";
            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexion exitosa a la base de datos (" + host + ":" + port + "/" + db + ")");
            return con;
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("No se pudo conectar a la base de datos: " + ex.getMessage());
            return null;
        }
    }
}
 