package Controlador;

import Modelo.Registros;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrosDAO {

    public RegistrosDAO() {}
    public RegistrosDAO(Connection conn) {}

    public boolean insertar(Registros r) {
        String sql = "INSERT INTO registros (ejercicio, repeticiones, peso, fecha, id_usuarios) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getEjercicio());
            ps.setInt(2, r.getRepeticiones());
            ps.setDouble(3, r.getPeso());
            ps.setDate(4, r.getFecha() == null ? new Date(System.currentTimeMillis()) : r.getFecha());
            ps.setInt(5, r.getId_usuarios());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar registro: " + e.getMessage());
            return false;
        }
    }

    public List<Registros> listarPorUsuario(int idUsuario) {
        List<Registros> lista = new ArrayList<>();
        String sql = "SELECT * FROM registros WHERE id_usuarios = ? ORDER BY fecha DESC, id_registros DESC";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listar registros: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Registros r) {
        String sql = "UPDATE registros SET ejercicio = ?, repeticiones = ?, peso = ?, fecha = ? WHERE id_registros = ? AND id_usuarios = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getEjercicio());
            ps.setInt(2, r.getRepeticiones());
            ps.setDouble(3, r.getPeso());
            ps.setDate(4, r.getFecha());
            ps.setInt(5, r.getId_registros());
            ps.setInt(6, r.getId_usuarios());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar registro: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idRegistro, int idUsuario) {
        String sql = "DELETE FROM registros WHERE id_registros = ? AND id_usuarios = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRegistro);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminar registro: " + e.getMessage());
            return false;
        }
    }

    public int sumarRepeticiones(int idUsuario, String ejercicio) {
        String sql = "SELECT COALESCE(SUM(repeticiones), 0) AS total FROM registros WHERE id_usuarios = ? AND LOWER(ejercicio) = LOWER(?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, ejercicio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error sumar repeticiones: " + e.getMessage());
        }
        return 0;
    }

    private Registros mapear(ResultSet rs) throws SQLException {
        Registros r = new Registros();
        r.setId_registros(rs.getInt("id_registros"));
        r.setEjercicio(rs.getString("ejercicio"));
        r.setRepeticiones(rs.getInt("repeticiones"));
        r.setPeso(rs.getDouble("peso"));
        r.setFecha(rs.getDate("fecha"));
        r.setId_usuarios(rs.getInt("id_usuarios"));
        return r;
    }
}
