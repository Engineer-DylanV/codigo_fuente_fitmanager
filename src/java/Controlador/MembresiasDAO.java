package Controlador;

import Modelo.Membresias;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembresiasDAO {

    public boolean insertar(Membresias m) {
        String sql = "INSERT INTO membresias(precio,tipo,duracion_dias) VALUES(?,?,?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, m.getPrecio());
            ps.setString(2, m.getTipo());
            ps.setInt(3, m.getDuracion_dias());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar: " + e.getMessage());
            return false;
        }
    }

    public Membresias consultar(int id) {
        String sql = "SELECT * FROM membresias WHERE id_membresias=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Membresias m = new Membresias();
                m.setId_membresias(rs.getInt("id_membresias"));
                m.setPrecio(rs.getDouble("precio"));
                m.setTipo(rs.getString("tipo"));
                m.setDuracion_dias(rs.getInt("duracion_dias"));
                return m;
            }
        } catch (SQLException e) {
            System.out.println("Error consultar: " + e.getMessage());
        }
        return null;
    }

    public List<Membresias> listar() {
        List<Membresias> lista = new ArrayList<>();
        String sql = "SELECT * FROM membresias";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Membresias m = new Membresias();
                m.setId_membresias(rs.getInt("id_membresias"));
                m.setPrecio(rs.getDouble("precio"));
                m.setTipo(rs.getString("tipo"));
                m.setDuracion_dias(rs.getInt("duracion_dias"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error listar: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Membresias m) {
        String sql = "UPDATE membresias SET precio=?,tipo=?,duracion_dias=? WHERE id_membresias=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, m.getPrecio());
            ps.setString(2, m.getTipo());
            ps.setInt(3, m.getDuracion_dias());
            ps.setInt(4, m.getId_membresias());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM membresias WHERE id_membresias=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminar: " + e.getMessage());
            return false;
        }
    }
}
