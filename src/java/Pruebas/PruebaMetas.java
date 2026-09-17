package Pruebas;

import Controlador.Conexion;
import Controlador.MetasDAO;
import Modelo.Metas;
import java.sql.Connection;
import java.util.List;

public class PruebaMetas {

    public static void main(String[] args) {
        Connection conn = Conexion.getConnect();
        MetasDAO dao = new MetasDAO(conn);

        Metas m = new Metas();
        m.setEjercicio("Press banca");
        m.setMeta(50);
        m.setId_usuarios(1);

        System.out.println("Insertado: " + dao.insertar(m));

        List<Metas> metas = dao.listarPorUsuario(1);
        for (Metas meta : metas) {
            System.out.println(meta.getId_metas() + " - " + meta.getEjercicio() + " - " + meta.getMeta());
        }
    }
}
