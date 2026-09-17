package Pruebas;

import Controlador.Conexion;
import Controlador.RutinasDAO;
import Modelo.Rutinas;
import java.sql.Connection;
import java.util.List;

public class PruebaRutinas {

    public static void main(String[] args) {
        Connection conn = Conexion.getConnect();
        RutinasDAO dao = new RutinasDAO(conn);

        Rutinas r = new Rutinas();
        r.setNombre("Rutina pecho");
        r.setDescripcion("Press banca, flexiones y aperturas");
        r.setId_usuarios(1);

        System.out.println("Insertado: " + dao.insertar(r));

        List<Rutinas> rutinas = dao.listarPorUsuario(1);
        for (Rutinas rutina : rutinas) {
            System.out.println(rutina.getId_rutinas() + " - " + rutina.getNombre() + " - " + rutina.getDescripcion());
        }
    }
}
