package Controlador;

import Modelo.Rutinas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RutinasDAO {

    public RutinasDAO() {}
    public RutinasDAO(Connection conn) {}

    public boolean insertar(Rutinas r) {
        String sql = "INSERT INTO rutinas (nombre, descripcion, id_usuarios) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNombre());
            ps.setString(2, r.getDescripcion());
            ps.setInt(3, r.getId_usuarios());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar rutina: " + e.getMessage());
            return false;
        }
    }

    public List<Rutinas> listarPorUsuario(int idUsuario) {
        List<Rutinas> lista = new ArrayList<>();
        String sql = "SELECT * FROM rutinas WHERE id_usuarios = ? AND id_evaluacion IS NOT NULL ORDER BY id_rutinas DESC";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listar rutinas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Rutinas r) {
        String sql = "UPDATE rutinas SET nombre = ?, descripcion = ? WHERE id_rutinas = ? AND id_usuarios = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getNombre());
            ps.setString(2, r.getDescripcion());
            ps.setInt(3, r.getId_rutinas());
            ps.setInt(4, r.getId_usuarios());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar rutina: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idRutina, int idUsuario) {
        String sql = "DELETE FROM rutinas WHERE id_rutinas = ? AND id_usuarios = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRutina);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminar rutina: " + e.getMessage());
            return false;
        }
    }

    private Rutinas mapear(ResultSet rs) throws SQLException {
        Rutinas r = new Rutinas();
        r.setId_rutinas(rs.getInt("id_rutinas"));
        r.setNombre(rs.getString("nombre"));
        r.setDescripcion(rs.getString("descripcion"));
        r.setId_usuarios(rs.getInt("id_usuarios"));
        r.setObjetivo(rs.getString("objetivo"));
        r.setEnlaceDrive(rs.getString("enlace_drive"));
        r.setPrograma(rs.getString("programa"));
        return r;
    }
}
