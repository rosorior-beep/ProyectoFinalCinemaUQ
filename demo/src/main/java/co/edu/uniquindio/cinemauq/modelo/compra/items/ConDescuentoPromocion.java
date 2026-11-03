package co.edu.uniquindio.cinemauq.modelo.compra.items;

import co.edu.uniquindio.cinemauq.modelo.promociones.Promocion;


public class ConDescuentoPromocion extends CobrableDecorator {

    private final String nombrePromocion;
    private final double porcentaje;

    public ConDescuentoPromocion(Cobrable envuelto, Promocion promocion) {
        super(envuelto);
        this.nombrePromocion = promocion.getNombre();
        this.porcentaje = promocion.getPorcentaje();
    }

    @Override
    public double getPrecio() {
        return Math.round(envuelto.getPrecio() * (1 - porcentaje / 100.0));
    }

    @Override
    public String getDescripcion() {
        return envuelto.getDescripcion() + " [" + nombrePromocion + " -" + (int) porcentaje + "%]";
    }
}
