package co.edu.uniquindio.cinemauq.modelo.tarjeta;

public enum TipoMovimiento {
    PAGO("Pago"), RECARGA("Recarga"), REEMBOLSO("Reembolso");

    private final String etiqueta;

    TipoMovimiento(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}
