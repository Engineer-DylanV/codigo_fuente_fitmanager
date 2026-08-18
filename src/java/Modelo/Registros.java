package Modelo;

import java.sql.Date;

public class Registros {

    private int id_registros;
    private String ejercicio;
    private int repeticiones;
    private double peso;
    private Date fecha;
    private int id_usuarios;

    public int getId_registros() {
        return id_registros;
    }

    public void setId_registros(int id_registros) {
        this.id_registros = id_registros;
    }

    public String getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(String ejercicio) {
        this.ejercicio = ejercicio;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
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