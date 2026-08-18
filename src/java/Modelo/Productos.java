package Modelo;

public class Productos {

    private int id_productos;
    private String nombre;
    private double precio;

    public int getId_productos() {
        return id_productos;
    }

    // Alias JavaBean para EL: ${producto.idProductos}
    public int getIdProductos() {
        return id_productos;
    }

    public void setId_productos(int id_productos) {
        this.id_productos = id_productos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
