package Pruebas;

import Controlador.Conexion;
import Controlador.UsuariosDAO;
import java.sql.Connection;
import java.sql.ResultSet;

public class PruebaUsuariosAdmin {

    public static void main(String[] args) throws Exception {
        Connection conn = Conexion.getConnect();
        UsuariosDAO dao = new UsuariosDAO(conn);

        try (ResultSet rs = dao.listarAdmin()) {
            while (rs.next()) {
                System.out.println(
                        rs.getInt("id_usuarios") + " - "
                        + rs.getString("nombre") + " "
                        + rs.getString("apellido") + " - "
                        + rs.getString("email") + " - "
                        + rs.getString("membresia")
                );
            }
        }
    }
}
