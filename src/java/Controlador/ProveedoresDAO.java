package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.Proveedores;

public class ProveedoresDAO {

    private Connection conn;

    public ProveedoresDAO(Connection conn) {
        this.conn = conn;
    }

    // INSERTAR
    public boolean insertar(Proveedores p) {
        String sql = "INSERT INTO proveedores (nombre, tipo_producto) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTipo_producto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
            return false;
        }
    }

    // CONSULTAR
    public Proveedores consultar(int id) {
        Proveedores p = null;
        String sql = "SELECT * FROM proveedores WHERE id_proveedores=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                p = new Proveedores();
                p.setId_proveedores(rs.getInt("id_proveedores"));
                p.setNombre(rs.getString("nombre"));
                p.setTipo_producto(rs.getString("tipo_producto"));
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar: " + e.getMessage());
        }

        return p;
    }

    // ACTUALIZAR
    public boolean actualizar(Proveedores p) {
        String sql = "UPDATE proveedores SET nombre=?, tipo_producto=? WHERE id_proveedores=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTipo_producto());
            ps.setInt(3, p.getId_proveedores());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR
    public boolean eliminar(int id) {
        String sql = "DELETE FROM proveedores WHERE id_proveedores=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    // LISTAR
    public List<Proveedores> listar() {
        List<Proveedores> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Proveedores p = new Proveedores();
                p.setId_proveedores(rs.getInt("id_proveedores"));
                p.setNombre(rs.getString("nombre"));
                p.setTipo_producto(rs.getString("tipo_producto"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }

        return lista;
    }
}