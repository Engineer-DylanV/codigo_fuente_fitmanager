package Modelo;

public class Proveedores {

    public int getId_proveedores() {
        return id_proveedores;
    }

    // Alias JavaBean para EL: ${proveedor.idProveedores}
    public int getIdProveedores() {
        return id_proveedores;
    }

    public void setId_proveedores(int id_proveedores) {
        this.id_proveedores = id_proveedores;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo_producto() {
        return tipo_producto;
    }

    // Alias JavaBean para EL: ${proveedor.tipoProducto}
    public String getTipoProducto() {
        return tipo_producto;
    }

    public void setTipo_producto(String tipo_producto) {
        this.tipo_producto = tipo_producto;
    }
    private int id_proveedores;
    private String nombre;
    private String tipo_producto;

}
