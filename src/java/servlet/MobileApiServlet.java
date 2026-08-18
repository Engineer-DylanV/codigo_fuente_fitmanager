package servlet;

import Controlador.*;
import Modelo.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet(name = "MobileApiServlet", urlPatterns = {"/MobileApiServlet"})
public class MobileApiServlet extends HttpServlet {

    private static final ConcurrentHashMap<String, Integer> TOKEN_STORE = new ConcurrentHashMap<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String action = param(request, "action");
        if (action.isEmpty()) { sendError(response, 400, "Parámetro 'action' requerido"); return; }

        switch (action.toLowerCase()) {
            case "login":             handleLogin(request, response);           break;
            case "forgot_password_request": handleForgotPasswordRequest(request, response); break;
            case "forgot_password_verify":  handleForgotPasswordVerify(request, response);  break;
            case "forgot_password_reset":   handleForgotPasswordReset(request, response);   break;
            case "users":             handleListUsers(request, response);       break;
            case "deleteuser":        handleDeleteUser(request, response);      break;
            case "facturas":          handleFacturas(request, response);        break;
            case "factura_detalle":   handleFacturaDetalle(request, response);  break;
            case "rutinas":           handleRutinas(request, response);         break;
            case "crear_rutina":      handleCrearRutina(request, response);     break;
            case "eliminar_rutina":   handleEliminarRutina(request, response);  break;
            case "registros":         handleRegistros(request, response);       break;
            case "crear_registro":    handleCrearRegistro(request, response);   break;
            case "eliminar_registro": handleEliminarRegistro(request, response);break;
            case "metas":             handleMetas(request, response);           break;
            case "crear_meta":        handleCrearMeta(request, response);       break;
            case "eliminar_meta":     handleEliminarMeta(request, response);    break;
            case "evaluaciones":      handleEvaluaciones(request, response);    break;
            case "asistencia_mes":       handleAsistenciaMes(request, response);       break;
            case "registrar_asistencia": handleRegistrarAsistencia(request, response); break;
            case "productos":         handleProductos(request, response);       break;
            case "iniciar_compra_producto": handleIniciarCompraProducto(request, response); break;
            case "clases":            handleClases(request, response);          break;
            case "mis_clases":        handleMisClases(request, response);       break;
            case "inscribir_clase":   handleInscribirClase(request, response);  break;
            case "desinscribir_clase":handleDesinscribirClase(request,response);break;
            default: sendError(response, 400, "Acción desconocida: " + action);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // ── LOGIN ──────────────────────────────────────────────────────────────────
    private void handleLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String email = param(req, "email"), password = param(req, "password");
        if (email.isEmpty() || password.isEmpty()) { sendError(res, 400, "Email y password requeridos"); return; }
        try {
            Usuarios u = new UsuariosDAO().login(email, password);
            if (u == null) { sendError(res, 401, "Credenciales incorrectas"); return; }
            String token = UUID.randomUUID().toString();
            TOKEN_STORE.put(token, u.getId_usuarios());

            String tipoDoc = "", membresia = "";
            try { TipoDocumento td = new TipoDocumentoDAO(Conexion.getConnect()).consultar(u.getId_tipo_documento()); if (td!=null) tipoDoc=td.getDescripcion(); } catch(Exception ignored){}
            try { Membresias m = new MembresiasDAO().consultar(u.getId_membresias()); if (m!=null) membresia=m.getTipo(); } catch(Exception ignored){}

            String user = "{\"id\":" + u.getId_usuarios()
                + ",\"nombre\":\"" + esc(u.getNombre()) + "\""
                + ",\"apellido\":\"" + esc(u.getApellido()) + "\""
                + ",\"documento\":\"" + esc(u.getDocumento()) + "\""
                + ",\"email\":\"" + esc(u.getEmail()) + "\""
                + ",\"telefono\":\"" + esc(u.getTelefono()!=null?u.getTelefono():"") + "\""
                + ",\"idRol\":" + u.getId_roles()
                + ",\"rol\":\"" + (u.getId_roles()==1?"Administrador":"Cliente") + "\""
                + ",\"tipoDocumento\":\"" + esc(tipoDoc) + "\""
                + ",\"membresia\":\"" + esc(membresia) + "\""
                + ",\"vencimiento\":\"" + esc(u.getVencimiento()!=null?u.getVencimiento():"") + "\""
                + "}";
            res.getWriter().print("{\"ok\":true,\"token\":\"" + token + "\",\"user\":" + user + "}");
        } catch (Exception e) { sendError(res, 500, e.getMessage()); }
    }

