package Controlador;

import Modelo.Asistencias;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsistenciasDAO {

    public AsistenciasDAO() {}
    public AsistenciasDAO(Connection conn) {}

    public boolean insertar(Asistencias a) {
        String sql = "INSERT INTO asistencias (fecha, id_usuarios) VALUES (?, ?)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, a.getFecha() == null ? new Date(System.currentTimeMillis()) : a.getFecha());
            ps.setInt(2, a.getId_usuarios());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar asistencia: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarHoy(int idUsuario) {
        if (yaAsistioHoy(idUsuario)) return false;
        Asistencias a = new Asistencias();
        a.setFecha(new Date(System.currentTimeMillis()));
        a.setId_usuarios(idUsuario);
        return insertar(a);
    }

    public List<Asistencias> listarPorUsuario(int idUsuario) {
        List<Asistencias> lista = new ArrayList<>();
        String sql = "SELECT * FROM asistencias WHERE id_usuarios = ? ORDER BY fecha DESC";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listar asistencias: " + e.getMessage());
        }
        return lista;
    }

    public boolean yaAsistioHoy(int idUsuario) {
        String sql = "SELECT 1 FROM asistencias WHERE id_usuarios = ? AND fecha = CURDATE()";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error validar asistencia hoy: " + e.getMessage());
            return false;
        }
    }

    public int contarMesActual(int idUsuario) {
        String sql = "SELECT COUNT(*) AS total FROM asistencias WHERE id_usuarios = ? AND YEAR(fecha) = YEAR(CURDATE()) AND MONTH(fecha) = MONTH(CURDATE())";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error contar asistencias mes: " + e.getMessage());
        }
        return 0;
    }

    public int contarSemanaActual(int idUsuario) {
        String sql = "SELECT COUNT(*) AS total FROM asistencias "
                + "WHERE id_usuarios = ? "
                + "AND YEARWEEK(fecha, 1) = YEARWEEK(CURDATE(), 1)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error contar asistencias semana: " + e.getMessage());
        }
        return 0;
    }

    private Asistencias mapear(ResultSet rs) throws SQLException {
        Asistencias a = new Asistencias();
        a.setId_asistencias(rs.getInt("id_asistencias"));
        a.setFecha(rs.getDate("fecha"));
        a.setId_usuarios(rs.getInt("id_usuarios"));
        return a;
    }

    /**
     * Calcula la racha actual de días CONSECUTIVOS de asistencia de un usuario.
     * Se considera "activa" si la última asistencia fue hoy o ayer (día de gracia
     * para no perder la racha antes de que termine el día). Si hay un hueco de
     * más de un día, la racha se corta ahí.
     */
    public int calcularRachaActual(int idUsuario) {
        String sql = "SELECT fecha FROM asistencias WHERE id_usuarios = ? ORDER BY fecha DESC";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                java.time.LocalDate esperado = null;
                int racha = 0;
                boolean primera = true;

                while (rs.next()) {
                    java.time.LocalDate fecha = rs.getDate("fecha").toLocalDate();

                    if (primera) {
                        java.time.LocalDate hoy = java.time.LocalDate.now();
                        long diasDesdeHoy = java.time.temporal.ChronoUnit.DAYS.between(fecha, hoy);
                        if (diasDesdeHoy > 1) {
                            return 0; // la última asistencia fue hace más de un día: racha rota
                        }
                        racha = 1;
                        esperado = fecha.minusDays(1);
                        primera = false;
                    } else if (fecha.equals(esperado)) {
                        racha++;
                        esperado = fecha.minusDays(1);
                    } else {
                        break; // hueco encontrado, la racha termina aquí
                    }
                }
                return racha;
            }
        } catch (SQLException e) {
            System.out.println("Error calcular racha: " + e.getMessage());
            return 0;
        }
    }
}
