package Modelo;

public class Clases {

    private int id_clases;
    private String nombre;
    private String dia;
    private String hora;
    private String instructor;

    public Clases() {
    }

    public Clases(int id_Clases, String nombre) {
        this.id_clases = id_Clases;
        this.nombre = nombre;
    }

    public Clases(int id_Clases, String nombre, String dia, String hora, String instructor) {
        this.id_clases = id_Clases;
        this.nombre = nombre;
        this.dia = dia;
        this.hora = hora;
        this.instructor = instructor;
    }

    public int getId_clases() {
        return id_clases;
    }

    public void setId_clases(int id_clases) {
        this.id_clases = id_clases;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
}