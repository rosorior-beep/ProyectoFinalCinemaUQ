package co.edu.uniquindio.cinemauq.modelo.tarjeta;

import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SolicitudRecarga implements Serializable {
    private final String id;
    private final Cliente cliente;
    private final double monto;
    private final LocalDateTime fechaSolicitud;
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;
    private LocalDateTime fechaRespuesta;
    private String observacion;

    public SolicitudRecarga(String id, Cliente cliente, double monto, LocalDateTime fechaSolicitud) {
        this.id = id;
        this.cliente = cliente;
        this.monto = monto;
        this.fechaSolicitud = fechaSolicitud;
    }

    public void responder(EstadoSolicitud nuevoEstado, LocalDateTime fecha, String observacion) {
        this.estado = nuevoEstado;
        this.fechaRespuesta = fecha;
        this.observacion = observacion;
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public double getMonto() { return monto; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public EstadoSolicitud getEstado() { return estado; }
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public String getObservacion() { return observacion; }
}
