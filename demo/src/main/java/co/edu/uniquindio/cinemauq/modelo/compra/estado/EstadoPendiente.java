package co.edu.uniquindio.cinemauq.modelo.compra.estado;

import co.edu.uniquindio.cinemauq.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.LocalDateTime;


public class EstadoPendiente implements EstadoCompra {

    @Override public String getNombre() { return "Pendiente"; }

    @Override
    public void confirmarPago(Compra compra) {
        compra.cambiarEstado(new EstadoPagada());
    }

    @Override
    public void cancelar(Compra compra, double monto, double porcentaje, String motivo, LocalDateTime fecha) {
        throw new OperacionNoPermitidaException("Una compra pendiente no ha sido pagada; no hay nada que reembolsar.");
    }

    @Override public boolean permiteCancelacion() { return false; }
}
