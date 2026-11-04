package co.edu.uniquindio.cinemauq.servicio.estrategias;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.Duration;
import java.time.LocalDateTime;


public class ReembolsoPorAnticipacionStrategy implements EstrategiaReembolso {

    private static final long MINUTOS_24_HORAS = 24 * 60;
    private static final long MINUTOS_2_HORAS = 2 * 60;

    @Override
    public double calcularPorcentaje(Compra compra, LocalDateTime ahora) {
        long minutosRestantes = Duration.between(ahora, compra.getFuncion().getFechaHora()).toMinutes();
        if (minutosRestantes > MINUTOS_24_HORAS) return 100;
        if (minutosRestantes >= MINUTOS_2_HORAS) return 80;
        return 0;
    }

    @Override
    public String getDescripcion() {
        return "Cancelación del cliente (100% / 80% / no permitida)";
    }
}
