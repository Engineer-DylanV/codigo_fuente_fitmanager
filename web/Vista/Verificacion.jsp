<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Verifica tu cuenta - FitManager</title>
        <link rel="icon" type="image/png" href="<c:url value='/Vista/img/Icono.png' />">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<c:url value='/Vista/CSS/styles.css?v=20260812' />">
        <link href="https://fonts.googleapis.com/css2?family=Alfa+Slab+One&display=swap" rel="stylesheet">
    </head>

    <body class="login-body">

        <button id="btnVolver" class="btn-volver-icon" aria-label="Volver al inicio" onclick="window.location.href = '<c:url value='/Index.jsp' />'">
        </button>

        <div class="container-login container">
            <div class="logo-container">
                <img src="<c:url value='/Vista/img/Fitmanagerpro.png' />" class="logo animated-logo">
                <h1>FitManager</h1>
            </div>

            <h2 class="text-center mb-3" style="color:#f2f2f2; font-size:18px;">Verifica tu cuenta</h2>
            <p class="text-center" style="color:#b8b8b8; font-size:13px; margin-bottom:18px;">
                Enviamos un código de 6 dígitos a <strong><c:out value="${correo}" /></strong>.
                Ingrésalo para activar tu cuenta.
            </p>

            <c:if test="${not empty requestScope.info}">
                <div style="background:#22c55e; color:white; padding:10px 14px;
                            border-radius:10px; font-size:13px; margin-bottom:12px; text-align:center;">
                    <c:out value="${requestScope.info}" />
                </div>
            </c:if>

            <c:if test="${not empty requestScope.error}">
                <div style="background:#ff4d4d; color:white; padding:10px 14px;
                            border-radius:10px; font-size:13px; margin-bottom:12px; text-align:center;">
                    <c:out value="${requestScope.error}" />
                </div>
            </c:if>

            <form action="<c:url value='/VerificacionServlet' />" method="post">
                <input type="hidden" name="correo" value="${correo}">
                <input type="hidden" name="tipoCompra" value="${tipoCompra}">
                <input type="hidden" name="idCompra" value="${idCompra}">

                <input type="text"
                       name="codigo"
                       maxlength="6"
                       inputmode="numeric"
                       pattern="[0-9]{6}"
                       placeholder="Código de 6 dígitos"
                       style="text-align:center; letter-spacing:8px; font-size:20px;"
                       required
                       autofocus>

                <button type="submit">Verificar cuenta</button>
            </form>

            <form action="<c:url value='/VerificacionServlet' />" method="get" style="margin-top:10px;">
                <input type="hidden" name="correo" value="${correo}">
                <input type="hidden" name="accion" value="reenviar">
                <input type="hidden" name="tipoCompra" value="${tipoCompra}">
                <input type="hidden" name="idCompra" value="${idCompra}">
                <button type="submit" style="background:transparent; border:1px solid var(--border); color:#d8d8d8;">
                    Reenviar código
                </button>
            </form>

            <p class="mt-3"><a href="<c:url value='/login' />">Volver a inicio de sesión</a></p>
        </div>

        <footer class="footer">
            © 2026 FitManager - Todos los derechos reservados
        </footer>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
