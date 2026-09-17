package Controlador;

import java.sql.*;
import java.util.ArrayList;
import Modelo.MetodoPago;

public class MetodoPagoDAO {

    public boolean insertar(MetodoPago mp) {
        String sql = "INSERT INTO metodo_pago(descripcion) VALUES(?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mp.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error insertar: " + e.getMessage());
            return false;
        }
    }

    public MetodoPago consultar(int id) {
        String sql = "SELECT * FROM metodo_pago WHERE id_metodo_pago=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MetodoPago mp = new MetodoPago();
                mp.setId_metodo_pago(rs.getInt("id_metodo_pago"));
                mp.setDescripcion(rs.getString("descripcion"));
                return mp;
            }
        } catch (Exception e) {
            System.out.println("Error consultar: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(MetodoPago mp) {
        String sql = "UPDATE metodo_pago SET descripcion=? WHERE id_metodo_pago=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mp.getDescripcion());
            ps.setInt(2, mp.getId_metodo_pago());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM metodo_pago WHERE id_metodo_pago=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error eliminar: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<MetodoPago> listar() {
        ArrayList<MetodoPago> lista = new ArrayList<>();
        String sql = "SELECT * FROM metodo_pago";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MetodoPago mp = new MetodoPago();
                mp.setId_metodo_pago(rs.getInt("id_metodo_pago"));
                mp.setDescripcion(rs.getString("descripcion"));
                lista.add(mp);
            }
        } catch (Exception e) {
            System.out.println("Error listar: " + e.getMessage());
        }
        return lista;
    }
}
