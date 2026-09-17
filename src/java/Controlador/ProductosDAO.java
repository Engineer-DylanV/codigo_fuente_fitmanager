package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.Productos;

public class ProductosDAO {

    private final Connection conn;

    public ProductosDAO() {
        this.conn = null;
    }

    public ProductosDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertar(Productos p) {
        String sql = "INSERT INTO productos (nombre, precio) VALUES (?, ?)";
        boolean conexionPropia = (conn == null);
        Connection c = null;
        try {
            c = conexionPropia ? Conexion.getConnect() : conn;
            if (c == null) throw new SQLException("No se pudo obtener conexion a la base de datos");
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setDouble(2, p.getPrecio());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error insertar: " + e.getMessage());
            return false;
        } finally {
            cerrarSiPropia(c, conexionPropia);
        }
    }

    public Productos consultar(int id) {
        String sql = "SELECT * FROM productos WHERE id_productos=?";
        boolean conexionPropia = (conn == null);
        Connection c = null;
        try {
            c = conexionPropia ? Conexion.getConnect() : conn;
            if (c == null) throw new SQLException("No se pudo obtener conexion a la base de datos");
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Productos p = new Productos();
                        p.setId_productos(rs.getInt("id_productos"));
                        p.setNombre(rs.getString("nombre"));
                        p.setPrecio(rs.getDouble("precio"));
                        return p;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error consultar: " + e.getMessage());
        } finally {
            cerrarSiPropia(c, conexionPropia);
        }
        return null;
    }

    public boolean actualizar(Productos p) {
        String sql = "UPDATE productos SET nombre=?, precio=? WHERE id_productos=?";
        boolean conexionPropia = (conn == null);
        Connection c = null;
        try {
            c = conexionPropia ? Conexion.getConnect() : conn;
            if (c == null) throw new SQLException("No se pudo obtener conexion a la base de datos");
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setDouble(2, p.getPrecio());
                ps.setInt(3, p.getId_productos());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error actualizar: " + e.getMessage());
            return false;
        } finally {
            cerrarSiPropia(c, conexionPropia);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id_productos=?";
        boolean conexionPropia = (conn == null);
        Connection c = null;
        try {
            c = conexionPropia ? Conexion.getConnect() : conn;
            if (c == null) throw new SQLException("No se pudo obtener conexion a la base de datos");
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error eliminar: " + e.getMessage());
            return false;
        } finally {
            cerrarSiPropia(c, conexionPropia);
        }
    }

    public List<Productos> listar() {
        List<Productos> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        boolean conexionPropia = (conn == null);
        Connection c = null;
        try {
            c = conexionPropia ? Conexion.getConnect() : conn;
            if (c == null) throw new SQLException("No se pudo obtener conexion a la base de datos");
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Productos p = new Productos();
                    p.setId_productos(rs.getInt("id_productos"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listar: " + e.getMessage());
        } finally {
            cerrarSiPropia(c, conexionPropia);
        }
        return lista;
    }

    private void cerrarSiPropia(Connection c, boolean conexionPropia) {
        if (conexionPropia && c != null) {
            try {
                c.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
