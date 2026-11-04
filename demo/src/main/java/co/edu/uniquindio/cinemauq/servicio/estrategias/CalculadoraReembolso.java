package co.edu.uniquindio.cinemauq.servicio.estrategias;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.LocalDateTime;


public class CalculadoraReembolso {

    private EstrategiaReembolso estrategia;

    public CalculadoraReembolso(EstrategiaReembolso estrategiaInicial) {
        this.estrategia = estrategiaInicial;
    }

    public void setEstrategia(EstrategiaReembolso nuevaEstrategia) {
        this.estrategia = nuevaEstrategia;
    }

    public double calcularPorcentaje(Compra compra, LocalDateTime ahora) {
        if (estrategia == null) {
            throw new IllegalStateException("No se ha configurado ninguna estrategia de reembolso.");
        }
        return estrategia.calcularPorcentaje(compra, ahora);
    }

    public EstrategiaReembolso getEstrategia() {
        return estrategia;
    }
}
