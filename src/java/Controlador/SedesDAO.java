package Controlador;

import Modelo.Sedes;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SedesDAO {

    private Connection conn;

    public SedesDAO(Connection conn) {
        this.conn = conn;
    }

    // INSERTAR
    public boolean insertar(Sedes sede) {
        String sql = "INSERT INTO sedes (nombre, direccion, horario, telefono, descripcion) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sede.getNombre());
            stmt.setString(2, sede.getDireccion());
            stmt.setString(3, sede.getHorario());
            stmt.setString(4, sede.getTelefono());
            stmt.setString(5, sede.getDescripcion());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
            return false;
        }
    }

    // CONSULTAR (igual a buscarPorId pero con el mismo nombre que usas en otros DAOs)
    public Sedes consultar(int id) {
        String sql = "SELECT * FROM sedes WHERE id_sedes=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar: " + e.getMessage());
        }

        return null;
    }

    // ACTUALIZAR
    public boolean actualizar(Sedes sede) {
        String sql = "UPDATE sedes SET nombre=?, direccion=?, horario=?, telefono=?, descripcion=? WHERE id_sedes=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sede.getNombre());
            stmt.setString(2, sede.getDireccion());
            stmt.setString(3, sede.getHorario());
            stmt.setString(4, sede.getTelefono());
            stmt.setString(5, sede.getDescripcion());
            stmt.setInt(6, sede.getId_sedes());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR
    public boolean eliminar(int id) {
        String sql = "DELETE FROM sedes WHERE id_sedes=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    // LISTAR
    public List<Sedes> listar() {
        List<Sedes> lista = new ArrayList<>();
        String sql = "SELECT * FROM sedes";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }

        return lista;
    }

    // BUSCAR POR ID (lo dejas porque ya lo tenías)
    public Sedes buscarPorId(int id) {
        return consultar(id);
    }

    private Sedes mapear(ResultSet rs) throws SQLException {
        Sedes s = new Sedes();
        s.setId_sedes(rs.getInt("id_sedes"));
        s.setNombre(rs.getString("nombre"));
        s.setDireccion(rs.getString("direccion"));
        s.setHorario(rs.getString("horario"));
        s.setTelefono(rs.getString("telefono"));
        s.setDescripcion(rs.getString("descripcion"));
        return s;
    }
}
