package servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * GlassFish/Payara configura la propiedad global de la JVM
 * "javax.net.ssl.keyStore" para que apunte a su propio keystore.jks
 * (el que usa para la consola de administración HTTPS).
 *
 * El driver MySQL Connector/J, al abrir una conexión SSL hacia Azure,
 * hereda esa misma propiedad de la JVM y trata de usar el keystore de
 * GlassFish con la contraseña por defecto del driver, lo cual falla con
 * UnrecoverableKeyException. Esto no tiene relación con la contraseña
 * de la base de datos: es un choque de configuración a nivel de proceso.
 *
 * Este listener limpia esas propiedades apenas arranca la aplicación,
 * antes de que se abra ninguna conexión a la base de datos, para que el
 * driver use su propio manejo de SSL/TLS en vez del keystore de GlassFish.
 */
@WebListener
public class SSLKeystoreFixListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.clearProperty("javax.net.ssl.keyStore");
        System.clearProperty("javax.net.ssl.keyStorePassword");
        System.clearProperty("javax.net.ssl.keyStoreType");
        System.out.println("SSLKeystoreFixListener: propiedades de keystore de GlassFish limpiadas para permitir SSL del driver MySQL hacia Azure.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No se requiere limpieza al detener la aplicación.
    }
}
