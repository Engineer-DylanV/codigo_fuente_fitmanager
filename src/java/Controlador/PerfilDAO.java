package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Acceso a los datos persistentes del perfil del cliente. */
public class PerfilDAO {

    /** Datos de cuenta leídos directamente de usuarios y del catálogo de sexos. */
    public Map<String, String> obtenerDatosCuenta(int idUsuario) {
        Map<String, String> datos = new HashMap<>();
        String sql = "SELECT u.nombre, u.apellido, u.email, g.descripcion AS genero "
                + "FROM usuarios u LEFT JOIN generos g ON g.codigo = u.genero "
                + "WHERE u.id_usuarios = ?";
        try (Connection con = Conexion.getConnect()) {
            if (con == null) return datos;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        datos.put("nombre", rs.getString("nombre"));
                        datos.put("apellido", rs.getString("apellido"));
                        datos.put("email", rs.getString("email"));
                        datos.put("genero", rs.getString("genero"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("No se pudieron cargar los datos del perfil: " + e.getMessage());
        }
        return datos;
    }

    public List<Map<String, String>> listarAvataresActivos() {
        List<Map<String, String>> avatares = new ArrayList<>();
        String sql = "SELECT codigo, nombre, icono, color_inicio, color_fin "
                + "FROM avatares_perfil WHERE activo = 1 ORDER BY orden, id_avatar";

        try (Connection con = Conexion.getConnect()) {
            if (con == null) return avatares;
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> avatar = new HashMap<>();
                    avatar.put("codigo", rs.getString("codigo"));
                    avatar.put("nombre", rs.getString("nombre"));
                    avatar.put("icono", rs.getString("icono"));
                    avatar.put("colorInicio", rs.getString("color_inicio"));
                    avatar.put("colorFin", rs.getString("color_fin"));
                    avatares.add(avatar);
                }
            }
        } catch (SQLException e) {
            System.err.println("No se pudieron cargar los avatares: " + e.getMessage());
        }
        return avatares;
    }

    public boolean existeAvatarActivo(String codigo) {
        String sql = "SELECT 1 FROM avatares_perfil WHERE codigo = ? AND activo = 1";
        try (Connection con = Conexion.getConnect()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, codigo);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            System.err.println("No se pudo validar el avatar: " + e.getMessage());
            return false;
        }
    }

    public boolean guardarAvatar(int idUsuario, String codigoAvatar) {
        String sql = "INSERT INTO perfiles_usuario (id_usuarios, avatar_codigo, actualizado_en) "
                + "VALUES (?, ?, NOW()) "
                + "ON DUPLICATE KEY UPDATE avatar_codigo = VALUES(avatar_codigo), foto_mime = NULL, "
                + "foto_datos = NULL, actualizado_en = NOW()";
        try (Connection con = Conexion.getConnect()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, codigoAvatar);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("No se pudo guardar el avatar: " + e.getMessage());
            return false;
        }
    }

    public boolean guardarFoto(int idUsuario, String mime, byte[] datos) {
        String sql = "INSERT INTO perfiles_usuario (id_usuarios, avatar_codigo, foto_mime, foto_datos, actualizado_en) "
                + "VALUES (?, NULL, ?, ?, NOW()) "
                + "ON DUPLICATE KEY UPDATE avatar_codigo = NULL, foto_mime = VALUES(foto_mime), "
                + "foto_datos = VALUES(foto_datos), actualizado_en = NOW()";
        try (Connection con = Conexion.getConnect()) {
            if (con == null) return false;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, mime);
                ps.setBytes(3, datos);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("No se pudo guardar la foto del perfil: " + e.getMessage());
            return false;
        }
    }

    public PerfilEstado obtenerEstado(int idUsuario) {
        String codigoAvatar = null;
        boolean tieneFoto = false;
        String sql = "SELECT avatar_codigo, foto_datos IS NOT NULL AS tiene_foto "
                + "FROM perfiles_usuario WHERE id_usuarios = ?";
        try (Connection con = Conexion.getConnect()) {
            if (con == null) return new PerfilEstado(null, false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        codigoAvatar = rs.getString("avatar_codigo");
                        tieneFoto = rs.getBoolean("tiene_foto");
                    }
                }
            }
            if (codigoAvatar == null && !tieneFoto) {
                codigoAvatar = obtenerAvatarPredeterminado(con);
            }
        } catch (SQLException e) {
            System.err.println("No se pudo consultar el perfil: " + e.getMessage());
        }
        return new PerfilEstado(codigoAvatar, tieneFoto);
    }

    public FotoPerfil obtenerFoto(int idUsuario) {
        String sql = "SELECT foto_mime, foto_datos FROM perfiles_usuario "
                + "WHERE id_usuarios = ? AND foto_datos IS NOT NULL";
        try (Connection con = Conexion.getConnect()) {
            if (con == null) return null;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new FotoPerfil(rs.getString("foto_mime"), rs.getBytes("foto_datos"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("No se pudo obtener la foto del perfil: " + e.getMessage());
        }
        return null;
    }

    private String obtenerAvatarPredeterminado(Connection con) throws SQLException {
        String sql = "SELECT codigo FROM avatares_perfil WHERE activo = 1 ORDER BY orden, id_avatar LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString("codigo") : null;
        }
    }

    public static final class PerfilEstado {
        private final String avatarCodigo;
        private final boolean tieneFoto;

        public PerfilEstado(String avatarCodigo, boolean tieneFoto) {
            this.avatarCodigo = avatarCodigo;
            this.tieneFoto = tieneFoto;
        }
        public String getAvatarCodigo() { return avatarCodigo; }
        public boolean isTieneFoto() { return tieneFoto; }
    }

    public static final class FotoPerfil {
        private final String mime;
        private final byte[] datos;

        public FotoPerfil(String mime, byte[] datos) {
            this.mime = mime;
            this.datos = datos;
        }
        public String getMime() { return mime; }
        public byte[] getDatos() { return datos; }
    }
}
