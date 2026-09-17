package pruebas;

import Controlador.Fact_CabeceraDAO;
import java.util.Scanner;
import java.util.List;
import Modelo.FactCabecera;

public class PruebaInsertarFactCabecera {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        FactCabecera factura = new FactCabecera();
        Fact_CabeceraDAO dao = new Fact_CabeceraDAO();

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();
        sc.nextLine();

        // INSERTAR
        if (opcion == 1) {

            System.out.print("Fecha (AAAA-MM-DD): ");
            factura.setFecha_fact(sc.nextLine());

            System.out.print("Numero factura: ");
            factura.setN_factura(sc.nextLine());

            System.out.print("Valor total: ");
            factura.setValor_total(sc.nextLine());

            System.out.println("Seleccione Metodo de Pago:");
            System.out.println("1. Efectivo");
            System.out.println("2. Tarjeta");
            System.out.println("3. Transferencia");
            System.out.print("Opcion: ");

            int metodo = sc.nextInt();
            factura.setId_metodo_pago(metodo);

            System.out.print("ID Usuario: ");
            factura.setId_usuarios(sc.nextInt());

            if (dao.insertar(factura))
                System.out.println("Factura insertada correctamente");
            else
                System.out.println("Error al insertar");
        }

        // ACTUALIZAR
        else if (opcion == 2) {

            System.out.print("ID de la factura a actualizar: ");
            factura.setId_fact_cabecera(sc.nextInt());
            sc.nextLine();

            System.out.print("Nueva fecha (AAAA-MM-DD): ");
            factura.setFecha_fact(sc.nextLine());

            System.out.print("Nuevo numero: ");
            factura.setN_factura(sc.nextLine());

            System.out.print("Nuevo valor total: ");
            factura.setValor_total(sc.nextLine());

            System.out.print("Nuevo ID Metodo Pago: ");
            factura.setId_metodo_pago(sc.nextInt());

            System.out.print("Nuevo ID Usuario: ");
            factura.setId_usuarios(sc.nextInt());

            if (dao.actualizar(factura))
                System.out.println("Factura actualizada correctamente");
            else
                System.out.println("Error al actualizar");
        }

        // ELIMINAR
        else if (opcion == 3) {

            System.out.print("ID de la factura a eliminar: ");
            int id = sc.nextInt();

            if (dao.eliminar(id))
                System.out.println("Factura eliminada correctamente");
            else
                System.out.println("Error al eliminar");
        }

        // LISTAR
        else if (opcion == 4) {

            List<FactCabecera> lista = dao.listar();

            if(lista.isEmpty()){
                System.out.println("No hay facturas registradas");
            }else{

                for(FactCabecera f : lista){

                    System.out.println("ID: " + f.getId_fact_cabecera());
                    System.out.println("Fecha: " + f.getFecha_fact());
                    System.out.println("Factura: " + f.getN_factura());
                    System.out.println("Valor Total: " + f.getValor_total());
                    System.out.println("Metodo Pago: " + f.getId_metodo_pago());
                    System.out.println("Usuario: " + f.getId_usuarios());

                    System.out.println("-----------------------");
                }
            }
        }

        sc.close();
    }
}