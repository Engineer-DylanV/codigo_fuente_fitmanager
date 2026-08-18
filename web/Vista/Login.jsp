<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FitManager - Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/Vista/CSS/styles.css?v=20260812' />">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer">
    <link href="https://fonts.googleapis.com/css2?family=Alfa+Slab+One&display=swap" rel="stylesheet">
</head>
<body class="login-body">
    <button id="btnVolver" class="btn-volver-icon" aria-label="Volver al inicio"
        onclick="window.location.href='<c:url value='/Index.jsp' />'">
    </button>
    <div class="container-login container">
        <div class="logo-container">
            <img src="<c:url value='/Vista/img/Fitmanagerpro.png' />" class="logo animated-logo">
            <h1>FitManager</h1>
        </div>
        <c:if test="${param.registrado eq 'true'}">
            <div style="background:#22c55e; color:white; padding:10px 14px;
                        border-radius:10px; font-size:13px; margin-bottom:12px; text-align:center;">
                OK Registro exitoso. Ahora puedes iniciar sesión.
            </div>
        </c:if>
        <c:if test="${param.passwordActualizada eq 'true'}">
            <div style="background:#22c55e; color:white; padding:10px 14px;
                        border-radius:10px; font-size:13px; margin-bottom:12px; text-align:center;">
                OK Contraseña actualizada. Ya puedes iniciar sesión con tu nueva contraseña.
            </div>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <div style="background:#ff4d4d; color:white; padding:10px 14px;
                        border-radius:10px; font-size:13px; margin-bottom:12px; text-align:center;">
                Aviso: <c:out value="${requestScope.error}" />
            </div>
        </c:if>
        <form action="<c:url value='/InicioSesionServlet' />" method="POST">
            <input type="hidden" name="tipo" value="${param.tipo}">
            <input type="hidden" name="id" value="${param.id}">
            <input type="email"
                   name="email"
                   placeholder="Correo"
                   required>
            <div class="password-box">
                <input type="password"
                       id="password"
                       name="password"
                       placeholder="Contraseña"
                       autocomplete="current-password"
                       required>
                <span class="toggle-password" onclick="togglePassword('password', this)" aria-label="Mostrar contraseña"><i class="fa-solid fa-eye"></i></span>
            </div>
            <button type="submit">Iniciar sesión</button>
        </form>
        <p><a href="<c:url value='/RecuperarPasswordServlet' />">¿Olvidaste tu contraseña?</a></p>
        <p>¿No tienes cuenta?</p>
        <a href="<c:url value='/Vista/Register.jsp' />">Registrarse</a>
    </div>
    <footer class="footer">
        © 2026 FitManager - Todos los derechos reservados
    </footer>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<c:url value='/Vista/JS/script.js?v=20260808' />"></script>
</body>
</html>


