package co.edu.uniquindio.cinemauq.modelo.asientos;

public enum TipoAsiento {
    GENERAL("General"), PREFERENCIAL("Preferencial"), ACCESIBLE("Accesible");

    private final String etiqueta;

    TipoAsiento(String etiqueta) { this.etiqueta = etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}