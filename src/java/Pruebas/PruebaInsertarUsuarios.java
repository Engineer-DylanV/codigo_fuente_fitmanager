package Pruebas;

import Controlador.UsuariosDAO;
import java.util.List;
import java.util.Scanner;
import Modelo.Usuarios;

public class PruebaInsertarUsuarios {

    public static void main(String[] args) {

        UsuariosDAO dao = new UsuariosDAO();
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();
        sc.nextLine();

        Usuarios u = new Usuarios();

        switch (opcion) {

            case 1:

                System.out.print("Nombre: ");
                u.setNombre(sc.nextLine());

                System.out.print("Apellido: ");
                u.setApellido(sc.nextLine());

                System.out.print("Documento: ");             
                u.setDocumento(sc.nextLine());

                System.out.print("Email: ");
                u.setEmail(sc.nextLine());

                System.out.print("Password: ");
                u.setPassword(sc.nextLine());

                System.out.print("Rol: ");
                u.setId_roles(sc.nextInt());

                System.out.print("Id Membresias: ");
                u.setId_membresias(sc.nextInt());

                System.out.println("Tipos de Documento:");
                System.out.println("1. T.I");
                System.out.println("2. C.C");
                System.out.println("3. C.E");
                System.out.println("4. P.E.P");

                System.out.print("Seleccione ID TipoDocumento: ");
                u.setId_tipo_documento(sc.nextInt());

                if (dao.insertar(u))
                    System.out.println("✅ Usuario insertado");
                else
                    System.out.println("❌ Error");

            break;


            case 2:

                System.out.print("Ingrese su numero de documento para actualizar sus datos: ");
                String documentoActual = sc.nextLine();

                System.out.print("Nuevo Nombre: ");
                u.setNombre(sc.nextLine());

                System.out.print("Nuevo Apellido: ");
                u.setApellido(sc.nextLine());

                System.out.print("Nuevo Documento: ");
                u.setDocumento(sc.nextLine());

                System.out.print("Nuevo Email: ");
                u.setEmail(sc.nextLine());

                System.out.print("Nuevo Password: ");
                u.setPassword(sc.nextLine());

                System.out.print("Nuevo ID TipoDocumento: ");
                u.setId_tipo_documento(sc.nextInt());

                System.out.print("Nuevo ID Rol: ");
                u.setId_roles(sc.nextInt());

                System.out.print("Nuevo Id Membresias: ");
                u.setId_membresias(sc.nextInt());

                if (dao.actualizar(u, documentoActual))
                    System.out.println("✅ Usuario actualizado");
                else
                    System.out.println("❌ Error al actualizar");

            break;


            case 3:

                System.out.print("ID Usuario a eliminar: ");
                int idEliminar = sc.nextInt();

                if (dao.eliminar(idEliminar))
                    System.out.println("✅ Usuario eliminado");
                else
                    System.out.println("❌ Error");

            break;


            case 4:

                List<Usuarios> lista = dao.listar();

                for (Usuarios user : lista) {

                    System.out.println(
                            "ID: " + user.getId_usuarios()
                            + " | Nombre: " + user.getNombre()
                            + " | Apellido: " + user.getApellido()
                            + " | Documento: " + user.getDocumento()
                            + " | Email: " + user.getEmail()
                            + " | Password: " + user.getPassword()
                            + " | Rol: " + user.getId_roles()
                    );
                }

            break;

            default:
                System.out.println("Opción inválida");
        }

        sc.close();
    }
}