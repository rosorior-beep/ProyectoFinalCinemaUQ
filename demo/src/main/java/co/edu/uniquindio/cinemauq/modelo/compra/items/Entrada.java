package co.edu.uniquindio.cinemauq.modelo.compra.items;

import co.edu.uniquindio.cinemauq.modelo.asientos.Asiento;
import co.edu.uniquindio.cinemauq.modelo.asientos.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;

public class Entrada implements Cobrable {

    private final String codigoAsiento;
    private final TipoAsiento tipoAsiento;
    private final String tituloPelicula;
    private final double precioBase;
    private final int puntosRedencion;

    public Entrada(Funcion funcion, Asiento asiento, int puntosRedencion) {
        this.codigoAsiento = asiento.getCodigo();
        this.tipoAsiento = asiento.getTipo();
        this.tituloPelicula = funcion.getPelicula().getTitulo();
        // Polimorfismo: cada tipo de asiento aporta su propio recargo.
        this.precioBase = funcion.getPrecioBase() + funcion.getSala().getTipo().getRecargo() + asiento.getRecargo();
        this.puntosRedencion = puntosRedencion;
    }

    @Override
    public String getDescripcion() {
        return "Entrada " + codigoAsiento + " (" + tipoAsiento + ") - " + tituloPelicula;
    }

    @Override public double getPrecioBase() { return precioBase; }
    @Override public double getPrecio() { return precioBase; }
    @Override public int getPuntosRedencion() { return puntosRedencion; }
    @Override public boolean esEntrada() { return true; }

    public String getCodigoAsiento() { return codigoAsiento; }
    public TipoAsiento getTipoAsiento() { return tipoAsiento; }
}