<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:if test="${empty sessionScope.usuario or sessionScope.rol ne 'cliente'}">
    <c:redirect url="/Vista/Login.jsp" />
</c:if>

<c:if test="${empty requestScope.clienteDatosCargados}">
    <c:redirect url="/PanelClienteServlet" />
</c:if>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Cliente - FitManager</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<c:url value='/Vista/CSS/styles.css?v=20260812' />">
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
                            <li onclick="mostrarSeccion('inicio', this)"${param.compra eq 'productoOk' ? '' : ' class="activo"'}>Inicio</li>
                            <li onclick="mostrarSeccion('facturaCliente', this)"${param.compra eq 'productoOk' ? ' class="activo"' : ''}>Comprobante de pago</li>
                            <li onclick="mostrarSeccion('rutinas', this)">Rutinas</li>
                            <li onclick="mostrarSeccion('registro', this)">Registro</li>
                            <li onclick="mostrarSeccion('metas', this)">Metas</li>
                            <li onclick="mostrarSeccion('evaluacionesCliente', this)">Evaluaciones</li>
                            </c:when>

                        <c:otherwise>
                            <li onclick="mostrarSeccion('membresiasCliente', this)" class="activo">Membresías</li>
                            </c:otherwise>
                        </c:choose>

                    <li onclick="logout()">Salir</li>
                </ul>
            </div>

            <div class="contenido">

                <c:if test="${not cuentaActiva}">
                    <div id="membresiasCliente" class="seccion activa">
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

                <c:if test="${cuentaActiva}">

                    <div id="inicio" class="seccion${param.compra eq 'productoOk' ? '' : ' activa'}">
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

        <footer class="footer">
            © 2026 FitManager - Todos los derechos reservados
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
        <script src="<c:url value='/Vista/JS/script.js?v=20260808' />"></script>

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
