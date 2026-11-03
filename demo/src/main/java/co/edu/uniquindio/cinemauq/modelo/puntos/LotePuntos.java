package co.edu.uniquindio.cinemauq.modelo.puntos;

import java.io.Serializable;
import java.time.LocalDate;

public class LotePuntos implements Serializable {

    private final String id;
    private final int cantidadInicial;
    private int disponibles;
    private final LocalDate fechaObtencion;
    private final LocalDate fechaVencimiento;
    private final String origen; // id de la compra que los generó

    public LotePuntos(String id, int cantidad, LocalDate fechaObtencion, LocalDate fechaVencimiento, String origen) {
        this.id = id;
        this.cantidadInicial = cantidad;
        this.disponibles = cantidad;
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaVencimiento;
        this.origen = origen;
    }


    public boolean estaVigente(LocalDate hoy) {
        return hoy.isBefore(fechaVencimiento);
    }

    public int disponiblesVigentes(LocalDate hoy) {
        return estaVigente(hoy) ? disponibles : 0;
    }

    int consumir(int cantidad) {
        int tomados = Math.min(cantidad, disponibles);
        disponibles -= tomados;
        return tomados;
    }

    void reponer(int cantidad) {
        disponibles = Math.min(cantidadInicial, disponibles + cantidad);
    }

    public String getEstado(LocalDate hoy) {
        if (!estaVigente(hoy)) return disponibles > 0 ? "Vencido" : "Vencido (usado)";
        if (disponibles == 0) return "Agotado";
        return hoy.plusDays(30).isBefore(fechaVencimiento) ? "Vigente" : "Por vencer";
    }

    public String getId() { return id; }
    public int getCantidadInicial() { return cantidadInicial; }
    public int getDisponibles() { return disponibles; }
    public LocalDate getFechaObtencion() { return fechaObtencion; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public String getOrigen() { return origen; }
}
