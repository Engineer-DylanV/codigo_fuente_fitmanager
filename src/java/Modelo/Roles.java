package Modelo;

public class Roles {

    private int id_roles;
    private String descripcion;

    public Roles() {}

    public Roles(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId_roles() {
        return id_roles;
    }

    public void setId_roles(int id_roles) {
        this.id_roles = id_roles;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}