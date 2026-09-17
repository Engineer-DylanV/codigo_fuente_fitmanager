package Pruebas;

import Controlador.MembresiasDAO;
import Modelo.Membresias;
import java.util.List;
import java.util.Scanner;

public class PruebaInsertarMembresia {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        MembresiasDAO dao = new MembresiasDAO();

        Membresias m = new Membresias();

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();
        sc.nextLine();

        // INSERTAR
        if(opcion==1){

            System.out.print("Precio: ");
            m.setPrecio(sc.nextDouble());
            sc.nextLine();

            System.out.print("Tipo: ");
            m.setTipo(sc.nextLine());

            System.out.print("Duracion dias: ");
            m.setDuracion_dias(sc.nextInt());
            sc.nextLine();

            if(dao.insertar(m))
                System.out.println("Insertado correctamente");
            else
                System.out.println("Error");
        }

        // ACTUALIZAR
        else if(opcion==2){

            System.out.print("ID Membresia: ");
            m.setId_membresias(sc.nextInt());
            sc.nextLine();

            System.out.print("Nuevo precio: ");
            m.setPrecio(sc.nextDouble());
            sc.nextLine();

            System.out.print("Nuevo tipo: ");
            m.setTipo(sc.nextLine());

            System.out.print("Nueva duracion: ");
            m.setDuracion_dias(sc.nextInt());
            sc.nextLine();

            if(dao.actualizar(m))
                System.out.println("Actualizado correctamente");
            else
                System.out.println("Error");
        }

        // ELIMINAR
        else if(opcion==3){

            System.out.print("ID eliminar: ");

            int id=sc.nextInt();

            if(dao.eliminar(id))
                System.out.println("Eliminado");
            else
                System.out.println("Error");
        }

        // LISTAR
        else if(opcion==4){

            List<Membresias> lista=dao.listar();

            if(lista.isEmpty()){

                System.out.println("No existen registros");

            }else{

                for(Membresias x:lista){

                    System.out.println("ID: "+x.getId_membresias());
                    System.out.println("Precio: "+x.getPrecio());
                    System.out.println("Tipo: "+x.getTipo());
                    System.out.println("Duracion: "+x.getDuracion_dias());
                    System.out.println("--------------------");
                }
            }
        }

        sc.close();
    }
}