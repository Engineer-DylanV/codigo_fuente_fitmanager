package Pruebas;

import Controlador.Fact_DetalladaDAO;
import Modelo.FactDetallada;
import java.util.Scanner;

public class PruebaConsultarFactDetallada {

    public static void main(String[] args) {

        Fact_DetalladaDAO dao = new Fact_DetalladaDAO();
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el ID de la Fact Detallada: ");
        int id = sc.nextInt();

        FactDetallada f = dao.consultar(id);

        if (f != null) {
            System.out.println("Fact Detallada encontrada");
            System.out.println("ID: " + f.getId_fact_detallada());
            System.out.println("Cantidad: " + f.getCantidad());
            System.out.println("ID Producto: " + f.getId_productos());
            System.out.println("ID Fact Cabecera: " + f.getId_fact_cabecera()); 
        } else {
            System.out.println("No se encontró la Fact Detallada");
        }

        sc.close();
    }
}