package es.jklabs.json.loteria;

import es.jklabs.lib.loteria.enumeradores.Sorteo;

import java.io.Serializable;
import java.util.Objects;

public class Numero implements Serializable {

    private static final long serialVersionUID = -4646944882143312206L;
    private Sorteo sorteo;
    private int anyo;
    private String num;
    private double cantidad;
    private double premio;
    private boolean comprobado;

    public Sorteo getSorteo() {
        return sorteo;
    }

    public void setSorteo(Sorteo sorteo) {
        this.sorteo = sorteo;
    }

    public int getAnyo() {
        return anyo;
    }

    public void setAnyo(int anyo) {
        this.anyo = anyo;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getPremio() {
        return premio;
    }

    public void setPremio(double premio) {
        this.premio = premio;
    }

    public boolean isComprobado() {
        return comprobado;
    }

    public void setComprobado(boolean comprobado) {
        this.comprobado = comprobado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Numero numero = (Numero) o;
        return anyo == numero.anyo &&
                sorteo == numero.sorteo &&
                num.equals(numero.num);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sorteo, anyo, num);
    }
}
