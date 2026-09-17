package Modelo;

public class Sedes {

    public int getId_sedes() {
        return id_sedes;
    }

    // Alias JavaBean para EL: ${sede.idSedes}
    public int getIdSedes() {
        return id_sedes;
    }

    public void setId_sedes(int id_sedes) {
        this.id_sedes = id_sedes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    private int id_sedes;
    private String nombre;
    private String direccion;
    private String horario;
    private String telefono;
    private String descripcion;


}
