package Controlador;

import Modelo.Usuarios;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuariosDAO {

    private static final SecureRandom GENERADOR_SEGURO = new SecureRandom();

    public UsuariosDAO() {}
    public UsuariosDAO(Connection conn) {}

    public boolean insertar(Usuarios u) {
        String sql = "INSERT INTO usuarios (nombre, apellido, documento, email, telefono, id_tipo_documento, id_roles, password, genero, id_membresias, vencimiento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getApellido());
            stmt.setString(3, u.getDocumento());
            stmt.setString(4, u.getEmail());
            stmt.setString(5, u.getTelefono());
            stmt.setInt(6, u.getId_tipo_documento());
            stmt.setInt(7, u.getId_roles());
            stmt.setString(8, PasswordUtil.cifrar(u.getPassword()));
            stmt.setString(9, u.getGenero());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insertar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si ya existe un usuario registrado con ese correo.
     * Se usa para bloquear registros duplicados antes de insertar.
     */
    public boolean existeEmail(String correo) {
        String sql = "SELECT 1 FROM usuarios WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error verificando email existente: " + e.getMessage());
            return false;
        }
    }

    public String generarYGuardarCodigo(String correo, int minutosValidez) {
        String codigo = String.format("%06d", GENERADOR_SEGURO.nextInt(1_000_000));
        String sql = "UPDATE usuarios SET codigo_verificacion = ?, codigo_expira = DATE_ADD(NOW(), INTERVAL ? MINUTE), verificado = 0 WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setInt(2, minutosValidez);
            stmt.setString(3, correo);
            return stmt.executeUpdate() > 0 ? codigo : null;
        } catch (SQLException e) {
            System.out.println("Error generando codigo: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un código para una acción sensible desde una sesión ya iniciada,
     * sin cambiar el estado de verificación de la cuenta. A diferencia del
     * código de registro, una solicitud para cambiar la contraseña no debe
     * dejar al usuario bloqueado ni exigir que verifique su cuenta otra vez.
     */
    public String generarCodigoCambioPassword(String correo, int minutosValidez) {
        String codigo = String.format("%06d", GENERADOR_SEGURO.nextInt(1_000_000));
        String sql = "UPDATE usuarios SET codigo_verificacion = ?, "
                + "codigo_expira = DATE_ADD(NOW(), INTERVAL ? MINUTE) "
                + "WHERE email = ? AND verificado = 1";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setInt(2, minutosValidez);
            stmt.setString(3, correo);
            return stmt.executeUpdate() > 0 ? codigo : null;
        } catch (SQLException e) {
            System.out.println("Error generando código de cambio de contraseña: " + e.getMessage());
            return null;
        }
    }

    /**
     * Genera un código para el flujo "olvidé mi contraseña" SIN tocar la
     * columna "verificado". Una cuenta que ya estaba verificada sigue estándolo
     * después de recuperar la contraseña, y una que no lo estaba tampoco cambia
     * de estado. A diferencia de generarCodigoCambioPassword, no exige que la
     * cuenta esté verificada, porque también se puede recuperar la contraseña
     * de una cuenta que quedó pendiente de verificar.
     */
    public String generarCodigoRecuperacion(String correo, int minutosValidez) {
        String codigo = String.format("%06d", GENERADOR_SEGURO.nextInt(1_000_000));
        String sql = "UPDATE usuarios SET codigo_verificacion = ?, "
                + "codigo_expira = DATE_ADD(NOW(), INTERVAL ? MINUTE) "
                + "WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setInt(2, minutosValidez);
            stmt.setString(3, correo);
            return stmt.executeUpdate() > 0 ? codigo : null;
        } catch (SQLException e) {
            System.out.println("Error generando código de recuperación: " + e.getMessage());
            return null;
        }
    }

    /**
     * Indica si el correo ya tiene un código de verificación pendiente y
     * todavía vigente. Se usa para no reenviar un código nuevo cuando el
     * usuario acaba de recibir uno (por ejemplo, justo después del registro).
     */
    public boolean tieneCodigoVigente(String correo) {
        String sql = "SELECT codigo_verificacion, codigo_expira, NOW() AS ahora_servidor "
                + "FROM usuarios WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return false;
                String codigoGuardado = rs.getString("codigo_verificacion");
                Timestamp expira = rs.getTimestamp("codigo_expira");
                Timestamp ahoraServidor = rs.getTimestamp("ahora_servidor");
                return codigoGuardado != null && expira != null && expira.after(ahoraServidor);
            }
        } catch (SQLException e) {
            System.out.println("Error consultando codigo vigente: " + e.getMessage());
            return false;
        }
    }

    public boolean verificarCodigo(String correo, String codigoIngresado) {
        return verificarCodigoDetallado(correo, codigoIngresado) == ResultadoVerificacion.OK;
    }

    public enum ResultadoVerificacion {
        OK, NO_EXISTE, SIN_CODIGO_PENDIENTE, CODIGO_INCORRECTO, CODIGO_VENCIDO
    }

    public ResultadoVerificacion verificarCodigoDetallado(String correo, String codigoIngresado) {
        String sql = "SELECT codigo_verificacion, codigo_expira, NOW() as ahora_servidor FROM usuarios WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return ResultadoVerificacion.NO_EXISTE;
                String codigoGuardado = rs.getString("codigo_verificacion");
                Timestamp expira = rs.getTimestamp("codigo_expira");
                Timestamp ahoraServidor = rs.getTimestamp("ahora_servidor");
                if (codigoGuardado == null || expira == null) return ResultadoVerificacion.SIN_CODIGO_PENDIENTE;
                boolean coincide = codigoGuardado.equals(codigoIngresado.trim());
                boolean vigente = expira.after(ahoraServidor);
                if (!coincide) return ResultadoVerificacion.CODIGO_INCORRECTO;
                if (!vigente) return ResultadoVerificacion.CODIGO_VENCIDO;
                marcarVerificado(correo);
                return ResultadoVerificacion.OK;
            }
        } catch (SQLException e) {
            System.out.println("Error verificando codigo: " + e.getMessage());
            return ResultadoVerificacion.NO_EXISTE;
        }
    }

    public boolean marcarVerificado(String correo) {
        String sql = "UPDATE usuarios SET verificado = 1, codigo_verificacion = NULL, codigo_expira = NULL WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error marcando verificado: " + e.getMessage());
            return false;
        }
    }

    public boolean estaVerificado(String correo) {
        String sql = "SELECT verificado FROM usuarios WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("verificado") == 1;
            }
        } catch (SQLException e) {
            System.out.println("Error consultando verificado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Igual que verificarCodigoDetallado pero SIN marcar la cuenta como
     * verificada ni borrar el código. Se usa en el flujo "olvidé mi
     * contraseña" (paso 2: validar el código antes de dejar cambiar la
     * contraseña, sin tocar el estado de verificación de la cuenta).
     */
    public ResultadoVerificacion verificarCodigoSinConsumir(String correo, String codigoIngresado) {
        String sql = "SELECT codigo_verificacion, codigo_expira, NOW() as ahora_servidor FROM usuarios WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return ResultadoVerificacion.NO_EXISTE;
                String codigoGuardado = rs.getString("codigo_verificacion");
                Timestamp expira = rs.getTimestamp("codigo_expira");
                Timestamp ahoraServidor = rs.getTimestamp("ahora_servidor");
                if (codigoGuardado == null || expira == null) return ResultadoVerificacion.SIN_CODIGO_PENDIENTE;
                boolean coincide = codigoGuardado.equals(codigoIngresado.trim());
                boolean vigente = expira.after(ahoraServidor);
                if (!coincide) return ResultadoVerificacion.CODIGO_INCORRECTO;
                if (!vigente) return ResultadoVerificacion.CODIGO_VENCIDO;
                return ResultadoVerificacion.OK;
            }
        } catch (SQLException e) {
            System.out.println("Error verificando codigo (sin consumir): " + e.getMessage());
            return ResultadoVerificacion.NO_EXISTE;
        }
    }

    /**
     * Cambia la contraseña de un usuario y limpia el código de verificación
     * usado, para que no pueda reutilizarse. Requiere haber validado antes
     * el código con verificarCodigoSinConsumir (el llamador es responsable
     * de ese chequeo).
     */
    public boolean actualizarPassword(String correo, String nuevaPassword) {
        String sql = "UPDATE usuarios SET password = ?, codigo_verificacion = NULL, codigo_expira = NULL WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordUtil.cifrar(nuevaPassword));
            stmt.setString(2, correo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizando password: " + e.getMessage());
            return false;
        }
    }

    public String obtenerNombrePorCorreo(String correo) {
        String sql = "SELECT nombre FROM usuarios WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("nombre") : null;
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo nombre: " + e.getMessage());
            return null;
        }
    }

    public List<Usuarios> listarParaAvisoVencimiento(int diasAntes) {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT id_usuarios, nombre, apellido, email, vencimiento FROM usuarios WHERE vencimiento IS NOT NULL AND notificado_vencimiento = 0 AND vencimiento <= DATE_ADD(CURDATE(), INTERVAL ? DAY)";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, diasAntes);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuarios u = new Usuarios();
                    u.setId_usuarios(rs.getInt("id_usuarios"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellido(rs.getString("apellido"));
                    u.setEmail(rs.getString("email"));
                    Date vencimiento = rs.getDate("vencimiento");
                    u.setVencimiento(vencimiento == null ? null : vencimiento.toString());
                    lista.add(u);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error listando avisos: " + e.getMessage());
        }
        return lista;
    }

    public boolean marcarNotificadoVencimiento(int idUsuario) {
        String sql = "UPDATE usuarios SET notificado_vencimiento = 1 WHERE id_usuarios = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error marcando notificado: " + e.getMessage());
            return false;
        }
    }

    public Usuarios login(String email, String passwordIngresada) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND activo = 1";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String passwordGuardada = rs.getString("password");
                    int idUsuario = rs.getInt("id_usuarios");

                    boolean credencialesValidas;
                    if (PasswordUtil.estaCifrada(passwordGuardada)) {
                        credencialesValidas = PasswordUtil.verificar(passwordIngresada, passwordGuardada);
                    } else {
                        // Cuenta creada antes de activar el cifrado: la contraseña
                        // todavía está en texto plano. Si coincide, se autentica
                        // y de inmediato se migra a formato cifrado en la BD, sin
                        // que el usuario tenga que hacer nada.
                        credencialesValidas = passwordGuardada != null && passwordGuardada.equals(passwordIngresada);
                        if (credencialesValidas) {
                            actualizarPassword(email, passwordIngresada);
                        }
                    }

                    if (!credencialesValidas) {
                        return null;
                    }

                    Usuarios usuario = new Usuarios();
                    usuario.setId_usuarios(idUsuario);
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setDocumento(rs.getString("documento"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setId_roles(rs.getInt("id_roles"));
                    int idMembresia = rs.getInt("id_membresias");
                    usuario.setId_membresias(rs.wasNull() ? 0 : idMembresia);
                    usuario.setPassword(passwordGuardada);
                    usuario.setId_tipo_documento(rs.getInt("id_tipo_documento"));
                    usuario.setVerificado(rs.getInt("verificado") == 1);
                    Date vencimiento = rs.getDate("vencimiento");
                    usuario.setVencimiento(vencimiento == null ? null : vencimiento.toString());
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error login: " + e.getMessage());
        }
        return null;
    }

    public List<Usuarios> listar() {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Usuarios u = new Usuarios();
                u.setId_usuarios(rs.getInt("id_usuarios"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setDocumento(rs.getString("documento"));
                u.setEmail(rs.getString("email"));
                u.setTelefono(rs.getString("telefono"));
                u.setId_roles(rs.getInt("id_roles"));
                int idMembresia = rs.getInt("id_membresias");
                u.setId_membresias(rs.wasNull() ? 0 : idMembresia);
                u.setPassword(rs.getString("password"));
                u.setId_tipo_documento(rs.getInt("id_tipo_documento"));
                Date vencimiento = rs.getDate("vencimiento");
                u.setVencimiento(vencimiento == null ? null : vencimiento.toString());
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public ResultSet listarAdmin() throws SQLException {
        Connection conn = Conexion.getConnect();
        String sql = "SELECT u.id_usuarios, u.nombre, u.apellido, u.documento, u.email, u.id_roles, td.descripcion AS tipoDoc, r.descripcion AS rol, m.tipo AS membresia, u.vencimiento FROM usuarios u LEFT JOIN tipo_documento td ON u.id_tipo_documento = td.id_tipo_documento LEFT JOIN roles r ON u.id_roles = r.id_roles LEFT JOIN membresias m ON u.id_membresias = m.id_membresias ORDER BY u.id_usuarios DESC";
        PreparedStatement stmt = conn.prepareStatement(sql);
        return stmt.executeQuery();
    }

    public boolean actualizarAdmin(int idUsuario, String nombreCompleto, String correo, int idMembresia) {
        String[] partes = nombreCompleto == null ? new String[]{""} : nombreCompleto.trim().split(" ", 2);
        String nombre = partes.length > 0 ? partes[0] : "";
        String apellido = partes.length > 1 ? partes[1] : "";
        String sql;
        if (idMembresia > 0) {
            sql = "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, id_membresias = ?, vencimiento = DATE_ADD(CURDATE(), INTERVAL (SELECT duracion_dias FROM membresias WHERE id_membresias = ?) DAY), notificado_vencimiento = 0 WHERE id_usuarios = ?";
        } else {
            sql = "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, id_membresias = NULL, vencimiento = NULL WHERE id_usuarios = ?";
        }
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, correo);
            if (idMembresia > 0) {
                stmt.setInt(4, idMembresia);
                stmt.setInt(5, idMembresia);
                stmt.setInt(6, idUsuario);
            } else {
                stmt.setInt(4, idUsuario);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar admin: " + e.getMessage());
            return false;
        }
    }

    public boolean asignarVencimiento(String correo, String fechaVencimiento) {
        String sql = "UPDATE usuarios SET vencimiento = ?, notificado_vencimiento = 0 WHERE email = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fechaVencimiento));
            stmt.setString(2, correo);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error asignar vencimiento: " + e.getMessage());
            return false;
        }
    }

    public boolean comprarMembresia(int idUsuario, int idMembresia) {
        String sql = "UPDATE usuarios SET id_membresias = ?, vencimiento = DATE_ADD(CURDATE(), INTERVAL (SELECT duracion_dias FROM membresias WHERE id_membresias = ?) DAY), notificado_vencimiento = 0 WHERE id_usuarios = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMembresia);
            stmt.setInt(2, idMembresia);
            stmt.setInt(3, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error comprar membresia: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Usuarios u, String documentoActual) {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, documento = ?, email = ?, telefono = ?, id_roles = ?, id_membresias = ?, password = ?, id_tipo_documento = ? WHERE documento = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getApellido());
            stmt.setString(3, u.getDocumento());
            stmt.setString(4, u.getEmail());
            stmt.setString(5, u.getTelefono());
            stmt.setInt(6, u.getId_roles());
            if (u.getId_membresias() > 0) stmt.setInt(7, u.getId_membresias());
            else stmt.setNull(7, Types.INTEGER);
            stmt.setString(8, PasswordUtil.estaCifrada(u.getPassword()) ? u.getPassword() : PasswordUtil.cifrar(u.getPassword()));
            stmt.setInt(9, u.getId_tipo_documento());
            stmt.setString(10, documentoActual);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id_usuarios = ?";
        try (Connection conn = Conexion.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}
