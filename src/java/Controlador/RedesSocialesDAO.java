package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Obtiene las redes publicadas desde la tabla de configuración. */
public class RedesSocialesDAO {

    public List<Map<String, String>> listarActivas() {
        List<Map<String, String>> redes = new ArrayList<>();
        String sql = "SELECT nombre, url, icono, css_clase FROM redes_sociales "
                + "WHERE activo = 1 ORDER BY orden, id_red_social";
        try (Connection con = Conexion.getConnect()) {
            if (con == null) return redes;
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> red = new HashMap<>();
                    red.put("nombre", rs.getString("nombre"));
                    red.put("url", rs.getString("url"));
                    red.put("icono", rs.getString("icono"));
                    red.put("cssClase", rs.getString("css_clase"));
                    redes.add(red);
                }
            }
        } catch (SQLException e) {
            System.err.println("No se pudieron cargar las redes sociales: " + e.getMessage());
        }
        return redes;
    }
}
