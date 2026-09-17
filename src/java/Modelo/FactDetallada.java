package Modelo;

public class FactDetallada {

    private int id_fact_detallada;
    private String cantidad;
    private int id_productos;
    private int id_fact_cabecera;

    public int getId_fact_detallada() {
        return id_fact_detallada;
    }

    public void setId_fact_detallada(int id_fact_detallada) {
        this.id_fact_detallada = id_fact_detallada;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public int getId_productos() {
        return id_productos;
    }

    public void setId_productos(int id_productos) {
        this.id_productos = id_productos;
    }

    public int getId_fact_cabecera() {
        return id_fact_cabecera;
    }

    public void setId_fact_cabecera(int id_fact_cabecera) {
        this.id_fact_cabecera = id_fact_cabecera;
    }
}