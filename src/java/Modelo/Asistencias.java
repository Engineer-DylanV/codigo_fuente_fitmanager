package Modelo;

import java.sql.Date;

public class Asistencias {

    private int id_asistencias;
    private Date fecha;
    private int id_usuarios;

    public int getId_asistencias() {
        return id_asistencias;
    }

    public void setId_asistencias(int id_asistencias) {
        this.id_asistencias = id_asistencias;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getId_usuarios() {
        return id_usuarios;
    }

    public void setId_usuarios(int id_usuarios) {
        this.id_usuarios = id_usuarios;
    }
}
