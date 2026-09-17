<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Recuperar contraseña - FitManager</title>
        <link rel="icon" type="image/png" href="<c:url value='/Vista/img/Icono.png' />">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<c:url value='/Vista/CSS/styles.css?v=20260812' />">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer">
        <link href="https://fonts.googleapis.com/css2?family=Alfa+Slab+One&display=swap" rel="stylesheet">
    </head>

    <body class="login-body">

        <button id="btnVolver" class="btn-volver-icon" aria-label="Volver" onclick="window.location.href = '<c:url value='/login' />'">
        </button>

        <div class="container-login container">
            <div class="logo-container">
                <img src="<c:url value='/Vista/img/Fitmanagerpro.png' />" class="logo animated-logo">
                <h1>FitManager</h1>
            </div>

            <h2 class="text-center mb-3" style="color:#f2f2f2; font-size:18px;">Recuperar contraseña</h2>

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

            <%-- Paso 1: pedir el correo --%>
            <c:if test="${paso == 'correo'}">
                <p class="text-center" style="color:#b8b8b8; font-size:13px; margin-bottom:18px;">
                    Ingresa el correo de tu cuenta y te enviaremos un código para restablecer tu contraseña.
                </p>
                <form action="<c:url value='/RecuperarPasswordServlet' />" method="post">
                    <input type="hidden" name="paso" value="correo">
                    <input type="email"
                           name="correo"
                           value="${correo}"
                           placeholder="Correo"
                           required
                           autofocus>
                    <button type="submit">Enviar código</button>
                </form>
            </c:if>

            <%-- Paso 2: ingresar el código --%>
            <c:if test="${paso == 'codigo'}">
                <p class="text-center" style="color:#b8b8b8; font-size:13px; margin-bottom:18px;">
                    Ingresa el código de 6 dígitos que enviamos a <strong><c:out value="${correo}" /></strong>.
                </p>
                <form action="<c:url value='/RecuperarPasswordServlet' />" method="post">
                    <input type="hidden" name="paso" value="codigo">
                    <input type="hidden" name="correo" value="${correo}">
                    <input type="text"
                           name="codigo"
                           maxlength="6"
                           inputmode="numeric"
                           pattern="[0-9]{6}"
                           placeholder="Código de 6 dígitos"
                           style="text-align:center; letter-spacing:8px; font-size:20px;"
                           required
                           autofocus>
                    <button type="submit">Verificar código</button>
                </form>

                <form action="<c:url value='/RecuperarPasswordServlet' />" method="post" style="margin-top:10px;">
                    <input type="hidden" name="paso" value="correo">
                    <input type="hidden" name="correo" value="${correo}">
                    <button type="submit" style="background:transparent; border:1px solid var(--border); color:#d8d8d8;">
                        Reenviar código
                    </button>
                </form>
            </c:if>

            <%-- Paso 3: nueva contraseña --%>
            <c:if test="${paso == 'nueva'}">
                <p class="text-center" style="color:#b8b8b8; font-size:13px; margin-bottom:18px;">
                    Código verificado. Ingresa tu nueva contraseña.
                </p>
                <form action="<c:url value='/RecuperarPasswordServlet' />" method="post">
                    <input type="hidden" name="paso" value="nueva">
                    <input type="hidden" name="correo" value="${correo}">
                    <input type="hidden" name="codigo" value="${codigo}">
                    <div class="password-box">
                        <input type="password"
                               id="password"
                               name="password"
                               placeholder="Nueva contraseña"
                               required
                               autofocus>
                        <span class="toggle-password" onclick="togglePassword('password', this)" aria-label="Mostrar contraseña"><i class="fa-solid fa-eye"></i></span>
                    </div>
                    <div class="password-box">
                        <input type="password"
                               id="confirmPassword"
                               name="confirmPassword"
                               placeholder="Confirmar contraseña"
                               required>
                        <span class="toggle-password" onclick="togglePassword('confirmPassword', this)" aria-label="Mostrar contraseña"><i class="fa-solid fa-eye"></i></span>
                    </div>
                    <button type="submit">Guardar nueva contraseña</button>
                </form>
            </c:if>

            <p class="mt-3"><a href="<c:url value='/login' />">Volver a inicio de sesión</a></p>
        </div>

        <footer class="footer">
            © 2026 FitManager - Todos los derechos reservados
        </footer>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="<c:url value='/Vista/JS/script.js?v=20260808' />"></script>
    </body>
</html>
