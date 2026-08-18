package Pruebas;

import Controlador.UsuariosDAO;
import java.util.Scanner;
import Modelo.Usuarios;

public class PruebaConsultarUsuario {

    public static void main(String[] args) {

        UsuariosDAO usuariosDAO =
                new UsuariosDAO();

        Scanner sc =
                new Scanner(System.in);

        // Pedir datos
        System.out.print(
                "Ingrese el email: ");

        String email =
                sc.nextLine();

        System.out.print(
                "Ingrese la contraseña: ");

        String password =
                sc.nextLine();

        // Probar login
        Usuarios usuario =
                usuariosDAO.login(
                        email,
                        password);

        if (usuario != null) {

            System.out.println(
                    "\n✅ LOGIN EXITOSO");

            System.out.println(
                    "ID: "
                    + usuario.getId_usuarios());

            System.out.println(
                    "Nombre: "
                    + usuario.getNombre());

            System.out.println(
                    "Apellido: "
                    + usuario.getApellido());

            System.out.println(
                    "Documento: "
                    + usuario.getDocumento());

            System.out.println(
                    "Correo: "
                    + usuario.getEmail());

            System.out.println(
                    "Rol: "
                    + usuario.getId_roles());

            if (usuario.getId_roles()
                    == 1) {

                System.out.println(
                        "Tipo usuario: ADMIN");

            } else {

                System.out.println(
                        "Tipo usuario: CLIENTE");
            }

        } else {

            System.out.println(
                    "\n❌ Correo o contraseña incorrectos");
        }

        sc.close();
    }
}