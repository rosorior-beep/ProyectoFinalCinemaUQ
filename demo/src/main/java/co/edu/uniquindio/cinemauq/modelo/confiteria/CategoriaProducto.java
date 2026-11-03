package co.edu.uniquindio.cinemauq.modelo.confiteria;

public enum CategoriaProducto {
    CRISPETAS("Crispetas"), BEBIDA("Bebida"), DULCES("Dulces"), COMIDA("Comida"), COMBO("Combo");

    private final String etiqueta;

    CategoriaProducto(String etiqueta) { this.etiqueta = etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}