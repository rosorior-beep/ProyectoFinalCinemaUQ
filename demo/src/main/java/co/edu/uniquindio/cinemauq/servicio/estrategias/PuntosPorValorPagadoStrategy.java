package co.edu.uniquindio.cinemauq.servicio.estrategias;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

public class PuntosPorValorPagadoStrategy implements EstrategiaPuntos {

    private final double valorPorPunto;

    public PuntosPorValorPagadoStrategy(double valorPorPunto) {
        this.valorPorPunto = valorPorPunto;
    }

    @Override
    public int calcularPuntos(Compra compra) {
        return (int) Math.floor(compra.getTotalAPagar() / valorPorPunto);
    }

    @Override
    public String getDescripcion() {
        return String.format("1 punto por cada $%,.0f pagados con la tarjeta", valorPorPunto);
    }
}
