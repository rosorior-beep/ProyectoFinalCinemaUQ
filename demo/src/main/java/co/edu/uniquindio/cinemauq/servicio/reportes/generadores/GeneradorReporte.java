package co.edu.uniquindio.cinemauq.servicio.reportes.generadores;

import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;

import java.time.LocalDate;
import java.time.LocalDateTime;


public interface GeneradorReporte {

    Reporte generar(IRepositorioCine repositorio, LocalDate desde, LocalDate hasta, LocalDateTime ahora);

    static boolean enPeriodo(LocalDateTime fecha, LocalDate desde, LocalDate hasta) {
        LocalDate d = fecha.toLocalDate();
        return !d.isBefore(desde) && !d.isAfter(hasta);
    }
}
