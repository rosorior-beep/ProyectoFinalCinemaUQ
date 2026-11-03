package co.edu.uniquindio.cinemauq.modelo.puntos;

public enum TipoMovimientoPuntos {
    ACUMULACION("Acumulación"),
    REDENCION("Redención"),
    DEVOLUCION("Devolución por cancelación"),
    REVERSION("Reversión por cancelación");

    private final String etiqueta;

    TipoMovimientoPuntos(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}
