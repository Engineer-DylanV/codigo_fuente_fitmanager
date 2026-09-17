package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.FactDetallada;

public class Fact_DetalladaDAO {

    public boolean insertar(FactDetallada f) {
        String sql = "INSERT INTO fact_detallada (cantidad, id_productos, id_fact_cabecera) VALUES(?,?,?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getCantidad());
            ps.setInt(2, f.getId_productos());
            ps.setInt(3, f.getId_fact_cabecera());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar: " + e.getMessage());
            return false;
        }
    }

    public FactDetallada consultar(int id) {
        String sql = "SELECT * FROM fact_detallada WHERE id_fact_detallada=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                FactDetallada f = new FactDetallada();
                f.setId_fact_detallada(rs.getInt(1));
                f.setCantidad(rs.getString(2));
                f.setId_productos(rs.getInt(3));
                f.setId_fact_cabecera(rs.getInt(4));
                return f;
            }
        } catch (SQLException e) {
            System.out.println("Error consultar: " + e.getMessage());
        }
        return null;
    }

    public List<FactDetallada> listar() {
        List<FactDetallada> lista = new ArrayList<>();
        String sql = "SELECT * FROM fact_detallada";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FactDetallada f = new FactDetallada();
                f.setId_fact_detallada(rs.getInt(1));
                f.setCantidad(rs.getString(2));
                f.setId_productos(rs.getInt(3));
                f.setId_fact_cabecera(rs.getInt(4));
                lista.add(f);
            }
        } catch (SQLException e) {
            System.out.println("Error listar: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(FactDetallada f) {
        String sql = "UPDATE fact_detallada SET cantidad=?, id_productos=?, id_fact_cabecera=? WHERE id_fact_detallada=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getCantidad());
            ps.setInt(2, f.getId_productos());
            ps.setInt(3, f.getId_fact_cabecera());
            ps.setInt(4, f.getId_fact_detallada());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM fact_detallada WHERE id_fact_detallada=?";
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
