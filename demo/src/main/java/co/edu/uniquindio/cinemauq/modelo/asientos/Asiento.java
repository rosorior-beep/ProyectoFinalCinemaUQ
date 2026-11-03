package co.edu.uniquindio.cinemauq.modelo.asientos;

import java.io.Serializable;

public abstract class Asiento implements Serializable {

    private final char fila;
    private final int numero;
    private boolean habilitado = true;

    protected Asiento(char fila, int numero) {
        this.fila = fila;
        this.numero = numero;
    }

    public abstract TipoAsiento getTipo();

    public abstract double getRecargo();

    public String getCodigo() {
        return "" + fila + numero;
    }

    public char getFila() { return fila; }
    public int getNumero() { return numero; }
    public boolean isHabilitado() { return habilitado; }
    public void setHabilitado(boolean habilitado) { this.habilitado = habilitado; }

    @Override
    public String toString() { return getCodigo() + " (" + getTipo() + ")"; }
}
