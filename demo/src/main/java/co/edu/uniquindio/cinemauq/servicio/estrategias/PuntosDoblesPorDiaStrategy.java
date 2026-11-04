package co.edu.uniquindio.cinemauq.servicio.estrategias;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.DayOfWeek;


public class PuntosDoblesPorDiaStrategy implements EstrategiaPuntos {

    private final EstrategiaPuntos estrategiaBase;
    private final DayOfWeek dia;

    public PuntosDoblesPorDiaStrategy(EstrategiaPuntos estrategiaBase, DayOfWeek dia) {
        this.estrategiaBase = estrategiaBase;
        this.dia = dia;
    }

    @Override
    public int calcularPuntos(Compra compra) {
        int base = estrategiaBase.calcularPuntos(compra);
        return compra.getFechaCompra().getDayOfWeek() == dia ? base * 2 : base;
    }

    @Override
    public String getDescripcion() {
        return estrategiaBase.getDescripcion() + " (doble los " + dia + ")";
    }
}
