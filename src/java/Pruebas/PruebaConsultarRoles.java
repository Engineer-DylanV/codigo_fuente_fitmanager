package Pruebas;

import Controlador.RolesDAO;
import Modelo.Roles;
import Controlador.Conexion;
import java.sql.Connection;
import java.util.Scanner;

public class PruebaConsultarRoles {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();
        RolesDAO dao = new RolesDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el ID del rol: ");
        int id = sc.nextInt();

        Roles rol = dao.consultar(id);

        if (rol != null) {
            System.out.println("Rol encontrado");
            System.out.println("ID: " + rol.getId_roles());
            System.out.println("Descripción: " + rol.getDescripcion());
        } else {
            System.out.println("No se encontró el rol");
        }

        sc.close();
    }
}