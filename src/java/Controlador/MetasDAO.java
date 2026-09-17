package Controlador;

import Modelo.Metas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetasDAO {

    public MetasDAO() {}
    public MetasDAO(Connection conn) {}

    public boolean insertar(Metas m) {
        String sql = "INSERT INTO metas (ejercicio, meta, id_usuarios) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getEjercicio());
            ps.setInt(2, m.getMeta());
            ps.setInt(3, m.getId_usuarios());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar meta: " + e.getMessage());
            return false;
        }
    }

    public List<Metas> listarPorUsuario(int idUsuario) {
        List<Metas> lista = new ArrayList<>();
        String sql = "SELECT * FROM metas WHERE id_usuarios = ? ORDER BY id_metas DESC";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listar metas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Metas m) {
        String sql = "UPDATE metas SET ejercicio = ?, meta = ? WHERE id_metas = ? AND id_usuarios = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getEjercicio());
            ps.setInt(2, m.getMeta());
            ps.setInt(3, m.getId_metas());
            ps.setInt(4, m.getId_usuarios());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar meta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idMeta, int idUsuario) {
        String sql = "DELETE FROM metas WHERE id_metas = ? AND id_usuarios = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMeta);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminar meta: " + e.getMessage());
            return false;
        }
    }

    public int buscarIdUsuarioPorCorreo(String correo) {
        String sql = "SELECT id_usuarios FROM usuarios WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_usuarios");
            }
        } catch (SQLException e) {
            System.out.println("Error buscar usuario por correo: " + e.getMessage());
        }
        return 0;
    }

    private Metas mapear(ResultSet rs) throws SQLException {
        Metas m = new Metas();
        m.setId_metas(rs.getInt("id_metas"));
        m.setEjercicio(rs.getString("ejercicio"));
        m.setMeta(rs.getInt("meta"));
        m.setId_usuarios(rs.getInt("id_usuarios"));
        return m;
    }
}
