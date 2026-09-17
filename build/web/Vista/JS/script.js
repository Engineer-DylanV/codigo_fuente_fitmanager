function mostrarNotificacion(mensaje) {
    let div = document.createElement("div");
    div.className = "notificacion-flotante";
    div.innerHTML = mensaje;
    document.body.appendChild(div);
    setTimeout(() => div.classList.add("show"), 50);
    setTimeout(() => {
        div.classList.remove("show");
        setTimeout(() => div.remove(), 400);
    }, 2500);
}

/* -------------------------------------------------------------
 BOTÓN VOLVER
 ------------------------------------------------------------- */
function volverPagina() {
    if (document.referrer !== "") {
        window.history.back();
    } else {
        window.location.href = (typeof CTX_PATH !== "undefined" ? CTX_PATH : "") + "/Index.jsp";
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const btnVolver = document.getElementById("btnVolver");
    if (btnVolver) {
        btnVolver.addEventListener("click", volverPagina);
    }

    /* -------------------------------------------------------------
     SELECTOR DE TEMA (paneles Cliente / Administrador)
     ------------------------------------------------------------- */
    const btnTema = document.getElementById("themeToggleBtn");
    if (btnTema) {
        const iconoTema = document.querySelector(".theme-switch i");

        const actualizarIcono = () => {
            if (!iconoTema) {
                return;
            }
            const claro = document.body.classList.contains("theme-light");
            iconoTema.className = claro ? "fa-solid fa-sun" : "fa-solid fa-moon";
        };

        actualizarIcono();

        btnTema.addEventListener("click", () => {
            const claro = document.body.classList.toggle("theme-light");
            try {
                localStorage.setItem("fitmanager_theme", claro ? "light" : "dark");
            } catch (e) {}
            actualizarIcono();
            actualizarColoresGraficas();
        });
    }

    if (typeof ASISTENCIAS_COUNT !== "undefined") {
        cargarTotalAsistencias();
        cargarGraficaAsistencia();
    }
    if (typeof METAS_PORCENTAJE !== "undefined") {
        cargarGraficaMetas();
    }

    if (document.getElementById("lista-clases-inicio"))
        cargarClasesInicio();
    if (document.getElementById("tabla-mis-clases"))
        cargarMisClasesInicio();
    if (document.getElementById("historial"))
        cargarHistorial();
    if (document.getElementById("metas-lista"))
        cargarMetas();
    if (document.getElementById("tabla-evaluaciones-cliente"))
        cargarEvaluacionesCliente();
    if (document.getElementById("perfilCliente"))
        inicializarPerfilCliente();

    if (document.getElementById("tabla-usuarios"))
        cargarUsuarios();
    if (document.getElementById("clases-disponibles-admin"))
        cargarClasesDisponiblesAdmin();
    if (document.getElementById("clases-admin"))
        cargarClasesAdmin();
});

/* -------------------------------------------------------------
 VER / OCULTAR CONTRASEÑA
 ------------------------------------------------------------- */
function togglePassword(id, icono) {
    const input = document.getElementById(id);
    if (input.type === "password") {
        input.type = "text";
        icono.innerHTML = '<i class="fa-solid fa-eye-slash"></i>';
    } else {
        input.type = "password";
        icono.innerHTML = '<i class="fa-solid fa-eye"></i>';
    }
}

/* -------------------------------------------------------------
 LOGOUT
 ------------------------------------------------------------- */
function logout() {
    const irCerrarSesion = () => {
        window.location.href = (typeof CTX_PATH !== "undefined" ? CTX_PATH : "") + "/CerrarSesionServlet";
    };
    if (typeof mostrarConfirm === "function") {
        mostrarConfirm("¿Seguro que quieres salir?", irCerrarSesion, {
            titulo: "Cerrar sesión",
            icono: "warning",
            textoAceptar: "Salir"
        });
    } else {
        irCerrarSesion();
    }
}

function abrirFacturaCliente(idFactura) {
    const base = typeof CTX_PATH !== "undefined" ? CTX_PATH : "";
    const id = idFactura ? "?id=" + encodeURIComponent(idFactura) : "";
    const ancho = 900;
    const alto = 760;
    const izquierda = Math.max((window.screen.width - ancho) / 2, 0);
    const arriba = Math.max((window.screen.height - alto) / 2, 0);

    window.open(
            base + "/FacturaClienteServlet" + id,
            "facturaCliente",
            `width=${ancho},height=${alto},left=${izquierda},top=${arriba},scrollbars=yes,resizable=yes`
            );
}

/* -------------------------------------------------------------
 MOSTRAR SECCIONES
 ------------------------------------------------------------- */
function mostrarSeccion(id, el) {
    document.querySelectorAll(".seccion").forEach(s => s.classList.remove("activa"));

    const seccion = document.getElementById(id);
    if (seccion) {
        seccion.classList.add("activa");
    }

    document.querySelectorAll(".sidebar li").forEach(li => li.classList.remove("activo"));
    document.querySelectorAll(".panel-profile-trigger").forEach(boton => boton.classList.remove("activo"));
    if (el) {
        el.classList.add("activo");
        const dropdownPadre = el.closest(".sidebar-dropdown");
        if (dropdownPadre) {
            dropdownPadre.classList.add("activo");
        }
    }

    if (id === "metas") {
        cargarMetas();
        cargarGraficaMetas();
    }
    if (id === "inicio") {
        cargarClasesInicio();
        cargarMisClasesInicio();
    }
    if (id === "rutinas") {
        cargarRutinasCliente();
    }
    if (id === "registro") {
        cargarHistorial();
    }
    if (id === "evaluacionesCliente") {
        cargarEvaluacionesCliente();
    }
    if (id === "clases") {
        cargarClasesDisponiblesAdmin();
    }
    if (id === "configClases") {
        cargarClasesAdmin();
    }
    if (id === "usuarios") {
        cargarUsuarios();
    }
}

/* -------------------------------------------------------------
 MI PERFIL (cliente)
 ------------------------------------------------------------- */
