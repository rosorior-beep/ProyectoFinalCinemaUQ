package co.edu.uniquindio.cinemauq.modelo.cine;

public enum Genero {
    ACCION("Acción"), AVENTURA("Aventura"), ANIMACION("Animación"), COMEDIA("Comedia"),
    DRAMA("Drama"), TERROR("Terror"), CIENCIA_FICCION("Ciencia ficción"), ROMANCE("Romance"),
    DOCUMENTAL("Documental"), SUSPENSO("Suspenso");

    private final String etiqueta;

    Genero(String etiqueta) { this.etiqueta = etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}
