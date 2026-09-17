<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:if test="${empty requestScope.catalogoCargado}">
    <c:redirect url="/InicioServlet" />
</c:if>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>FitManager - Inicio</title>
        <link rel="icon" type="image/png" href="<c:url value='/Vista/img/Icono.png' />">
        <link rel="stylesheet" href="<c:url value='/Vista/CSS/styles.css?v=20260904' />">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==" crossorigin="anonymous" referrerpolicy="no-referrer">
        <link href="https://fonts.googleapis.com/css2?family=Alfa+Slab+One&family=DM+Sans:ital,wght@0,400;0,500;0,600;0,700;0,800;1,400&display=swap" rel="stylesheet">
        <script defer src="<c:url value='/Vista/JS/script.js?v=20260808' />"></script>
</head>

    <body class="index-page" id="inicio">

        <c:if test="${not empty requestScope.errorCatalogo}">
            <div style="background:#a52329;color:#fff;padding:12px 20px;text-align:center;font-family:'DM Sans',sans-serif;font-size:13px;position:relative;z-index:5000;">
                <c:out value="${errorCatalogo}" />
            </div>
        </c:if>

        <!-- NAVBAR -->
        <header class="navbar">
            <div class="logo-nav">
                <img src="<c:url value='/Vista/img/Icono.png' />" alt="logo">
                <h2>FITMANAGER</h2>
            </div>

            <div class="nav-catalogo">
                <a href="#membresias" class="btn-nav"><i class="fa-solid fa-tag"></i> Membresías</a>
                <a href="#productos" class="btn-nav"><i class="fa-solid fa-bag-shopping"></i> Productos</a>
                <a href="#sedes" class="btn-nav"><i class="fa-solid fa-location-dot"></i> Sedes</a>
            </div>

            <div class="nav-buttons">
                <a href="<c:url value='/login' />" class="btn-nav"><i class="fa-solid fa-user"></i> Iniciar sesión</a>
                <a href="<c:url value='/FormularioRegistroServlet' />" class="btn-nav"><i class="fa-solid fa-pen-to-square"></i> Registrarse</a>
            </div>
        </header>

        <!-- HERO -->
        <section class="hero">
            <div class="hero-content">
                <div class="hero-badge">Sistema de gestión deportiva</div>

                <img src="<c:url value='/Vista/img/Fitmanagerpro.png' />"
                     class="logo animated-logo" alt="FitManager">

                <h1>TRANSFORMA TU <span>PROGRESO</span></h1>

                <p>Lleva el control de tus entrenamientos, registra tu asistencia,
                   cumple metas y mejora tu rendimiento con FitManager.</p>

                <div class="hero-buttons">
                    <a href="<c:url value='/FormularioRegistroServlet' />" class="btn-main">Comenzar ahora</a>
                    <a href="<c:url value='/login' />" class="btn-main">Ya tengo cuenta</a>
                </div>

                <div class="hero-stats">
                    <div class="hero-stat">
                        <div class="hero-stat-num">1</div>
                        <div class="hero-stat-label">Sede activa</div>
                    </div>
                    <div class="hero-stat">
                        <div class="hero-stat-num">100<span>%</span></div>
                        <div class="hero-stat-label">Control en tiempo real</div>
                    </div>
                    <div class="hero-stat">
                        <div class="hero-stat-num">24<span>/7</span></div>
                        <div class="hero-stat-label">Acceso a tu cuenta</div>
                    </div>
                </div>
            </div>
        </section>

        <!-- CARTEL PUBLICITARIO -->
        <section class="promo-banner">
            <img src="<c:url value='/Vista/img/taurus/taurus-logo-metal.jpg' />" alt="Taurus Gym">
            <div class="promo-banner-content">
                <div class="promo-banner-eyebrow">Oferta Taurus Gym</div>
                <h2>Entrena en un espacio pensado para tu progreso</h2>
                <p>Equipos completos de fuerza, cardio y powerlifting, con seguimiento
                   digital de tu asistencia y tus metas incluido en tu membresía.</p>
                <a href="#membresias" class="btn-main">Ver membresías</a>
            </div>
        </section>

        <!-- FEATURES -->
        <section class="features-section">
            <p class="features-label">Qué ofrece FitManager</p>
            <h2 class="features-title">Todo lo que necesitas para <span>rendir mejor</span></h2>

            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon"><i class="fa-solid fa-chart-column"></i></div>
                    <h3>SEGUIMIENTO</h3>
                    <p>Visualiza tu constancia con gráficas en tiempo real. Cada sesión registrada construye tu historial de progreso.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon"><i class="fa-solid fa-dumbbell"></i></div>
                    <h3>RUTINAS</h3>
                    <p>Organiza y registra tus ejercicios fácilmente. Accede a tus rutinas asignadas y anota tu desempeño.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon"><i class="fa-solid fa-bullseye"></i></div>
                    <h3>METAS</h3>
                    <p>Define objetivos personalizados y mide tu avance. FitManager te mantiene enfocado y motivado.</p>
                </div>
            </div>
        </section>

        <!-- GALERÍA -->
        <section id="galeria" class="gym-media-section">
            <div class="gym-section-heading">
                <span class="section-eyebrow">Conoce el espacio</span>
                <h2>Taurus Gym en imágenes</h2>
                <p>Fotos y videos reales del gimnasio para que veas el ambiente antes de entrenar.</p>
            </div>

            <div class="gym-carousel" data-carousel>
                <button class="carousel-control carousel-prev" type="button" data-carousel-prev aria-label="Foto anterior">&#8249;</button>

                <div class="carousel-track">
                    <figure class="gym-slide active">
                        <img class="js-lightbox-img" src="<c:url value='/Vista/img/taurus/taurus-logo-metal.jpg' />" alt="Logo de Taurus Gym en metal">
                        <button type="button" class="gym-zoom-btn" aria-label="Ampliar imagen"><i class="fa-solid fa-expand"></i></button>
                    </figure>
                    <figure class="gym-slide">
                        <img class="js-lightbox-img" src="<c:url value='/Vista/img/taurus/taurus-comunidad-gym.jpg' />" alt="Ambiente y comunidad en Taurus Gym">
                        <button type="button" class="gym-zoom-btn" aria-label="Ampliar imagen"><i class="fa-solid fa-expand"></i></button>
                    </figure>
                    <figure class="gym-slide">
                        <img class="js-lightbox-img" src="<c:url value='/Vista/img/taurus/taurus-spinning.jpg' />" alt="Zona de spinning en Taurus Gym">
                        <button type="button" class="gym-zoom-btn" aria-label="Ampliar imagen"><i class="fa-solid fa-expand"></i></button>
                    </figure>
                    <figure class="gym-slide">
                        <img class="js-lightbox-img" src="<c:url value='/Vista/img/taurus/taurus-cables-machine.jpg' />" alt="Máquina de cables de Taurus Gym">
                        <button type="button" class="gym-zoom-btn" aria-label="Ampliar imagen"><i class="fa-solid fa-expand"></i></button>
                    </figure>
                    <figure class="gym-slide">
                        <img class="js-lightbox-img" src="<c:url value='/Vista/img/taurus/taurus-mma-bag.jpg' />" alt="Costal MMA Everlast en Taurus Gym">
                        <button type="button" class="gym-zoom-btn" aria-label="Ampliar imagen"><i class="fa-solid fa-expand"></i></button>
                    </figure>
                    <figure class="gym-slide">
                        <img class="js-lightbox-img" src="<c:url value='/Vista/img/taurus/taurus-bumper-plates.jpg' />" alt="Discos bumper plates en Taurus Gym">
                        <button type="button" class="gym-zoom-btn" aria-label="Ampliar imagen"><i class="fa-solid fa-expand"></i></button>
                    </figure>
                    <figure class="gym-slide">
                        <img class="js-lightbox-img" src="<c:url value='/Vista/img/taurus/taurus-discos-20kg.jpg' />" alt="Discos 20kg Multisports en Taurus Gym">
                        <button type="button" class="gym-zoom-btn" aria-label="Ampliar imagen"><i class="fa-solid fa-expand"></i></button>
                    </figure>
                    <figure class="gym-slide">
                        <img class="js-lightbox-img" src="<c:url value='/Vista/img/taurus/taurus-maquina-piernas.jpg' />" alt="Máquina de piernas en Taurus Gym">
                        <button type="button" class="gym-zoom-btn" aria-label="Ampliar imagen"><i class="fa-solid fa-expand"></i></button>
                    </figure>
                    <figure class="gym-slide">
                        <img class="js-lightbox-img" src="<c:url value='/Vista/img/taurus/taurus-detalle-pesas.jpg' />" alt="Detalle de pesas en Taurus Gym">
                        <button type="button" class="gym-zoom-btn" aria-label="Ampliar imagen"><i class="fa-solid fa-expand"></i></button>
                    </figure>
                    <figure class="gym-slide">
                        <img class="js-lightbox-img" src="<c:url value='/Vista/img/taurus/taurus-senalizacion.jpg' />" alt="Señalización de Taurus Gym">
                        <button type="button" class="gym-zoom-btn" aria-label="Ampliar imagen"><i class="fa-solid fa-expand"></i></button>
                    </figure>
                    <figure class="gym-slide gym-video-slide">
                        <video controls preload="metadata" poster="<c:url value='/Vista/img/taurus/taurus-video-poster.jpg' />">
                            <source src="<c:url value='/Vista/video/taurus/taurus-video-gimnasio.mp4' />" type="video/mp4">
                        </video>
                    </figure>
                    <figure class="gym-slide gym-video-slide">
                        <video controls preload="metadata" poster="<c:url value='/Vista/img/taurus/taurus-recorrido-poster.jpg' />">
                            <source src="<c:url value='/Vista/video/taurus/taurus-recorrido-gimnasio.mp4' />" type="video/mp4">
                        </video>
                    </figure>
                </div>

                <button class="carousel-control carousel-next" type="button" data-carousel-next aria-label="Foto siguiente">&#8250;</button>

                <div class="carousel-dots" data-carousel-dots aria-label="Seleccionar medio"></div>
            </div>
        </section>

        <!-- LIGHTBOX: previsualización ampliada de imágenes de la galería -->
        <div class="lightbox-overlay" id="lightboxOverlay">
            <button type="button" class="lightbox-close" id="lightboxClose" aria-label="Cerrar previsualización">
                <i class="fa-solid fa-xmark"></i>
            </button>
            <button type="button" class="lightbox-fullscreen" id="lightboxFullscreen" aria-label="Pantalla completa">
                <i class="fa-solid fa-expand"></i>
            </button>
            <img id="lightboxImg" src="" alt="">
        </div>

        <!-- MEMBRESÍAS -->
        <section id="membresias" class="catalogo-panel">
            <div class="catalogo-bull-bg" aria-hidden="true"></div>
            <a href="#inicio" class="btn-volver-icon" aria-label="Volver al inicio"></a>
            <div class="catalogo-panel-inner">
                <div class="catalogo-header">
                    <h2>Membresías</h2>
                    <p class="membership-intro">
                        Todas las membresías te dan acceso completo a la plataforma web y al gimnasio.
                        Entre más alta la membresía, más beneficio extra obtienes.
                    </p>
                </div>

                <div class="catalogo-grid membership-grid">
                    <c:choose>
                        <c:when test="${empty membresias}">
                            <p>No se pudieron cargar las membresías.</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="m" items="${membresias}">
                                <%-- Icono y beneficio extra por defecto segun duracion, se usan solo
                                     si la membresia todavia no tiene beneficio_extra en la base de datos --%>
                                <c:set var="mIcono" value="fa-crown" />
                                <c:set var="mExtraDefault" value="Acceso VIP y el mejor precio por día." />
                                <c:choose>
                                    <c:when test="${m.duracion_dias le 1}">
                                        <c:set var="mIcono" value="fa-bolt" />
                                        <c:set var="mExtraDefault" value="Acceso por un día a todas las máquinas e instalaciones." />
                                    </c:when>
                                    <c:when test="${m.duracion_dias le 30}">
                                        <c:set var="mIcono" value="fa-calendar-days" />
                                        <c:set var="mExtraDefault" value="Acceso ilimitado durante todo el mes." />
                                    </c:when>
                                    <c:when test="${m.duracion_dias le 60}">
                                        <c:set var="mIcono" value="fa-calendar-week" />
                                        <c:set var="mExtraDefault" value="2 meses de acceso + rutina personalizada." />
                                    </c:when>
                                    <c:when test="${m.duracion_dias le 90}">
                                        <c:set var="mIcono" value="fa-chart-line" />
                                        <c:set var="mExtraDefault" value="Trimestre completo + seguimiento de metas." />
                                    </c:when>
                                    <c:when test="${m.duracion_dias le 180}">
                                        <c:set var="mIcono" value="fa-star" />
                                        <c:set var="mExtraDefault" value="Semestre completo + clases grupales incluidas." />
                                    </c:when>
                                </c:choose>
                                <c:set var="mExtra" value="${not empty m.beneficioExtra ? m.beneficioExtra : mExtraDefault}" />

                                <div class="membership-card${m.esMasComprada ? ' membership-card--featured' : ''}">
                                    <c:if test="${m.esMasComprada}">
                                        <span class="membership-badge membership-badge--featured">Más comprada</span>
                                    </c:if>
                                    <c:if test="${m.esMayorAhorro}">
                                        <span class="membership-badge membership-badge--ahorro">Mayor ahorro</span>
                                    </c:if>

                                    <div class="membership-icon"><i class="fa-solid ${mIcono}"></i></div>
                                    <h3 class="membership-name"><c:out value="${m.nombre}" /></h3>

                                    <div class="membership-price">
                                        <fmt:formatNumber value="${m.precio}" type="currency" currencySymbol="$ " />
                                        <span>/ ${m.duracion_dias} días</span>
                                    </div>

                                    <c:url var="comprarMembresiaUrl" value="/FormularioRegistroServlet">
                                        <c:param name="tipo" value="membresia" />
                                        <c:param name="id" value="${m.id}" />
                                    </c:url>
                                    <a href="${comprarMembresiaUrl}" class="membership-cta">
                                        <i class="fa-solid fa-bolt"></i> Comprar
                                    </a>

                                    <ul class="membership-features">
                                        <li><i class="fa-solid fa-check"></i> Acceso completo a la plataforma web</li>
                                        <li><i class="fa-solid fa-check"></i> Acceso completo al gimnasio</li>
                                        <li><i class="fa-solid fa-check"></i> Registro de asistencia y rutinas</li>
                                        <li class="membership-feature-extra"><i class="fa-solid fa-star"></i> <c:out value="${mExtra}" /></li>
                                    </ul>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </section>

        <!-- PRODUCTOS -->
        <section id="productos" class="catalogo-panel">
            <div class="catalogo-bull-bg" aria-hidden="true"></div>
            <a href="#inicio" class="btn-volver-icon" aria-label="Volver al inicio"></a>
            <div class="catalogo-panel-inner">
                <div class="catalogo-header">
                    <h2>Productos</h2>
                </div>

                <div class="catalogo-grid">
                    <c:choose>
                        <c:when test="${empty productos}">
                            <p>No se pudieron cargar los productos.</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="p" items="${productos}">
                                <%-- Icono y descripcion de consumo por defecto segun el nombre del
                                     producto, se usan solo si el producto todavia no tiene
                                     descripcion_consumo en la base de datos --%>
                                <c:set var="pIcono" value="fa-cart-shopping" />
                                <c:set var="pDescDefault" value="Producto disponible para tu entrenamiento." />
                                <c:choose>
                                    <c:when test="${fn:containsIgnoreCase(p.nombre, 'agua') or fn:containsIgnoreCase(p.nombre, 'botella') or fn:containsIgnoreCase(p.nombre, 'botilo')}">
                                        <c:set var="pIcono" value="fa-droplet" />
                                        <c:set var="pDescDefault" value="Mantente hidratado antes, durante y después de entrenar." />
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.nombre, 'proteina')}">
                                        <c:set var="pIcono" value="fa-bottle-water" />
                                        <c:set var="pDescDefault" value="Toma 5g al día para apoyar la recuperación y el desarrollo muscular." />
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.nombre, 'creatina')}">
                                        <c:set var="pIcono" value="fa-pills" />
                                        <c:set var="pDescDefault" value="Consume 1 dosis después de entrenar, mezclada con agua o jugo." />
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.nombre, 'pre-entreno') or fn:containsIgnoreCase(p.nombre, 'preentreno')}">
                                        <c:set var="pIcono" value="fa-bolt" />
                                        <c:set var="pDescDefault" value="Consume 20-30 minutos antes de entrenar para más energía." />
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.nombre, 'toalla')}">
                                        <c:set var="pIcono" value="fa-box" />
                                    </c:when>
                                    <c:when test="${fn:containsIgnoreCase(p.nombre, 'guante')}">
                                        <c:set var="pIcono" value="fa-hand" />
                                    </c:when>
                                </c:choose>
                                <c:set var="pDesc" value="${not empty p.descripcionConsumo ? p.descripcionConsumo : pDescDefault}" />

                                <div class="catalogo-card">
                                    <div class="catalogo-icon">
                                        <i class="fa-solid ${pIcono}"></i>
                                    </div>
                                    <c:if test="${not empty p.proveedor}">
                                        <span class="catalogo-proveedor">Proveedor: <c:out value="${p.proveedor}" /></span>
                                    </c:if>
                                    <h3><c:out value="${p.nombre}" /></h3>
                                    <p><fmt:formatNumber value="${p.precio}" type="currency" currencySymbol="$ " /></p>
                                    <span>Producto disponible</span>
                                    <p class="catalogo-desc-consumo"><i class="fa-solid fa-circle-info"></i> <c:out value="${pDesc}" /></p>
                                    <div class="catalogo-actions">
                                        <c:url var="comprarProductoUrl" value="/login">
                                            <c:param name="tipo" value="producto" />
                                            <c:param name="id" value="${p.id}" />
                                        </c:url>
                                        <a href="${comprarProductoUrl}" class="btn-main">Comprar</a>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </section>

        <!-- SEDES -->
        <section id="sedes" class="catalogo-panel sedes-panel">
            <div class="catalogo-bull-bg" aria-hidden="true"></div>
            <a href="#inicio" class="btn-volver-icon" aria-label="Volver al inicio"></a>
            <div class="catalogo-panel-inner">
            <div class="catalogo-header">
                <h2>Sedes</h2>
            </div>

            <div class="sedes-lista">
                <c:choose>
                    <c:when test="${empty sedes}">
                        <p>No se pudieron cargar las sedes.</p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="s" items="${sedes}">
                            <div class="sede-card sede-unica">
                                <span class="sede-name"><c:out value="${s.nombre}" /></span>
                                <div class="sede-detalle">
                                    <div class="sede-main-photo">
                                        <img src="<c:url value='/Vista/img/taurus/taurus-comunidad-gym.jpg' />" alt="Foto de la sede">
                                    </div>
                                    <div class="sede-info-boxes">
                                        <div class="sede-info-box">
                                            <strong><i class="fa-solid fa-location-dot"></i> Dirección</strong>
                                            <p class="sede-direccion-linea">
                                                <c:out value="${s.direccion}" />
                                                <a href="#" class="btn-ver-mapa js-ver-mapa" title="Ver en Google Maps"
                                                   aria-label="Ver ubicación en Google Maps" data-direccion="<c:out value='${s.direccion}' />">
                                                    <i class="fa-solid fa-map-location-dot"></i>
                                                </a>
                                            </p>
                                        </div>
                                        <div class="sede-info-box">
                                            <strong><i class="fa-solid fa-clock"></i> Horario</strong>
                                            <p style="white-space:pre-line;"><c:out value="${s.horario}" /></p>
                                        </div>
                                        <div class="sede-info-box">
                                            <strong><i class="fa-solid fa-phone"></i> Contacto</strong>
                                            <p>Teléfono: <c:out value="${s.telefono}" /><br><c:out value="${s.descripcion}" /></p>
                                        </div>
                                    </div>
                                    <div class="sede-gallery">
                                        <img src="<c:url value='/Vista/img/taurus/taurus-cables-machine.jpg' />" alt="Instalaciones de la sede">
                                        <img src="<c:url value='/Vista/img/taurus/taurus-spinning.jpg' />" alt="Zona de cardio del gimnasio">
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
            </div>
        </section>

        <!-- FOOTER -->
        <footer class="footer footer--with-social">
            <div class="footer-social">
                <div class="footer-social-copy">
                    <span class="footer-kicker">COMUNIDAD FITMANAGER</span>
                    <p>Síguenos y entérate primero de promociones, novedades y contenido para potenciar tu entrenamiento.</p>
                </div>
                <nav class="footer-social-icons" aria-label="Redes sociales">
                    <c:forEach var="red" items="${redesSociales}">
                        <a class="social-link social-link--${red.cssClase}" href="<c:out value='${red.url}' />" target="_blank" rel="noopener noreferrer" aria-label="Visitar <c:out value='${red.nombre}' />">
                            <i class="fa-brands <c:out value='${red.icono}' />" aria-hidden="true"></i><span><c:out value="${red.nombre}" /></span>
                        </a>
                    </c:forEach>
                </nav>
            </div>
            <div class="footer-bottom">
                <span class="footer-brand">FITMANAGER</span>
                <span class="footer-copy">© 2026 FitManager <span class="footer-dot"></span> Todos los derechos reservados</span>
            </div>
        </footer>

        <script>
            document.addEventListener('click', function (e) {
                var btn = e.target.closest('.js-ver-mapa');
                if (!btn) return;
                e.preventDefault();
                var direccion = btn.getAttribute('data-direccion') || '';
                var url = 'https://www.google.com/maps/search/?api=1&query=' + encodeURIComponent(direccion);
                window.open(url, '_blank', 'noopener');
            });
        </script>

    </body>
</html>
