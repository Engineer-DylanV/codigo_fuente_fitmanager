package Pruebas;

import Controlador.ProveedoresDAO;
import Modelo.Proveedores;
import Controlador.Conexion;
import java.sql.Connection;
import java.util.Scanner;

public class PruebaConsultarProveedores {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();
        ProveedoresDAO dao = new ProveedoresDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el ID del proveedor: ");
        int id = sc.nextInt();

        Proveedores p = dao.consultar(id);

        if (p != null) {
            System.out.println("Proveedor encontrado");
            System.out.println("ID: " + p.getId_proveedores());
            System.out.println("Nombre: " + p.getNombre());
            System.out.println("Tipo Producto: " + p.getTipo_producto());
        } else {
            System.out.println("No se encontró el proveedor");
        }

        sc.close();
    }
}