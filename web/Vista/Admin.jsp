<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:if test="${empty sessionScope.usuario or sessionScope.rol ne 'admin'}">
    <c:redirect url="/login" />
</c:if>

<c:if test="${empty requestScope.datosAdminCargados}">
    <c:redirect url="/PanelAdministradorServlet" />
</c:if>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Admin - FitManager</title>
        <link rel="icon" type="image/png" href="<c:url value='/Vista/img/Icono.png' />">

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<c:url value='/Vista/CSS/styles.css?v=20260904' />">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer">
        <link href="https://fonts.googleapis.com/css2?family=Alfa+Slab+One&display=swap" rel="stylesheet">
    </head>

    <body class="panel-view">
        <script>
            (function () {
                try {
                    if (localStorage.getItem('fitmanager_theme') === 'light') {
                        document.body.classList.add('theme-light');
                    }
                } catch (e) {}
            })();
        </script>
        <div class="layout">

            <div class="sidebar">
                <div class="sidebar-logo">
                    <img src="<c:url value='/Vista/img/Fitmanagerpro.png' />" class="logo" alt="FitManager">
                    <h2>FITMANAGER</h2>
                </div>

                <div class="theme-switch">
                    <span><i class="fa-solid fa-moon"></i> Tema</span>
                    <button type="button" id="themeToggleBtn" class="theme-switch-btn" aria-label="Cambiar entre modo oscuro y modo claro"></button>
                </div>

                <ul>
                    <li onclick="mostrarSeccion('usuarios', this)" class="activo"><i class="fa-solid fa-users"></i> Usuarios</li>
                    <li onclick="mostrarSeccion('clases', this)"><i class="fa-solid fa-dumbbell"></i> Clases</li>
                    <li onclick="mostrarSeccion('configGeneral', this)"><i class="fa-solid fa-gear"></i> Configuraciones generales</li>
                    <li onclick="logout()"><i class="fa-solid fa-right-from-bracket"></i> Salir</li>
                </ul>
            </div>

            <div class="contenido">

                <div class="admin-welcome">
                    <h1>Bienvenido <c:out value="${sessionScope.usuario.nombre}" />, estas en el panel admin</h1>
                </div>

                <c:if test="${not empty errorFacturas}">
                    <div class="alert-error-admin">
                        Error cargando datos:
                        <c:out value="${errorFacturas}" />
                    </div>
                </c:if>

                <c:if test="${not empty param.mensaje}">
                    <div id="mensajeAdmin" class="${param.estado eq 'ok' ? 'alert-success-admin' : 'alert-error-admin'}">
                        <c:out value="${param.mensaje}" />
                    </div>
                </c:if>

                <div id="configGeneral" class="seccion">
                    <h1>Configuraciones generales</h1>

                    <div class="config-grupo">
                        <h3>Usuarios y clases</h3>
                        <div class="config-cards">
                            <div class="config-card" onclick="mostrarSeccion('registroAdmin', null)">Registrar usuario</div>
                            <div class="config-card" onclick="mostrarSeccion('eliminarUsuarioAdmin', null)">Eliminar usuario</div>
                            <div class="config-card" onclick="mostrarSeccion('configClases', null)">Gestionar clases</div>
                            <div class="config-card" onclick="mostrarSeccion('vencimientos', null)">Vencimientos</div>
                        </div>
                    </div>

                    <div class="config-grupo">
                        <h3>Catálogo</h3>
                        <div class="config-cards">
                            <div class="config-card" onclick="mostrarSeccion('sedesAdmin', null)">Sedes</div>
                            <div class="config-card" onclick="mostrarSeccion('proveedoresAdmin', null)">Proveedores</div>
                            <div class="config-card" onclick="mostrarSeccion('productosAdmin', null)">Productos</div>
                            <div class="config-card" onclick="mostrarSeccion('tipoDocumentoAdmin', null)">Tipos documento</div>
                        </div>
                    </div>

                    <div class="config-grupo">
                        <h3>Sistema</h3>
                        <div class="config-cards">
                            <div class="config-card" onclick="mostrarSeccion('rolesAdmin', null)">Roles</div>
                            <div class="config-card" onclick="mostrarSeccion('membresiasAdmin', null)">Membresias</div>
                        </div>
                    </div>

                    <div class="config-grupo">
                        <h3>Seguimiento</h3>
                        <div class="config-cards">
                            <div class="config-card" onclick="mostrarSeccion('metasAdmin', null)">Metas</div>
                            <div class="config-card" onclick="mostrarSeccion('facturasAdmin', null)">Comprobantes de pago</div>
                            <div class="config-card" onclick="mostrarSeccion('evaluacionesAdmin', null)">Evaluaciones fisicas</div>
                        </div>
                    </div>
                </div>


                <div id="usuarios" class="seccion activa">
                    <h1>Usuarios registrados</h1>
                    <div class="buscador-admin">

                        <div class="search-box-admin">
                            <span class="icon-search"><i class="fa-solid fa-magnifying-glass"></i></span>

                            <input type="text"
                                   id="buscadorUsuarios"
                                   placeholder="Buscar por correo o documento..."
                                   onkeyup="buscarUsuariosAdmin()">
                        </div>

                    </div>
                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Nombre</th>
                                    <th>Tipo Doc</th>
                                    <th>Documento</th>
                                    <th>Correo</th>
                                    <th>Rol</th>
                                    <th>Membresía</th>
                                    <th>Vencimiento</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>

                            <tbody id="tablaUsuariosAdmin">
                                <c:choose>
                                    <c:when test="${empty usuariosAdmin}">
                                        <tr>
                                            <td colspan="8">No hay usuarios registrados.</td>
                                        </tr>
                                    </c:when>

                                    <c:otherwise>
                                        <c:forEach var="usuario" items="${usuariosAdmin}">
                                            <tr>
                                                <td>${usuario.nombre}</td>
                                                <td>${usuario.tipoDoc}</td>
                                                <td>${usuario.documento}</td>
                                                <td>${usuario.correo}</td>
                                                <td>${usuario.rol}</td>
                                                <td>${usuario.idRol == 1 ? 'N/A' : usuario.membresia}</td>
                                                <td>${usuario.idRol == 1 ? 'N/A' : usuario.vencimiento}</td>
                                                <td>
                                                    <button type="button"
                                                            class="btn-editar-usuario"

                                                            data-id="${usuario.id}"
                                                            data-nombre="${fn:escapeXml(usuario.nombre)}"
                                                            data-tipodoc="${fn:escapeXml(usuario.tipoDoc)}"
                                                            data-documento="${usuario.documento}"
                                                            data-correo="${usuario.correo}"
                                                            data-membresia="${usuario.membresia}"
                                                            data-rol="${usuario.idRol}"

                                                            onclick="abrirModalEditarUsuario(this)">
                                                        Editar
                                                    </button>

                                                    <c:if test="${usuario.idRol != 1}">
                                                        <c:choose>
                                                            <c:when test="${usuario.activo}">
                                                                <c:choose>
                                                                    <c:when test="${usuario.bloqueado}">
                                                                        <button type="button"
                                                                                class="btn-danger"
                                                                                onclick="mostrarAlerta('No se puede desactivar a ${fn:escapeXml(usuario.nombre)} porque tiene una membresía comprada.', { titulo: 'Acción no permitida', icono: 'ban' })">
                                                                            Desactivar
                                                                        </button>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <form action="<c:url value='/GestionUsuariosAdminServlet' />"
                                                                              method="post"
                                                                              class="form-eliminar-inline">

                                                                            <input type="hidden" name="accion" value="desactivar">
                                                                            <input type="hidden" name="id" value="${usuario.id}">

                                                                            <button type="button"
                                                                                    class="btn-danger"
                                                                                    onclick="var f=this.closest('form'); mostrarConfirm('¿Desactivar este usuario?', function(){ f.submit(); }, { titulo: 'Desactivar usuario', icono: 'ban', textoAceptar: 'Sí' })">
                                                                                Desactivar
                                                                            </button>
                                                                        </form>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <form action="<c:url value='/GestionUsuariosAdminServlet' />"
                                                                      method="post"
                                                                      class="form-eliminar-inline">

                                                                    <input type="hidden" name="accion" value="reactivar">
                                                                    <input type="hidden" name="id" value="${usuario.id}">

                                                                    <button type="button"
                                                                            class="btn-success"
                                                                            onclick="var f=this.closest('form'); mostrarConfirm('¿Reactivar este usuario?', function(){ f.submit(); }, { titulo: 'Reactivar usuario', icono: 'check', textoAceptar: 'Sí' })">
                                                                        Reactivar
                                                                    </button>
                                                                </form>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="eliminarUsuarioAdmin" class="seccion">
                    <h1>Eliminar usuario</h1>

                    <div class="alert-error-admin">
                        <i class="fa-solid fa-triangle-exclamation"></i>
                        Esta accion borra al usuario de forma permanente de la base de datos.
                        Los clientes con una membresía comprada no se pueden eliminar ni desactivar.
                    </div>

                    <div class="buscador-admin">
                        <div class="search-box-admin">
                            <span class="icon-search"><i class="fa-solid fa-magnifying-glass"></i></span>

                            <input type="text"
                                   id="buscadorEliminarUsuarios"
                                   placeholder="Buscar por correo o documento..."
                                   onkeyup="buscarUsuariosEliminar()">
                        </div>
                    </div>

                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Nombre</th>
                                    <th>Documento</th>
                                    <th>Correo</th>
                                    <th>Rol</th>
                                    <th>Membresía</th>
                                    <th>Vencimiento</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>

                            <tbody id="tablaEliminarUsuarios">
                                <c:choose>
                                    <c:when test="${empty usuariosAdmin}">
                                        <tr>
                                            <td colspan="7">No hay usuarios registrados.</td>
                                        </tr>
                                    </c:when>

                                    <c:otherwise>
                                        <c:forEach var="usuario" items="${usuariosAdmin}">
                                            <tr>
                                                <td>${usuario.nombre}</td>
                                                <td>${usuario.documento}</td>
                                                <td>${usuario.correo}</td>
                                                <td>${usuario.rol}</td>
                                                <td>${usuario.idRol == 1 ? 'N/A' : usuario.membresia}</td>
                                                <td>${usuario.idRol == 1 ? 'N/A' : usuario.vencimiento}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${usuario.idRol == 1}">
                                                            <button type="button"
                                                                    class="btn-danger"
                                                                    onclick="mostrarAlerta('No se puede eliminar a ${fn:escapeXml(usuario.nombre)} porque es un administrador.', { titulo: 'Acción no permitida', icono: 'ban' })">
                                                                Eliminar
                                                            </button>
                                                        </c:when>

                                                        <c:when test="${usuario.bloqueado}">
                                                            <button type="button"
                                                                    class="btn-danger"
                                                                    onclick="mostrarAlerta('No se puede eliminar a ${fn:escapeXml(usuario.nombre)} porque tiene una membresía comprada.', { titulo: 'Acción no permitida', icono: 'ban' })">
                                                                Eliminar
                                                            </button>
                                                        </c:when>

                                                        <c:otherwise>
                                                            <form action="<c:url value='/GestionUsuariosAdminServlet' />"
                                                                  method="post"
                                                                  class="form-eliminar-inline">

                                                                <input type="hidden" name="accion" value="eliminar">
                                                                <input type="hidden" name="id" value="${usuario.id}">

                                                                <button type="button"
                                                                        class="btn-danger"
                                                                        onclick="var f=this.closest('form'); mostrarConfirm('¿Eliminar definitivamente a ${fn:escapeXml(usuario.nombre)}? Esta accion no se puede deshacer.', function(){ f.submit(); }, { titulo: 'Eliminar usuario', icono: 'trash', textoAceptar: 'Sí, eliminar' })">
                                                                    Eliminar
                                                                </button>
                                                            </form>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="clases" class="seccion">
                    <h1>Clases disponibles</h1>
                    <div id="clases-disponibles-admin" class="cards"></div>
                </div>

                <div id="configClases" class="seccion">
                    <h1>Gestionar clases</h1>

                    <form onsubmit="crearClase(event)">
                        <input type="text" id="nombreClase" placeholder="Nombre de la clase" required>
                        <select id="diaClase" required>
                            <option value="">Dia</option>
                            <option value="Lunes">Lunes</option>
                            <option value="Martes">Martes</option>
                            <option value="Miercoles">Miercoles</option>
                            <option value="Jueves">Jueves</option>
                            <option value="Viernes">Viernes</option>
                            <option value="Sabado">Sabado</option>
                            <option value="Domingo">Domingo</option>
                        </select>
                        <input type="time" id="horaClase" required>
                        <input type="text" id="instructorClase" placeholder="Instructor o entrenador" required>
                        <button type="submit">Crear Clase</button>
                    </form>

                    <div id="clases-admin" class="cards"></div>
                </div>

                <div id="registroAdmin" class="seccion">
                    <h1>Registrar usuario</h1>

                    <form action="<c:url value='/GestionUsuariosAdminServlet' />"
                          method="post"
                          class="admin-user-form">

                        <input type="hidden" name="accion" value="registrar">

                        <input type="text" name="nombres" placeholder="Nombre completo" required>

                        <select name="tipoDoc" class="select-pro" required>
                            <option value="">Tipo de documento</option>
                            <c:forEach var="td" items="${tiposDocumento}">
                                <option value="${td.id}">
                                    <c:out value="${td.descripcion}" />
                                </option>
                            </c:forEach>
                        </select>

                        <input type="text" name="documento" placeholder="Documento" required>
                        <input type="email" name="correo" placeholder="Correo" required>

                        <select name="membresia" class="select-pro" required>
                            <option value="">Membresía</option>

                            <c:forEach var="m" items="${membresias}">
                                <option value="${m.id}">
                                    <c:out value="${m.tipo}" /> - <c:out value="${m.duracionDias}" /> días
                                </option>
                            </c:forEach>
                        </select>

                        <select name="rol" class="select-pro" required>
                            <option value="">Rol</option>
                            <c:forEach var="rol" items="${rolesAdmin}">
                                <option value="${rol.id}">
                                    <c:out value="${rol.descripcion}" />
                                </option>
                            </c:forEach>
                        </select>

                        <input type="password" name="password" placeholder="Contraseña" required>
                        <input type="password" name="confirmPassword" placeholder="Confirmar contraseña" required>

                        <button type="submit">Registrar usuario</button>
                    </form>
                </div>

                <div id="vencimientos" class="seccion">
                    <h1>Asignar vencimiento</h1>

                    <form onsubmit="asignarVencimiento(event)">
                        <input type="email" id="correoVencimiento" placeholder="Correo del usuario" required>
                        <input type="date" id="fechaVencimiento" required>
                        <button type="submit">Asignar</button>
                    </form>
                </div>

                <div id="metasAdmin" class="seccion">
                    <h1>Asignar metas a usuarios</h1>

                    <form onsubmit="asignarMeta(event)">
                        <input type="email" id="correoMeta" placeholder="Correo del usuario" required>
                        <input type="text" id="ejercicioMeta" placeholder="Ejercicio" required>
                        <input type="number" id="valorMeta" placeholder="Meta (reps)" required>
                        <button type="submit">Asignar Meta</button>
                    </form>
                </div>

                <div id="facturasAdmin" class="seccion">
                    <h1>Comprobantes de pago</h1>

                    <h2>Cabecera</h2>

                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Fecha</th>
                                    <th>N° Comprobante</th>
                                    <th>Concepto</th>
                                    <th>Valor total</th>
                                    <th>Método de pago</th>
                                    <th>Usuario</th>
                                    <th>Correo</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:choose>
                                    <c:when test="${empty facturasCabecera}">
                                        <tr>
                                            <td colspan="8">No hay facturas cabecera registradas.</td>
                                        </tr>
                                    </c:when>

                                    <c:otherwise>
                                        <c:forEach var="factura" items="${facturasCabecera}">
                                            <tr>
                                                <td>${factura.id}</td>
                                                <td>${factura.fecha}</td>
                                                <td>${factura.numero}</td>
                                                <td>${factura.concepto}</td>
                                                <td>$ ${factura.valorTotal}</td>
                                                <td>${factura.metodoPago}</td>
                                                <td>${factura.usuario}</td>
                                                <td>${factura.correo}</td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <h2 class="subtitulo-admin">Detalle</h2>

                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID detalle</th>
                                    <th>N° Comprobante</th>
                                    <th>Producto</th>
                                    <th>Precio producto</th>
                                    <th>Cantidad</th>
                                    <th>Fecha comprobante</th>
                                    <th>Usuario</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:choose>
                                    <c:when test="${empty facturasDetalladas}">
                                        <tr>
                                            <td colspan="7">No hay facturas detalladas registradas.</td>
                                        </tr>
                                    </c:when>

                                    <c:otherwise>
                                        <c:forEach var="detalle" items="${facturasDetalladas}">
                                            <tr>
                                                <td>${detalle.id}</td>
                                                <td>${detalle.numeroFactura}</td>
                                                <td>${detalle.producto}</td>
                                                <td>$ ${detalle.precio}</td>
                                                <td>${detalle.cantidad}</td>
                                                <td>${detalle.fecha}</td>
                                                <td>${detalle.usuario}</td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="sedesAdmin" class="seccion">
                    <h1>Sedes</h1>

                    <button type="button" onclick="abrirModalAgregar('sede')" style="margin-bottom:14px;">
                        Añadir sede
                    </button>

                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nombre</th>
                                    <th>Dirección</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:choose>
                                    <c:when test="${empty sedes}">
                                        <tr>
                                            <td colspan="4">No hay sedes registradas.</td>
                                        </tr>
                                    </c:when>

                                    <c:otherwise>
                                        <c:forEach var="sede" items="${sedes}">
                                            <tr>
                                                <td>${sede.idSedes}</td>
                                                <td>${sede.nombre}</td>
                                                <td>${sede.direccion}</td>
                                                <td>
                                                    <button type="button"
                                                            data-tipo="sede"
                                                            data-id="${sede.idSedes}"
                                                            data-nombre="${fn:escapeXml(sede.nombre)}"
                                                            data-direccion="${fn:escapeXml(sede.direccion)}"
                                                            onclick="abrirModalEditarExtra(this)">
                                                        Editar
                                                    </button>

                                                    <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                        <input type="hidden" name="tipo" value="sede">
                                                        <input type="hidden" name="accion" value="eliminar">
                                                        <input type="hidden" name="id" value="${sede.idSedes}">
                                                        <button class="btn-danger" type="button" onclick="var f=this.closest('form'); mostrarConfirm('¿Eliminar esta sede?', function(){ f.submit(); }, { titulo: 'Eliminar sede', icono: 'trash', textoAceptar: 'Sí' })">Eliminar</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="proveedoresAdmin" class="seccion">
                    <h1>Proveedores</h1>

                    <button type="button" onclick="abrirModalAgregar('proveedor')" style="margin-bottom:14px;">
                        Añadir proveedor
                    </button>

                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Nombre</th>
                                    <th>Tipo producto</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:choose>
                                    <c:when test="${empty proveedores}">
                                        <tr>
                                            <td colspan="4">No hay proveedores registrados.</td>
                                        </tr>
                                    </c:when>

                                    <c:otherwise>
                                        <c:forEach var="proveedor" items="${proveedores}">
                                            <tr>
                                                <td>${proveedor.idProveedores}</td>
                                                <td>${proveedor.nombre}</td>
                                                <td>${proveedor.tipoProducto}</td>
                                                <td>
                                                    <button type="button"
                                                            data-tipo="proveedor"
                                                            data-id="${proveedor.idProveedores}"
                                                            data-nombre="${fn:escapeXml(proveedor.nombre)}"
                                                            data-tipo-producto="${fn:escapeXml(proveedor.tipoProducto)}"
                                                            onclick="abrirModalEditarExtra(this)">
                                                        Editar
                                                    </button>

                                                    <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                        <input type="hidden" name="tipo" value="proveedor">
                                                        <input type="hidden" name="accion" value="eliminar">
                                                        <input type="hidden" name="id" value="${proveedor.idProveedores}">
                                                        <button class="btn-danger" type="button" onclick="var f=this.closest('form'); mostrarConfirm('¿Eliminar este proveedor?', function(){ f.submit(); }, { titulo: 'Eliminar proveedor', icono: 'trash', textoAceptar: 'Sí' })">Eliminar</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="productosAdmin" class="seccion">
                    <h1>Productos</h1>

                    <button type="button" onclick="abrirModalAgregar('producto')" style="margin-bottom:14px;">
                        Añadir producto
                    </button>

                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Producto</th>
                                    <th>Precio</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:choose>
                                    <c:when test="${empty productos}">
                                        <tr>
                                            <td colspan="4">No hay productos registrados.</td>
                                        </tr>
                                    </c:when>

                                    <c:otherwise>
                                        <c:forEach var="producto" items="${productos}">
                                            <tr>
                                                <td>${producto.idProductos}</td>
                                                <td>${producto.nombre}</td>
                                                <td>$ ${producto.precio}</td>
                                                <td>
                                                    <button type="button"
                                                            data-tipo="producto"
                                                            data-id="${producto.idProductos}"
                                                            data-nombre="${fn:escapeXml(producto.nombre)}"
                                                            data-precio="${producto.precio}"
                                                            onclick="abrirModalEditarExtra(this)">
                                                        Editar
                                                    </button>

                                                    <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                        <input type="hidden" name="tipo" value="producto">
                                                        <input type="hidden" name="accion" value="eliminar">
                                                        <input type="hidden" name="id" value="${producto.idProductos}">
                                                        <button class="btn-danger" type="button" onclick="var f=this.closest('form'); mostrarConfirm('¿Eliminar este producto?', function(){ f.submit(); }, { titulo: 'Eliminar producto', icono: 'trash', textoAceptar: 'Sí' })">Eliminar</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="rolesAdmin" class="seccion">
                    <h1>Roles</h1>

                    <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="admin-user-form">
                        <input type="hidden" name="tipo" value="rol">
                        <input type="hidden" name="accion" value="agregar">
                        <input type="text" name="descripcion" placeholder="Nuevo rol" required>
                        <button type="submit">Registrar rol</button>
                    </form>

                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Descripcion</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="rol" items="${rolesAdmin}">
                                    <tr>
                                        <td>${rol.id}</td>
                                        <td>${rol.descripcion}</td>
                                        <td>
                                            <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                <input type="hidden" name="tipo" value="rol">
                                                <input type="hidden" name="accion" value="editar">
                                                <input type="hidden" name="id" value="${rol.id}">
                                                <input type="text" name="descripcion" value="${fn:escapeXml(rol.descripcion)}" required>
                                                <button type="submit">Editar</button>
                                            </form>
                                            <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                <input type="hidden" name="tipo" value="rol">
                                                <input type="hidden" name="accion" value="eliminar">
                                                <input type="hidden" name="id" value="${rol.id}">
                                                <button class="btn-danger" type="button" onclick="var f=this.closest('form'); mostrarConfirm('¿Eliminar este rol?', function(){ f.submit(); }, { titulo: 'Eliminar rol', icono: 'trash', textoAceptar: 'Sí' })">Eliminar</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="tipoDocumentoAdmin" class="seccion">
                    <h1>Tipos de documento</h1>

                    <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="admin-user-form">
                        <input type="hidden" name="tipo" value="tipoDocumento">
                        <input type="hidden" name="accion" value="agregar">
                        <input type="text" name="descripcion" placeholder="Nuevo tipo de documento" required>
                        <button type="submit">Registrar tipo</button>
                    </form>

                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Descripcion</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="td" items="${tiposDocumento}">
                                    <tr>
                                        <td>${td.id}</td>
                                        <td>${td.descripcion}</td>
                                        <td>
                                            <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                <input type="hidden" name="tipo" value="tipoDocumento">
                                                <input type="hidden" name="accion" value="editar">
                                                <input type="hidden" name="id" value="${td.id}">
                                                <input type="text" name="descripcion" value="${fn:escapeXml(td.descripcion)}" required>
                                                <button type="submit">Editar</button>
                                            </form>
                                            <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                <input type="hidden" name="tipo" value="tipoDocumento">
                                                <input type="hidden" name="accion" value="eliminar">
                                                <input type="hidden" name="id" value="${td.id}">
                                                <button class="btn-danger" type="button" onclick="var f=this.closest('form'); mostrarConfirm('¿Eliminar este tipo de documento?', function(){ f.submit(); }, { titulo: 'Eliminar tipo documento', icono: 'trash', textoAceptar: 'Sí' })">Eliminar</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="membresiasAdmin" class="seccion">
                    <h1>Membresias</h1>

                    <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="admin-user-form">
                        <input type="hidden" name="tipo" value="membresia">
                        <input type="hidden" name="accion" value="agregar">
                        <input type="text" name="nombre" placeholder="Tipo de membresia" required>
                        <input type="number" name="precio" placeholder="Precio" step="0.01" required>
                        <input type="number" name="duracionDias" placeholder="Duracion en dias" required>
                        <button type="submit">Registrar membresia</button>
                    </form>

                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Tipo</th>
                                    <th>Precio</th>
                                    <th>Duracion</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="m" items="${membresias}">
                                    <tr>
                                        <td>${m.id}</td>
                                        <td>${m.tipo}</td>
                                        <td>$ ${m.precio}</td>
                                        <td>${m.duracionDias} dias</td>
                                        <td>
                                            <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                <input type="hidden" name="tipo" value="membresia">
                                                <input type="hidden" name="accion" value="editar">
                                                <input type="hidden" name="id" value="${m.id}">
                                                <input type="text" name="nombre" value="${fn:escapeXml(m.tipo)}" required>
                                                <input type="number" name="precio" value="${m.precio}" step="0.01" required>
                                                <input type="number" name="duracionDias" value="${m.duracionDias}" required>
                                                <button type="submit">Editar</button>
                                            </form>
                                            <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                <input type="hidden" name="tipo" value="membresia">
                                                <input type="hidden" name="accion" value="eliminar">
                                                <input type="hidden" name="id" value="${m.id}">
                                                <button class="btn-danger" type="button" onclick="var f=this.closest('form'); mostrarConfirm('¿Eliminar esta membresía?', function(){ f.submit(); }, { titulo: 'Eliminar membresía', icono: 'trash', textoAceptar: 'Sí' })">Eliminar</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div id="evaluacionesAdmin" class="seccion">
                    <h1>Evaluaciones fisicas</h1>

                    <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="admin-user-form">
                        <input type="hidden" name="tipo" value="evaluacion">
                        <input type="hidden" name="accion" value="agregar">
                        <input type="email" name="correo" placeholder="Correo del usuario" required>
                        <input type="date" name="fecha" required>
                        <input type="text" name="peso" placeholder="Peso" required>
                        <input type="text" name="edad" placeholder="Edad" required>
                        <input type="text" name="condicion" placeholder="Condicion" required>
                        <input type="text" name="pruebas" placeholder="Pruebas realizadas" required>
                        <select name="programa" required>
                            <option value="">Programa de entrenamiento</option>
                            <option value="PROGRAMA 01 H">PROGRAMA 01 H</option>
                            <option value="PROGRAMA 02 H">PROGRAMA 02 H</option>
                        </select>
                        <select name="objetivo" required>
                            <option value="">Objetivo de entrenamiento</option>
                            <option value="Bajar de peso">Bajar de peso</option>
                            <option value="Perder grasa">Perder grasa</option>
                            <option value="Ganar masa">Ganar masa</option>
                        </select>
                        <button type="submit">Registrar evaluacion</button>
                    </form>

                    <div class="tabla-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Fecha</th>
                                    <th>Usuario</th>
                                    <th>Correo</th>
                                    <th>Peso</th>
                                    <th>Edad</th>
                                    <th>Condicion</th>
                                    <th>Pruebas</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty evaluacionesAdmin}">
                                        <tr>
                                            <td colspan="9">No hay evaluaciones fisicas registradas.</td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="ev" items="${evaluacionesAdmin}">
                                            <tr>
                                                <td>${ev.id}</td>
                                                <td>${ev.fecha}</td>
                                                <td>${ev.usuario}</td>
                                                <td>${ev.correo}</td>
                                                <td>${ev.peso}</td>
                                                <td>${ev.edad}</td>
                                                <td>${ev.condicion}</td>
                                                <td>${ev.pruebas}</td>
                                                <td>
                                                    <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                        <input type="hidden" name="tipo" value="evaluacion">
                                                        <input type="hidden" name="accion" value="editar">
                                                        <input type="hidden" name="id" value="${ev.id}">
                                                        <input type="email" name="correo" value="${fn:escapeXml(ev.correo)}" required>
                                                        <input type="date" name="fecha" value="${ev.fecha}" required>
                                                        <input type="text" name="peso" value="${fn:escapeXml(ev.peso)}" required>
                                                        <input type="text" name="edad" value="${fn:escapeXml(ev.edad)}" required>
                                                        <input type="text" name="condicion" value="${fn:escapeXml(ev.condicion)}" required>
                                                        <input type="text" name="pruebas" value="${fn:escapeXml(ev.pruebas)}" required>
                                                        <button type="submit">Editar</button>
                                                    </form>
                                                    <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post" class="form-eliminar-inline">
                                                        <input type="hidden" name="tipo" value="evaluacion">
                                                        <input type="hidden" name="accion" value="eliminar">
                                                        <input type="hidden" name="id" value="${ev.id}">
                                                        <button class="btn-danger" type="button" onclick="var f=this.closest('form'); mostrarConfirm('¿Eliminar esta evaluación?', function(){ f.submit(); }, { titulo: 'Eliminar evaluación', icono: 'trash', textoAceptar: 'Sí' })">Eliminar</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>
        </div>

        <div id="modalEditar" class="modal-admin">
            <div class="modal-admin-content">
                <button type="button" class="modal-close" onclick="cerrarModalEditar()">×</button>

                <h2 id="modalTitulo">Editar</h2>

                <form action="<c:url value='/GestionCatalogosAdminServlet' />" method="post">
                    <input type="hidden" id="modalTipo" name="tipo">
                    <input type="hidden" id="modalAccion" name="accion">
                    <input type="hidden" id="modalId" name="id">

                    <div id="campoNombre">
                        <label>Nombre</label>
                        <input type="text" id="modalNombre" name="nombre" required>
                    </div>

                    <div id="campoDireccion">
                        <label>Dirección</label>
                        <input type="text" id="modalDireccion" name="direccion">
                    </div>

                    <div id="campoTipoProducto">
                        <label>Tipo producto</label>
                        <input type="text" id="modalTipoProducto" name="tipoProducto">
                    </div>

                    <div id="campoPrecio">
                        <label>Precio</label>
                        <input type="number" id="modalPrecio" name="precio">
                    </div>

                    <button type="submit">Guardar cambios</button>
                </form>
            </div>
        </div>
        <div id="modalUsuario" class="modal-admin">
            <div class="modal-admin-content">

                <button type="button"
                        class="modal-close"
                        onclick="cerrarModalUsuario()">
                    ×
                </button>

                <h2>Editar usuario</h2>

                <form action="<c:url value='/GestionUsuariosAdminServlet' />"
                      method="post">

                    <input type="hidden" name="accion" value="editar">
                    <input type="hidden" id="usuarioId" name="id">

                    <label>¿Qué quieres editar?</label>

                    <select id="campoEditar"
                            name="campo"
                            onchange="mostrarCampoEditar()"
                            required>

                        <option value="">Seleccione</option>
                        <option value="nombre">Nombre</option>
                        <option value="correo">Correo</option>
                        <option value="tipoDoc">Tipo documento</option>
                        <option value="membresia">Membresía</option>
                        <option value="rol">Rol</option>

                    </select>

                    <div id="contenedorNuevoValor" style="margin-top:15px;">

                        <input type="text"
                               id="nuevoValor"
                               name="valor"
                               placeholder="Nuevo valor"
                               required>

                        <select id="selectRol"
                                name="valorRol"
                                style="display:none;">

                            <c:forEach var="rol" items="${rolesAdmin}">
                                <option value="${rol.id}">
                                    <c:out value="${rol.descripcion}" />
                                </option>
                            </c:forEach>

                        </select>

                    </div>

                    <button type="submit">
                        Guardar cambios
                    </button>
                </form>

            </div>
        </div>

        <footer class="panel-footer" aria-label="Redes sociales de FitManager">
            <div class="panel-footer-brand"><span>FITMANAGER</span><small>Gestión deportiva inteligente</small></div>
            <p>© 2026 FitManager · Todos los derechos reservados</p>
            <nav class="panel-footer-social" aria-label="Redes sociales">
                <c:forEach var="red" items="${redesSociales}">
                    <a href="<c:out value='${red.url}' />" target="_blank" rel="noopener noreferrer" aria-label="Visitar <c:out value='${red.nombre}' />"><i class="fa-brands <c:out value='${red.icono}' />"></i></a>
                </c:forEach>
            </nav>
        </footer>

        <script>
            const CTX_PATH = '<c:url value="/" />'.replace(/\/$/, "");
            const ADMIN_ROLES = [
                <c:forEach var="rol" items="${rolesAdmin}" varStatus="st">
                {id: "${rol.id}", descripcion: "${fn:escapeXml(rol.descripcion)}"}${st.last ? '' : ','}
                </c:forEach>
            ];
            const ADMIN_MEMBRESIAS = [
                <c:forEach var="m" items="${membresias}" varStatus="st">
                {id: "${m.id}", descripcion: "${fn:escapeXml(m.tipo)}"}${st.last ? '' : ','}
                </c:forEach>
            ];
            const ADMIN_TIPOS_DOCUMENTO = [
                <c:forEach var="td" items="${tiposDocumento}" varStatus="st">
                {id: "${td.id}", descripcion: "${fn:escapeXml(td.descripcion)}"}${st.last ? '' : ','}
                </c:forEach>
            ];
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="<c:url value='/Vista/JS/script.js?v=20260904' />"></script>

    </body>
</html>
