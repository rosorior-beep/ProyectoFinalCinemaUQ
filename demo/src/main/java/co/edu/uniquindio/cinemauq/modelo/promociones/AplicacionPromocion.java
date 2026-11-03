package co.edu.uniquindio.cinemauq.modelo.promociones;

public enum AplicacionPromocion {
    ENTRADAS("Entradas"), CONFITERIA("Confitería"), TODO("Toda la compra");

    private final String etiqueta;

    AplicacionPromocion(String etiqueta) { this.etiqueta = etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}
