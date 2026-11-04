package co.edu.uniquindio.cinemauq.servicio.reportes.exportadores;

import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;

import java.nio.file.Path;

public interface ExportadorReporte {

    String getExtension();

    String getDescripcion();

    Path exportar(Reporte reporte, Path destino);
}
