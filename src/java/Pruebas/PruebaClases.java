package Pruebas;

import Controlador.ClasesDAO;
import Controlador.Conexion;
import Modelo.Clases;
import java.sql.Connection;
import java.util.List;

public class PruebaClases {

    public static void main(String[] args) {
        Connection conn = Conexion.getConnect();
        ClasesDAO dao = new ClasesDAO(conn);

        Clases nueva = new Clases();
        nueva.setNombre("Funcional");
        nueva.setDia("Lunes");
        nueva.setHora("07:00:00");
        nueva.setInstructor("Instructor Prueba");

        boolean insertado = dao.insertar(nueva);
        System.out.println("Insertado: " + insertado);

        List<Clases> clases = dao.listar();
        System.out.println("Clases registradas:");
        for (Clases clase : clases) {
            System.out.println(clase.getId_clases() + " - " + clase.getNombre());
        }

        if (!clases.isEmpty()) {
            Clases primera = clases.get(0);
            primera.setNombre(primera.getNombre() + " Actualizada");
            boolean actualizado = dao.actualizar(primera);
            System.out.println("Actualizado: " + actualizado);

            Clases consultada = dao.consultar(primera.getId_clases());
            if (consultada != null) {
                System.out.println("Consultada: " + consultada.getId_clases() + " - " + consultada.getNombre());
            }
        }
    }
}