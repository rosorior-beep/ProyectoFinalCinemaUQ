package co.edu.uniquindio.cinemauq.servicio.reportes;

public enum TipoReporte {
    VENTAS_PERIODO("Ventas por periodo"),
    ENTRADAS_POR_PELICULA("Entradas vendidas por película"),
    ENTRADAS_POR_FUNCION("Entradas vendidas por función"),
    OCUPACION_SALAS("Ocupación de las salas"),
    PRODUCTOS_VENDIDOS("Productos y combos vendidos"),
    RECARGAS("Recargas realizadas"),
    CANCELACIONES("Cancelaciones y reembolsos"),
    PUNTOS("Puntos acumulados y redimidos");

    private final String titulo;

    TipoReporte(String titulo) { this.titulo = titulo; }

    public String getTitulo() { return titulo; }

    @Override
    public String toString() { return titulo; }
}
