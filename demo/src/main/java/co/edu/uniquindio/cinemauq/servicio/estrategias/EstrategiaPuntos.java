package co.edu.uniquindio.cinemauq.servicio.estrategias;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

public interface EstrategiaPuntos {

    int calcularPuntos(Compra compra);

    String getDescripcion();
}
