package Modelo;

public class Rutinas {

    private int id_rutinas;
    private String nombre;
    private String descripcion;
    private int id_usuarios;
    private String objetivo;
    private String enlaceDrive;
    private String programa;

    public int getId_rutinas() {
        return id_rutinas;
    }

    public void setId_rutinas(int id_rutinas) {
        this.id_rutinas = id_rutinas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId_usuarios() {
        return id_usuarios;
    }

    public void setId_usuarios(int id_usuarios) {
        this.id_usuarios = id_usuarios;
    }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public String getEnlaceDrive() { return enlaceDrive; }
    public void setEnlaceDrive(String enlaceDrive) { this.enlaceDrive = enlaceDrive; }

    public String getPrograma() { return programa; }
    public void setPrograma(String programa) { this.programa = programa; }
}
