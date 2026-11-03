package co.edu.uniquindio.cinemauq.modelo.compra.estado;

import co.edu.uniquindio.cinemauq.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.LocalDateTime;

public class EstadoPagada implements EstadoCompra {

    @Override public String getNombre() { return "Pagada"; }

    @Override
    public void confirmarPago(Compra compra) {
        throw new OperacionNoPermitidaException("La compra " + compra.getId() + " ya fue pagada.");
    }

    @Override
    public void cancelar(Compra compra, double monto, double porcentaje, String motivo, LocalDateTime fecha) {
        compra.registrarCancelacion(monto, porcentaje, motivo, fecha);
        compra.cambiarEstado(new EstadoCancelada());
    }

    @Override public boolean permiteCancelacion() { return true; }
}