function inicializarPerfilCliente() {
    const contenedor = document.getElementById("perfilCliente");
    if (!contenedor) return;

    const avatar = document.getElementById("perfilAvatar");
    const foto = document.getElementById("perfilFoto");
    const icono = document.getElementById("perfilAvatarIcon");
    const iniciales = document.getElementById("perfilIniciales");
    const avatarResumen = document.getElementById("perfilAvatarResumen");
    const fotoResumen = document.getElementById("perfilFotoResumen");
    const iconoResumen = document.getElementById("perfilIconoResumen");
    const inicialesResumen = document.getElementById("perfilInicialesResumen");
    const inputFoto = document.getElementById("fotoPerfil");
    const etiquetaFoto = document.querySelector(".profile-upload-label");
    const base = typeof CTX_PATH !== "undefined" ? CTX_PATH : "";
    const opcionesAvatar = Array.from(document.querySelectorAll(".avatar-option"));

    const nombre = (contenedor.dataset.nombre || "").trim();
    const textoIniciales = nombre.split(/\s+/).filter(Boolean).slice(0, 2).map(parte => parte.charAt(0).toUpperCase()).join("") || "FM";

    function marcarOpcion(modo) {
        opcionesAvatar.forEach(boton => {
            boton.classList.toggle("activo", boton.dataset.avatar === modo);
        });
    }

    function opcionPorCodigo(codigo) {
        return opcionesAvatar.find(opcion => opcion.dataset.avatar === codigo) || opcionesAvatar[0] || null;
    }

    function aplicarColores(elemento, opcion) {
        if (!elemento || !opcion) return;
        elemento.style.setProperty("--avatar-start", opcion.dataset.start || "#4b5563");
        elemento.style.setProperty("--avatar-end", opcion.dataset.end || "#1f2937");
    }

    function mostrarAvatar(codigo) {
        const opcion = opcionPorCodigo(codigo);
        if (!opcion) {
            mostrarIniciales();
            return;
        }
        const iconoAvatar = opcion.dataset.icon;
        aplicarColores(avatar, opcion);
        aplicarColores(avatarResumen, opcion);
        foto.hidden = true;
        fotoResumen.hidden = true;
        icono.hidden = false;
        iconoResumen.hidden = false;
        iniciales.hidden = true;
        inicialesResumen.hidden = true;
        icono.innerHTML = '<i class="fa-solid ' + iconoAvatar + '" aria-hidden="true"></i>';
        iconoResumen.className = "fa-solid " + iconoAvatar;
        marcarOpcion(opcion.dataset.avatar);
    }

    function mostrarFoto() {
        icono.hidden = true;
        iconoResumen.hidden = true;
        iniciales.hidden = true;
        inicialesResumen.hidden = true;
        foto.hidden = false;
        fotoResumen.hidden = false;
        const urlFoto = base + "/PerfilClienteServlet?accion=foto&t=" + Date.now();
        foto.src = urlFoto;
        fotoResumen.src = urlFoto;
        foto.onerror = function () {
            mostrarIniciales();
        };
        marcarOpcion("");
    }

    function mostrarIniciales() {
        foto.hidden = true;
        fotoResumen.hidden = true;
        icono.hidden = true;
        iconoResumen.hidden = true;
        iniciales.hidden = false;
        inicialesResumen.hidden = false;
        iniciales.textContent = textoIniciales;
        inicialesResumen.textContent = textoIniciales;
        marcarOpcion("");
    }

    fetch(base + "/PerfilClienteServlet?accion=estado", {
        headers: { "Accept": "application/json" },
        credentials: "same-origin"
    })
        .then(respuesta => respuesta.ok ? respuesta.json() : Promise.reject())
        .then(estado => {
            if (estado.tieneFoto) mostrarFoto();
            else mostrarAvatar(estado.avatarCodigo);
        })
        .catch(mostrarIniciales);

    if (inputFoto && etiquetaFoto) {
        inputFoto.addEventListener("change", function () {
            const archivo = this.files && this.files[0];
            etiquetaFoto.innerHTML = archivo
                ? '<i class="fa-solid fa-image"></i> ' + archivo.name
                : '<i class="fa-solid fa-camera"></i> Subir foto';
        });
    }

    opcionesAvatar.forEach(boton => {
        boton.addEventListener("click", function () {
            const elegido = boton.dataset.avatar;
            // Enviamos un formulario normal para que el servidor confirme la
            // escritura en MySQL antes de reflejar el cambio en la interfaz.
            const formulario = document.createElement("form");
            formulario.method = "post";
            formulario.action = base + "/PerfilClienteServlet";
            [["accion", "avatar"], ["avatar", elegido]].forEach(([nombreCampo, valorCampo]) => {
                const campo = document.createElement("input");
                campo.type = "hidden";
                campo.name = nombreCampo;
                campo.value = valorCampo;
                formulario.appendChild(campo);
            });
            document.body.appendChild(formulario);
            formulario.submit();
        });
    });
}

/* =============================================================
 CLASES
 ============================================================= */
function iconoClase(nombre) {
    const n = (nombre || "").toLowerCase();
    if (n.includes("spinning") || n.includes("ciclismo")) return '<i class="fa-solid fa-person-biking"></i>';
    if (n.includes("yoga")) return '<i class="fa-solid fa-spa"></i>';
    if (n.includes("zumba") || n.includes("baile")) return '<i class="fa-solid fa-music"></i>';
    if (n.includes("crossfit")) return '<i class="fa-solid fa-dumbbell"></i>';
    if (n.includes("pilates")) return '<i class="fa-solid fa-person-walking"></i>';
    if (n.includes("funcional")) return '<i class="fa-solid fa-fire"></i>';
    if (n.includes("boxeo") || n.includes("box")) return '<i class="fa-solid fa-hand-fist"></i>';
    if (n.includes("natacion") || n.includes("natación")) return '<i class="fa-solid fa-person-swimming"></i>';
    return '<i class="fa-solid fa-trophy"></i>';
}

