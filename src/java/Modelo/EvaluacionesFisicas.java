package Modelo;

public class EvaluacionesFisicas {

    private int id_evaluaciones_fisicas;
    private String fecha;
    private double peso;
    private int edad;
    private String condicion;
    private String pruebas;
    private int id_usuarios;

    public int getId_evaluaciones_fisicas() {
        return id_evaluaciones_fisicas;
    }

    public void setId_evaluaciones_fisicas(int id) {
        this.id_evaluaciones_fisicas = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getPruebas() {
        return pruebas;
    }

    public void setPruebas(String pruebas) {
        this.pruebas = pruebas;
    }

    public int getId_usuarios() {
        return id_usuarios;
    }

    public void setId_usuarios(int id_usuarios) {
        this.id_usuarios = id_usuarios;
    }
}