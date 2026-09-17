/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Pruebas;

import Controlador.Conexion;
import Controlador.EvaluacionesFisicasDAO;
import Modelo.EvaluacionesFisicas;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class PruebaInsertarEvaluacionesFisicas {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();

        if (conn == null) {
            System.out.println("No hay conexión.");
            return;
        }

        EvaluacionesFisicasDAO dao = new EvaluacionesFisicasDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();
        sc.nextLine();

        EvaluacionesFisicas e = new EvaluacionesFisicas();

        switch (opcion) {

            // INSERTAR
            case 1:
                System.out.print("Fecha (YYYY-MM-DD): ");
                e.setFecha(sc.nextLine());

                System.out.print("Peso (Solo numeros en Kg): ");
                e.setPeso(sc.nextDouble());

                System.out.print("Edad (Solo numeros): ");
                e.setEdad(sc.nextInt());
                sc.nextLine();

                System.out.print("Condicion Fisica: ");
                e.setCondicion(sc.nextLine());

                System.out.print("Pruebas fisicas realizadas: ");
                e.setPruebas(sc.nextLine());

                System.out.print("ID Usuario: ");
                e.setId_usuarios(sc.nextInt());

                if (dao.insertar(e)) {
                    System.out.println("Insertado correctamente");
                } else {
                    System.out.println("Error al insertar");
                }
                break;

            // ACTUALIZAR
            case 2:
                System.out.print("ID a actualizar: ");
                e.setId_evaluaciones_fisicas(sc.nextInt());
                sc.nextLine();

                System.out.print("Nueva fecha: ");
                e.setFecha(sc.nextLine());

                System.out.print("Nuevo Peso (Solo numeros en Kg): ");
                e.setPeso(sc.nextDouble());

                System.out.print("Nueva Edad (Solo numeros): ");
                e.setEdad(sc.nextInt());
                sc.nextLine();

                System.out.print("Nueva condicion fisica: ");
                e.setCondicion(sc.nextLine());

                System.out.print("Nuevas pruebas fisicas: ");
                e.setPruebas(sc.nextLine());

                System.out.print("Nuevo ID Usuario: ");
                e.setId_usuarios(sc.nextInt());

                if (dao.actualizar(e)) {
                    System.out.println("Actualizado correctamente");
                } else {
                    System.out.println("Error al actualizar");
                }
                break;

            // ELIMINAR
            case 3:
                System.out.print("ID a eliminar: ");
                int id = sc.nextInt();

                if (dao.eliminar(id)) {
                    System.out.println("Eliminado correctamente");
                } else {
                    System.out.println("Error al eliminar");
                }
                break;

            // LISTAR
            case 4:
                List<EvaluacionesFisicas> lista = dao.listar();

                for (EvaluacionesFisicas ev : lista) {
                    System.out.println(
                        "ID: " + ev.getId_evaluaciones_fisicas() +
                        " | Fecha: " + ev.getFecha() +
                        " | Peso: " + ev.getPeso() +
                        " | Edad: " + ev.getEdad() +
                        " | Condicion fisica: " + ev.getCondicion() +
                        " | Pruebas fisicas: " + ev.getPruebas() +
                        " | Usuario: " + ev.getId_usuarios()
                    );
                }
                break;

            default:
                System.out.println("Opcion invalida");
        }

        sc.close();
    }
}