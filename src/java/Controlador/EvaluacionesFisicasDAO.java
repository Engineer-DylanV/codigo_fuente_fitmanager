package Controlador;

import Modelo.EvaluacionesFisicas;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluacionesFisicasDAO {

    public EvaluacionesFisicasDAO() {}
    public EvaluacionesFisicasDAO(Connection conn) {}

    public boolean insertar(EvaluacionesFisicas e) {
        String sql = "INSERT INTO evaluaciones_fisicas (fecha, peso, edad, condicion, pruebas, id_usuarios) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getFecha());
            stmt.setDouble(2, e.getPeso());
            stmt.setInt(3, e.getEdad());
            stmt.setString(4, e.getCondicion());
            stmt.setString(5, e.getPruebas());
            stmt.setInt(6, e.getId_usuarios());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    public List<EvaluacionesFisicas> listar() {
        List<EvaluacionesFisicas> lista = new ArrayList<>();
        String sql = "SELECT * FROM evaluaciones_fisicas";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return lista;
    }

    public List<EvaluacionesFisicas> listarPorUsuario(int idUsuario) {
        List<EvaluacionesFisicas> lista = new ArrayList<>();
        String sql = "SELECT * FROM evaluaciones_fisicas WHERE id_usuarios = ? ORDER BY fecha DESC";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public EvaluacionesFisicas consultar(int id) {
        String sql = "SELECT * FROM evaluaciones_fisicas WHERE id_evaluaciones_fisicas = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public boolean actualizar(EvaluacionesFisicas e) {
        String sql = "UPDATE evaluaciones_fisicas SET fecha=?, peso=?, edad=?, condicion=?, pruebas=?, id_usuarios=? WHERE id_evaluaciones_fisicas=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getFecha());
            stmt.setDouble(2, e.getPeso());
            stmt.setInt(3, e.getEdad());
            stmt.setString(4, e.getCondicion());
            stmt.setString(5, e.getPruebas());
            stmt.setInt(6, e.getId_usuarios());
            stmt.setInt(7, e.getId_evaluaciones_fisicas());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM evaluaciones_fisicas WHERE id_evaluaciones_fisicas=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    private EvaluacionesFisicas mapear(ResultSet rs) throws SQLException {
        EvaluacionesFisicas e = new EvaluacionesFisicas();
        e.setId_evaluaciones_fisicas(rs.getInt("id_evaluaciones_fisicas"));
        e.setFecha(rs.getString("fecha"));
        e.setPeso(rs.getDouble("peso"));
        e.setEdad(rs.getInt("edad"));
        e.setCondicion(rs.getString("condicion"));
        e.setPruebas(rs.getString("pruebas"));
        e.setId_usuarios(rs.getInt("id_usuarios"));
        return e;
    }
}