    // ── OLVIDÉ MI CONTRASEÑA ───────────────────────────────────────────────────
    private void handleForgotPasswordRequest(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String correo = param(req, "correo");
        if (correo.isEmpty()) correo = param(req, "email");
        if (correo.isEmpty()) { sendError(res, 400, "Correo requerido"); return; }
        try {
            UsuariosDAO dao = new UsuariosDAO();
            if (!dao.existeEmail(correo)) {
                // No revelamos si el correo existe o no, para no filtrar información de cuentas.
                res.getWriter().print("{\"ok\":true,\"message\":\"Si el correo existe, se envió un código\"}");
                return;
            }
            String codigo = dao.generarYGuardarCodigo(correo, 10);
            if (codigo == null) { sendError(res, 500, "No se pudo generar el código"); return; }
            String nombre = dao.obtenerNombrePorCorreo(correo);
            boolean enviado = EmailService.enviarCodigoRecuperacion(correo, nombre != null ? nombre : "", codigo);
            if (!enviado) { sendError(res, 500, "No se pudo enviar el correo: " + EmailService.getUltimoError()); return; }
            res.getWriter().print("{\"ok\":true,\"message\":\"Código enviado al correo\"}");
        } catch (Exception e) { sendError(res, 500, e.getMessage()); }
    }

    private void handleForgotPasswordVerify(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String correo = param(req, "correo");
        if (correo.isEmpty()) correo = param(req, "email");
        String codigo = param(req, "codigo");
        if (correo.isEmpty() || codigo.isEmpty()) { sendError(res, 400, "Correo y código requeridos"); return; }
        try {
            UsuariosDAO.ResultadoVerificacion resultado = new UsuariosDAO().verificarCodigoSinConsumir(correo, codigo);
            switch (resultado) {
                case OK: res.getWriter().print("{\"ok\":true,\"message\":\"Código válido\"}"); break;
                case CODIGO_INCORRECTO: sendError(res, 400, "Código incorrecto"); break;
                case CODIGO_VENCIDO: sendError(res, 400, "El código venció, solicita uno nuevo"); break;
                case SIN_CODIGO_PENDIENTE: sendError(res, 400, "No hay un código pendiente, solicita uno nuevo"); break;
                default: sendError(res, 404, "No existe una cuenta con ese correo");
            }
        } catch (Exception e) { sendError(res, 500, e.getMessage()); }
    }

    private void handleForgotPasswordReset(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String correo = param(req, "correo");
        if (correo.isEmpty()) correo = param(req, "email");
        String codigo = param(req, "codigo");
        String nuevaPassword = param(req, "nuevaPassword");
        if (nuevaPassword.isEmpty()) nuevaPassword = param(req, "password");
        String confirmPassword = param(req, "confirmPassword");
        if (correo.isEmpty() || codigo.isEmpty() || nuevaPassword.isEmpty()) { sendError(res, 400, "Datos incompletos"); return; }
        if (!confirmPassword.isEmpty() && !confirmPassword.equals(nuevaPassword)) { sendError(res, 400, "Las contraseñas no coinciden"); return; }
        try {
            UsuariosDAO dao = new UsuariosDAO();
            UsuariosDAO.ResultadoVerificacion resultado = dao.verificarCodigoSinConsumir(correo, codigo);
            if (resultado != UsuariosDAO.ResultadoVerificacion.OK) {
                switch (resultado) {
                    case CODIGO_INCORRECTO: sendError(res, 400, "Código incorrecto"); return;
                    case CODIGO_VENCIDO: sendError(res, 400, "El código venció, solicita uno nuevo"); return;
                    case SIN_CODIGO_PENDIENTE: sendError(res, 400, "No hay un código pendiente, solicita uno nuevo"); return;
                    default: sendError(res, 404, "No existe una cuenta con ese correo"); return;
                }
            }
            if (!dao.actualizarPassword(correo, nuevaPassword)) { sendError(res, 500, "No se pudo actualizar la contraseña"); return; }
            res.getWriter().print("{\"ok\":true,\"message\":\"Contraseña actualizada\"}");
        } catch (Exception e) { sendError(res, 500, e.getMessage()); }
    }

