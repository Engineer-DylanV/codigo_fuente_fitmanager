package pruebas;

import Controlador.Fact_DetalladaDAO;
import java.util.List;
import java.util.Scanner;
import Modelo.FactDetallada;

public class PruebaInsertarFactDetallada {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        FactDetallada detalle = new FactDetallada();
        Fact_DetalladaDAO dao = new Fact_DetalladaDAO();

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();

        // INSERTAR
        if (opcion == 1) {

            System.out.print("Cantidad: ");
            detalle.setCantidad(sc.next());

            System.out.print("ID Producto: ");
            detalle.setId_productos(sc.nextInt());

            System.out.print("ID Fact Cabecera: ");
            detalle.setId_fact_cabecera(sc.nextInt());

            if (dao.insertar(detalle))
                System.out.println("Insertado correctamente");
            else
                System.out.println("Error");
        }

        // ACTUALIZAR
        else if (opcion == 2) {

            System.out.print("ID Detalle: ");
            detalle.setId_fact_detallada(sc.nextInt());

            System.out.print("Nueva cantidad: ");
            detalle.setCantidad(sc.next());

            System.out.print("Nuevo ID Producto: ");
            detalle.setId_productos(sc.nextInt());

            System.out.print("Nuevo ID Fact Cabecera: ");
            detalle.setId_fact_cabecera(sc.nextInt());

            if (dao.actualizar(detalle))
                System.out.println("Actualizado correctamente");
            else
                System.out.println("Error");
        }

        // ELIMINAR
        else if (opcion == 3) {

            System.out.print("ID a eliminar: ");
            int id = sc.nextInt();

            if (dao.eliminar(id))
                System.out.println("Eliminado correctamente");
            else
                System.out.println("Error");
        }

        // LISTAR
        else if (opcion == 4) {

            List<FactDetallada> lista = dao.listar();

            if(lista.isEmpty()){

                System.out.println("No hay registros");

            }else{

                for(FactDetallada f : lista){

                    System.out.println("ID: " + f.getId_fact_detallada());
                    System.out.println("Cantidad: " + f.getCantidad());
                    System.out.println("ID Producto: " + f.getId_productos());
                    System.out.println("ID Factura: " + f.getId_fact_cabecera());

                    System.out.println("--------------------");
                }
            }
        }

        sc.close();
    }
}