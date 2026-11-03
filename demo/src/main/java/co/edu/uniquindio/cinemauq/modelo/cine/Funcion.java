package co.edu.uniquindio.cinemauq.modelo.cine;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Funcion implements Serializable, Prototipo<Funcion> {

    private String id;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime fechaHora;
    private double precioBase;
    private EstadoFuncion estado = EstadoFuncion.PROGRAMADA;
    private Set<String> asientosOcupados = new HashSet<>();

    public Funcion(String id, Pelicula pelicula, Sala sala, LocalDateTime fechaHora, double precioBase) {
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHora = fechaHora;
        this.precioBase = precioBase;
    }

    @Override
    public Funcion clonar() {
        Funcion copia = new Funcion(id, pelicula, sala, fechaHora, precioBase);
        copia.asientosOcupados = new HashSet<>(); // la copia no hereda asientos vendidos
        copia.estado = EstadoFuncion.PROGRAMADA;
        return copia;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHora.plusMinutes(pelicula.getDuracionMinutos());
    }

    public boolean estaDisponible(String codigoAsiento) {
        return !asientosOcupados.contains(codigoAsiento) && sala.buscarAsiento(codigoAsiento).isHabilitado();
    }

    public void ocuparAsiento(String codigo) {
        if (!estaDisponible(codigo)) {
            throw new AsientoNoDisponibleException("El asiento " + codigo + " ya no está disponible para esta función.");
        }
        asientosOcupados.add(codigo);
    }

    public void liberarAsiento(String codigo) {
        asientosOcupados.remove(codigo);
    }

    public long getAsientosDisponibles() {
        return sala.getCapacidad() - asientosOcupados.size();
    }

    public double getPorcentajeOcupacion() {
        long capacidad = sala.getCapacidad();
        return capacidad == 0 ? 0 : asientosOcupados.size() * 100.0 / capacidad;
    }

    public boolean estaCancelada() { return estado == EstadoFuncion.CANCELADA; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Pelicula getPelicula() { return pelicula; }
    public Sala getSala() { return sala; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
    public EstadoFuncion getEstado() { return estado; }
    public void setEstado(EstadoFuncion estado) { this.estado = estado; }
    public Set<String> getAsientosOcupados() { return Collections.unmodifiableSet(asientosOcupados); }

    @Override
    public String toString() {
        return pelicula.getTitulo() + " | " + sala.getNombre() + " | " + fechaHora;
    }
}
