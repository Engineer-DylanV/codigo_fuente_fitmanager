package Pruebas;

import Controlador.TipoDocumentoDAO;
import Modelo.TipoDocumento;
import Controlador.Conexion;
import java.sql.Connection;
import java.util.Scanner;

public class PruebaConsultarTipoDocumento {

    public static void main(String[] args) {

        Connection conn = Conexion.getConnect();
        TipoDocumentoDAO dao = new TipoDocumentoDAO(conn);
        Scanner sc = new Scanner(System.in);

        System.out.print("Ingrese el ID del tipo de documento: ");
        int id = sc.nextInt();

        TipoDocumento td = dao.consultar(id);

        if (td != null) {
            System.out.println("Tipo de documento encontrado");
            System.out.println("ID: " + td.getId_tipo_documento());
            System.out.println("Descripción: " + td.getDescripcion());
        } else {
            System.out.println("No se encontró el tipo de documento");
        }

        sc.close();
    }
}