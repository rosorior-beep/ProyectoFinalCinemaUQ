package co.edu.uniquindio.cinemauq.modelo.cine;

import java.io.Serializable;

public class Pelicula implements Serializable {
    private final String id;
    private String titulo;
    private Genero genero;
    private int duracionMinutos;
    private Clasificacion clasificacion;
    private String sinopsis;
    private boolean enCartelera = true;

    public Pelicula(String id, String titulo, Genero genero, int duracionMinutos,
                    Clasificacion clasificacion, String sinopsis) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.duracionMinutos = duracionMinutos;
        this.clasificacion = clasificacion;
        this.sinopsis = sinopsis;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public Clasificacion getClasificacion() { return clasificacion; }
    public void setClasificacion(Clasificacion clasificacion) { this.clasificacion = clasificacion; }
    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }
    public boolean isEnCartelera() { return enCartelera; }
    public void setEnCartelera(boolean enCartelera) { this.enCartelera = enCartelera; }

    @Override
    public String toString() { return titulo; }
}
