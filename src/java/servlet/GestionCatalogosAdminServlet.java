package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;

@WebServlet(name = "GestionCatalogosAdminServlet", urlPatterns = {"/GestionCatalogosAdminServlet"})
public class GestionCatalogosAdminServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://" + System.getenv().getOrDefault("MYSQLHOST","fitmanager-mysqldb.mysql.database.azure.com") + ":" + System.getenv().getOrDefault("MYSQLPORT","3306") + "/" + System.getenv().getOrDefault("MYSQLDATABASE","railway") + "?useSSL=true&requireSSL=true&verifyServerCertificate=false&serverTimezone=UTC&autoReconnect=true";
    private static final String DB_USER = System.getenv().getOrDefault("MYSQLUSER","fitmanager_admin");
    private static final String DB_PASS = System.getenv().getOrDefault("MYSQLPASSWORD","Fitmanagerpassword#");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String tipo = request.getParameter("tipo");
        String accion = request.getParameter("accion");
        int id = parseInt(request.getParameter("id"));

        boolean ok = false;
        String mensaje = "Accion no valida";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                if ("sede".equals(tipo)) {
                    ok = procesarSede(con, accion, id, request);
                } else if ("proveedor".equals(tipo)) {
                    ok = procesarProveedor(con, accion, id, request);
                } else if ("producto".equals(tipo)) {
                    ok = procesarProducto(con, accion, id, request);
                } else if ("rol".equals(tipo)) {
                    ok = procesarCatalogo(con, accion, id, request, "roles", "id_roles");
                } else if ("tipoDocumento".equals(tipo)) {
                    ok = procesarCatalogo(con, accion, id, request, "tipo_documento", "id_tipo_documento");
                } else if ("metodoPago".equals(tipo)) {
                    ok = procesarCatalogo(con, accion, id, request, "metodo_pago", "id_metodo_pago");
                } else if ("membresia".equals(tipo)) {
                    ok = procesarMembresia(con, accion, id, request);
                } else if ("evaluacion".equals(tipo)) {
                    ok = procesarEvaluacion(con, accion, id, request);
                }

                mensaje = ok ? "Cambio realizado correctamente" : "No se pudo realizar el cambio";
            }

        } catch (Exception e) {
            mensaje = e.getMessage();
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath()
                + "/PanelAdministradorServlet?estado=" + (ok ? "ok" : "error")
                + "&mensaje=" + URLEncoder.encode(mensaje, StandardCharsets.UTF_8));
    }

    private boolean procesarSede(Connection con, String accion, int id, HttpServletRequest request) throws SQLException {
        if ("agregar".equals(accion)) {
            validarObligatorio(request.getParameter("nombre"), "El nombre de la sede es obligatorio.");
            validarObligatorio(request.getParameter("direccion"), "La direccion de la sede es obligatoria.");
            return ejecutar(con, "INSERT INTO sedes (Nombre, Direccion) VALUES (?, ?)",
                    request.getParameter("nombre"), request.getParameter("direccion"));
        }

        if ("editar".equals(accion)) {
            validarId(id);
            validarObligatorio(request.getParameter("nombre"), "El nombre de la sede es obligatorio.");
            validarObligatorio(request.getParameter("direccion"), "La direccion de la sede es obligatoria.");
            return ejecutar(con, "UPDATE sedes SET nombre = ?, direccion = ? WHERE id_sedes = ?",
                    request.getParameter("nombre"), request.getParameter("direccion"), id);
        }

        if ("eliminar".equals(accion)) {
            validarId(id);
            return ejecutar(con, "DELETE FROM sedes WHERE id_sedes = ?", id);
        }

        return false;
    }

    private boolean procesarProveedor(Connection con, String accion, int id, HttpServletRequest request) throws SQLException {
        if ("agregar".equals(accion)) {
            validarObligatorio(request.getParameter("nombre"), "El nombre del proveedor es obligatorio.");
            validarObligatorio(request.getParameter("tipoProducto"), "El tipo de producto es obligatorio.");
            return ejecutar(con, "INSERT INTO proveedores (Nombre, tipo_producto) VALUES (?, ?)",
                    request.getParameter("nombre"), request.getParameter("tipoProducto"));
        }

        if ("editar".equals(accion)) {
            validarId(id);
            validarObligatorio(request.getParameter("nombre"), "El nombre del proveedor es obligatorio.");
            validarObligatorio(request.getParameter("tipoProducto"), "El tipo de producto es obligatorio.");
            return ejecutar(con, "UPDATE proveedores SET Nombre = ?, tipo_producto = ? WHERE id_Proveedores = ?",
                    request.getParameter("nombre"), request.getParameter("tipoProducto"), id);
        }

        if ("eliminar".equals(accion)) {
            validarId(id);
            return ejecutar(con, "DELETE FROM proveedores WHERE id_Proveedores = ?", id);
        }

        return false;
    }

    private boolean procesarProducto(Connection con, String accion, int id, HttpServletRequest request) throws SQLException {
        if ("agregar".equals(accion)) {
            validarObligatorio(request.getParameter("nombre"), "El nombre del producto es obligatorio.");
            validarObligatorio(request.getParameter("precio"), "El precio del producto es obligatorio.");
            return ejecutar(con, "INSERT INTO productos (nombre, precio) VALUES (?, ?)",
                    request.getParameter("nombre"), new BigDecimal(request.getParameter("precio")));
        }

        if ("editar".equals(accion)) {
            validarId(id);
            validarObligatorio(request.getParameter("nombre"), "El nombre del producto es obligatorio.");
            validarObligatorio(request.getParameter("precio"), "El precio del producto es obligatorio.");
            return ejecutar(con, "UPDATE productos SET nombre = ?, precio = ? WHERE id_productos = ?",
                    request.getParameter("nombre"), new BigDecimal(request.getParameter("precio")), id);
        }

        if ("eliminar".equals(accion)) {
            validarId(id);
            if (existeReferencia(con, "SELECT COUNT(*) FROM fact_detallada WHERE id_productos = ?", id)) {
                throw new SQLException("No se puede eliminar este producto porque ya tiene facturas registradas.");
            }
            return ejecutar(con, "DELETE FROM productos WHERE id_productos = ?", id);
        }

        return false;
    }

    private boolean procesarCatalogo(Connection con, String accion, int id, HttpServletRequest request,
            String tabla, String idCampo) throws SQLException {
        String descripcion = request.getParameter("descripcion");

        if ("agregar".equals(accion)) {
            validarObligatorio(descripcion, "La descripcion es obligatoria.");
            return ejecutar(con, "INSERT INTO " + tabla + " (descripcion) VALUES (?)", descripcion.trim());
        }

        if ("editar".equals(accion)) {
            validarId(id);
            validarObligatorio(descripcion, "La descripcion es obligatoria.");
            return ejecutar(con, "UPDATE " + tabla + " SET descripcion = ? WHERE " + idCampo + " = ?", descripcion.trim(), id);
        }

        if ("eliminar".equals(accion)) {
            validarId(id);
            return ejecutar(con, "DELETE FROM " + tabla + " WHERE " + idCampo + " = ?", id);
        }

        return false;
    }

    private boolean procesarMembresia(Connection con, String accion, int id, HttpServletRequest request) throws SQLException {
        String tipo = request.getParameter("nombre");
        String precio = request.getParameter("precio");
        int duracion = parseInt(request.getParameter("duracionDias"));

        if ("agregar".equals(accion)) {
            validarMembresia(tipo, precio, duracion);
            return ejecutar(con, "INSERT INTO membresias (tipo, precio, duracion_dias) VALUES (?, ?, ?)",
                    tipo.trim(), new BigDecimal(precio), duracion);
        }

        if ("editar".equals(accion)) {
            validarId(id);
            validarMembresia(tipo, precio, duracion);
            return ejecutar(con, "UPDATE membresias SET tipo = ?, precio = ?, duracion_dias = ? WHERE id_membresias = ?",
                    tipo.trim(), new BigDecimal(precio), duracion, id);
        }

        if ("eliminar".equals(accion)) {
            validarId(id);
            return ejecutar(con, "DELETE FROM membresias WHERE id_membresias = ?", id);
        }

        return false;
    }

    private boolean procesarEvaluacion(Connection con, String accion, int id, HttpServletRequest request) throws SQLException {
        if ("eliminar".equals(accion)) {
            validarId(id);
            return ejecutar(con, "DELETE FROM evaluaciones_fisicas WHERE id_evaluaciones_fisicas = ?", id);
        }

        String correo = request.getParameter("correo");
        String fecha = request.getParameter("fecha");
        String peso = request.getParameter("peso");
        String edad = request.getParameter("edad");
        String condicion = request.getParameter("condicion");
        String pruebas = request.getParameter("pruebas");
        String objetivo = request.getParameter("objetivo");
        String programa = request.getParameter("programa");
        int idUsuario = buscarIdUsuarioPorCorreo(con, correo);

        validarObligatorio(correo, "El correo del usuario es obligatorio.");
        validarObligatorio(fecha, "La fecha es obligatoria.");
        validarObligatorio(peso, "El peso es obligatorio.");
        validarObligatorio(edad, "La edad es obligatoria.");
        validarObligatorio(condicion, "La condicion es obligatoria.");
        validarObligatorio(pruebas, "Las pruebas son obligatorias.");
        if ("agregar".equals(accion)) {
            validarObligatorio(objetivo, "Selecciona el objetivo de entrenamiento.");
            validarPrograma(programa);
        }

        if (idUsuario <= 0) {
            throw new SQLException("No existe un usuario con ese correo.");
        }

        if ("agregar".equals(accion)) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO evaluaciones_fisicas (fecha, peso, edad, condicion, pruebas, id_usuarios) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, fecha); ps.setString(2, peso); ps.setString(3, edad);
                ps.setString(4, condicion.trim()); ps.setString(5, pruebas.trim()); ps.setInt(6, idUsuario);
                if (ps.executeUpdate() != 1) { con.rollback(); return false; }
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) { con.rollback(); return false; }
                    String enlaceRutina = buscarEnlaceMaterial(con, programa, objetivo);
                    boolean rutinaCreada = ejecutar(con,
                            "INSERT INTO rutinas (nombre, descripcion, id_usuarios, objetivo, programa, enlace_drive, id_evaluacion) VALUES (?, ?, ?, ?, ?, ?, ?)",
                            programa.trim() + " - " + objetivo.trim(),
                            "Asignada después de la evaluación física. Objetivo: " + objetivo.trim(),
                            idUsuario, objetivo.trim(), programa.trim(), enlaceRutina.trim(), keys.getInt(1));
                    if (!rutinaCreada) { con.rollback(); return false; }
                }
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback(); throw e;
            } finally { con.setAutoCommit(true); }
        }

        if ("editar".equals(accion)) {
            validarId(id);
            return ejecutar(con, "UPDATE evaluaciones_fisicas SET fecha = ?, peso = ?, edad = ?, condicion = ?, pruebas = ?, id_usuarios = ? "
                    + "WHERE id_evaluaciones_fisicas = ?", fecha, peso, edad, condicion.trim(), pruebas.trim(), idUsuario, id);
        }

        return false;
    }

    private int buscarIdUsuarioPorCorreo(Connection con, String correo) throws SQLException {
        if (correo == null || correo.trim().isEmpty()) {
            return 0;
        }

        try (PreparedStatement ps = con.prepareStatement("SELECT id_usuarios FROM usuarios WHERE email = ?")) {
            ps.setString(1, correo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id_usuarios") : 0;
            }
        }
    }

    private boolean existeReferencia(Connection con, String sql, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean ejecutar(Connection con, String sql, Object... valores) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) {
                ps.setObject(i + 1, valores[i]);
            }
            return ps.executeUpdate() > 0;
        }
    }

    private int parseInt(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }

    private void validarId(int id) throws SQLException {
        if (id <= 0) {
            throw new SQLException("Selecciona un registro valido.");
        }
    }

    private void validarObligatorio(String valor, String mensaje) throws SQLException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new SQLException(mensaje);
        }
    }

    private void validarMembresia(String tipo, String precio, int duracion) throws SQLException {
        validarObligatorio(tipo, "El tipo de membresia es obligatorio.");
        validarObligatorio(precio, "El precio de la membresia es obligatorio.");
        if (duracion <= 0) {
            throw new SQLException("La duracion debe ser mayor a cero.");
        }
    }

    private void validarEnlaceDrive(String enlace) throws SQLException {
        validarObligatorio(enlace, "El enlace de la rutina es obligatorio.");
        if (!enlace.trim().startsWith("https://drive.google.com/")) {
            throw new SQLException("El enlace debe ser una URL de Google Drive.");
        }
    }

    private String buscarEnlaceMaterial(Connection con, String programa, String objetivo) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT enlace_drive FROM materiales_rutina WHERE programa = ? AND objetivo = ? AND activo = 1")) {
            ps.setString(1, programa.trim());
            ps.setString(2, objetivo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("enlace_drive");
            }
        }
        throw new SQLException("No existe material configurado para el programa y objetivo seleccionados.");
    }

    private void validarPrograma(String programa) throws SQLException {
        if (!"PROGRAMA 01 H".equals(programa) && !"PROGRAMA 02 H".equals(programa)) {
            throw new SQLException("Selecciona un programa de entrenamiento válido.");
        }
    }
}
