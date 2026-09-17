package Pruebas;

import Controlador.SedesDAO;
import Modelo.Sedes;
import Controlador.Conexion;
import java.sql.Connection;
import java.util.Scanner;

public class PruebaConsultarSedes {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();
        SedesDAO dao = new SedesDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el ID de la sede: ");
        int id = sc.nextInt();

        Sedes s = dao.consultar(id); // también podrías usar buscarPorId(id)

        if (s != null) {
            System.out.println("Sede encontrada");
            System.out.println("ID: " + s.getId_sedes());
            System.out.println("Nombre: " + s.getNombre());
            System.out.println("Dirección: " + s.getDireccion());
        } else {
            System.out.println("No se encontró la sede");
        }

        sc.close();
    }
}