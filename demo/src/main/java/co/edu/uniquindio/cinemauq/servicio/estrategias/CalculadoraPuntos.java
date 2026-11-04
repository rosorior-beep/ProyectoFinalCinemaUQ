package co.edu.uniquindio.cinemauq.servicio.estrategias;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

public class CalculadoraPuntos {

    private EstrategiaPuntos estrategia;

    public CalculadoraPuntos(EstrategiaPuntos estrategiaInicial) {
        this.estrategia = estrategiaInicial;
    }

    public void setEstrategia(EstrategiaPuntos nuevaEstrategia) {
        this.estrategia = nuevaEstrategia;
    }

    public int calcularPuntos(Compra compra) {
        if (estrategia == null) {
            throw new IllegalStateException("No se ha configurado ninguna estrategia de puntos.");
        }
        return estrategia.calcularPuntos(compra);
    }

    public String getDescripcion() {
        return estrategia == null ? "Sin estrategia" : estrategia.getDescripcion();
    }
}
