package Pruebas;

import Controlador.TipoDocumentoDAO;
import Controlador.Conexion;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;
import Modelo.TipoDocumento;

public class PruebaInsertarTipoDocumento {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();

        if (conn == null) {
            System.out.println("No hay conexión.");
            return;
        }

        TipoDocumentoDAO dao = new TipoDocumentoDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Insertar");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Listar");
        System.out.print("Seleccione opcion: ");

        int opcion = sc.nextInt();
        sc.nextLine();
// CREAR
        TipoDocumento td = new TipoDocumento();

        switch (opcion) {

            case 1:
                System.out.print("Descripcion: ");
                td.setDescripcion(sc.nextLine());

                if (dao.insertar(td)) {
                    System.out.println("TipoDocumento insertado correctamente");
                } else {
                    System.out.println("Error al insertar");
                }
                break;
// ACTUALIZAR
            case 2:
                System.out.print("ID a actualizar: ");
                td.setId_tipo_documento(sc.nextInt());
                sc.nextLine();

                System.out.print("Nueva descripcion: ");
                td.setDescripcion(sc.nextLine());

                if (dao.actualizar(td)) {
                    System.out.println("TipoDocumento actualizado");
                } else {
                    System.out.println("Error al actualizar");
                }
                break;
// ELIMINAR
            case 3:
                System.out.print("ID a eliminar: ");
                int idEliminar = sc.nextInt();

                if (dao.eliminar(idEliminar)) {
                    System.out.println("TipoDocumento eliminado");
                } else {
                    System.out.println("Error al eliminar");
                }
                break;
// LISTAR
            case 4:
                List<TipoDocumento> lista = dao.listar();

                for (TipoDocumento t : lista) {
                    System.out.println("ID: " + t.getId_tipo_documento() +
                                       " | Descripcion: " + t.getDescripcion());
                }
                break;

            default:
                System.out.println("Opcion invalida");
        }

        sc.close();
    }
}