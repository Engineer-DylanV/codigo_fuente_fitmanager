package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/GestionUsuariosAdminServlet")
public class GestionUsuariosAdminServlet extends HttpServlet {

    private static final String URL =
            "jdbc:mysql://" + System.getenv().getOrDefault("MYSQLHOST","fitmanager-mysqldb.mysql.database.azure.com") + ":" + System.getenv().getOrDefault("MYSQLPORT","3306") + "/" + System.getenv().getOrDefault("MYSQLDATABASE","railway") + "?useSSL=true&requireSSL=true&verifyServerCertificate=false&serverTimezone=UTC&autoReconnect=true";

    private static final String USER = System.getenv().getOrDefault("MYSQLUSER","fitmanager_admin");
    private static final String PASS = System.getenv().getOrDefault("MYSQLPASSWORD","Fitmanagerpassword#");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        boolean ok = false;
        String mensaje = "Error al realizar la operacion";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
                String accion = request.getParameter("accion");

                if ("registrar".equals(accion)) {
                    Resultado resultado = registrarUsuario(con, request);
                    ok = resultado.ok;
                    mensaje = resultado.mensaje;

                } else if ("editar".equals(accion)) {
                    Resultado resultado = editarUsuario(con, request);
                    ok = resultado.ok;
                    mensaje = resultado.mensaje;

                } else if ("vencimiento".equals(accion)) {
                    Resultado resultado = actualizarVencimiento(con, request);
                    ok = resultado.ok;
                    mensaje = resultado.mensaje;

                } else if ("eliminar".equals(accion)) {
                    Resultado resultado = eliminarUsuario(con, request);
                    ok = resultado.ok;
                    mensaje = resultado.mensaje;

                } else if ("desactivar".equals(accion)) {
                    Resultado resultado = cambiarEstadoUsuario(con, request, false);
                    ok = resultado.ok;
                    mensaje = resultado.mensaje;

                } else if ("reactivar".equals(accion)) {
                    Resultado resultado = cambiarEstadoUsuario(con, request, true);
                    ok = resultado.ok;
                    mensaje = resultado.mensaje;

                } else {
                    mensaje = "Accion no valida.";
                }
            }

        } catch (NumberFormatException e) {
            mensaje = "Verifica que los numeros seleccionados sean validos.";
        } catch (Exception e) {
            mensaje = "No se pudo realizar la operacion: " + e.getMessage();
            e.printStackTrace();
        }

        String accept = request.getHeader("Accept");

        if (accept != null && accept.contains("application/json")) {
            response.setContentType("application/json;charset=UTF-8");

            try (PrintWriter out = response.getWriter()) {
                out.print(ok
                        ? "{\"ok\":true}"
                        : "{\"ok\":false,\"mensaje\":\"" + escaparJson(mensaje) + "\"}");
            }

            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/PanelAdministradorServlet?mensaje="
                + URLEncoder.encode(mensaje, StandardCharsets.UTF_8)
                + "&estado="
                + (ok ? "ok" : "error")
        );
    }

    private Resultado registrarUsuario(Connection con, HttpServletRequest request) throws SQLException {
        String nombres = request.getParameter("nombres");
        String tipoDoc = request.getParameter("tipoDoc");
        String documento = request.getParameter("documento");
        String correo = request.getParameter("correo");
        String membresia = request.getParameter("membresia");
        String rol = request.getParameter("rol");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (estaVacio(nombres) || estaVacio(tipoDoc) || estaVacio(documento)
                || estaVacio(correo) || estaVacio(membresia) || estaVacio(rol)
                || estaVacio(password) || estaVacio(confirmPassword)) {
            return new Resultado(false, "Completa todos los campos del usuario.");
        }

        if (!password.equals(confirmPassword)) {
            return new Resultado(false, "Las contrasenas no coinciden.");
        }

        String[] partes = nombres.trim().split(" ", 2);
        String nombre = partes[0];
        String apellido = partes.length > 1 ? partes[1] : "";
        int idMembresia = Integer.parseInt(membresia);

        String sql = "INSERT INTO usuarios "
                + "(nombre, apellido, documento, email, id_roles, id_membresias, password, id_tipo_documento, vencimiento) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, DATE_ADD(CURDATE(), INTERVAL "
                + "(SELECT duracion_dias FROM membresias WHERE id_membresias = ?) DAY))";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, documento.trim());
            ps.setString(4, correo.trim());
            ps.setInt(5, Integer.parseInt(rol));
            ps.setInt(6, idMembresia);
            ps.setString(7, password);
            ps.setInt(8, Integer.parseInt(tipoDoc));
            ps.setInt(9, idMembresia);

            boolean ok = ps.executeUpdate() > 0;
            return new Resultado(ok, ok ? "Usuario registrado correctamente" : "No se pudo registrar el usuario.");
        }
    }

    private Resultado editarUsuario(Connection con, HttpServletRequest request) throws SQLException {
        String idStr = obtenerParametro(request, "id", "idUsuario");
        String campo = request.getParameter("campo");
        String valor = request.getParameter("valor");

        if (estaVacio(idStr) || estaVacio(campo) || estaVacio(valor)) {
            return new Resultado(false, "Datos incompletos para editar el usuario.");
        }

        int id = Integer.parseInt(idStr);
        String sql = "";

        switch (campo) {
            case "nombre":
                sql = "UPDATE usuarios SET nombre=? WHERE id_usuarios=?";
                break;
            case "correo":
                sql = "UPDATE usuarios SET email=? WHERE id_usuarios=?";
                break;
            case "tipoDoc":
                sql = "UPDATE usuarios SET id_tipo_documento=? WHERE id_usuarios=?";
                break;
            case "membresia":
                sql = "UPDATE usuarios SET id_membresias=?, vencimiento = DATE_ADD(CURDATE(), INTERVAL "
                        + "(SELECT duracion_dias FROM membresias WHERE id_membresias = ?) DAY) "
                        + "WHERE id_usuarios=?";
                break;
            case "rol":
                sql = "UPDATE usuarios SET id_roles=? WHERE id_usuarios=?";
                break;
            default:
                return new Resultado(false, "Campo no valido para editar.");
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if ("membresia".equals(campo)) {
                ps.setInt(1, Integer.parseInt(valor));
                ps.setInt(2, Integer.parseInt(valor));
                ps.setInt(3, id);

            } else if ("rol".equals(campo) || "tipoDoc".equals(campo)) {
                ps.setInt(1, Integer.parseInt(valor));
                ps.setInt(2, id);

            } else {
                ps.setString(1, valor.trim());
                ps.setInt(2, id);
            }

            boolean ok = ps.executeUpdate() > 0;
            return new Resultado(ok, ok ? "Usuario actualizado correctamente" : "No se encontro el usuario.");
        }
    }

    private Resultado actualizarVencimiento(Connection con, HttpServletRequest request) throws SQLException {
        String correo = request.getParameter("correoVencimiento");
        String fecha = request.getParameter("fechaVencimiento");

        if (estaVacio(correo) || estaVacio(fecha)) {
            return new Resultado(false, "Correo y fecha de vencimiento son obligatorios.");
        }

        String sql = "UPDATE usuarios SET vencimiento=? WHERE email=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setString(2, correo.trim());

            boolean ok = ps.executeUpdate() > 0;
            return new Resultado(ok, ok ? "Vencimiento actualizado correctamente" : "No se encontro un usuario con ese correo.");
        }
    }

    private Resultado cambiarEstadoUsuario(Connection con, HttpServletRequest request, boolean activar) throws SQLException {
        String idStr = obtenerParametro(request, "id", "idUsuario");

        if (estaVacio(idStr)) {
            return new Resultado(false, "Selecciona un usuario.");
        }

        int id = Integer.parseInt(idStr);

        // Los administradores no se pueden desactivar
        String sqlRol = "SELECT id_roles FROM usuarios WHERE id_usuarios = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlRol)) {
            ps.setInt(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("id_roles") == 1 && !activar) {
                    return new Resultado(false, "No se puede desactivar a un administrador.");
                }
            }
        }

        String sql = "UPDATE usuarios SET activo=? WHERE id_usuarios=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, activar ? 1 : 0);
            ps.setInt(2, id);

            boolean ok = ps.executeUpdate() > 0;
            String mensajeOk = activar ? "Usuario reactivado correctamente" : "Usuario desactivado correctamente";
            return new Resultado(ok, ok ? mensajeOk : "No se encontro el usuario.");
        }
    }

    Resultado eliminarUsuario(Connection con, HttpServletRequest request) throws SQLException {
        String idStr = obtenerParametro(request, "id", "idUsuario");

        if (estaVacio(idStr)) {
            return new Resultado(false, "Selecciona un usuario para eliminar.");
        }

        int id = Integer.parseInt(idStr);

        // Verificar si el usuario tiene una membresía activa (vencimiento >= hoy)
        String sqlMembresia = "SELECT u.nombre, u.apellido, m.tipo, u.vencimiento "
                + "FROM usuarios u "
                + "JOIN membresias m ON u.id_membresias = m.id_membresias "
                + "WHERE u.id_usuarios = ? AND u.vencimiento >= CURDATE()";

        try (PreparedStatement ps = con.prepareStatement(sqlMembresia)) {
            ps.setInt(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreUsuario = rs.getString("nombre") + " " + rs.getString("apellido");
                    String tipoMembresia = rs.getString("tipo");
                    String fechaVencimiento = rs.getString("vencimiento");
                    return new Resultado(false,
                            "No se puede eliminar al cliente " + nombreUsuario
                            + " porque tiene una membres\u00eda activa ("
                            + tipoMembresia + ") vigente hasta el " + fechaVencimiento + ".");
                }
            }
        }

        // Verificar si el usuario tiene compras (facturas) registradas
        String sqlCompras = "SELECT COUNT(*) AS total FROM fact_cabecera WHERE id_usuarios = ?";

        try (PreparedStatement ps = con.prepareStatement(sqlCompras)) {
            ps.setInt(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("total") > 0) {
                    int totalCompras = rs.getInt("total");
                    return new Resultado(false,
                            "No se puede eliminar este cliente porque tiene "
                            + totalCompras + " compra(s) registrada(s) en el sistema.");
                }
            }
        }

        con.setAutoCommit(false);

        try {
            eliminarDependenciasUsuario(con, id);

            String sql = "DELETE FROM usuarios WHERE id_usuarios=?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                boolean ok = ps.executeUpdate() > 0;

                if (ok) {
                    con.commit();
                    return new Resultado(true, "Usuario eliminado correctamente");
                }

                con.rollback();
                return new Resultado(false, "No se encontro el usuario para eliminar.");
            }

        } catch (SQLException e) {
            con.rollback();
            throw e;

        } finally {
            con.setAutoCommit(true);
        }
    }

    private String obtenerParametro(HttpServletRequest request, String principal, String alterno) {
        String valor = request.getParameter(principal);

        if (valor == null || valor.trim().isEmpty()) {
            valor = request.getParameter(alterno);
        }

        return valor;
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void eliminarDependenciasUsuario(Connection con, int idUsuario) throws SQLException {
        ejecutarDelete(con, "DELETE FROM fact_detallada WHERE id_fact_cabecera IN "
                + "(SELECT id_fact_cabecera FROM fact_cabecera WHERE id_usuarios=?)", idUsuario);

        ejecutarDelete(con, "DELETE FROM fact_cabecera WHERE id_usuarios=?", idUsuario);
        ejecutarDelete(con, "DELETE FROM evaluaciones_fisicas WHERE id_usuarios=?", idUsuario);
        ejecutarDelete(con, "DELETE FROM asistencias WHERE id_usuarios=?", idUsuario);
        ejecutarDelete(con, "DELETE FROM metas WHERE id_usuarios=?", idUsuario);
        ejecutarDelete(con, "DELETE FROM registros WHERE id_usuarios=?", idUsuario);
        ejecutarDelete(con, "DELETE FROM rutinas WHERE id_usuarios=?", idUsuario);
        ejecutarDelete(con, "DELETE FROM usuarios_clases WHERE id_usuarios=?", idUsuario);
    }

    private void ejecutarDelete(Connection con, String sql, int idUsuario) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    static class Resultado {

        private final boolean ok;
        private final String mensaje;

        private Resultado(boolean ok, String mensaje) {
            this.ok = ok;
            this.mensaje = mensaje;
        }

        boolean isOk() {
            return ok;
        }

        String getMensaje() {
            return mensaje;
        }
    }
}


