<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:if test="${empty sessionScope.usuario or sessionScope.rol ne 'cliente'}">
    <c:redirect url="/Vista/Login.jsp" />
</c:if>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Comprar - FitManager</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/Vista/CSS/styles.css?v=20260812' />">
    <link href="https://fonts.googleapis.com/css2?family=Alfa+Slab+One&display=swap" rel="stylesheet">
</head>

<body class="register-body">

    <button class="btn-volver-icon" aria-label="Volver" onclick="window.location.href = '<c:url value='/PanelClienteServlet' />'">
    </button>

    <div class="container">
        <div class="row justify-content-center align-items-center">
            <div class="col-12 col-sm-10 col-md-8 col-lg-7 d-flex justify-content-center">

                <div class="container-login compra-card">
                    <h1 class="text-center mb-4">Finalizar compra</h1>

                    <c:if test="${param.error eq 'declinado'}">
                        <div class="alert-error">
                            El pago fue rechazado por Wompi. Verifica los datos o intenta con otro medio de pago.
                        </div>
                    </c:if>

                    <c:if test="${param.error eq 'pendiente'}">
                        <div class="alert-error">
                            Tu pago quedo pendiente de confirmacion (comun en PSE, Nequi o transferencias).
                            En cuanto Wompi confirme el pago tu compra se activara automaticamente; puedes
                            revisar el estado mas tarde en tu panel.
                        </div>
                    </c:if>

                    <div style="text-align:center; margin-bottom:18px; color:#ffffff;">
                        <h3 style="font-size:18px;">
                            <c:out value="${nombreItem}" />
                        </h3>

                        <p style="margin:4px 0;">
                            <fmt:formatNumber value="${precio}" type="currency" currencySymbol="$ " />
                        </p>

                        <span style="color:#bfbfbf; font-size:13px;">
                            <c:choose>
                                <c:when test="${tipoCompra eq 'membresia'}">
                                    Membresia por <c:out value="${duracionDias}" /> dias
                                </c:when>
                                <c:otherwise>
                                    Producto disponible
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <div class="pago-panel" style="margin-bottom:20px;">
                        <p style="color:#bfbfbf; font-size:13px; text-align:center;">
                            El pago se procesa de forma segura con
                            <strong style="color:#ffffff;">Wompi</strong>.
                            Podras pagar con tarjeta, PSE, Nequi o Bancolombia.
                        </p>
                    </div>

                    <form action="${wompiCheckoutUrl}" method="GET">
                        <input type="hidden" name="public-key" value="${wompiPublicKey}">
                        <input type="hidden" name="currency" value="${wompiMoneda}">
                        <input type="hidden" name="amount-in-cents" value="${wompiMontoCentavos}">
                        <input type="hidden" name="reference" value="${wompiReferencia}">
                        <input type="hidden" name="signature:integrity" value="${wompiFirma}">
                        <input type="hidden" name="redirect-url" value="${wompiRedirectUrl}">

                        <button type="submit" class="w-100">
                            Pagar con Wompi
                        </button>
                    </form>
                </div>

            </div>
        </div>
    </div>

    <footer class="footer">
        © 2026 FitManager - Todos los derechos reservados
    </footer>
</body>
</html>
