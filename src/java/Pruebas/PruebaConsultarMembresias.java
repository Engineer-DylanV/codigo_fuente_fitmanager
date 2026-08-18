package Pruebas;

import Controlador.MembresiasDAO;
import Modelo.Membresias;
import java.util.Scanner;

public class PruebaConsultarMembresias {

    public static void main(String[] args) {

        MembresiasDAO membresiasDAO = new MembresiasDAO();
        Scanner sc = new Scanner(System.in);

        // Pedir ID al usuario
        System.out.print("Ingrese el ID de la membresía: ");
        int idMembresia = sc.nextInt();

        Membresias m = membresiasDAO.consultar(idMembresia);

        if (m != null) {
            System.out.println("Membresía encontrada");
            System.out.println("ID: " + m.getId_membresias());
            System.out.println("Precio: " + m.getPrecio());
            System.out.println("Tipo: " + m.getTipo());
            System.out.println("Duración (días): " + m.getDuracion_dias());
        } else {
            System.out.println("No se encontró la membresía con ese ID");
        }

        sc.close();
    }
}