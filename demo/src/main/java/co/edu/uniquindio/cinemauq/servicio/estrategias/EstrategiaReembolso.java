package co.edu.uniquindio.cinemauq.servicio.estrategias;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.LocalDateTime;

public interface EstrategiaReembolso {

    double calcularPorcentaje(Compra compra, LocalDateTime ahora);

    String getDescripcion();
}
