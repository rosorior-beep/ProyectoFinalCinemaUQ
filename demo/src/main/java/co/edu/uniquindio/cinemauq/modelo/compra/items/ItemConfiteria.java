package co.edu.uniquindio.cinemauq.modelo.compra.items;

import co.edu.uniquindio.cinemauq.modelo.confiteria.CategoriaProducto;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;

public class ItemConfiteria implements Cobrable {

    private final String idProducto;
    private final String nombre;
    private final CategoriaProducto categoria;
    private final int cantidad;
    private final double precioUnitario;
    private final int puntosUnitarios;

    public ItemConfiteria(ComponenteConfiteria componente, int cantidad) {
        this.idProducto = componente.getId();
        this.nombre = componente.getNombre();
        this.categoria = componente.getCategoria();
        this.cantidad = cantidad;
        this.precioUnitario = componente.getPrecio(); // Composite: igual para producto o combo
        this.puntosUnitarios = componente.getPuntosRedencion();
    }

    @Override public String getDescripcion() { return cantidad + " x " + nombre; }
    @Override public double getPrecioBase() { return precioUnitario * cantidad; }
    @Override public double getPrecio() { return getPrecioBase(); }
    @Override public int getPuntosRedencion() { return puntosUnitarios * cantidad; }
    @Override public boolean esEntrada() { return false; }

    public String getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public CategoriaProducto getCategoria() { return categoria; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
}
