package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.TipoDocumento;

public class TipoDocumentoDAO {

    private Connection conn;

    public TipoDocumentoDAO(Connection conn) {
        this.conn = conn;
    }

    // INSERTAR
    public boolean insertar(TipoDocumento td) {
        String sql = "INSERT INTO tipo_documento (descripcion) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, td.getDescripcion());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
            return false;
        }
    }

    // CONSULTAR
    public TipoDocumento consultar(int id) {
        TipoDocumento td = null;
        String sql = "SELECT * FROM tipo_documento WHERE id_tipo_documento=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                td = new TipoDocumento();
                td.setId_tipo_documento(rs.getInt("id_tipo_documento"));
                td.setDescripcion(rs.getString("descripcion"));
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar: " + e.getMessage());
        }

        return td;
    }

    // ACTUALIZAR
    public boolean actualizar(TipoDocumento td) {
        String sql = "UPDATE tipo_documento SET descripcion=? WHERE id_tipo_documento=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, td.getDescripcion());
            stmt.setInt(2, td.getId_tipo_documento());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR
    public boolean eliminar(int id) {
        String sql = "DELETE FROM tipo_documento WHERE id_tipo_documento=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    // LISTAR
    public List<TipoDocumento> listar() {
        List<TipoDocumento> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipo_documento";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TipoDocumento td = new TipoDocumento();
                td.setId_tipo_documento(rs.getInt("id_tipo_documento"));
                td.setDescripcion(rs.getString("descripcion"));
                lista.add(td);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar: " + e.getMessage());
        }

        return lista;
    }
}