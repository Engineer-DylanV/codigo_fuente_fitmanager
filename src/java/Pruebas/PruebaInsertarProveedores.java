/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Pruebas;

import Controlador.ProveedoresDAO;
import Controlador.Conexion;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;
import Modelo.Proveedores;

public class PruebaInsertarProveedores {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();

        if (conn == null) {
            System.out.println("No hay conexión.");
            return;
        }

        ProveedoresDAO dao = new ProveedoresDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();
        sc.nextLine();
// CREAR
        Proveedores p = new Proveedores();

        switch (opcion) {

            case 1:
                System.out.print("Nombre: ");
                p.setNombre(sc.nextLine());

                System.out.print("Tipo de producto: ");
                p.setTipo_producto(sc.nextLine());

                if (dao.insertar(p)) {
                    System.out.println("Proveedor insertado correctamente");
                } else {
                    System.out.println("Error al insertar");
                }
                break;
// ACTUALIZAR
            case 2:
                System.out.print("ID a actualizar: ");
                p.setId_proveedores(sc.nextInt());
                sc.nextLine();

                System.out.print("Nuevo nombre: ");
                p.setNombre(sc.nextLine());

                System.out.print("Nuevo tipo producto: ");
                p.setTipo_producto(sc.nextLine());

                if (dao.actualizar(p)) {
                    System.out.println("Proveedor actualizado");
                } else {
                    System.out.println("Error al actualizar");
                }
                break;
// ELIMINAR
            case 3:
                System.out.print("ID a eliminar: ");
                int idEliminar = sc.nextInt();

                if (dao.eliminar(idEliminar)) {
                    System.out.println("Proveedor eliminado");
                } else {
                    System.out.println("Error al eliminar");
                }
                break;
// LISTAR
            case 4:
                List<Proveedores> lista = dao.listar();

                for (Proveedores prov : lista) {
                    System.out.println(
                        "ID: " + prov.getId_proveedores() +
                        " | Nombre: " + prov.getNombre() +
                        " | Tipo: " + prov.getTipo_producto()
                    );
                }
                break;

            default:
                System.out.println("Opcion invalida");
        }

        sc.close();
    }
}