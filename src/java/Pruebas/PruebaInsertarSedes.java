/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Pruebas;

import Controlador.SedesDAO;
import Modelo.Sedes;
import Controlador.Conexion;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class PruebaInsertarSedes {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();
        if (conn == null) return;

        SedesDAO dao = new SedesDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();
        sc.nextLine(); 
// CREAR        
        Sedes sede = new Sedes();

        switch (opcion) {

            case 1:
                System.out.print("Nombre: ");
                sede.setNombre(sc.nextLine());

                System.out.print("Direccion: ");
                sede.setDireccion(sc.nextLine());

                if (dao.insertar(sede)) {
                    System.out.println("Sede insertada correctamente");
                } else {
                    System.out.println("Error al insertar");
                }
                break;
// ACTUALIZAR
            case 2:
                System.out.print("ID a actualizar: ");
                sede.setId_sedes(sc.nextInt());
                sc.nextLine();

                System.out.print("Nuevo nombre: ");
                sede.setNombre(sc.nextLine());

                System.out.print("Nueva direccion: ");
                sede.setDireccion(sc.nextLine());

                if (dao.actualizar(sede)) {
                    System.out.println("Sede actualizada");
                } else {
                    System.out.println("Error al actualizar");
                }
                break;
// ELIMINAR
            case 3:
                System.out.print("ID a eliminar: ");
                int idEliminar = sc.nextInt();

                if (dao.eliminar(idEliminar)) {
                    System.out.println("Sede eliminada");
                } else {
                    System.out.println("Error al eliminar");
                }
                break;
// LISTAR
            case 4:
                List<Sedes> lista = dao.listar();

                for (Sedes s : lista) {
                    System.out.println("ID: " + s.getId_sedes()
                            + " | Nombre: " + s.getNombre()
                            + " | Direccion: " + s.getDireccion());
                }
                break;

            default:
                System.out.println("Opcion invalida");
        }

        sc.close();
    }
}