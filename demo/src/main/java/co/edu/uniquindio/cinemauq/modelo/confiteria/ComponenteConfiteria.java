package co.edu.uniquindio.cinemauq.modelo.confiteria;

import co.edu.uniquindio.cinemauq.excepciones.OperacionNoPermitidaException;

import java.io.Serializable;
import java.util.List;


public abstract class ComponenteConfiteria implements Serializable {

    private final String id;
    private String nombre;
    private int puntosRedencion;
    private boolean disponible = true;

    protected ComponenteConfiteria(String id, String nombre, int puntosRedencion) {
        this.id = id;
        this.nombre = nombre;
        this.puntosRedencion = puntosRedencion;
    }

    public abstract double getPrecio();

    public abstract CategoriaProducto getCategoria();

    public abstract String getDetalle();

    public void agregar(ComponenteConfiteria componente) {
        throw new OperacionNoPermitidaException("No se pueden agregar elementos a un producto individual.");
    }

    public void quitar(ComponenteConfiteria componente) {
        throw new OperacionNoPermitidaException("No se pueden quitar elementos de un producto individual.");
    }

    public List<ComponenteConfiteria> getComponentes() {
        return List.of();
    }

    public boolean esCombo() { return false; }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getPuntosRedencion() { return puntosRedencion; }
    public void setPuntosRedencion(int puntosRedencion) { this.puntosRedencion = puntosRedencion; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public String toString() { return nombre; }
}