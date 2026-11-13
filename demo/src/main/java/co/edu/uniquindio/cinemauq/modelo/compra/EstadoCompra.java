package co.edu.uniquindio.cinemauq.modelo.compra;

public enum EstadoCompra {
    PENDIENTE("Pendiente"),
    PAGADA("Pagada"),
    CANCELADA("Cancelada");

    private final String etiqueta;

    EstadoCompra(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
