package co.edu.uniquindio.cinemauq.modelo.compra.estado;

import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.LocalDateTime;

public class EstadoCancelada implements EstadoCompra {

    @Override public String getNombre() { return "Cancelada"; }

    @Override
    public void confirmarPago(Compra compra) {
        throw new OperacionNoPermitidaException("La compra " + compra.getId() + " está cancelada.");
    }

    @Override
    public void cancelar(Compra compra, double monto, double porcentaje, String motivo, LocalDateTime fecha) {
        throw new CancelacionNoPermitidaException("La compra " + compra.getId()
                + " ya fue cancelada y reembolsada; no se puede reembolsar dos veces.");
    }

    @Override public boolean permiteCancelacion() { return false; }
}