    // ── USERS ──────────────────────────────────────────────────────────────────
    /** Reutiliza PanelAdministradorServlet.listarUsuariosAdmin(...) en vez de consultar la BD por su cuenta. */
    private void handleListUsers(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try (Connection con = Conexion.getConnect()) {
            List<java.util.Map<String,Object>> lista = new PanelAdministradorServlet().listarUsuariosAdmin(con);
            StringBuilder sb = new StringBuilder("{\"ok\":true,\"users\":[");
            for (int i=0;i<lista.size();i++) {
                java.util.Map<String,Object> u=lista.get(i);
                sb.append("{\"id\":").append(u.get("id"))
                  .append(",\"nombre\":\"").append(esc(String.valueOf(u.get("nombre"))).trim()).append("\"")
                  .append(",\"email\":\"").append(esc(String.valueOf(u.get("correo")))).append("\"")
                  .append(",\"tipoDoc\":\"").append(esc(String.valueOf(u.get("tipoDoc")))).append("\"")
                  .append(",\"documento\":\"").append(esc(String.valueOf(u.get("documento")))).append("\"")
                  .append(",\"rol\":\"").append(esc(String.valueOf(u.get("rol")))).append("\"")
                  .append(",\"idRol\":").append(u.get("idRol"))
                  .append(",\"membresia\":\"").append(esc(String.valueOf(u.get("membresia")))).append("\"")
                  .append(",\"vencimiento\":\"").append(esc(String.valueOf(u.get("vencimiento")))).append("\"")
                  .append(",\"activo\":").append(u.get("activo"))
                  .append("}");
                if (i<lista.size()-1) sb.append(",");
            }
            res.getWriter().print(sb.append("]}").toString());
        } catch (Exception e) { sendError(res,500,e.getMessage()); }
    }

