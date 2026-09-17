<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:if test="${empty sessionScope.usuario or sessionScope.rol ne 'cliente'}">
    <c:redirect url="/login" />
</c:if>

<c:if test="${empty requestScope.clienteDatosCargados}">
    <c:redirect url="/PanelClienteServlet" />
</c:if>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Cliente - FitManager</title>
        <link rel="icon" type="image/png" href="<c:url value='/Vista/img/Icono.png' />">

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<c:url value='/Vista/CSS/styles.css?v=20260904' />">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
                    <img src="<c:url value='/Vista/img/Fitmanagerpro.png' />" class="logo">
                    <h2>FITMANAGER</h2>
                </div>

                <div class="theme-switch">
                    <span><i class="fa-solid fa-moon"></i> Tema</span>
                    <button type="button" id="themeToggleBtn" class="theme-switch-btn" aria-label="Cambiar entre modo oscuro y modo claro"></button>
                </div>

                <ul>
                    <c:choose>
                        <c:when test="${cuentaActiva}">
                            <li onclick="mostrarSeccion('inicio', this)"${param.compra eq 'productoOk' or param.seccion eq 'perfil' ? '' : ' class="activo"'}><i class="fa-solid fa-house"></i> Inicio</li>
                            <li onclick="mostrarSeccion('facturaCliente', this)"${param.compra eq 'productoOk' ? ' class="activo"' : ''}><i class="fa-solid fa-receipt"></i> Comprobante de pago</li>
                            <li onclick="mostrarSeccion('rutinas', this)"><i class="fa-solid fa-dumbbell"></i> Rutinas</li>
                            <li onclick="mostrarSeccion('registro', this)"><i class="fa-solid fa-clipboard-list"></i> Registro</li>
                            <li onclick="mostrarSeccion('metas', this)"><i class="fa-solid fa-bullseye"></i> Metas</li>
                            <li onclick="mostrarSeccion('evaluacionesCliente', this)"><i class="fa-solid fa-chart-line"></i> Evaluaciones</li>
                            </c:when>

                        <c:otherwise>
                            <li onclick="mostrarSeccion('membresiasCliente', this)"${param.seccion eq 'perfil' ? '' : ' class="activo"'}><i class="fa-solid fa-tag"></i> Membresías</li>
                            </c:otherwise>
                        </c:choose>

                    <li onclick="logout()"><i class="fa-solid fa-right-from-bracket"></i> Salir</li>
                </ul>
            </div>

            <div class="contenido">
                <header class="panel-profile-bar">
                    <div class="panel-profile-context">
                        <span>Panel de cliente</span>
                        <small>Gestiona tu entrenamiento desde un solo lugar</small>
                    </div>
                    <button type="button" class="panel-profile-trigger${param.seccion eq 'perfil' ? ' activo' : ''}" onclick="mostrarSeccion('miPerfil', this)" aria-label="Abrir mi perfil">
                        <span class="panel-profile-avatar" id="perfilAvatarResumen">
                            <img id="perfilFotoResumen" alt="" hidden>
                            <i id="perfilIconoResumen" class="fa-solid fa-user" aria-hidden="true"></i>
                            <span id="perfilInicialesResumen" hidden></span>
                        </span>
                        <span class="panel-profile-name"><strong><c:out value="${perfilCuenta.nombre}" /> <c:out value="${perfilCuenta.apellido}" /></strong><small>Mi perfil <i class="fa-solid fa-chevron-right" aria-hidden="true"></i></small></span>
                    </button>
                </header>

                <c:if test="${not cuentaActiva}">
                    <div id="membresiasCliente" class="seccion${param.seccion eq 'perfil' ? '' : ' activa'}">
                        <h1>Activa tu cuenta</h1>

                        <p style="color:#bfbfbf; font-size:13px; margin-bottom:18px;">
                            Para entrar al panel de cliente, compra una membresía.
                        </p>

                        <c:if test="${param.compra eq 'error'}">
                            <div class="alert-error">
                                No se pudo activar la membresía. Inténtalo de nuevo.
                            </div>
                        </c:if>

                        <div class="catalogo-grid">
                            <c:choose>
                                <c:when test="${empty membresias}">
                                    <div class="card">
                                        <p style="color:#9ca3af;">
                                            No se pudieron cargar las membresías.
                                        </p>
                                    </div>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="m" items="${membresias}">
                                        <div class="catalogo-card">
                                            <h3>
                                                <c:out value="${m.nombre}" />
                                            </h3>

                                            <p>
                                                <fmt:formatNumber value="${m.precio}" type="currency" currencySymbol="$ " />
                                            </p>

                                            <span>
                                                Duración: <c:out value="${m.duracion_dias}" /> días
                                            </span>

                                            <form action="<c:url value='/CompraServlet' />" method="get" class="catalogo-actions">
                                                <input type="hidden" name="tipo" value="membresia">
                                                <input type="hidden" name="id" value="${m.id}">
                                                <button type="submit" class="btn-main">Comprar</button>
                                            </form>
                                        </div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:if>

                <div id="miPerfil" class="seccion${param.seccion eq 'perfil' ? ' activa' : ''}">
                    <div class="profile-heading">
                        <span class="section-eyebrow">CUENTA PERSONAL</span>
                        <h1>Mi perfil</h1>
                        <p>Administra tu imagen y protege el acceso a tu cuenta.</p>
                    </div>

                    <c:if test="${not empty requestScope.perfilMensaje}">
                        <div class="${requestScope.perfilError ? 'alert-error' : 'alert-success'} profile-message" role="status">
                            <c:out value="${requestScope.perfilMensaje}" />
                        </div>
                    </c:if>

                    <div class="profile-grid" id="perfilCliente" data-nombre="<c:out value='${perfilCuenta.nombre} ${perfilCuenta.apellido}' />">
                        <section class="profile-card profile-card--identity">
                            <div class="profile-avatar" id="perfilAvatar" aria-label="Foto de perfil">
                                <img id="perfilFoto" alt="Foto de perfil" hidden>
                                <span id="perfilAvatarIcon" class="profile-avatar-icon" hidden></span>
                                <span id="perfilIniciales" class="profile-initials" hidden></span>
                            </div>
                            <h2><c:out value="${perfilCuenta.nombre}" /> <c:out value="${perfilCuenta.apellido}" /></h2>
                            <p class="profile-member-label">Miembro FitManager</p>

                            <form action="<c:url value='/PerfilClienteServlet' />" method="post" enctype="multipart/form-data" class="profile-photo-form">
                                <input type="hidden" name="accion" value="foto">
                                <label for="fotoPerfil" class="profile-upload-label"><i class="fa-solid fa-camera"></i> Subir foto</label>
                                <input id="fotoPerfil" name="fotoPerfil" type="file" accept="image/jpeg,image/png" required>
                                <button type="submit" class="btn-main">Guardar foto</button>
                            </form>

                            <div class="profile-avatar-picker" aria-label="Elegir avatar">
                                <span>O elige un avatar</span>
                                <div class="avatar-options">
                                    <c:forEach var="avatar" items="${avataresPerfil}">
                                        <button type="button" class="avatar-option" data-avatar="<c:out value='${avatar.codigo}' />" data-icon="<c:out value='${avatar.icono}' />" data-start="<c:out value='${avatar.colorInicio}' />" data-end="<c:out value='${avatar.colorFin}' />" style="--avatar-start:<c:out value='${avatar.colorInicio}' />;--avatar-end:<c:out value='${avatar.colorFin}' />;" aria-label="Avatar <c:out value='${avatar.nombre}' />"><i class="fa-solid <c:out value='${avatar.icono}' />"></i></button>
                                    </c:forEach>
                                </div>
                            </div>
                        </section>

                        <section class="profile-card profile-card--details">
                            <h2><i class="fa-solid fa-address-card"></i> Datos de la cuenta</h2>
                            <dl class="profile-details-list">
                                <div><dt><i class="fa-solid fa-envelope"></i> Correo electrónico</dt><dd><c:out value="${perfilCuenta.email}" /></dd></div>
                                <div><dt><i class="fa-solid fa-venus-mars"></i> Sexo</dt><dd><c:out value="${empty perfilCuenta.genero ? 'Sin registrar' : perfilCuenta.genero}" /></dd></div>
                            </dl>
                            <p class="profile-details-note"><i class="fa-solid fa-circle-info"></i> Estos datos se muestran como información de tu cuenta.</p>
                        </section>

                        <section class="profile-card profile-card--security">
                            <div class="profile-security-heading">
                                <div><span class="profile-card-kicker">SEGURIDAD</span><h2>Actualizar contraseña</h2></div>
                                <i class="fa-solid fa-shield-halved" aria-hidden="true"></i>
                            </div>
                            <p>Para cambiar la contraseña debes confirmar un código de seis dígitos enviado a tu correo. El código vence en 10 minutos.</p>

                            <form action="<c:url value='/PerfilClienteServlet' />" method="post" class="profile-code-request">
                                <input type="hidden" name="accion" value="solicitar-codigo">
                                <button type="submit" class="btn-main"><i class="fa-solid fa-paper-plane"></i> Enviar código a mi correo</button>
                            </form>

                            <form action="<c:url value='/PerfilClienteServlet' />" method="post" class="profile-password-form">
                                <input type="hidden" name="accion" value="cambiar-password">
                                <label for="codigoPerfil">Código de verificación</label>
                                <input id="codigoPerfil" name="codigo" type="text" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" placeholder="000000" autocomplete="one-time-code" required>
                                <label for="nuevaPasswordPerfil">Nueva contraseña</label>
                                <input id="nuevaPasswordPerfil" name="password" type="password" minlength="8" maxlength="100" placeholder="Mínimo 8 caracteres" autocomplete="new-password" required>
                                <label for="confirmPasswordPerfil">Confirmar nueva contraseña</label>
                                <input id="confirmPasswordPerfil" name="confirmPassword" type="password" minlength="8" maxlength="100" placeholder="Repite la nueva contraseña" autocomplete="new-password" required>
                                <button type="submit" class="btn-main">Actualizar contraseña</button>
                            </form>
                        </section>
                    </div>
                </div>

                <c:if test="${cuentaActiva}">

                    <div id="inicio" class="seccion${param.compra eq 'productoOk' or param.seccion eq 'perfil' ? '' : ' activa'}">
                        <h1>
                            Bienvenido, <c:out value="${sessionScope.usuario.nombre}" />
                        </h1>

                        <p style="color:#bfbfbf; font-size:13px; margin-bottom:4px; font-family:'DM Sans', sans-serif;">
                            Controla tu progreso y tus clases inscritas.
                        </p>

                        <c:if test="${false}">
                            <div class="catalogo-card" style="max-width:520px; margin:18px 0;">
                                <h3>Comprobante de pago</h3>

                                <p style="margin:4px 0;">
                                    NÂ° <c:out value="${sessionScope.facturaResumen.numero}" />
                                </p>

                                <span>
                                    Fecha: <c:out value="${sessionScope.facturaResumen.fecha}" />
                                </span>
                                <span>
                                    Concepto: <c:out value="${sessionScope.facturaResumen.item}" />
                                </span>
                                <span>
                                    Cantidad: <c:out value="${sessionScope.facturaResumen.cantidad}" />
                                </span>
                                <span>
                                    Metodo: <c:out value="${sessionScope.facturaResumen.metodoPago}" />
                                </span>

                                <p style="margin-top:10px;">
                                    Total:
                                    <fmt:formatNumber value="${sessionScope.facturaResumen.valor}" type="currency" currencySymbol="$ " />
                                </p>
                            </div>
                        </c:if>

                        <c:if test="${not empty param.factura}">
                            <div id="alert-pago-confirmado" class="alert-success" style="margin:18px 0; transition: opacity 0.5s ease;">
                                Pago confirmado. Puedes abrir y descargar tu comprobante de pago desde el apartado Comprobante de pago.
                                <div style="margin-top:12px;">
                                    <button type="button"
                                            class="btn-main"
                                            onclick="abrirFacturaCliente(${param.factura})">
                                        Abrir resumen del comprobante
                                    </button>
                                </div>
                            </div>
                        </c:if>

                        <div class="dashboard-inicio">
                            <div class="grafica-card">
                                <div class="grafica-asistencia-wrap">
                                    <canvas id="grafica"></canvas>
                                </div>

                                <div id="racha-badge" class="racha-badge" style="display:none;">
                                    <i class="fa-solid fa-fire"></i>
                                    <span id="racha-numero">0</span>
                                    <span class="racha-label">días seguidos</span>
                                </div>

                                <p id="texto-constancia" style="font-size:13px;"></p>

                                <button onclick="asistir()" class="btn-asistencia">
                                    ASISTÍ HOY
                                </button>
                            </div>

                            <div class="clases-columna">
                                <div class="clases-card">
                                    <h2>Clases disponibles</h2>

                                    <div id="lista-clases-inicio">
                                        <p style="color:#9ca3af;font-size:13px;">
                                            Cargando...
                                        </p>
                                    </div>
                                </div>

                                <div class="clases-card">
                                    <h2>Mis clases</h2>

                                    <table class="tabla-mis-clases">
                                        <thead>
                                            <tr>
                                                <th>Clase</th>
                                                <th>Acción</th>
                                            </tr>
                                        </thead>

                                        <tbody id="tabla-mis-clases">
                                            <tr>
                                                <td colspan="2">Cargando...</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="facturaCliente" class="seccion${param.compra eq 'productoOk' ? ' activa' : ''}">
                        <h1>Comprobante de pago</h1>

                        <c:if test="${param.compra eq 'productoOk'}">
                            <div class="alert-success">
                                ¡Pago confirmado con Wompi! Tu compra fue registrada correctamente.
                            </div>
                        </c:if>

                        <c:choose>
                            <c:when test="${empty ultimaFactura}">
                                <div class="card">
                                    <p style="color:#9ca3af;">
                                        Todavia no tienes comprobantes de pago registrados.
                                    </p>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <%-- Última factura destacada --%>
                                <div class="catalogo-card" style="max-width:520px; margin-bottom:24px;">
                                    <h3>Último comprobante de pago</h3>

                                    <p style="margin:4px 0;">
                                        N. <c:out value="${ultimaFactura.numero}" />
                                    </p>

                                    <p style="margin:4px 0; color:#f2f2f2; font-weight:bold;">
                                        <c:out value="${ultimaFactura.concepto}" />
                                    </p>

                                    <span>
                                        Fecha: <c:out value="${ultimaFactura.fecha}" />
                                    </span>
                                    <span>
                                        Total: $
                                        <c:choose>
                                            <c:when test="${not empty ultimaFactura.valorTotal}">
                                                <fmt:formatNumber value="${ultimaFactura.valorTotal}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                            </c:when>
                                            <c:otherwise>0.00</c:otherwise>
                                        </c:choose>
                                    </span>

                                    <div class="catalogo-actions">
                                        <button type="button"
                                                class="btn-main"
                                                onclick="abrirFacturaCliente(${ultimaFactura.id})">
                                            Abrir resumen
                                        </button>
                                    </div>
                                </div>

                                <%-- Historial completo de facturas --%>
                                <c:if test="${not empty todasFacturas}">
                                    <h3 style="margin-bottom:12px;">Historial de comprobantes de pago</h3>
                                    <div class="tabla-container">
                                        <table class="table">
                                            <thead>
                                                <tr>
                                                    <th>N° Comprobante</th>
                                                    <th>Concepto</th>
                                                    <th>Fecha</th>
                                                    <th>Total</th>
                                                    <th>Acción</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="fact" items="${todasFacturas}">
                                                    <tr>
                                                        <td><c:out value="${fact.numero}" /></td>
                                                        <td><c:out value="${fact.concepto}" /></td>
                                                        <td><c:out value="${fact.fecha}" /></td>
                                                        <td>
                                                            $
                                                            <c:choose>
                                                                <c:when test="${not empty fact.valorTotal}">
                                                                    <fmt:formatNumber value="${fact.valorTotal}" type="number" minFractionDigits="2" maxFractionDigits="2" />
                                                                </c:when>
                                                                <c:otherwise>0.00</c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <button type="button"
                                                                    class="btn-main"
                                                                    onclick="abrirFacturaCliente(${fact.id})">
                                                                Ver
                                                            </button>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div id="rutinas" class="seccion">
                        <h1>Rutinas</h1>

                        <p>Estas rutinas se desbloquean cuando tu administrador registra tu evaluación física y selecciona tu objetivo.</p>

                        <div class="tabla-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Programa</th>
                                        <th>Rutina</th>
                                        <th>Descripción</th>
                                        <th>Objetivo</th>
                                        <th>Material</th>
                                    </tr>
                                </thead>

                                <tbody id="tabla-rutinas">
                                    <tr>
                                        <td colspan="5">Aún no tienes rutinas asignadas. Contacta a tu entrenador.</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div id="registro" class="seccion">
                        <h1>Registro</h1>

                        <form onsubmit="guardarRegistro(event)">
                            <input type="text" id="ejercicio" placeholder="Ejercicio" required>
                            <input type="number" id="repeticiones" placeholder="Reps" required>
                            <input type="number" id="peso" placeholder="Peso (kg)" required>
                            <button type="submit">Guardar</button>
                        </form>

                        <div class="tabla-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Icono</th>
                                        <th>Ejercicio</th>
                                        <th>Repeticiones</th>
                                        <th>Peso</th>
                                        <th>Fecha</th>
                                    </tr>
                                </thead>

                                <tbody id="historial">
                                    <tr>
                                        <td colspan="5">No hay registros todavía</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div id="metas" class="seccion">
                        <h1>Metas</h1>

                        <form onsubmit="crearMetaCliente(event)">
                            <input type="text" id="ejercicioMetaCliente" placeholder="Ejercicio" required>
                            <input type="number" id="valorMetaCliente" placeholder="Meta (reps)" min="1" required>
                            <button type="submit">Agregar meta</button>
                        </form>

                        <div class="metas-resumen">
                            <div class="metas-grafica">
                                <canvas id="grafica-metas"></canvas>
                            </div>

                            <p id="texto-metas"></p>
                        </div>

                        <div id="metas-lista" class="cards"></div>
                    </div>

                    <div id="evaluacionesCliente" class="seccion">
                        <h1>Evaluaciones fisicas</h1>

                        <div class="tabla-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Fecha</th>
                                        <th>Peso</th>
                                        <th>Edad</th>
                                        <th>Condicion</th>
                                        <th>Pruebas</th>
                                    </tr>
                                </thead>
                                <tbody id="tabla-evaluaciones-cliente">
                                    <tr>
                                        <td colspan="5">Cargando evaluaciones...</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                </c:if>

            </div>
        </div>

        <audio id="sonidoMeta"
               src="https://assets.mixkit.co/sfx/preview/mixkit-achievement-bell-600.mp3"></audio>

        <footer class="panel-footer" aria-label="Redes sociales de FitManager">
            <div class="panel-footer-brand"><span>FITMANAGER</span><small>Entrena. Registra. Progresa.</small></div>
            <p>© 2026 FitManager · Todos los derechos reservados</p>
            <nav class="panel-footer-social" aria-label="Redes sociales">
                <c:forEach var="red" items="${redesSociales}">
                    <a href="<c:out value='${red.url}' />" target="_blank" rel="noopener noreferrer" aria-label="Visitar <c:out value='${red.nombre}' />"><i class="fa-brands <c:out value='${red.icono}' />"></i></a>
                </c:forEach>
            </nav>
        </footer>

        <script>
            const CTX_PATH = '<c:url value="/" />'.replace(/\/$/, "");
            const ID_USUARIO = ${empty sessionScope.idUsuario ? 0 : sessionScope.idUsuario};
            let ASISTENCIAS_COUNT = 0;
            let RACHA_ACTUAL = 0;
            let METAS_PORCENTAJE = 0;
            let METAS_CUMPLIDAS = 0;
            let METAS_TOTAL = 0;
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="<c:url value='/Vista/JS/script.js?v=20260904' />"></script>

        <c:if test="${not empty param.factura}">
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    abrirFacturaCliente(${param.factura});

                    // Ocultar alert después de 3 segundos (solo una vez)
                    var alerta = document.getElementById("alert-pago-confirmado");
                    if (alerta) {
                        setTimeout(function () {
                            alerta.style.opacity = "0";
                            setTimeout(function () {
                                alerta.style.display = "none";
                            }, 500);
                        }, 3000);
                    }
                });
            </script>
        </c:if>
    </body>
</html>
