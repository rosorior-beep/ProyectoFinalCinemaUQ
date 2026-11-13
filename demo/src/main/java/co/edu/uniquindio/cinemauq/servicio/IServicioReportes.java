package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.servicio.reportes.Indicadores;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

public interface IServicioReportes {

    Reporte generar(TipoReporte tipo, LocalDate desde, LocalDate hasta);

    Path exportar(Reporte reporte, String extension, Path destino);

    Indicadores indicadores(LocalDate desde, LocalDate hasta);

    Map<LocalDate, Double> ventasPorDia(LocalDate desde, LocalDate hasta);

    Map<LocalDate, Double> recargasPorDia(LocalDate desde, LocalDate hasta);

    Map<String, Long> entradasPorPelicula(LocalDate desde, LocalDate hasta);

    Map<String, Double> ocupacionPorSala(LocalDate desde, LocalDate hasta);

    Map<String, Double> ventasPorCategoria(LocalDate desde, LocalDate hasta);

    Map<String, Long> productosMasVendidos(LocalDate desde, LocalDate hasta, int limite);
}
