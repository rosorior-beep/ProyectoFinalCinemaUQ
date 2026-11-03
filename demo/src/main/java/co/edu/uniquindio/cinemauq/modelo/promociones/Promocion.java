package co.edu.uniquindio.cinemauq.modelo.promociones;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;


public class Promocion implements Serializable {

    private final String id;
    private String nombre;
    private AplicacionPromocion aplicaA;
    private double porcentaje;
    private DayOfWeek diaSemana; // null = todos los días
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activa = true;

    public Promocion(String id, String nombre, AplicacionPromocion aplicaA, double porcentaje,
                     DayOfWeek diaSemana, LocalDate fechaInicio, LocalDate fechaFin) {
        if (porcentaje <= 0 || porcentaje > 70) {
            throw new DatosInvalidosException("El porcentaje de una promoción debe estar entre 1% y 70%.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new DatosInvalidosException("La fecha final de la promoción no puede ser anterior a la inicial.");
        }
        this.id = id;
        this.nombre = nombre;
        this.aplicaA = aplicaA;
        this.porcentaje = porcentaje;
        this.diaSemana = diaSemana;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public boolean esAplicable(boolean esEntrada, LocalDate fechaFuncion) {
        if (!activa) return false;
        if (fechaFuncion.isBefore(fechaInicio) || fechaFuncion.isAfter(fechaFin)) return false;
        if (diaSemana != null && fechaFuncion.getDayOfWeek() != diaSemana) return false;
        return switch (aplicaA) {
            case TODO -> true;
            case ENTRADAS -> esEntrada;
            case CONFITERIA -> !esEntrada;
        };
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public AplicacionPromocion getAplicaA() { return aplicaA; }
    public double getPorcentaje() { return porcentaje; }
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    @Override
    public String toString() { return nombre + " (-" + (int) porcentaje + "%)"; }
}
