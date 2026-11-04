package co.edu.uniquindio.cinemauq.servicio.reportes.exportadores;

import java.util.List;

public final class FabricaExportadores {

    private FabricaExportadores() {
    }

    public static List<ExportadorReporte> disponibles() {
        return List.of(new AdaptadorPdfBox(), new ExportadorTxt());
    }

    public static ExportadorReporte paraExtension(String extension) {
        return disponibles().stream().filter(e -> e.getExtension().equalsIgnoreCase(extension)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Formato no soportado: " + extension));
    }
}
