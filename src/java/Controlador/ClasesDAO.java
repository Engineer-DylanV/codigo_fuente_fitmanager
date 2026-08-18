package Controlador;

import Modelo.Clases;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClasesDAO {

    private String ultimoError;

    public ClasesDAO() {}
    public ClasesDAO(Connection conn) {}

    public String getUltimoError() {
        return ultimoError;
    }

    public boolean insertar(Clases clase) {
        String sql = "INSERT INTO clases (nombre, dia, hora, instructor) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clase.getNombre());
            ps.setString(2, clase.getDia());
            ps.setString(3, clase.getHora());
            ps.setString(4, clase.getInstructor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar clase: " + e.getMessage());
            return false;
        }
    }

    public List<Clases> listar() {
        List<Clases> lista = new ArrayList<>();
        String sql = "SELECT id_clases, nombre, dia, hora, instructor FROM clases ORDER BY dia, hora, nombre";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Clases clase = new Clases();
                clase.setId_clases(rs.getInt("id_clases"));
                clase.setNombre(rs.getString("nombre"));
                clase.setDia(rs.getString("dia"));
                clase.setHora(rs.getString("hora"));
                clase.setInstructor(rs.getString("instructor"));
                lista.add(clase);
            }
        } catch (SQLException e) {
            System.out.println("Error listar clases: " + e.getMessage());
        }
        return lista;
    }

    public Clases consultar(int id) {
        String sql = "SELECT id_clases, nombre, dia, hora, instructor FROM clases WHERE id_clases = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Clases clase = new Clases();
                    clase.setId_clases(rs.getInt("id_clases"));
                    clase.setNombre(rs.getString("nombre"));
                    clase.setDia(rs.getString("dia"));
                    clase.setHora(rs.getString("hora"));
                    clase.setInstructor(rs.getString("instructor"));
                    return clase;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error consultar clase: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(Clases clase) {
        String sql = "UPDATE clases SET nombre = ?, dia = ?, hora = ?, instructor = ? WHERE id_clases = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clase.getNombre());
            ps.setString(2, clase.getDia());
            ps.setString(3, clase.getHora());
            ps.setString(4, clase.getInstructor());
            ps.setInt(5, clase.getId_clases());
            int filas = ps.executeUpdate();
            if (filas == 0) {
                ultimoError = "0 filas afectadas: no existe una clase con id_clases=" + clase.getId_clases();
            }
            return filas > 0;
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            System.out.println("Error actualizar clase: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM clases WHERE id_clases = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminar clase: " + e.getMessage());
            return false;
        }
    }

    public boolean inscribir(int idUsuario, int idClase) {
        String sql = "INSERT IGNORE INTO usuarios_clases (id_usuarios, id_clases) VALUES (?, ?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idClase);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error inscribir clase: " + e.getMessage());
            return false;
        }
    }

    public boolean desinscribir(int idUsuario, int idClase) {
        String sql = "DELETE FROM usuarios_clases WHERE id_usuarios = ? AND id_clases = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idClase);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error desinscribir clase: " + e.getMessage());
            return false;
        }
    }

    public List<Clases> listarMisClases(int idUsuario) {
        List<Clases> lista = new ArrayList<>();
        String sql = "SELECT c.id_clases, c.nombre, c.dia, c.hora, c.instructor FROM clases c INNER JOIN usuarios_clases uc ON c.id_clases = uc.id_clases WHERE uc.id_usuarios = ? ORDER BY c.dia, c.hora, c.nombre";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Clases clase = new Clases();
                    clase.setId_clases(rs.getInt("id_clases"));
                    clase.setNombre(rs.getString("nombre"));
                    clase.setDia(rs.getString("dia"));
                    clase.setHora(rs.getString("hora"));
                    clase.setInstructor(rs.getString("instructor"));
                    lista.add(clase);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listar mis clases: " + e.getMessage());
        }
        return lista;
    }

    public boolean estaInscrito(int idUsuario, int idClase) {
        String sql = "SELECT 1 FROM usuarios_clases WHERE id_usuarios = ? AND id_clases = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idClase);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error validar inscripcion: " + e.getMessage());
            return false;
        }
    }
}
