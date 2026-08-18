package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.FactCabecera;

public class Fact_CabeceraDAO {

    public boolean insertar(FactCabecera f) {
        String sql = "INSERT INTO fact_cabecera (fecha_fact, n_factura, valor_total, concepto, id_metodo_pago, id_usuarios) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getFecha_fact());
            ps.setString(2, f.getN_factura());
            ps.setString(3, f.getValor_total());
            ps.setString(4, f.getConcepto());
            ps.setInt(5, f.getId_metodo_pago());
            ps.setInt(6, f.getId_usuarios());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar factura: " + e.getMessage());
            return false;
        }
    }

    public FactCabecera consultaFactCabecera(int id) {
        String sql = "SELECT * FROM fact_cabecera WHERE id_fact_cabecera=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                FactCabecera fact = new FactCabecera();
                fact.setId_fact_cabecera(rs.getInt("id_fact_cabecera"));
                fact.setFecha_fact(rs.getString("fecha_fact"));
                fact.setN_factura(rs.getString("n_factura"));
                fact.setValor_total(rs.getString("valor_total"));
                fact.setConcepto(rs.getString("concepto"));
                fact.setId_metodo_pago(rs.getInt("id_metodo_pago"));
                fact.setId_usuarios(rs.getInt("id_usuarios"));
                return fact;
            }
        } catch (SQLException e) {
            System.out.println("Error consultar factura: " + e.getMessage());
        }
        return null;
    }

    public List<FactCabecera> listar() {
        List<FactCabecera> lista = new ArrayList<>();
        String sql = "SELECT * FROM fact_cabecera";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FactCabecera fact = new FactCabecera();
                fact.setId_fact_cabecera(rs.getInt("id_fact_cabecera"));
                fact.setFecha_fact(rs.getString("fecha_fact"));
                fact.setN_factura(rs.getString("n_factura"));
                fact.setValor_total(rs.getString("valor_total"));
                fact.setConcepto(rs.getString("concepto"));
                fact.setId_metodo_pago(rs.getInt("id_metodo_pago"));
                fact.setId_usuarios(rs.getInt("id_usuarios"));
                lista.add(fact);
            }
        } catch (SQLException e) {
            System.out.println("Error listar facturas: " + e.getMessage());
        }
        return lista;
    }

    public List<FactCabecera> listarPorUsuario(int idUsuario) {
        List<FactCabecera> lista = new ArrayList<>();
        String sql = "SELECT * FROM fact_cabecera WHERE id_usuarios = ? ORDER BY id_fact_cabecera DESC";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FactCabecera fact = new FactCabecera();
                fact.setId_fact_cabecera(rs.getInt("id_fact_cabecera"));
                fact.setFecha_fact(rs.getString("fecha_fact"));
                fact.setN_factura(rs.getString("n_factura"));
                fact.setValor_total(rs.getString("valor_total"));
                fact.setConcepto(rs.getString("concepto"));
                fact.setId_metodo_pago(rs.getInt("id_metodo_pago"));
                fact.setId_usuarios(rs.getInt("id_usuarios"));
                lista.add(fact);
            }
        } catch (SQLException e) {
            System.out.println("Error listar facturas por usuario: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(FactCabecera f) {
        String sql = "UPDATE fact_cabecera SET fecha_fact=?, n_factura=?, valor_total=?, concepto=?, id_metodo_pago=?, id_usuarios=? WHERE id_fact_cabecera=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getFecha_fact());
            ps.setString(2, f.getN_factura());
            ps.setString(3, f.getValor_total());
            ps.setString(4, f.getConcepto());
            ps.setInt(5, f.getId_metodo_pago());
            ps.setInt(6, f.getId_usuarios());
            ps.setInt(7, f.getId_fact_cabecera());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar factura: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM fact_cabecera WHERE id_fact_cabecera=?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminar factura: " + e.getMessage());
            return false;
        }
    }
}
