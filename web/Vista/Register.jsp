<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${empty requestScope.registroDatosCargados}">
    <c:redirect url="/FormularioRegistroServlet" />
</c:if>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Registro - FitManager</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<c:url value='/Vista/CSS/styles.css?v=20260812' />">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer">
        <link href="https://fonts.googleapis.com/css2?family=Alfa+Slab+One&display=swap" rel="stylesheet">
    </head>

    <body class="register-body">

        <button id="btnVolver" class="btn-volver-icon" aria-label="Volver al inicio" onclick="window.location.href = '<c:url value='/Index.jsp' />'">
        </button>

        <div class="container">
            <div class="row justify-content-center align-items-center">
                <div class="col-12 col-sm-10 col-md-8 col-lg-5 d-flex justify-content-center">
                    <div class="container-login">
                        <h1 class="text-center mb-4">Registro</h1>

                        <c:if test="${not empty requestScope.error}">
                            <div class="alert-error">
                                <c:out value="${requestScope.error}" />
                            </div>
                        </c:if>

                        <form id="formRegistro" action="<c:url value='/RegistroUsuarioServlet' />" method="post" novalidate>

                            <input type="hidden" name="tipoCompra" value="${tipoCompra}">
                            <input type="hidden" name="idCompra" value="${idCompra}">

                            <div class="row g-3">

                                <div class="col-12">
                                    <input type="text" id="nombres" name="nombres" placeholder="Nombre completo" maxlength="80" autocomplete="name" required>
                                </div>

                                <div class="col-12">
                                    <select id="tipoDoc" name="tipoDoc" class="select-pro" required>
                                        <option value="">Tipo de documento</option>

                                        <c:forEach var="td" items="${tiposDocumento}">
                                            <option value="${td.id}">
                                                <c:out value="${td.descripcion}" />
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="col-12">
                                    <input type="text" id="documento" name="documento" placeholder="Documento" inputmode="numeric" maxlength="10" required>
                                </div>

                                <div class="col-12">
                                    <label for="fechaNacimiento" class="fecha-label">Fecha de nacimiento</label>
                                    <input type="date" id="fechaNacimiento" name="fechaNacimiento" class="fecha-input" max="2011-12-31" required>
                                </div>

                                <div class="col-12">
                                    <select id="genero" name="genero" class="select-pro" required>
                                        <option value="">Género</option>
                                        <option value="M">Hombre</option>
                                        <option value="F">Mujer</option>
                                    </select>
                                </div>

                                <div class="col-12">
                                    <input type="email" id="correo" name="correo" placeholder="Correo" required>
                                </div>

                                <div class="col-12">
                                    <input type="tel" id="telefono" name="telefono" placeholder="Teléfono" inputmode="numeric" maxlength="10" required>
                                </div>

                                <div class="col-12">
                                    <div class="password-box">
                                        <input type="password" id="password" name="password" placeholder="Contraseña" autocomplete="new-password" required>
                                        <span class="toggle-password" onclick="togglePassword('password', this)" aria-label="Mostrar contraseña"><i class="fa-solid fa-eye"></i></span>
                                    </div>
                                </div>

                                <div class="col-12">
                                    <div class="password-box">
                                        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirmar contraseña" autocomplete="new-password" required>
                                        <span class="toggle-password" onclick="togglePassword('confirmPassword', this)" aria-label="Mostrar contraseña"><i class="fa-solid fa-eye"></i></span>
                                    </div>
                                </div>

                                <div class="col-12">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" id="aceptaPolitica" name="aceptaPolitica" required>
                                        <label class="form-check-label" for="aceptaPolitica">
                                            Acepto la
                                            <a href="#" style="color:#ff8a8a;"
                                               onclick="event.preventDefault(); document.getElementById('modalTratamiento').style.display='flex';">
                                                política de tratamiento de datos
                                            </a>
                                            (Ley 1581 de 2012)
                                        </label>
                                    </div>
                                </div>

                                <div class="col-12">
                                    <button type="submit" class="w-100">Registrarse</button>
                                </div>

                            </div>
                        </form>

                        <p style="margin-top:14px; font-size:13px; color:#9ca3af; text-align:center;">
                            ¿Ya tienes cuenta?
                            <a href="<c:url value='/Vista/Login.jsp' />" style="color:#ff8a8a;">Inicia sesión</a>
                        </p>

                    </div>
                </div>
            </div>
        </div>

        <div class="footer">
            © 2026 FitManager - Todos los derechos reservados
        </div>

        <!-- ===== MODAL POLÍTICA DE TRATAMIENTO DE DATOS – LEY 1581 DE 2012 ===== -->
        <div id="modalTratamiento"
             style="display:none; position:fixed; inset:0; background:rgba(0,0,0,.7);
                    z-index:9999; justify-content:center; align-items:center; padding:16px;">
            <div style="background:#1a1a2e; border:1px solid #ff4d4d; border-radius:12px;
                        max-width:620px; width:100%; max-height:85vh; overflow-y:auto; padding:32px 28px;
                        color:#e5e7eb; font-size:14px; line-height:1.6;">

                <h3 style="color:#ff8a8a; margin-bottom:18px; font-size:18px;">
                    Política de Tratamiento de Datos Personales
                </h3>
                <p style="margin-bottom:10px; font-size:12px; color:#9ca3af;">
                    En cumplimiento de la <strong style="color:#ff8a8a;">Ley 1581 de 2012</strong>
                    y el Decreto 1377 de 2013, FitManager informa su política de tratamiento de datos:
                </p>

                <h4 style="color:#ff8a8a; margin-bottom:6px;">1. Responsable del Tratamiento</h4>
                <p style="margin-bottom:16px;">
                    <strong>FitManager</strong> actúa como responsable del tratamiento de sus datos
                    personales. Para consultas puede escribir a:
                    <a href="mailto:fitmanagercol@gmail.com" style="color:#ff8a8a;">
                        fitmanagercol@gmail.com 
                    </a>.
                </p>

                <h4 style="color:#ff8a8a; margin-bottom:6px;">2. Datos Recopilados</h4>
                <p style="margin-bottom:16px;">
                    Se recopilan: nombre completo, tipo y número de documento, fecha de nacimiento,
                    correo electrónico y datos de pago asociados a la compra de membresías y productos.
                </p>

                <h4 style="color:#ff8a8a; margin-bottom:6px;">3. Finalidades del Tratamiento</h4>
                <p style="margin-bottom:16px;">
                    Sus datos se utilizan para: gestionar su cuenta y membresía, procesar pagos y
                    emitir facturas, registrar su actividad física, enviar comunicaciones relacionadas
                    con el servicio y cumplir obligaciones legales y contables.
                </p>

                <h4 style="color:#ff8a8a; margin-bottom:6px;">4. Derechos del Titular</h4>
                <p style="margin-bottom:16px;">
                    De conformidad con el Art. 8 de la Ley 1581 de 2012, usted tiene derecho a:
                    conocer, actualizar, rectificar y suprimir sus datos; revocar la autorización;
                    acceder gratuitamente a ellos y presentar quejas ante la
                    Superintendencia de Industria y Comercio.
                </p>

                <h4 style="color:#ff8a8a; margin-bottom:6px;">5. Seguridad de la Información</h4>
                <p style="margin-bottom:16px;">
                    FitManager adopta medidas técnicas y administrativas para proteger sus datos
                    contra acceso no autorizado, pérdida o uso fraudulento, en cumplimiento del
                    <strong style="color:#ff8a8a;">principio de seguridad</strong>
                    (Art. 4, Ley 1581 de 2012).
                </p>

                <h4 style="color:#ff8a8a; margin-bottom:6px;">6. Transferencia y Transmisión</h4>
                <p style="margin-bottom:16px;">
                    Sus datos <strong>no serán vendidos ni cedidos</strong> a terceros sin su
                    autorización previa, salvo obligaciones legales o encargados del tratamiento que
                    actúen bajo nuestra instrucción.
                </p>

                <h4 style="color:#ff8a8a; margin-bottom:6px;">7. Vigencia</h4>
                <p style="margin-bottom:20px;">
                    Esta política rige desde su publicación y permanecerá vigente mientras exista la
                    relación contractual. Los datos se conservarán el tiempo necesario para cumplir
                    las finalidades descritas y las obligaciones legales aplicables.
                </p>

                <h4 style="color:#ff8a8a; margin-bottom:6px;">8. Autoridad de Control</h4>
                <p style="margin-bottom:24px;">
                    La entidad encargada de vigilar el cumplimiento es la
                    <strong style="color:#ff8a8a;">Superintendencia de Industria y Comercio (SIC)</strong>.
                    Más información en
                    <a href="https://www.sic.gov.co" target="_blank" style="color:#ff8a8a;">
                        www.sic.gov.co
                    </a>.
                </p>

                <div style="text-align:center;">
                    <button onclick="
                        document.getElementById('aceptaPolitica').checked = true;
                        document.getElementById('modalTratamiento').style.display='none';"
                            style="background:#ff4d4d; color:#fff; border:none; border-radius:8px;
                                   padding:10px 32px; font-size:15px; font-weight:600; cursor:pointer;
                                   margin-right:10px;">
                        Acepto
                    </button>
                    <button onclick="document.getElementById('modalTratamiento').style.display='none'"
                            style="background:transparent; color:#9ca3af; border:1px solid #555;
                                   border-radius:8px; padding:10px 24px; font-size:15px; cursor:pointer;">
                        Cerrar
                    </button>
                </div>
            </div>
        </div>
        <!-- ===== FIN MODAL ===== -->


        <!-- ===== MODAL DE VALIDACIÓN ===== -->
        <div id="validacionModal" style="display:none; position:fixed; inset:0;
             background:rgba(0,0,0,0.78); z-index:999999; align-items:center;
             justify-content:center; padding:20px; backdrop-filter:blur(5px);">
            <div style="background:var(--surface,#121212); border-radius:16px;
                        padding:32px 28px 24px; max-width:400px; width:100%;
                        box-shadow:0 0 24px rgba(201,47,53,0.3), 0 20px 50px rgba(0,0,0,0.7);
                        border:1px solid rgba(201,47,53,0.25); text-align:center;
                        animation:slideUpBox .2s ease;">
                <span style="font-size:42px; display:block; margin-bottom:14px;"><i class="fa-solid fa-triangle-exclamation"></i></span>
                <div style="font-size:17px; font-weight:700; color:#f1f5f9; margin-bottom:10px;">
                    Campos incompletos
                </div>
                <ul id="validacionLista"
                    style="list-style:none; padding:0; margin:0 0 24px;
                           text-align:left; display:flex; flex-direction:column; gap:6px;">
                </ul>
                <button onclick="cerrarValidacionModal()"
                        style="background:#c92f35; color:#fff; border:none; border-radius:8px;
                               padding:11px 36px; font-size:14px; font-weight:700;
                               cursor:pointer; transition:opacity .15s;">
                    Entendido
                </button>
            </div>
        </div>
        <!-- ===== FIN MODAL VALIDACIÓN ===== -->

        <style>
            .campo-error {
                border: 1px solid #c92f35 !important;
                box-shadow: 0 0 0 2px rgba(201,47,53,.25) !important;
                animation: shakeField .35s ease;
            }
            @keyframes shakeField {
                0%,100% { transform: translateX(0); }
                20%      { transform: translateX(-5px); }
                60%      { transform: translateX(5px); }
            }
        </style>

        <script>
        (function () {
            /* ---- helpers ---- */
            function mostrarValidacionModal(errores) {
                var lista = document.getElementById("validacionLista");
                lista.innerHTML = errores.map(function(e) {
                    return '<li style="background:rgba(201,47,53,.1); border-radius:8px;' +
                           'padding:8px 12px; font-size:13px; color:#f87171;' +
                           'font-family:\'DM Sans\', sans-serif;">' +
                           '<span style="margin-right:6px;">&#x2715;</span>' + e + '</li>';
                }).join("");
                var modal = document.getElementById("validacionModal");
                modal.style.display = "flex";
            }

            window.cerrarValidacionModal = function() {
                document.getElementById("validacionModal").style.display = "none";
            };

            /* cerrar con Escape o clic en fondo */
            document.addEventListener("keydown", function(e) {
                if (e.key === "Escape") window.cerrarValidacionModal();
            });
            document.getElementById("validacionModal").addEventListener("click", function(e) {
                if (e.target === this) window.cerrarValidacionModal();
            });

            function limpiarErrores() {
                document.querySelectorAll(".campo-error").forEach(function(el) {
                    el.classList.remove("campo-error");
                });
            }

            function marcarError(el) {
                el.classList.remove("campo-error");
                void el.offsetWidth; /* reflow para reiniciar animación */
                el.classList.add("campo-error");
                el.addEventListener("input",  function() { el.classList.remove("campo-error"); }, { once: true });
                el.addEventListener("change", function() { el.classList.remove("campo-error"); }, { once: true });
            }

            /* ---- filtrado en vivo mientras se escribe ---- */
            var nombresInput   = document.getElementById("nombres");
            var documentoInput = document.getElementById("documento");
            var telefonoInput  = document.getElementById("telefono");

            // Nombre completo: solo letras (con tildes/ñ) y espacios.
            if (nombresInput) {
                nombresInput.addEventListener("input", function () {
                    this.value = this.value.replace(/[^A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]/g, "");
                });
            }

            // Documento: solo números, máximo 10 dígitos.
            if (documentoInput) {
                documentoInput.addEventListener("input", function () {
                    this.value = this.value.replace(/\D/g, "").slice(0, 10);
                });
            }

            // Teléfono: solo números, máximo 10 dígitos.
            if (telefonoInput) {
                telefonoInput.addEventListener("input", function () {
                    this.value = this.value.replace(/\D/g, "").slice(0, 10);
                });
            }

            /* ---- validación principal ---- */
            document.getElementById("formRegistro").addEventListener("submit", function(e) {
                e.preventDefault();
                limpiarErrores();

                var errores  = [];
                var nombres  = document.getElementById("nombres");
                var tipoDoc  = document.getElementById("tipoDoc");
                var documento= document.getElementById("documento");
                var fecha    = document.getElementById("fechaNacimiento");
                var genero   = document.getElementById("genero");
                var correo   = document.getElementById("correo");
                var telefono = document.getElementById("telefono");
                var pwd      = document.getElementById("password");
                var cpwd     = document.getElementById("confirmPassword");
                var politica = document.getElementById("aceptaPolitica");

                if (!nombres.value.trim()) {
                    errores.push("El nombre completo es obligatorio.");
                    marcarError(nombres);
                } else if (!/^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ\s]+$/.test(nombres.value.trim())) {
                    errores.push("El nombre completo solo puede contener letras.");
                    marcarError(nombres);
                }
                if (!tipoDoc.value) {
                    errores.push("Selecciona el tipo de documento.");
                    marcarError(tipoDoc);
                }
                if (!documento.value.trim()) {
                    errores.push("El número de documento es obligatorio.");
                    marcarError(documento);
                } else if (!/^\d+$/.test(documento.value.trim())) {
                    errores.push("El documento solo puede contener números.");
                    marcarError(documento);
                } else if (documento.value.trim().length > 10) {
                    errores.push("El documento no puede tener más de 10 dígitos.");
                    marcarError(documento);
                }
                if (!fecha.value) {
                    errores.push("La fecha de nacimiento es obligatoria.");
                    marcarError(fecha);
                } else if (new Date(fecha.value + "T00:00:00").getFullYear() > 2011) {
                    errores.push("La fecha de nacimiento no puede ser posterior al 2011.");
                    marcarError(fecha);
                }

                if (!genero.value) {
                    errores.push("Selecciona tu género.");
                    marcarError(genero);
                }

                if (!telefono.value.trim()) {
                    errores.push("El teléfono es obligatorio.");
                    marcarError(telefono);
                } else if (!/^\d{10}$/.test(telefono.value.trim())) {
                    errores.push("El teléfono debe tener exactamente 10 números.");
                    marcarError(telefono);
                }

                /* validación básica de correo */
                var emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!correo.value.trim()) {
                    errores.push("El correo electrónico es obligatorio.");
                    marcarError(correo);
                } else if (!emailRe.test(correo.value.trim())) {
                    errores.push("Ingresa un correo electrónico válido.");
                    marcarError(correo);
                }

                if (!pwd.value) {
                    errores.push("La contraseña es obligatoria.");
                    marcarError(pwd);
                } else if (pwd.value.length < 6) {
                    errores.push("La contraseña debe tener al menos 6 caracteres.");
                    marcarError(pwd);
                }

                if (!cpwd.value) {
                    errores.push("Debes confirmar la contraseña.");
                    marcarError(cpwd);
                } else if (pwd.value && cpwd.value !== pwd.value) {
                    errores.push("Las contraseñas no coinciden.");
                    marcarError(pwd);
                    marcarError(cpwd);
                }

                if (!politica.checked) {
                    errores.push("Debes aceptar la política de tratamiento de datos.");
                    marcarError(politica);
                }

                if (errores.length > 0) {
                    mostrarValidacionModal(errores);
                } else {
                    /* sin errores → enviar el form normalmente */
                    this.submit();
                }
            });
        })();
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="<c:url value='/Vista/JS/script.js?v=20260808' />"></script>

    </body>
</html>


