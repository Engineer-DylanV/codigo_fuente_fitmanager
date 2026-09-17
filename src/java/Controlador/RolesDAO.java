package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.Roles;

public class RolesDAO {

    private Connection conn;

    public RolesDAO(Connection conn) {
        this.conn = conn;
    }

    // INSERTAR
    public boolean insertar(Roles rol) {
        String sql = "INSERT INTO roles (descripcion) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rol.getDescripcion());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
            return false;
        }
    }

    // CONSULTAR
    public Roles consultar(int id) {
        Roles rol = null;
        String sql = "SELECT * FROM roles WHERE id_roles=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                rol = new Roles();
                rol.setId_roles(rs.getInt("id_roles"));
                rol.setDescripcion(rs.getString("descripcion"));
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar: " + e.getMessage());
        }

        return rol;
    }

    // ACTUALIZAR
    public boolean actualizar(Roles rol) {
        String sql = "UPDATE roles SET descripcion=? WHERE id_roles=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rol.getDescripcion());
            stmt.setInt(2, rol.getId_roles());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR
    public boolean eliminar(int id) {
        String sql = "DELETE FROM roles WHERE id_roles=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    // LISTAR
    public List<Roles> listar() {
        List<Roles> lista = new ArrayList<>();
        String sql = "SELECT * FROM roles";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Roles rol = new Roles();
                rol.setId_roles(rs.getInt("id_roles"));
                rol.setDescripcion(rs.getString("descripcion"));
                lista.add(rol);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }

        return lista;
    }
}