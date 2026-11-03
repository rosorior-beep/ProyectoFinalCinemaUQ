package co.edu.uniquindio.cinemauq.modelo.confiteria;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Combo extends ComponenteConfiteria {

    private final List<ComponenteConfiteria> componentes = new ArrayList<>();
    private double porcentajeDescuento;

    public Combo(String id, String nombre, double porcentajeDescuento, int puntosRedencion) {
        super(id, nombre, puntosRedencion);
        setPorcentajeDescuento(porcentajeDescuento);
    }

    @Override
    public void agregar(ComponenteConfiteria componente) {
        if (componente == this) throw new DatosInvalidosException("Un combo no puede contenerse a sí mismo.");
        componentes.add(componente);
    }

    @Override
    public void quitar(ComponenteConfiteria componente) {
        componentes.remove(componente);
    }

    @Override
    public List<ComponenteConfiteria> getComponentes() {
        return Collections.unmodifiableList(componentes);
    }

    @Override
    public double getPrecio() {
        double suma = 0;
        for (ComponenteConfiteria c : componentes) {
            suma += c.getPrecio();
        }
        return Math.round(suma * (1 - porcentajeDescuento / 100.0));
    }

    public double getPrecioSinDescuento() {
        return componentes.stream().mapToDouble(ComponenteConfiteria::getPrecio).sum();
    }

    @Override
    public CategoriaProducto getCategoria() { return CategoriaProducto.COMBO; }

    @Override
    public String getDetalle() {
        return componentes.stream().map(ComponenteConfiteria::getNombre).collect(Collectors.joining(" + "));
    }

    @Override
    public boolean esCombo() { return true; }

    public double getPorcentajeDescuento() { return porcentajeDescuento; }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento > 60) {
            throw new DatosInvalidosException("El descuento de un combo debe estar entre 0% y 60%.");
        }
        this.porcentajeDescuento = porcentajeDescuento;
    }
}