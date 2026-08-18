package Modelo;

public class FactCabecera {

    private int id_fact_cabecera;
    private String fecha_fact;
    private String n_factura;
    private String valor_total;
    private String concepto;
    private int id_metodo_pago;
    private int id_usuarios;

    public int getId_fact_cabecera() {
        return id_fact_cabecera;
    }

    public void setId_fact_cabecera(int id_fact_cabecera) {
        this.id_fact_cabecera = id_fact_cabecera;
    }

    public String getFecha_fact() {
        return fecha_fact;
    }

    public void setFecha_fact(String fecha_fact) {
        this.fecha_fact = fecha_fact;
    }

    public String getN_factura() {
        return n_factura;
    }

    public void setN_factura(String n_factura) {
        this.n_factura = n_factura;
    }

    public String getValor_total() {
        return valor_total;
    }

    public void setValor_total(String valor_total) {
        this.valor_total = valor_total;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public int getId_metodo_pago() {
        return id_metodo_pago;
    }

    public void setId_metodo_pago(int id_metodo_pago) {
        this.id_metodo_pago = id_metodo_pago;
    }

    public int getId_usuarios() {
        return id_usuarios;
    }

    public void setId_usuarios(int id_usuarios) {
        this.id_usuarios = id_usuarios;
    }
}