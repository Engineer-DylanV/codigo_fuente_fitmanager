package Pruebas;

import Controlador.MetodoPagoDAO;
import Modelo.MetodoPago;
import java.util.Scanner;

public class PruebaConsultarMetodoPago {

    public static void main(String[] args) {

        MetodoPagoDAO dao = new MetodoPagoDAO();
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el ID del método de pago: ");
        int id = sc.nextInt();

        MetodoPago mp = dao.consultar(id);

        if (mp != null) {
            System.out.println("Método de pago encontrado");
            System.out.println("ID: " + mp.getId_metodo_pago());
            System.out.println("Descripción: " + mp.getDescripcion());
        } else {
            System.out.println("No se encontró el método de pago");
        }

        sc.close();
    }
}