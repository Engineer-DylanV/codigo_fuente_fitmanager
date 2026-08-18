package pruebas;

import Controlador.RolesDAO;
import Controlador.Conexion;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;
import Modelo.Roles;

public class PruebaInsertarRoles {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();

        if (conn == null) {
            System.out.println("No hay conexión.");
            return;
        }

        RolesDAO dao = new RolesDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();
        sc.nextLine();
// CREAR
        Roles rol = new Roles();

        switch (opcion) {

            case 1:
                System.out.print("Descripcion: ");
                rol.setDescripcion(sc.nextLine());

                if (dao.insertar(rol)) {
                    System.out.println("Rol insertado correctamente");
                } else {
                    System.out.println("Error al insertar");
                }
                break;
// ACTUALIZAR
            case 2:
                System.out.print("ID a actualizar: ");
                rol.setId_roles(sc.nextInt());
                sc.nextLine();

                System.out.print("Nueva descripcion: ");
                rol.setDescripcion(sc.nextLine());

                if (dao.actualizar(rol)) {
                    System.out.println("Rol actualizado");
                } else {
                    System.out.println("Error al actualizar");
                }
                break;
// ELIMINAR
            case 3:
                System.out.print("ID a eliminar: ");
                int idEliminar = sc.nextInt();

                if (dao.eliminar(idEliminar)) {
                    System.out.println("Rol eliminado");
                } else {
                    System.out.println("Error al eliminar");
                }
                break;
// LISTAR
            case 4:
                List<Roles> lista = dao.listar();

                for (Roles r : lista) {
                    System.out.println("ID: " + r.getId_roles() +
                                       " | Descripcion: " + r.getDescripcion());
                }
                break;

            default:
                System.out.println("Opcion invalida");
        }

        sc.close();
    }
}