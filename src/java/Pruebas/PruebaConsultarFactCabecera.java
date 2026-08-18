package Pruebas;

import Controlador.Conexion;
import Controlador.Fact_CabeceraDAO;
import Modelo.FactCabecera;
import java.sql.Connection;
import java.util.Scanner;

public class PruebaConsultarFactCabecera {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();
        Fact_CabeceraDAO fact_cabeceraDAO = new Fact_CabeceraDAO();

        Scanner sc = new Scanner(System.in);

        // Pedir ID al usuario
        System.out.print("Ingrese el ID de la factura: ");
        int id_fact_cabecera = sc.nextInt();
            FactCabecera factcabecera = fact_cabeceraDAO.consultaFactCabecera(id_fact_cabecera);

        if (factcabecera != null) {
            System.out.println("Factura encontrada");
            System.out.println("ID Factura: " + factcabecera.getId_fact_cabecera());
            System.out.println("Fecha: " + factcabecera.getFecha_fact());
            System.out.println("Numero Factura: " + factcabecera.getN_factura());
            System.out.println("Valor Total: " + factcabecera.getValor_total());
            System.out.println("ID Metodo Pago: " + factcabecera.getId_metodo_pago());
            System.out.println("ID Usuario: " + factcabecera.getId_usuarios());

        } else {
            System.out.println("No se encontró la factura con ese ID");
        }

        sc.close();
    }
}