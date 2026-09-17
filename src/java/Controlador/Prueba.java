package Controlador;

import java.sql.Connection;
/**
 *
 * @author ke281 y dylan
*/
public class Prueba {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
     Conexion con = new Conexion();

     Connection reg = con.getConnect();
    }
    
}
