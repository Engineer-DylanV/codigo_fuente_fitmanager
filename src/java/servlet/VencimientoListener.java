package servlet;

import Controlador.EmailService;
import Controlador.UsuariosDAO;
import Modelo.Usuarios;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Al iniciar la aplicación, programa una tarea que corre una vez al día y
 * envía un correo a los usuarios cuya membresía está vencida o vence
 * dentro de los próximos DIAS_ANTES_DE_AVISAR días.
 *
 * Cada usuario recibe el aviso una sola vez por vencimiento (se controla
 * con la columna `notificado_vencimiento`, que se resetea automáticamente
 * cuando el usuario renueva o compra una nueva membresía).
 */
@WebListener
public class VencimientoListener implements ServletContextListener {

    private static final int DIAS_ANTES_DE_AVISAR = 3;
    private static final long INTERVALO_HORAS = 24;

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::revisarVencimientos, 0, INTERVALO_HORAS, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    private void revisarVencimientos() {
        try {
            UsuariosDAO dao = new UsuariosDAO();
            List<Usuarios> usuarios = dao.listarParaAvisoVencimiento(DIAS_ANTES_DE_AVISAR);

            for (Usuarios u : usuarios) {

                if (u.getEmail() == null || u.getEmail().trim().isEmpty() || u.getVencimiento() == null) {
                    continue;
                }

                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(u.getVencimiento()));

                boolean enviado = EmailService.enviarAvisoVencimiento(
                        u.getEmail(), u.getNombre(), u.getVencimiento(), diasRestantes);

                if (enviado) {
                    dao.marcarNotificadoVencimiento(u.getId_usuarios());
                }
            }

        } catch (Exception e) {
            System.out.println("Error revisando vencimientos de membresia: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
