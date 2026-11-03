package co.edu.uniquindio.cinemauq.modelo.cine;

public enum Clasificacion {
    TODOS("Todo público"), MAYORES_7("+7"), MAYORES_12("+12"), MAYORES_15("+15"), MAYORES_18("+18");

    private final String etiqueta;

    Clasificacion(String etiqueta) { this.etiqueta = etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}
