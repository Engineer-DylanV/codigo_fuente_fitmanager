package Pruebas;

import Controlador.Conexion;
import Controlador.EvaluacionesFisicasDAO;
import Modelo.EvaluacionesFisicas;
import java.sql.Connection;
import java.util.Scanner;

public class PruebaConsultarEvaluacionesFisicas {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();
        EvaluacionesFisicasDAO evaluaciones_fisicasDAO = new EvaluacionesFisicasDAO(conn);

        Scanner sc = new Scanner(System.in);

        // Pedir ID al usuario
        System.out.print("Ingrese el ID de la evaluacion: ");
        int idEvaluacion = sc.nextInt();

        EvaluacionesFisicas evaluacion = evaluaciones_fisicasDAO.consultar(idEvaluacion);

        if (evaluacion != null) {
            System.out.println("Evaluacion Fisica encontrada");
            System.out.println("ID de la Evaluacion Fisica: " + evaluacion.getId_evaluaciones_fisicas());
            System.out.println("Fecha: " + evaluacion.getFecha());
            System.out.println("Peso: " + evaluacion.getPeso());
            System.out.println("Edad: " + evaluacion.getEdad());
            System.out.println("Condicion: " + evaluacion.getCondicion());
            System.out.println("Pruebas: " + evaluacion.getPruebas());
            System.out.println("ID del Usuario: " + evaluacion.getId_usuarios());

        } else {
            System.out.println("No se encontró la Evaluacion Fisica con ese ID");
        }

        sc.close();
    }
}