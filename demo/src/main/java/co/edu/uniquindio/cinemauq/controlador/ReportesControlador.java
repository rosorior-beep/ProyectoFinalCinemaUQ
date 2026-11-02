package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.fachada.CinemaFacade;
import co.edu.uniquindio.cinemauq.servicio.IServicioReportes;
import co.edu.uniquindio.cinemauq.servicio.reportes.Indicadores;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.nio.file.Path;
import java.time.LocalDate;


public class ReportesControlador {

    private final CinemaFacade fachada;

    public ReportesControlador(CinemaFacade fachada) {
        this.fachada = fachada;
    }

    public LocalDate hoy() {
        return fachada.ahora().toLocalDate();
    }

    public Reporte generar(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        if (tipo == null) throw new DatosInvalidosException("Selecciona el tipo de reporte.");
        return fachada.generarReporte(tipo, desde, hasta);
    }

    public Path exportar(Reporte reporte, String extension, Path destino) {
        if (reporte == null) throw new DatosInvalidosException("Primero genera un reporte.");
        return fachada.exportarReporte(reporte, extension, destino);
    }

    public Indicadores indicadores(LocalDate desde, LocalDate hasta) {
        return fachada.indicadores(desde, hasta);
    }

    public IServicioReportes estadisticas() {
        return fachada.estadisticas();
    }
}