    // ── DELETE USER ────────────────────────────────────────────────────────────
    /** Reutiliza GestionUsuariosAdminServlet.eliminarUsuario(...), que valida membresia activa y facturas antes de borrar. */
    private void handleDeleteUser(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String id=param(req,"id"); if(id.isEmpty()){sendError(res,400,"ID requerido");return;}
        try (Connection con = Conexion.getConnect()) {
            GestionUsuariosAdminServlet.Resultado resultado = new GestionUsuariosAdminServlet().eliminarUsuario(con, req);
            if(!resultado.isOk()){sendError(res,409,resultado.getMensaje());return;}
            res.getWriter().print("{\"ok\":true,\"message\":\""+esc(resultado.getMensaje())+"\"}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    // ── FACTURAS ───────────────────────────────────────────────────────────────
    private void handleFacturas(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid = TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        try {
            List<FactCabecera> lista = new Fact_CabeceraDAO().listarPorUsuario(uid);
            StringBuilder sb = new StringBuilder("{\"ok\":true,\"facturas\":[");
            for (int i=0;i<lista.size();i++) {
                FactCabecera f=lista.get(i);
                sb.append("{\"id\":").append(f.getId_fact_cabecera()).append(",\"fecha\":\"").append(esc(f.getFecha_fact())).append("\",\"numero\":\"").append(esc(f.getN_factura())).append("\",\"total\":\"").append(esc(f.getValor_total())).append("\",\"concepto\":\"").append(esc(f.getConcepto()!=null?f.getConcepto():"Membresía FitManager")).append("\"}");
                if(i<lista.size()-1) sb.append(",");
            }
            res.getWriter().print(sb.append("]}").toString());
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    // ── COMPROBANTE DE PAGO (DETALLE) ─────────────────────────────────────────
    /**
     * Reutiliza FacturaClienteServlet.consultarFactura(...), que ya contiene
     * toda la logica de consulta y enriquecimiento (metodo de pago, detalle,
     * tipo de compra segun la referencia de Wompi), en vez de duplicarla aqui.
     */
    private void handleFacturaDetalle(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid = TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        String idStr=param(req,"id_factura"); if(idStr.isEmpty()){sendError(res,400,"id_factura requerido");return;}
        try (Connection con = Conexion.getConnect()) {
            int idFact=Integer.parseInt(idStr);
            java.util.Map<String, Object> f = new FacturaClienteServlet().consultarFactura(con, uid, idFact);
            if (f == null) { sendError(res, 404, "Comprobante no encontrado"); return; }

            String numero=esc(String.valueOf(f.get("numero"))), fecha=esc(String.valueOf(f.get("fecha"))),
                   total=esc(String.valueOf(f.get("valor_total"))), metodo=esc(String.valueOf(f.get("metodoPago"))),
                   cliente=esc(String.valueOf(f.get("cliente"))), correo=esc(String.valueOf(f.get("correo"))),
                   telefono=esc(String.valueOf(f.get("telefono"))), concepto=esc(String.valueOf(f.get("concepto"))),
                   cantidad=esc(String.valueOf(f.get("cantidad"))), precioUnit=esc(String.valueOf(f.get("precioUnitario"))),
                   tipoCompra=esc(String.valueOf(f.get("tipoCompra"))), nombreMembresia=esc(String.valueOf(f.get("nombreMembresia"))),
                   idTransaccion=esc(String.valueOf(f.get("idTransaccion"))), referenciaPago=esc(String.valueOf(f.get("referenciaPago")));
            int duracionDias = f.get("duracionDias")!=null ? (Integer) f.get("duracionDias") : 0;

            res.getWriter().print("{\"ok\":true,\"factura\":{"
                +"\"numero\":\""+numero+"\",\"fecha\":\""+fecha+"\",\"total\":\""+total+"\","
                +"\"metodo\":\""+metodo+"\",\"cliente\":\""+cliente+"\",\"correo\":\""+correo+"\",\"telefono\":\""+telefono+"\","
                +"\"concepto\":\""+concepto+"\",\"cantidad\":\""+cantidad+"\",\"precioUnit\":\""+precioUnit+"\","
                +"\"tipoCompra\":\""+tipoCompra+"\",\"nombreMembresia\":\""+nombreMembresia+"\",\"duracionDias\":"+duracionDias+","
                +"\"idTransaccion\":\""+idTransaccion+"\",\"referenciaPago\":\""+referenciaPago+"\"}}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    // ── RUTINAS ────────────────────────────────────────────────────────────────
    /**
     * Reutiliza GestionRutinasServlet.rutinasJson(...) en vez de armar el JSON
     * por su cuenta. Nota: el campo de id ahora se llama "id_Rutinas" (no "id"),
     * igual que en el servlet web.
     */
    private void handleRutinas(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        try {
            List<Rutinas> lista=new RutinasDAO().listarPorUsuario(uid);
            String arreglo = new GestionRutinasServlet().rutinasJson(lista);
            res.getWriter().print("{\"ok\":true,\"rutinas\":" + arreglo + "}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    private void handleCrearRutina(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        String nombre=param(req,"nombre"), desc=param(req,"descripcion");
        if(nombre.isEmpty()){sendError(res,400,"Nombre requerido");return;}
        try {
            Rutinas r=new Rutinas(); r.setNombre(nombre); r.setDescripcion(desc); r.setId_usuarios(uid);
            if(!new RutinasDAO().insertar(r)){sendError(res,500,"No se pudo crear");return;}
            res.getWriter().print("{\"ok\":true,\"message\":\"Rutina creada\"}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    private void handleEliminarRutina(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        String id=param(req,"id"); if(id.isEmpty()){sendError(res,400,"ID requerido");return;}
        try { new RutinasDAO().eliminar(Integer.parseInt(id),uid); res.getWriter().print("{\"ok\":true,\"message\":\"Rutina eliminada\"}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    // ── REGISTROS ──────────────────────────────────────────────────────────────
    /**
     * Reutiliza RegistroEntrenamientoServlet.registrosJson(...) en vez de armar
     * el JSON por su cuenta. Nota: el campo de id ahora se llama "id_Registros"
     * (no "id"), igual que en el servlet web.
     */
    private void handleRegistros(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        try {
            List<Registros> lista=new RegistrosDAO().listarPorUsuario(uid);
            String arreglo = new RegistroEntrenamientoServlet().registrosJson(lista);
            res.getWriter().print("{\"ok\":true,\"registros\":" + arreglo + "}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    private void handleCrearRegistro(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        String ej=param(req,"ejercicio"), reps=param(req,"repeticiones"), peso=param(req,"peso");
        if(ej.isEmpty()||reps.isEmpty()||peso.isEmpty()){sendError(res,400,"Datos incompletos");return;}
        try {
            Registros r=new Registros(); r.setEjercicio(ej); r.setRepeticiones(Integer.parseInt(reps));
            r.setPeso(Double.parseDouble(peso)); r.setFecha(new Date(System.currentTimeMillis())); r.setId_usuarios(uid);
            if(!new RegistrosDAO().insertar(r)){sendError(res,500,"No se pudo guardar");return;}
            res.getWriter().print("{\"ok\":true,\"message\":\"Registro guardado\"}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    private void handleEliminarRegistro(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        String id=param(req,"id"); if(id.isEmpty()){sendError(res,400,"ID requerido");return;}
        try { new RegistrosDAO().eliminar(Integer.parseInt(id),uid); res.getWriter().print("{\"ok\":true,\"message\":\"Registro eliminado\"}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    // ── METAS ──────────────────────────────────────────────────────────────────
    /**
     * Reutiliza GestionMetasServlet.porcentajeJson(...) para el resumen agregado.
     * El listado detallado de metas SI se arma aqui: metasJson(...) del servlet
     * web no incluye progreso/cumplida por meta individual (solo existe el
     * agregado de porcentajeJson), y ese dato es indispensable para la UI
     * movil (barra de progreso y aviso de "meta cumplida" por ejercicio). Como
     * ningun servlet web expone ese calculo por item, no hay nada que
     * reutilizar aqui: es logica propia y unica de esta accion movil.
     */
    private void handleMetas(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        try {
            List<Metas> lista=new MetasDAO().listarPorUsuario(uid);
            RegistrosDAO registrosDAO = new RegistrosDAO();
            StringBuilder sb=new StringBuilder("{\"ok\":true,\"metas\":[");
            for(int i=0;i<lista.size();i++){
                Metas m=lista.get(i);
                int progreso = registrosDAO.sumarRepeticiones(uid, m.getEjercicio());
                boolean cumplida = progreso >= m.getMeta();
                sb.append("{\"id_Metas\":").append(m.getId_metas()).append(",\"ejercicio\":\"").append(esc(m.getEjercicio())).append("\",\"meta\":").append(m.getMeta())
                  .append(",\"progreso\":").append(progreso).append(",\"cumplida\":").append(cumplida).append("}");
                if(i<lista.size()-1)sb.append(",");
            }
            sb.append("]");
            String resumen = new GestionMetasServlet().porcentajeJson(uid, lista);
            sb.append(",\"resumen\":").append(resumen).append("}");
            res.getWriter().print(sb.toString());
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }


    private void handleCrearMeta(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        String ej=param(req,"ejercicio"), metaStr=param(req,"meta");
        if(ej.isEmpty()||metaStr.isEmpty()){sendError(res,400,"Datos incompletos");return;}
        try {
            Metas m=new Metas(); m.setEjercicio(ej); m.setMeta(Integer.parseInt(metaStr)); m.setId_usuarios(uid);
            if(!new MetasDAO().insertar(m)){sendError(res,500,"No se pudo crear");return;}
            res.getWriter().print("{\"ok\":true,\"message\":\"Meta creada\"}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    private void handleEliminarMeta(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        String id=param(req,"id"); if(id.isEmpty()){sendError(res,400,"ID requerido");return;}
        try { new MetasDAO().eliminar(Integer.parseInt(id),uid); res.getWriter().print("{\"ok\":true,\"message\":\"Meta eliminada\"}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    // ── ASISTENCIA ─────────────────────────────────────────────────────────────
    /** Reutiliza RegistroAsistenciaServlet.totalMesJson(...) en vez de armar el JSON por su cuenta. */
    private void handleAsistenciaMes(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid = TOKEN_STORE.get(param(req, "token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        try {
            res.getWriter().print(new RegistroAsistenciaServlet().totalMesJson(uid));
        } catch (Exception e) { sendError(res, 500, e.getMessage()); }
    }

    /** Reutiliza RegistroAsistenciaServlet.registrarJson(...) en vez de armar el JSON por su cuenta. */
    private void handleRegistrarAsistencia(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid = TOKEN_STORE.get(param(req, "token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        try {
            String json = new RegistroAsistenciaServlet().registrarJson(uid);
            if (json.contains("\"ok\":false")) { res.setStatus(409); }
            res.getWriter().print(json);
        } catch (Exception e) { sendError(res, 500, e.getMessage()); }
    }


    /** Reutiliza ConsultaEvaluacionFisicaServlet.consultarJson(...) en vez de reconsultar la BD por su cuenta. */
    private void handleEvaluaciones(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        String arreglo = new ConsultaEvaluacionFisicaServlet().consultarJson(uid);
        res.getWriter().print("{\"ok\":true,\"evaluaciones\":" + arreglo + "}");
    }

    // ── PRODUCTOS ──────────────────────────────────────────────────────────────
    /** Reutiliza ClienteServlet.listarProductos(...) en vez de consultar la BD por su cuenta. */
    private void handleProductos(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid=TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        try (Connection con = Conexion.getConnect()) {
            List<java.util.Map<String,Object>> lista = new ClienteServlet().listarProductos(con);
            StringBuilder sb=new StringBuilder("{\"ok\":true,\"productos\":[");
            for(int i=0;i<lista.size();i++){
                java.util.Map<String,Object> p=lista.get(i);
                sb.append("{\"id\":").append(p.get("id")).append(",\"nombre\":\"").append(esc(String.valueOf(p.get("nombre")))).append("\",\"precio\":").append(p.get("precio")).append("}");
                if(i<lista.size()-1)sb.append(",");
            }
            res.getWriter().print(sb.append("]}").toString());
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    // ── COMPRAS (WOMPI) ────────────────────────────────────────────────────────
    /**
     * Version "movil" de CompraServlet: en vez de basarse en la sesion HTTP
     * del navegador (que la app Flutter no tiene, porque usa token), valida
     * el token contra TOKEN_STORE y devuelve en JSON los mismos datos
     * firmados que Comprar.jsp usa para abrir el Web Checkout de Wompi.
     */
    private void handleIniciarCompraProducto(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid = TOKEN_STORE.get(param(req, "token"));
        if (uid == null) { sendError(res, 401, "Token inválido"); return; }

        int idProducto;
        try { idProducto = Integer.parseInt(param(req, "idProducto")); }
        catch (Exception e) { idProducto = 0; }
        if (idProducto <= 0) { sendError(res, 400, "idProducto requerido"); return; }

        try (Connection con = Conexion.getConnect()) {
            CompraServlet compras = new CompraServlet();
            java.util.Map<String, Object> item = compras.consultarItem(con, "producto", idProducto);
            if (item == null) { sendError(res, 404, "Producto no encontrado"); return; }

            java.math.BigDecimal precio = (java.math.BigDecimal) item.get("precio");
            long montoEnCentavos = precio.multiply(new java.math.BigDecimal(100)).longValueExact();

            String referencia = "FM-producto-" + idProducto + "-" + uid + "-" + System.currentTimeMillis();
            String firma = WompiService.generarFirmaIntegridad(referencia, montoEnCentavos, WompiConfig.MONEDA);
            String urlRedireccion = urlAbsoluta(req, req.getContextPath() + "/WompiRedirectServlet");

            StringBuilder sb = new StringBuilder("{\"ok\":true");
            sb.append(",\"nombreItem\":\"").append(esc(String.valueOf(item.get("nombre")))).append("\"");
            sb.append(",\"precio\":").append(precio.toPlainString());
            sb.append(",\"wompiPublicKey\":\"").append(esc(WompiConfig.getLlavePublica())).append("\"");
            sb.append(",\"wompiMoneda\":\"").append(esc(WompiConfig.MONEDA)).append("\"");
            sb.append(",\"wompiMontoCentavos\":").append(montoEnCentavos);
            sb.append(",\"wompiReferencia\":\"").append(esc(referencia)).append("\"");
            sb.append(",\"wompiFirma\":\"").append(esc(firma)).append("\"");
            sb.append(",\"wompiRedirectUrl\":\"").append(esc(urlRedireccion)).append("\"");
            sb.append(",\"wompiCheckoutUrl\":\"").append(esc(WompiConfig.URL_CHECKOUT)).append("\"");
            sb.append("}");
            res.getWriter().print(sb.toString());
        } catch (Exception e) { sendError(res, 500, e.getMessage()); }
    }

    private String urlAbsoluta(HttpServletRequest request, String path) {
        String esquema = request.getScheme();
        String host = request.getServerName();
        int puerto = request.getServerPort();
        boolean puertoEstandar = ("http".equals(esquema) && puerto == 80)
                || ("https".equals(esquema) && puerto == 443);
        return esquema + "://" + host + (puertoEstandar ? "" : ":" + puerto) + path;
    }

    // ── CLASES ─────────────────────────────────────────────────────────────────
    /**
     * Reutiliza GestionClasesServlet.clasesConInscripcionJson(...) en vez de
     * armar el JSON por su cuenta. Nota: el campo de id ahora se llama
     * "id_Clases" (no "id"), igual que en el resto de acciones delegadas.
     */
    private void handleClases(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid = TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        try {
            ClasesDAO dao = new ClasesDAO();
            GestionClasesServlet servlet = new GestionClasesServlet();
            String arreglo = servlet.clasesConInscripcionJson(dao.listar(), dao, uid);
            res.getWriter().print("{\"ok\":true,\"clases\":" + arreglo + "}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    /** Reutiliza GestionClasesServlet.clasesJson(...) en vez de armar el JSON por su cuenta. */
    private void handleMisClases(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid = TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        try {
            List<Clases> lista = new ClasesDAO().listarMisClases(uid);
            String arreglo = new GestionClasesServlet().clasesJson(lista);
            res.getWriter().print("{\"ok\":true,\"clases\":" + arreglo + "}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    private void handleInscribirClase(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid = TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        String id=param(req,"id"); if(id.isEmpty()){sendError(res,400,"ID requerido");return;}
        try {
            boolean ok = new ClasesDAO().inscribir(uid, Integer.parseInt(id));
            if(ok) res.getWriter().print("{\"ok\":true,\"message\":\"Inscrito en la clase\"}");
            else sendError(res,409,"Ya estás inscrito o la clase no existe");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    private void handleDesinscribirClase(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Integer uid = TOKEN_STORE.get(param(req,"token")); if(uid==null){sendError(res,401,"Token inválido");return;}
        String id=param(req,"id"); if(id.isEmpty()){sendError(res,400,"ID requerido");return;}
        try {
            new ClasesDAO().desinscribir(uid, Integer.parseInt(id));
            res.getWriter().print("{\"ok\":true,\"message\":\"Saliste de la clase\"}");
        } catch(Exception e){sendError(res,500,e.getMessage());}
    }

    // ── HELPERS ────────────────────────────────────────────────────────────────
    private String param(HttpServletRequest req, String name) {
        String v=req.getParameter(name); return v==null?"":v.trim();
    }
    private String esc(String s) {
        if(s==null)return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","").replace("\r","");
    }
    private void sendError(HttpServletResponse res, int code, String msg) throws IOException {
        res.setStatus(code); res.getWriter().print("{\"ok\":false,\"message\":\""+esc(msg)+"\"}");
    }
}
