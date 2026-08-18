package Pruebas;

import Controlador.Conexion;
import Controlador.ProductosDAO;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;
import Modelo.Productos;

public class PruebaInsertarProductos {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();

        if (conn == null) {
            System.out.println("No hay conexión con la base de datos.");
            return;
        }

        ProductosDAO dao = new ProductosDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();
        sc.nextLine();
// CREAR
        Productos p = new Productos();

        switch (opcion) {

            case 1:
                System.out.print("Nombre: ");
                p.setNombre(sc.nextLine());

                System.out.print("Precio: ");
                p.setPrecio(sc.nextDouble());

                if (dao.insertar(p)) {
                    System.out.println("Producto insertado correctamente");
                } else {
                    System.out.println("Error al insertar");
                }
                break;
// ACTUALIZAR
            case 2:
                System.out.print("ID a actualizar: ");
                p.setId_productos(sc.nextInt());
                sc.nextLine();

                System.out.print("Nuevo nombre: ");
                p.setNombre(sc.nextLine());

                System.out.print("Nuevo precio: ");
                p.setPrecio(sc.nextDouble());

                if (dao.actualizar(p)) {
                    System.out.println("Producto actualizado");
                } else {
                    System.out.println("Error al actualizar");
                }
                break;
// ELIMINAR
            case 3:
                System.out.print("ID a eliminar: ");
                int idEliminar = sc.nextInt();

                if (dao.eliminar(idEliminar)) {
                    System.out.println("Producto eliminado");
                } else {
                    System.out.println("Error al eliminar");
                }
                break;
// LISTAR
            case 4:
                List<Productos> lista = dao.listar();

                for (Productos prod : lista) {
                    System.out.println(
                        "ID: " + prod.getId_productos() +
                        " | Nombre: " + prod.getNombre() +
                        " | Precio: " + prod.getPrecio()
                    );
                }
                break;

            default:
                System.out.println("Opcion invalida");
        }

        sc.close();
    }
}