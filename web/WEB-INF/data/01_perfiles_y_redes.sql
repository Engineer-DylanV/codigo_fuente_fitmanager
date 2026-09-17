-- Migración de datos para el perfil de cliente y enlaces sociales.
-- Ejecutar una sola vez sobre la base de datos configurada por MYSQLDATABASE.

CREATE TABLE IF NOT EXISTS generos (
    codigo CHAR(1) NOT NULL,
    descripcion VARCHAR(30) NOT NULL,
    PRIMARY KEY (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO generos (codigo, descripcion) VALUES
    ('M', 'Masculino'),
    ('F', 'Femenino')
ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

CREATE TABLE IF NOT EXISTS avatares_perfil (
    id_avatar INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(40) NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    icono VARCHAR(60) NOT NULL,
    color_inicio VARCHAR(12) NOT NULL,
    color_fin VARCHAR(12) NOT NULL,
    orden INT NOT NULL DEFAULT 0,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_avatar),
    UNIQUE KEY uk_avatares_perfil_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO avatares_perfil (codigo, nombre, icono, color_inicio, color_fin, orden, activo) VALUES
    ('strong', 'Fuerza', 'fa-dumbbell', '#c92f35', '#6f171c', 1, 1),
    ('energy', 'Energía', 'fa-bolt', '#f59e0b', '#c2410c', 2, 1),
    ('focus', 'Enfoque', 'fa-bullseye', '#2563eb', '#1d4ed8', 3, 1),
    ('zen', 'Bienestar', 'fa-heart-pulse', '#0d9488', '#0f766e', 4, 1)
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre), icono = VALUES(icono), color_inicio = VALUES(color_inicio),
    color_fin = VALUES(color_fin), orden = VALUES(orden), activo = VALUES(activo);

CREATE TABLE IF NOT EXISTS perfiles_usuario (
    id_usuarios INT NOT NULL,
    avatar_codigo VARCHAR(40) NULL,
    foto_mime VARCHAR(40) NULL,
    foto_datos MEDIUMBLOB NULL,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuarios),
    CONSTRAINT fk_perfiles_usuario_usuario FOREIGN KEY (id_usuarios)
        REFERENCES usuarios(id_usuarios) ON DELETE CASCADE,
    CONSTRAINT fk_perfiles_usuario_avatar FOREIGN KEY (avatar_codigo)
        REFERENCES avatares_perfil(codigo) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS redes_sociales (
    id_red_social INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(60) NOT NULL,
    url VARCHAR(500) NOT NULL,
    icono VARCHAR(60) NOT NULL,
    css_clase VARCHAR(40) NOT NULL,
    orden INT NOT NULL DEFAULT 0,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_red_social),
    UNIQUE KEY uk_redes_sociales_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO redes_sociales (nombre, url, icono, css_clase, orden, activo) VALUES
    ('Facebook', 'https://www.facebook.com/share/1FA6sZk68j/', 'fa-facebook-f', 'facebook', 1, 1),
    ('Instagram', 'https://www.instagram.com/taurusgympowerlifting', 'fa-instagram', 'instagram', 2, 1),
    ('TikTok', 'https://www.tiktok.com/@taurusgympower', 'fa-tiktok', 'tiktok', 3, 1)
ON DUPLICATE KEY UPDATE
    url = VALUES(url), icono = VALUES(icono), css_clase = VALUES(css_clase),
    orden = VALUES(orden), activo = VALUES(activo);
