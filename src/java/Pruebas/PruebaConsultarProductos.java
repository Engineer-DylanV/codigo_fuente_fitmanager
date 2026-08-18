package Pruebas;

import Controlador.ProductosDAO;
import Modelo.Productos;
import Controlador.Conexion;
import java.sql.Connection;
import java.util.Scanner;

public class PruebaConsultarProductos {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();
        ProductosDAO dao = new ProductosDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el ID del producto: ");
        int id = sc.nextInt();

        Productos p = dao.consultar(id);

        if (p != null) {
            System.out.println("Producto encontrado");
            System.out.println("ID: " + p.getId_productos());
            System.out.println("Nombre: " + p.getNombre());
            System.out.println("Precio: " + p.getPrecio());
        } else {
            System.out.println("No se encontró el producto");
        }

        sc.close();
    }
}