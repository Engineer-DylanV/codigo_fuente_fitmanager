package Pruebas;

import Controlador.AsistenciasDAO;
import Controlador.Conexion;
import java.sql.Connection;

public class PruebaAsistencias {

    public static void main(String[] args) {
        Connection conn = Conexion.getConnect();
        AsistenciasDAO dao = new AsistenciasDAO(conn);

        int idUsuario = 1;
        boolean registrada = dao.registrarHoy(idUsuario);

        System.out.println("Registrada hoy: " + registrada);
        System.out.println("Total del mes: " + dao.contarMesActual(idUsuario));
    }
}
