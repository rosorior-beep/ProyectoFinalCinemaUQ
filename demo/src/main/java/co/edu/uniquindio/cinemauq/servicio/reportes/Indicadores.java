package co.edu.uniquindio.cinemauq.servicio.reportes;

public record Indicadores(double ventasNetas, long entradasVendidas, double ocupacionPromedio,
                          String peliculaMasVista, String productoMasVendido, double totalReembolsado,
                          long cancelaciones, double totalRecargado) {
}