function cargarClasesInicio() {
    const cont = document.getElementById("lista-clases-inicio");
    if (!cont)
        return;

    fetch(CTX_PATH + "/GestionClasesServlet?accion=disponibles")
            .then(r => r.json())
            .then(clases => {
                cont.innerHTML = "";
                if (!clases || clases.length === 0) {
                    cont.innerHTML = "<p style='color:#9ca3af;font-size:13px;'>No hay clases disponibles</p>";
                    return;
                }
                clases.forEach(c => {
                    cont.innerHTML += `
                <div class="clase-item">
                    <div class="clase-info">
                        <span class="clase-icono">${iconoClase(c.nombre)}</span>
                        <div>
                            <span>${c.nombre}</span>
                            <div class="clase-detalle">${c.dia || ""} ${formatearHoraClase(c.hora)} - ${c.instructor || ""}</div>
                        </div>
                    </div>
                    <button onclick="${c.inscrito ? `desinscribirClase(${c.id_Clases})` : `inscribirClase(${c.id_Clases})`}">
                        ${c.inscrito ? "Salir" : "Inscribirse"}
                    </button>
                </div>`;
                });
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error al cargar clases"));
}

function cargarMisClasesInicio() {
    const cont = document.getElementById("tabla-mis-clases");
    if (!cont)
        return;

    fetch(CTX_PATH + "/GestionClasesServlet?accion=misClases")
            .then(r => r.json())
            .then(clases => {
                cont.innerHTML = "";
                if (!clases || clases.length === 0) {
                    cont.innerHTML = `<tr><td colspan="2">No estás inscrito en ninguna clase</td></tr>`;
                    return;
                }
                clases.forEach(c => {
                    cont.innerHTML += `
                <tr>
                    <td>
                        <strong>${iconoClase(c.nombre)} ${c.nombre}</strong><br>
                        <span class="clase-detalle">${c.dia || ""} ${formatearHoraClase(c.hora)} - ${c.instructor || ""}</span>
                    </td>
                    <td><button onclick="desinscribirClase(${c.id_Clases})">Salir</button></td>
                </tr>`;
                });
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error al cargar tus clases"));
}

function inscribirClase(idClase) {
    fetch(CTX_PATH + "/GestionClasesServlet", {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "accion=inscribir&idClase=" + idClase
    })
            .then(r => r.json())
            .then(data => {
                mostrarNotificacion(data.ok ? "<i class='fa-solid fa-circle-check'></i> Inscrito en la clase" : "<i class='fa-solid fa-triangle-exclamation'></i> " + data.mensaje);
                if (data.ok) {
                    cargarClasesInicio();
                    cargarMisClasesInicio();
                }
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error de conexión"));
}

function desinscribirClase(idClase) {
    fetch(CTX_PATH + "/GestionClasesServlet", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "accion=desinscribir&idClase=" + idClase
    })
            .then(r => r.json())
            .then(data => {
                mostrarNotificacion(data.ok ? "<i class='fa-solid fa-door-open'></i> Saliste de la clase" : "<i class='fa-solid fa-triangle-exclamation'></i> " + data.mensaje);
                if (data.ok) {
                    cargarClasesInicio();
                    cargarMisClasesInicio();
                }
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error de conexión"));
}

function cargarClasesAdmin() {
    const cont = document.getElementById("clases-admin");
    if (!cont)
        return;

    fetch(CTX_PATH + "/GestionClasesServlet?accion=todas")
            .then(r => r.json())
            .then(clases => {
                window.clasesAdminCache = clases || [];
                cont.innerHTML = "";
                if (!clases || clases.length === 0) {
                    cont.innerHTML = "<p style='color:#9ca3af;font-size:13px;'>No hay clases creadas</p>";
                    return;
                }
                clases.forEach(c => {
                    cont.innerHTML += `
                <div class="card" style="display:flex;justify-content:space-between;align-items:center;gap:14px;">
                    <div>
                        <strong>${iconoClase(c.nombre)} ${c.nombre}</strong>
                        <div class="clase-detalle">${c.dia || ""} ${formatearHoraClase(c.hora)} - ${c.instructor || ""}</div>
                    </div>
                    <button onclick="editarClaseAdmin(${c.id_Clases})"
                        style="font-size:12px;padding:6px 10px;">
                        <i class="fa-solid fa-pen"></i> Editar
                    </button>
                    <button onclick="eliminarClaseAdmin(${c.id_Clases})"
                        style="background:#ff0000;font-size:12px;padding:6px 10px;">
                        <i class="fa-solid fa-trash"></i> Eliminar
                    </button>
                </div>`;
                });
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error al cargar clases"));
}

function crearClase(e) {
    e.preventDefault();
    const nombre = document.getElementById("nombreClase").value.trim();
    const dia = document.getElementById("diaClase").value;
    const hora = document.getElementById("horaClase").value;
    const instructor = document.getElementById("instructorClase").value.trim();
    if (!nombre || !dia || !hora || !instructor)
        return;

    fetch(CTX_PATH + "/GestionClasesServlet", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "accion=crear"
                + "&nombre=" + encodeURIComponent(nombre)
                + "&dia=" + encodeURIComponent(dia)
                + "&hora=" + encodeURIComponent(hora)
                + "&instructor=" + encodeURIComponent(instructor)
    })
            .then(r => r.json())
            .then(data => {
                mostrarNotificacion(data.ok ? "<i class='fa-solid fa-circle-check'></i> Clase creada" : "<i class='fa-solid fa-triangle-exclamation'></i> " + data.mensaje);
                if (data.ok) {
                    document.getElementById("nombreClase").value = "";
                    document.getElementById("diaClase").value = "";
                    document.getElementById("horaClase").value = "";
                    document.getElementById("instructorClase").value = "";
                    cargarClasesAdmin();
                    cargarClasesDisponiblesAdmin();
                }
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error de conexión"));
}

function cargarClasesDisponiblesAdmin() {
    const cont = document.getElementById("clases-disponibles-admin");
    if (!cont)
        return;

    fetch(CTX_PATH + "/GestionClasesServlet?accion=todas")
            .then(r => r.json())
            .then(clases => {
                cont.innerHTML = "";
                if (!clases || clases.length === 0) {
                    cont.innerHTML = "<p style='color:#9ca3af;font-size:13px;'>No hay clases disponibles</p>";
                    return;
                }
                clases.forEach(c => {
                    cont.innerHTML += `
                <div class="card">
                    <strong>${iconoClase(c.nombre)} ${c.nombre}</strong>
                    <div class="clase-detalle">${c.dia || ""} ${formatearHoraClase(c.hora)} - ${c.instructor || ""}</div>
                </div>`;
                });
            })
            .catch(() => mostrarNotificacion("Error: Error al cargar clases"));
}

function editarClaseAdmin(idClase) {
    const clase = (window.clasesAdminCache || []).find(c => c.id_Clases === idClase);
    if (!clase) {
        mostrarNotificacion("No se encontro la clase");
        return;
    }

    const nombre = prompt("Nombre de la clase:", clase.nombre || "");
    if (nombre === null)
        return;

    const dia = prompt("Dia:", clase.dia || "");
    if (dia === null)
        return;

    const hora = prompt("Hora (HH:MM):", formatearHoraClase(clase.hora));
    if (hora === null)
        return;

    const instructor = prompt("Instructor o entrenador:", clase.instructor || "");
    if (instructor === null)
        return;

    fetch(CTX_PATH + "/GestionClasesServlet", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "accion=actualizar"
                + "&idClase=" + encodeURIComponent(clase.id_Clases)
                + "&nombre=" + encodeURIComponent(nombre.trim())
                + "&dia=" + encodeURIComponent(dia.trim())
                + "&hora=" + encodeURIComponent(hora.trim())
                + "&instructor=" + encodeURIComponent(instructor.trim())
    })
            .then(r => r.json())
            .then(data => {
                mostrarNotificacion(data.ok ? "Clase actualizada" : ("<i class='fa-solid fa-triangle-exclamation'></i> " + (data.mensaje || "Error al actualizar la clase")));
                if (data.ok) {
                    cargarClasesAdmin();
                    cargarClasesDisponiblesAdmin();
                }
            })
            .catch(() => mostrarNotificacion("Error de conexion"));
}

function eliminarClaseAdmin(idClase) {
    mostrarConfirm("¿Seguro que deseas eliminar esta clase?", function () {
        fetch(CTX_PATH + "/GestionClasesServlet", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: "accion=eliminar&idClase=" + idClase
        })
                .then(r => r.json())
                .then(data => {
                    mostrarNotificacion(data.ok ? "<i class='fa-solid fa-trash'></i> Clase eliminada" : "<i class='fa-solid fa-triangle-exclamation'></i> " + data.mensaje);
                    if (data.ok) {
                        cargarClasesAdmin();
                        cargarClasesDisponiblesAdmin();
                    }
                })
                .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error de conexión"));
    }, { titulo: "Eliminar clase", icono: "trash", textoAceptar: "Sí" });
}

function formatearHoraClase(hora) {
    if (!hora)
        return "";
    return String(hora).substring(0, 5);
}

/* =============================================================
 ASISTENCIA
 ============================================================= */
function cargarTotalAsistencias() {
    fetch(CTX_PATH + "/RegistroAsistenciaServlet?accion=totalMes")
            .then(r => r.json())
            .then(data => {
                ASISTENCIAS_COUNT = data.totalMes || 0;
                actualizarGraficaAsistencia(ASISTENCIAS_COUNT);
                actualizarRacha(data.racha, false);
            })
            .catch(() => {
            });
}

function asistir() {
    fetch(CTX_PATH + "/RegistroAsistenciaServlet", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "accion=registrar"
    })
            .then(r => r.json())
            .then(data => {
                mostrarNotificacion(data.ok ? "<i class='fa-solid fa-dumbbell'></i> Asistencia registrada" : "<i class='fa-solid fa-circle-info'></i> " + data.mensaje);
                ASISTENCIAS_COUNT = data.totalMes || ASISTENCIAS_COUNT;
                actualizarGraficaAsistencia(ASISTENCIAS_COUNT);
                // Solo felicitar cuando el registro de HOY fue exitoso (data.ok),
                // para no repetir el mensaje si ya había asistido o al recargar la página.
                actualizarRacha(data.racha, data.ok === true);
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error de conexión"));
}

/* =============================================================
 RACHA DE ASISTENCIA
 Se muestra junto a la gráfica de asistencia. Se activa (brilla)
 a partir de 3 días consecutivos, y felicita al usuario justo al
 registrar el día que completa/extiende una racha de 3+.
 ============================================================= */
function actualizarRacha(racha, mostrarFelicitacion) {
    RACHA_ACTUAL = racha || 0;
    const badge = document.getElementById("racha-badge");
    const numero = document.getElementById("racha-numero");
    if (!badge || !numero)
        return;

    if (RACHA_ACTUAL >= 1) {
        badge.style.display = "flex";
        numero.textContent = RACHA_ACTUAL;
        badge.classList.toggle("racha-activa", RACHA_ACTUAL >= 3);
    } else {
        badge.style.display = "none";
        badge.classList.remove("racha-activa");
    }

    if (mostrarFelicitacion && RACHA_ACTUAL >= 3) {
        mostrarNotificacion(
            "<i class='fa-solid fa-fire'></i> ¡Felicidades! Has conseguido una racha de " +
            RACHA_ACTUAL + " días seguidos. Sigue asistiendo para mantenerla."
        );
    }
}

/* =============================================================
 RUTINAS
 ============================================================= */
function cargarRutinasCliente() {
    const tabla = document.getElementById("tabla-rutinas");
    if (!tabla)
        return;

    fetch(CTX_PATH + "/GestionRutinasServlet?accion=listar")
            .then(r => r.json())
            .then(rutinas => {
                tabla.innerHTML = "";
                if (!rutinas || rutinas.length === 0) {
                    tabla.innerHTML = `<tr><td colspan="5">Aún no tienes rutinas asignadas. Contacta a tu entrenador.</td></tr>`;
                    return;
                }
                rutinas.forEach(r => {
                    tabla.innerHTML += `
                <tr>
                    <td>${r.programa || "Programa personalizado"}</td>
                    <td>${r.nombre}</td>
                    <td>${r.descripcion}</td>
                    <td>${r.objetivo || "Personalizado"}</td>
                    <td><a class="btn-main" href="${r.enlaceDrive}" target="_blank" rel="noopener">Abrir rutina</a></td>
                </tr>`;
                });
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error al cargar rutinas"));
}


/* =============================================================
 REGISTROS DE EJERCICIO
 ============================================================= */
function obtenerIconoEjercicio(ejercicio) {
    ejercicio = ejercicio.toLowerCase();
    if (ejercicio.includes("press") || ejercicio.includes("banca") || ejercicio.includes("pecho"))
        return '<i class="fa-solid fa-dumbbell"></i>';
    if (ejercicio.includes("curl") || ejercicio.includes("bíceps") || ejercicio.includes("biceps"))
        return '<i class="fa-solid fa-hand-fist"></i>';
    if (ejercicio.includes("sentadilla") || ejercicio.includes("pierna") || ejercicio.includes("prensa"))
        return '<i class="fa-solid fa-shoe-prints"></i>';
    if (ejercicio.includes("correr") || ejercicio.includes("trote") || ejercicio.includes("cardio") || ejercicio.includes("cinta"))
        return '<i class="fa-solid fa-person-running"></i>';
    if (ejercicio.includes("abdomen") || ejercicio.includes("crunch") || ejercicio.includes("plancha"))
        return '<i class="fa-solid fa-fire"></i>';
    if (ejercicio.includes("remo") || ejercicio.includes("espalda") || ejercicio.includes("dominadas"))
        return '<i class="fa-solid fa-arrows-up-to-line"></i>';
    if (ejercicio.includes("hombro") || ejercicio.includes("militar"))
        return '<i class="fa-solid fa-bolt"></i>';
    return '<i class="fa-solid fa-medal"></i>';
}

function cargarHistorial() {
    const cont = document.getElementById("historial");
    if (!cont)
        return;

    fetch(CTX_PATH + "/RegistroEntrenamientoServlet?accion=listar")
            .then(r => r.json())
            .then(registros => {
                cont.innerHTML = "";
                if (!registros || registros.length === 0) {
                    cont.innerHTML = `<tr><td colspan="5">No hay registros todavía</td></tr>`;
                    return;
                }
                registros.forEach(r => {
                    cont.innerHTML += `
                <tr>
                    <td style="font-size:22px;">${obtenerIconoEjercicio(r.ejercicio)}</td>
                    <td>${r.ejercicio}</td>
                    <td>${r.repeticiones}</td>
                    <td>${r.peso} kg</td>
                    <td>${r.fecha}</td>
                </tr>`;
                });
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error al cargar historial"));
}

function guardarRegistro(e) {
    e.preventDefault();
    const ejercicio = document.getElementById("ejercicio").value.trim();
    const repeticiones = document.getElementById("repeticiones").value;
    const peso = document.getElementById("peso").value;

    fetch(CTX_PATH + "/RegistroEntrenamientoServlet", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "accion=guardar"
                + "&ejercicio=" + encodeURIComponent(ejercicio)
                + "&repeticiones=" + encodeURIComponent(repeticiones)
                + "&peso=" + encodeURIComponent(peso)
    })
            .then(r => r.json())
            .then(data => {
                mostrarNotificacion(data.ok ? "<i class='fa-solid fa-circle-check'></i> Registro guardado" : "<i class='fa-solid fa-triangle-exclamation'></i> " + data.mensaje);
                if (data.ok) {
                    document.getElementById("ejercicio").value = "";
                    document.getElementById("repeticiones").value = "";
                    document.getElementById("peso").value = "";
                    cargarHistorial();
                    refreshGraficaMetas();
                }
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error de conexión"));
}

/* =============================================================
 METAS
 ============================================================= */
function cargarMetas() {
    const cont = document.getElementById("metas-lista");
    if (!cont)
        return;

    fetch(CTX_PATH + "/GestionMetasServlet?accion=listar")
            .then(r => r.json())
            .then(metas => {
                cont.innerHTML = "";
                if (!metas || metas.length === 0) {
                    cont.innerHTML = `<div class="card"><p style="color:#9ca3af;">No tienes metas asignadas</p></div>`;
                    return;
                }
                metas.forEach(m => {
                    cont.innerHTML += `
                <div class="card">
                    <h3>${obtenerIconoEjercicio(m.ejercicio)} ${m.ejercicio}</h3>
                    <p>Meta: <strong style="color:#ff8a8a;">${m.meta} reps</strong></p>
                </div>`;
                });
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error al cargar metas"));
}

function crearMetaCliente(e) {
    e.preventDefault();
    const ejercicio = document.getElementById("ejercicioMetaCliente").value.trim();
    const valor = document.getElementById("valorMetaCliente").value;

    fetch(CTX_PATH + "/GestionMetasServlet", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "accion=crear"
                + "&ejercicioMeta=" + encodeURIComponent(ejercicio)
                + "&valorMeta=" + encodeURIComponent(valor)
    })
            .then(r => r.json())
            .then(data => {
                mostrarNotificacion(data.ok ? "<i class='fa-solid fa-circle-check'></i> Meta creada" : "<i class='fa-solid fa-triangle-exclamation'></i> " + (data.mensaje || "No se pudo crear la meta"));
                if (data.ok) {
                    document.getElementById("ejercicioMetaCliente").value = "";
                    document.getElementById("valorMetaCliente").value = "";
                    cargarMetas();
                    refreshGraficaMetas();
                }
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error de conexión"));
}

function asignarMeta(e) {
    e.preventDefault();
    const correo = document.getElementById("correoMeta").value.trim();
    const ejercicio = document.getElementById("ejercicioMeta").value.trim();
    const valor = document.getElementById("valorMeta").value;

    fetch(CTX_PATH + "/GestionMetasServlet", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "accion=asignar"
                + "&correoMeta=" + encodeURIComponent(correo)
                + "&ejercicioMeta=" + encodeURIComponent(ejercicio)
                + "&valorMeta=" + encodeURIComponent(valor)
    })
            .then(r => r.json())
            .then(data => {
                mostrarNotificacion(data.ok ? "<i class='fa-solid fa-circle-check'></i> Meta asignada" : "<i class='fa-solid fa-triangle-exclamation'></i> " + data.mensaje);
                if (data.ok) {
                    document.getElementById("correoMeta").value = "";
                    document.getElementById("ejercicioMeta").value = "";
                    document.getElementById("valorMeta").value = "";
                }
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error de conexión"));
}

/* =============================================================
 ADMIN — USUARIOS
 ============================================================= */
function cargarUsuarios() {
    const tabla = document.getElementById("tabla-usuarios");
    if (!tabla)
        return;

    fetch(CTX_PATH + "/GestionUsuariosAdminServlet?accion=listar")
            .then(r => r.json())
            .then(usuarios => {
                tabla.innerHTML = "";
                if (!usuarios || usuarios.length === 0) {
                    tabla.innerHTML = `<tr><td colspan="7">No hay usuarios registrados</td></tr>`;
                    return;
                }
                usuarios.forEach(u => {
                    tabla.innerHTML += `
                <tr>
                    <td>${u.nombre} ${u.apellido}</td>
                    <td>${u.tipoDoc || ""}</td>
                    <td>${u.documento || ""}</td>
                    <td>${u.email || ""}</td>
                    <td>${u.membresia || ""}</td>
                    <td>${u.vencimiento || "—"}</td>
                    <td>
    <button
        data-id="${u.id_Usuarios}"
        onclick="abrirModalUsuario(this)">
        <i class="fa-solid fa-pen"></i> Editar
    </button>

    <button onclick="eliminarUsuario(${u.id_Usuarios}, '${u.email}')"
        style="background:#ff0000;color:white;border:none;margin-left:6px;">
        <i class="fa-solid fa-trash"></i> Eliminar
    </button>
</td>
                </tr>`;
                });
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error al cargar usuarios"));
}

function editarUsuario(idUsuario) {

    const campo = prompt(
            "¿Qué deseas editar?\n\n" +
            "nombre\n" +
            "documento\n" +
            "correo\n" +
            "membresia"
            );

    if (!campo)
        return;

    const valor = prompt("Nuevo valor:");

    if (!valor)
        return;

    fetch(CTX_PATH + "/GestionUsuariosAdminServlet", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body:
                "accion=editar" +
                "&id=" + encodeURIComponent(idUsuario) +
                "&campo=" + encodeURIComponent(campo) +
                "&valor=" + encodeURIComponent(valor)
    })
            .then(r => r.text())
            .then(data => {

                console.log(data);

                mostrarNotificacion("<i class='fa-solid fa-circle-check'></i> Usuario actualizado");

                cargarUsuarios();

            })
            .catch(error => {

                console.error(error);

                mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error al editar");

            });
}

function eliminarUsuario(idUsuario, correo) {
    mostrarConfirm(`¿Seguro que deseas eliminar a ${correo}?`, function () {
        fetch(CTX_PATH + "/GestionUsuariosAdminServlet", {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: "accion=eliminar&idUsuario=" + idUsuario
        })
                .then(r => r.json())
                .then(data => {
                    mostrarNotificacion(data.ok ? "<i class='fa-solid fa-trash'></i> Usuario eliminado" : "<i class='fa-solid fa-triangle-exclamation'></i> " + data.mensaje);
                    if (data.ok)
                        cargarUsuarios();
                })
                .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error de conexión"));
    }, { titulo: "Eliminar usuario", icono: "trash", textoAceptar: "Sí" });
}

function asignarVencimiento(e) {
    e.preventDefault();
    const correo = document.getElementById("correoVencimiento").value.trim();
    const fecha = document.getElementById("fechaVencimiento").value;

    fetch(CTX_PATH + "/GestionUsuariosAdminServlet", {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "accion=vencimiento"
                + "&correoVencimiento=" + encodeURIComponent(correo)
                + "&fechaVencimiento=" + encodeURIComponent(fecha)
    })
            .then(r => r.json())
            .then(data => {
                mostrarNotificacion(data.ok ? "<i class='fa-solid fa-circle-check'></i> Vencimiento asignado" : "<i class='fa-solid fa-triangle-exclamation'></i> " + data.mensaje);
                if (data.ok) {
                    document.getElementById("correoVencimiento").value = "";
                    document.getElementById("fechaVencimiento").value = "";
                }
            })
            .catch(() => mostrarNotificacion("<i class='fa-solid fa-circle-xmark'></i> Error de conexión"));
}

function cargarEvaluacionesCliente() {
    const tbody = document.getElementById("tabla-evaluaciones-cliente");
    if (!tbody)
        return;

    fetch(CTX_PATH + "/ConsultaEvaluacionFisicaServlet")
            .then(r => r.json())
            .then(evaluaciones => {
                tbody.innerHTML = "";

                if (!evaluaciones.length) {
                    tbody.innerHTML = `<tr><td colspan="5">Aun no tienes evaluaciones fisicas registradas.</td></tr>`;
                    return;
                }

                evaluaciones.forEach(ev => {
                    tbody.innerHTML += `
                        <tr>
                            <td>${ev.fecha}</td>
                            <td>${ev.peso}</td>
                            <td>${ev.edad}</td>
                            <td>${ev.condicion}</td>
                            <td>${ev.pruebas}</td>
                        </tr>`;
                });
            })
            .catch(() => {
                tbody.innerHTML = `<tr><td colspan="5">No se pudieron cargar las evaluaciones.</td></tr>`;
            });
}

setInterval(cargarEvaluacionesCliente, 8000);
document.addEventListener("DOMContentLoaded", cargarEvaluacionesCliente);

/* =============================================================
 GRÁFICAS
 ============================================================= */

/* Lee los colores del tema activo (oscuro/claro) directo de las
   variables CSS del panel, para que las gráficas de Chart.js
   siempre sean legibles sin importar el tema. */
function colorTextoTema() {
    const valor = getComputedStyle(document.body).getPropertyValue("--text").trim();
    return valor || "#f2f2f2";
}
function colorMutedTema() {
    const valor = getComputedStyle(document.body).getPropertyValue("--muted").trim();
    return valor || "#b8b8b8";
}

/* Plugin de Chart.js que dibuja un texto principal y uno secundario
   en el centro de una gráfica doughnut, igual que el centerText de
   la app móvil (ej. "3/4" + "días"). Se activa pasando la opción
   plugins.centerText = { text, subText } al configurar la gráfica. */
const centerTextPlugin = {
    id: "centerText",
    afterDraw(chart) {
        const opts = chart.config.options.plugins && chart.config.options.plugins.centerText;
        if (!opts || !opts.text) return;
        const { ctx, chartArea } = chart;
        if (!chartArea) return;
        const centerX = (chartArea.left + chartArea.right) / 2;
        const centerY = (chartArea.top + chartArea.bottom) / 2;
        ctx.save();
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.fillStyle = opts.color || colorTextoTema();
        ctx.font = "800 20px 'DM Sans', sans-serif";
        ctx.fillText(opts.text, centerX, centerY - (opts.subText ? 11 : 0));
        if (opts.subText) {
            ctx.font = "600 11px 'DM Sans', sans-serif";
            ctx.fillStyle = opts.subColor || colorMutedTema();
            ctx.fillText(opts.subText, centerX, centerY + 13);
        }
        ctx.restore();
    }
};
if (typeof Chart !== "undefined") {
    Chart.register(centerTextPlugin);
}

/* Refresca colores de leyenda y del texto central de las gráficas
   activas cuando el usuario cambia de tema, ya que Chart.js no
   recalcula esos colores solo (no están ligados a variables CSS). */
function actualizarColoresGraficas() {
    if (window.miGrafica) {
        window.miGrafica.options.plugins.legend.labels.color = colorTextoTema();
        if (window.miGrafica.options.plugins.centerText) {
            window.miGrafica.options.plugins.centerText.color = colorTextoTema();
            window.miGrafica.options.plugins.centerText.subColor = colorMutedTema();
        }
        window.miGrafica.update();
    }
    if (window.miGraficaMetas) {
        window.miGraficaMetas.options.plugins.legend.labels.color = colorTextoTema();
        if (window.miGraficaMetas.options.plugins.centerText) {
            window.miGraficaMetas.options.plugins.centerText.color = colorTextoTema();
            window.miGraficaMetas.options.plugins.centerText.subColor = colorMutedTema();
        }
        window.miGraficaMetas.update();
    }
}

function cargarGraficaAsistencia() {
    const canvas = document.getElementById("grafica");
    if (!canvas)
        return;

    const porcentaje = Math.min((ASISTENCIAS_COUNT / 4) * 100, 100);

    if (window.miGrafica)
        window.miGrafica.destroy();

    window.miGrafica = new Chart(canvas.getContext("2d"), {
        type: "doughnut",
        data: {
            labels: ["Constancia", "Faltante"],
            datasets: [{
                    data: [porcentaje, 100 - porcentaje],
                    backgroundColor: ["#38b6a6", "#e35d6a"],
                    hoverBackgroundColor: ["#5ccfc1", "#f08a93"],
                    hoverOffset: 1
                }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: "70%",
            animation: {
                animateScale: true,
                animateRotate: true,
                duration: 1200,
                easing: "easeOutQuart"
            },
            interaction: {mode: "nearest", intersect: true},
            animations: {colors: false},
            plugins: {
                legend: {
                    position: "top",
                    align: "center",
                    labels: {
                        color: colorTextoTema(),
                        boxWidth: 34,
                        boxHeight: 12,
                        padding: 10,
                        font: {
                            family: "DM Sans",
                            size: 11,
                            weight: "600"
                        }
                    }
                },
                centerText: {
                    text: `${ASISTENCIAS_COUNT}/4`,
                    subText: "días",
                    color: colorTextoTema(),
                    subColor: colorMutedTema()
                }
            }
        }
    });

    const texto = document.getElementById("texto-constancia");
    if (texto)
        texto.textContent = `Constancia semanal: ${ASISTENCIAS_COUNT}/4 días esta semana`;
}

function actualizarGraficaAsistencia(nuevoTotal) {
    const porcentaje = Math.min((nuevoTotal / 4) * 100, 100);

    if (!window.miGrafica) {
        cargarGraficaAsistencia();
        return;
    }

    window.miGrafica.options.animation = false;
    window.miGrafica.data.datasets[0].data = [porcentaje, 100 - porcentaje];
    window.miGrafica.options.plugins.centerText.text = `${nuevoTotal}/4`;
    window.miGrafica.update();

    const texto = document.getElementById("texto-constancia");
    if (texto)
        texto.textContent = `Constancia semanal: ${nuevoTotal}/4 días esta semana`;
}

function cargarGraficaMetas() {
    const canvas = document.getElementById("grafica-metas");
    if (!canvas)
        return;

    const pct = typeof METAS_PORCENTAJE !== "undefined" ? METAS_PORCENTAJE : 0;
    const cum = typeof METAS_CUMPLIDAS !== "undefined" ? METAS_CUMPLIDAS : 0;
    const total = typeof METAS_TOTAL !== "undefined" ? METAS_TOTAL : 0;

    if (window.miGraficaMetas) {
        window.miGraficaMetas.options.animation = false;
        window.miGraficaMetas.data.datasets[0].data = [pct, 100 - pct];
        window.miGraficaMetas.options.plugins.centerText.text = `${cum}/${total}`;
        window.miGraficaMetas.update();
    } else {
        window.miGraficaMetas = new Chart(canvas.getContext("2d"), {
            type: "doughnut",
            data: {
                labels: ["Cumplidas", "Faltantes"],
                datasets: [{
                        data: [pct, 100 - pct],
                        backgroundColor: ["#38b6a6", "#e35d6a"],
                        hoverBackgroundColor: ["#5ccfc1", "#f08a93"]
                    }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: "70%",
                animation: {
                    animateScale: true,
                    animateRotate: true,
                    duration: 1200,
                    easing: "easeOutBack"
                },
                plugins: {
                    legend: {labels: {color: colorTextoTema()}},
                    centerText: {
                        text: `${cum}/${total}`,
                        subText: "metas",
                        color: colorTextoTema(),
                        subColor: colorMutedTema()
                    }
                }
            }
        });
    }

    const texto = document.getElementById("texto-metas");
    if (texto) {
        texto.textContent = `Metas completadas: ${cum}/${total} (${pct}%)`;
    }
}

function refreshGraficaMetas() {
    fetch(CTX_PATH + "/GestionMetasServlet?accion=porcentaje")
            .then(r => r.json())
            .then(data => {
                METAS_PORCENTAJE = data.porcentaje || 0;
                METAS_CUMPLIDAS = data.cumplidas || 0;
                METAS_TOTAL = data.total || 0;
                cargarGraficaMetas();
            })
            .catch(() => {
            });
}

/* =============================================================
 ADMIN EXTRAS — MODAL EDITAR SEDES / PROVEEDORES / PRODUCTOS
 ============================================================= */
/* =============================================================
 ADMIN EXTRAS — MODAL AGREGAR / EDITAR
 ============================================================= */

function limpiarModalAdmin() {
    const modalTipo = document.getElementById("modalTipo");
    if (!modalTipo)
        return;

    document.getElementById("modalTipo").value = "";
    document.getElementById("modalAccion").value = "";
    document.getElementById("modalId").value = "";
    document.getElementById("modalNombre").value = "";

    document.getElementById("modalDireccion").value = "";
    document.getElementById("modalTipoProducto").value = "";
    document.getElementById("modalPrecio").value = "";

    document.getElementById("campoDireccion").style.display = "none";
    document.getElementById("campoTipoProducto").style.display = "none";
    document.getElementById("campoPrecio").style.display = "none";

    document.getElementById("modalDireccion").required = false;
    document.getElementById("modalTipoProducto").required = false;
    document.getElementById("modalPrecio").required = false;
}

function prepararCamposPorTipo(tipo) {
    if (tipo === "sede") {
        document.getElementById("campoDireccion").style.display = "block";
        document.getElementById("modalDireccion").required = true;
    }

    if (tipo === "proveedor") {
        document.getElementById("campoTipoProducto").style.display = "block";
        document.getElementById("modalTipoProducto").required = true;
    }

    if (tipo === "producto") {
        document.getElementById("campoPrecio").style.display = "block";
        document.getElementById("modalPrecio").required = true;
    }
}

function abrirModalAgregar(tipo) {
    limpiarModalAdmin();

    document.getElementById("modalTipo").value = tipo;
    document.getElementById("modalAccion").value = "agregar";
    document.getElementById("modalTitulo").textContent = "Añadir " + tipo;

    prepararCamposPorTipo(tipo);

    document.getElementById("modalEditar").classList.add("show");
}

function abrirModalEditarExtra(boton) {
    limpiarModalAdmin();

    const tipo = boton.dataset.tipo;

    document.getElementById("modalTipo").value = tipo;
    document.getElementById("modalAccion").value = "editar";
    document.getElementById("modalId").value = boton.dataset.id || "";
    document.getElementById("modalNombre").value = boton.dataset.nombre || "";
    document.getElementById("modalTitulo").textContent = "Editar " + tipo;

    prepararCamposPorTipo(tipo);

    if (tipo === "sede") {
        document.getElementById("modalDireccion").value = boton.dataset.direccion || "";
    }

    if (tipo === "proveedor") {
        document.getElementById("modalTipoProducto").value = boton.dataset.tipoProducto || "";
    }

    if (tipo === "producto") {
        document.getElementById("modalPrecio").value = boton.dataset.precio || "";
    }

    document.getElementById("modalEditar").classList.add("show");
}

function cerrarModalEditar() {
    const modal = document.getElementById("modalEditar");
    if (modal) {
        modal.classList.remove("show");
    }
}

document.addEventListener("click", function (e) {
    const modal = document.getElementById("modalEditar");

    if (modal && e.target === modal) {
        cerrarModalEditar();
    }
});
/* =============================================================
 POLLING CLIENTE
 ============================================================= */
(function iniciarPolling() {
    const esCliente = !!document.getElementById("lista-clases-inicio");
    if (!esCliente)
        return;

    setInterval(() => {
        cargarClasesInicio();
        cargarMisClasesInicio();

        if (document.getElementById("metas-lista")) {
            cargarMetas();
            refreshGraficaMetas();
        }
    }, 10000);
})();

function abrirModalUsuario(btn) {

    document.getElementById("usuarioId").value =
            btn.dataset.id;

    document.getElementById("modalUsuario")
            .classList.add("show");
}

function abrirModalEditarUsuario(btn) {
    abrirModalUsuario(btn);
}

function cerrarModalUsuario() {

    document.getElementById("modalUsuario")
            .classList.remove("show");
}

setTimeout(() => {

    const mensaje =
            document.getElementById("mensajeAdmin");

    if (mensaje) {

        mensaje.style.transition = "0.5s";
        mensaje.style.opacity = "0";

        setTimeout(() => {
            mensaje.remove();
        }, 500);
    }

}, 3000);

function mostrarCampoEditar() {

    const campo =
            document.getElementById("campoEditar").value;

    const contenedor =
            document.getElementById("contenedorNuevoValor");

    const crearSelect = (opciones) => `
            <select name="valor" required>
                <option value="">Seleccione</option>
                ${opciones.map(op => `<option value="${op.id}">${op.descripcion}</option>`).join("")}
            </select>
        `;

    if (campo === "membresia") {

        contenedor.innerHTML = crearSelect(typeof ADMIN_MEMBRESIAS !== "undefined" ? ADMIN_MEMBRESIAS : []);

    } else if (campo === "rol") {

        contenedor.innerHTML = crearSelect(typeof ADMIN_ROLES !== "undefined" ? ADMIN_ROLES : []);

    } else if (campo === "tipoDoc") {

        contenedor.innerHTML = crearSelect(typeof ADMIN_TIPOS_DOCUMENTO !== "undefined" ? ADMIN_TIPOS_DOCUMENTO : []);

    } else if (campo === "vencimiento") {

        contenedor.innerHTML = `
            <input type="date"
                   name="valor"
                   required>
        `;

    } else {

        contenedor.innerHTML = `
            <input type="text"
                   id="nuevoValor"
                   name="valor"
                   placeholder="Nuevo valor"
                   required>
        `;
    }
}
function buscarUsuariosAdmin() {

    const input = document
            .getElementById("buscadorUsuarios")
            .value
            .toLowerCase()
            .trim();

    const filas = document.querySelectorAll(
            "#tablaUsuariosAdmin tr"
            );

    filas.forEach(fila => {

        const columnas =
                fila.querySelectorAll("td");

        if (columnas.length === 0)
            return;

        const documento =
                columnas[2]
                .textContent
                .toLowerCase();

        const correo =
                columnas[3]
                .textContent
                .toLowerCase();

        const coincide =
                documento.includes(input)
                || correo.includes(input);

        fila.style.display =
                coincide ? "" : "none";
    });
}

/* -------------------------------------------------------------
 BUSCADOR DE LA SECCION ELIMINAR USUARIO
 ------------------------------------------------------------- */
function buscarUsuariosEliminar() {

    const campo = document.getElementById("buscadorEliminarUsuarios");

    if (!campo)
        return;

    const input = campo.value.toLowerCase().trim();

    const filas = document.querySelectorAll(
            "#tablaEliminarUsuarios tr"
            );

    filas.forEach(fila => {

        const columnas = fila.querySelectorAll("td");

        if (columnas.length === 0)
            return;

        const documento =
                columnas[1]
                .textContent
                .toLowerCase();

        const correo =
                columnas[2]
                .textContent
                .toLowerCase();

        const coincide =
                documento.includes(input)
                || correo.includes(input);

        fila.style.display =
                coincide ? "" : "none";
    });
}




/* =============================================================
 LANDING - CARRUSEL Y SEDES
 ============================================================= */
function iniciarCarruselesLanding() {
    document.querySelectorAll("[data-carousel]").forEach(carousel => {
        const slides = Array.from(carousel.querySelectorAll(".gym-slide"));
        const dotsWrap = carousel.querySelector("[data-carousel-dots]");
        const prev = carousel.querySelector("[data-carousel-prev]");
        const next = carousel.querySelector("[data-carousel-next]");

        if (!slides.length) return;

        const totalSlides = slides.length;
        const step = 2;
        let currentPair = 0;

        const pauseVideos = () => {
            slides.forEach(slide => {
                const video = slide.querySelector("video");
                if (video && !video.paused) video.pause();
            });
        };

        const show = (pairIndex) => {
            pauseVideos();
            const numPairs = Math.ceil(totalSlides / step);
            const normalizedPair = ((pairIndex % numPairs) + numPairs) % numPairs;
            currentPair = normalizedPair * step;

            slides.forEach((slide, i) => {
                slide.classList.remove("active", "active-second");
                if (i === currentPair) slide.classList.add("active");
                else if (i === currentPair + 1) slide.classList.add("active-second");
            });

            if (dotsWrap) {
                dotsWrap.querySelectorAll(".carousel-dot").forEach((dot, i) => {
                    dot.classList.toggle("active", i === normalizedPair);
                });
            }
        };

        if (dotsWrap && dotsWrap.children.length === 0) {
            const numPairs = Math.ceil(totalSlides / step);
            for (let i = 0; i < numPairs; i++) {
                const dot = document.createElement("button");
                dot.type = "button";
                dot.className = "carousel-dot";
                dot.setAttribute("aria-label", "Ver par " + (i + 1));
                dot.addEventListener("click", () => show(i));
                dotsWrap.appendChild(dot);
            }
        }

        const getPairIndex = () => Math.floor(currentPair / step);
        if (prev) prev.addEventListener("click", () => show(getPairIndex() - 1));
        if (next) next.addEventListener("click", () => show(getPairIndex() + 1));

        show(0);
    });
}

document.addEventListener("DOMContentLoaded", iniciarCarruselesLanding);

/* =============================================================
 LIGHTBOX - PREVISUALIZACIÓN AMPLIADA DE IMÁGENES DE LA GALERÍA
 Permite abrir cualquier imagen del carrusel en grande, y ponerla
 en pantalla completa igual que ya se puede hacer con los videos.
 ============================================================= */
document.addEventListener("DOMContentLoaded", () => {
    const overlay = document.getElementById("lightboxOverlay");
    if (!overlay) return;

    const img = document.getElementById("lightboxImg");
    const btnClose = document.getElementById("lightboxClose");
    const btnFullscreen = document.getElementById("lightboxFullscreen");

    const abrirLightbox = (src, alt) => {
        img.src = src;
        img.alt = alt || "";
        overlay.classList.add("active");
        document.body.style.overflow = "hidden";
    };

    const cerrarLightbox = () => {
        overlay.classList.remove("active");
        document.body.style.overflow = "";
        if (document.fullscreenElement === overlay || document.webkitFullscreenElement === overlay) {
            if (document.exitFullscreen) document.exitFullscreen().catch(() => {});
            else if (document.webkitExitFullscreen) document.webkitExitFullscreen();
        }
    };

    document.querySelectorAll(".js-lightbox-img, .gym-zoom-btn").forEach(el => {
        el.addEventListener("click", (e) => {
            e.preventDefault();
            e.stopPropagation();
            const figure = el.closest(".gym-slide");
            const imagen = figure ? figure.querySelector("img") : null;
            if (imagen) abrirLightbox(imagen.src, imagen.alt);
        });
    });

    btnClose.addEventListener("click", cerrarLightbox);

    overlay.addEventListener("click", (e) => {
        if (e.target === overlay) cerrarLightbox();
    });

    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape" && overlay.classList.contains("active")) cerrarLightbox();
    });

    btnFullscreen.addEventListener("click", () => {
        if (overlay.requestFullscreen) {
            overlay.requestFullscreen().catch(() => {});
        } else if (overlay.webkitRequestFullscreen) {
            overlay.webkitRequestFullscreen();
        }
    });
});

/* =============================================================
 SCROLL - EVITAR QUE LA PÁGINA SALTE ARRIBA AL SALIR DE PANTALLA
 COMPLETA. Algunos navegadores (Chrome/Edge) resetean el scroll
 de la ventana a 0 al terminar la transición de fullscreen.
 Guardamos la posición justo antes de entrar y la restauramos
 apenas se sale, en varios intentos para ganarle a la condición
 de carrera con el reset del navegador.
 ============================================================= */
document.addEventListener("DOMContentLoaded", () => {
    let scrollAntesDeFullscreen = 0;

    const guardarScroll = () => {
        scrollAntesDeFullscreen = window.scrollY;
    };

    const restaurarScroll = () => {
        const y = scrollAntesDeFullscreen;
        window.scrollTo(0, y);
        requestAnimationFrame(() => window.scrollTo(0, y));
        setTimeout(() => window.scrollTo(0, y), 50);
        setTimeout(() => window.scrollTo(0, y), 200);
    };

    document.addEventListener("fullscreenchange", () => {
        if (document.fullscreenElement) {
            guardarScroll();
        } else {
            restaurarScroll();
        }
    });

    document.addEventListener("webkitfullscreenchange", () => {
        if (document.webkitFullscreenElement) {
            guardarScroll();
        } else {
            restaurarScroll();
        }
    });

    document.querySelectorAll("video").forEach(video => {
        video.addEventListener("webkitbeginfullscreen", guardarScroll);
        video.addEventListener("webkitendfullscreen", restaurarScroll);
    });
});

/* =============================================================
 VIDEOS - PRESERVAR POSICIÓN AL ENTRAR/SALIR DE PANTALLA COMPLETA
 Algunos navegadores reinician el video (currentTime a 0) cuando
 su contenedor pasa por display:none, o al recalcular el layout
 justo al salir de pantalla completa. Guardamos la posición y el
 estado de reproducción justo antes del cambio, y los restauramos
 apenas termina la transición, sin importar la causa exacta.
 ============================================================= */
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("video").forEach(video => {
        let lastTime = 0;
        let wasPlaying = false;

        video.addEventListener("timeupdate", () => {
            lastTime = video.currentTime;
        });

        const restaurar = () => {
            // Pequeño margen: solo corregir si de verdad se reinició
            if (Math.abs(video.currentTime - lastTime) > 0.5) {
                video.currentTime = lastTime;
            }
            if (wasPlaying && video.paused) {
                video.play().catch(() => {});
            }
        };

        const alEntrarFullscreen = () => {
            wasPlaying = !video.paused;
            lastTime = video.currentTime;
        };

        // API estándar (Chrome, Firefox, Edge)
        document.addEventListener("fullscreenchange", () => {
            if (document.fullscreenElement === video) {
                alEntrarFullscreen();
            } else {
                restaurar();
            }
        });

        // Safari de escritorio (prefijo webkit)
        document.addEventListener("webkitfullscreenchange", () => {
            if (document.webkitFullscreenElement === video) {
                alEntrarFullscreen();
            } else {
                restaurar();
            }
        });

        // iOS Safari: pantalla completa nativa del <video>, API distinta
        video.addEventListener("webkitbeginfullscreen", alEntrarFullscreen);
        video.addEventListener("webkitendfullscreen", restaurar);
    });
});

/* ============================================================
   MODAL DE CONFIRMACIÓN PERSONALIZADO
   Reemplaza todos los confirm() del navegador.
   
   Uso:
     mostrarConfirm("¿Eliminar este usuario?", function() {
         // acción si acepta
     });
   O con ícono/título custom:
     mostrarConfirm("¿Eliminar?", callback, { titulo: "Confirmar", icono: "trash" });
============================================================ */

(function () {
    // Inyectar el HTML del modal si no existe
    function inyectarModal() {
        if (document.getElementById("confirmModal")) return;
        const modal = document.createElement("div");
        modal.id = "confirmModal";
        modal.innerHTML = `
          <div class="confirm-box">
            <span class="confirm-icon" id="confirmIcon"><i class="fa-solid fa-triangle-exclamation"></i></span>
            <div class="confirm-title" id="confirmTitle">¿Estás seguro?</div>
            <p class="confirm-msg" id="confirmMsg"></p>
            <div class="confirm-actions">
              <button class="btn-confirm-no"  id="confirmBtnNo">No</button>
              <button class="btn-confirm-yes" id="confirmBtnYes">Sí</button>
            </div>
          </div>`;
        document.body.appendChild(modal);

        document.getElementById("confirmBtnNo").addEventListener("click", cerrarConfirm);
        // Cerrar al click sobre el fondo
        modal.addEventListener("click", function (e) {
            if (e.target === modal) cerrarConfirm();
        });
        // Cerrar con Escape
        document.addEventListener("keydown", function (e) {
            if (e.key === "Escape") cerrarConfirm();
        });
    }

    function cerrarConfirm() {
        const modal = document.getElementById("confirmModal");
        if (modal) modal.classList.remove("show");
    }

    const ICONOS_CONFIRM = {
        trash: "fa-trash",
        ban: "fa-ban",
        check: "fa-circle-check",
        warning: "fa-triangle-exclamation"
    };

    window.mostrarConfirm = function (mensaje, callbackAceptar, opciones) {
        inyectarModal();
        opciones = opciones || {};
        document.getElementById("confirmMsg").textContent   = mensaje;
        document.getElementById("confirmTitle").textContent = opciones.titulo  || "¿Estás seguro?";
        const claveIcono = opciones.icono || "warning";
        const clasesIcono = ICONOS_CONFIRM[claveIcono] || claveIcono;
        document.getElementById("confirmIcon").innerHTML = '<i class="fa-solid ' + clasesIcono + '"></i>';

        const btnYes = document.getElementById("confirmBtnYes");
        // Clonar para quitar listeners anteriores
        const btnYesNuevo = btnYes.cloneNode(true);
        btnYes.parentNode.replaceChild(btnYesNuevo, btnYes);
        btnYesNuevo.textContent = opciones.textoAceptar || "Sí";
        btnYesNuevo.addEventListener("click", function () {
            cerrarConfirm();
            if (typeof callbackAceptar === "function") callbackAceptar();
        });

        document.getElementById("confirmModal").classList.add("show");
    };
})();

/* ============================================================
   MODAL DE AVISO (informativo, un solo botón)
   Para casos donde una acción no se puede realizar y hay que
   explicar el porqué, en vez de solo deshabilitar el botón.

   Uso:
     mostrarAlerta("No se puede eliminar: tiene una membresía activa.");
   O con ícono/título custom:
     mostrarAlerta("...", { titulo: "Acción no permitida", icono: "ban" });
============================================================ */

(function () {
    function inyectarAlerta() {
        if (document.getElementById("alertaModal")) return;
        const modal = document.createElement("div");
        modal.id = "alertaModal";
        modal.innerHTML = `
          <div class="confirm-box">
            <span class="confirm-icon" id="alertaIcon"><i class="fa-solid fa-circle-exclamation"></i></span>
            <div class="confirm-title" id="alertaTitle">Acción no permitida</div>
            <p class="confirm-msg" id="alertaMsg"></p>
            <div class="confirm-actions">
              <button class="btn-confirm-yes" id="alertaBtnOk">Entendido</button>
            </div>
          </div>`;
        document.body.appendChild(modal);

        document.getElementById("alertaBtnOk").addEventListener("click", cerrarAlerta);
        modal.addEventListener("click", function (e) {
            if (e.target === modal) cerrarAlerta();
        });
        document.addEventListener("keydown", function (e) {
            if (e.key === "Escape") cerrarAlerta();
        });
    }

    function cerrarAlerta() {
        const modal = document.getElementById("alertaModal");
        if (modal) modal.classList.remove("show");
    }

    const ICONOS_ALERTA = {
        trash: "fa-trash",
        ban: "fa-ban",
        info: "fa-circle-info",
        warning: "fa-circle-exclamation"
    };

    window.mostrarAlerta = function (mensaje, opciones) {
        inyectarAlerta();
        opciones = opciones || {};
        document.getElementById("alertaMsg").textContent = mensaje;
        document.getElementById("alertaTitle").textContent = opciones.titulo || "Acción no permitida";
        const claveIcono = opciones.icono || "warning";
        const clasesIcono = ICONOS_ALERTA[claveIcono] || claveIcono;
        document.getElementById("alertaIcon").innerHTML = '<i class="fa-solid ' + clasesIcono + '"></i>';

        document.getElementById("alertaModal").classList.add("show");
    };
})();
