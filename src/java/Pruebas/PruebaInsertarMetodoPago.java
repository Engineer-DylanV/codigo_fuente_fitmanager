/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Pruebas;

import Controlador.MetodoPagoDAO;
import java.util.ArrayList;
import java.util.Scanner;
import Modelo.MetodoPago;

public class PruebaInsertarMetodoPago {

    public static void main(String[] args) {

        MetodoPagoDAO dao = new MetodoPagoDAO();
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();
        sc.nextLine(); // limpiar buffer
// CREAR
        MetodoPago mp = new MetodoPago();

        switch (opcion) {

            case 1:
                System.out.print("Descripcion: ");
                mp.setDescripcion(sc.nextLine());

                if (dao.insertar(mp)) {
                    System.out.println("Metodo de pago insertado correctamente");
                } else {
                    System.out.println("Error al insertar");
                }
                break;
// ACTUALIZAR
            case 2:
                System.out.print("ID a actualizar: ");
                mp.setId_metodo_pago(sc.nextInt());
                sc.nextLine();

                System.out.print("Nueva descripcion: ");
                mp.setDescripcion(sc.nextLine());

                if (dao.actualizar(mp)) {
                    System.out.println("Metodo de pago actualizado");
                } else {
                    System.out.println("Error al actualizar");
                }
                break;
// ELIMINAR
            case 3:
                System.out.print("ID a eliminar: ");
                int idEliminar = sc.nextInt();

                if (dao.eliminar(idEliminar)) {
                    System.out.println("Metodo de pago eliminado");
                } else {
                    System.out.println("Error al eliminar");
                }
                break;
// LISTAR
            case 4:
                ArrayList<MetodoPago> lista = dao.listar();

                for (MetodoPago m : lista) {
                    System.out.println("ID: " + m.getId_metodo_pago() +
                                       " | Descripcion: " + m.getDescripcion());
                }
                break;

            default:
                System.out.println("Opcion invalida");
        }
    }
}