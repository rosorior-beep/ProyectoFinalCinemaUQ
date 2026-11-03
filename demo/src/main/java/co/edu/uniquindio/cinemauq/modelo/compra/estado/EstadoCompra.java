package co.edu.uniquindio.cinemauq.modelo.compra.estado;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.io.Serializable;
import java.time.LocalDateTime;


public interface EstadoCompra extends Serializable {

    String getNombre();

    void confirmarPago(Compra compra);

    void cancelar(Compra compra, double montoReembolso, double porcentaje, String motivo, LocalDateTime fecha);

    boolean permiteCancelacion();
}
