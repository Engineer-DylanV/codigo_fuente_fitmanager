package Pruebas;

import Controlador.Conexion;
import Controlador.RegistrosDAO;
import Modelo.Registros;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class PruebaRegistros {

    public static void main(String[] args) {
        Connection conn = Conexion.getConnect();
        RegistrosDAO dao = new RegistrosDAO(conn);

        Registros r = new Registros();
        r.setEjercicio("Press banca");
        r.setRepeticiones(20);
        r.setPeso(40);
        r.setFecha(new Date(System.currentTimeMillis()));
        r.setId_usuarios(1);

        System.out.println("Insertado: " + dao.insertar(r));

        List<Registros> registros = dao.listarPorUsuario(1);
        for (Registros registro : registros) {
            System.out.println(registro.getId_registros() + " - " + registro.getEjercicio()
                    + " - " + registro.getRepeticiones() + " reps - " + registro.getPeso() + " kg");
        }
    }
}
