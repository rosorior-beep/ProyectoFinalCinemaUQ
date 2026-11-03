package co.edu.uniquindio.cinemauq.modelo.tarjeta;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Movimiento implements Serializable {
    private final LocalDateTime fecha;
    private final TipoMovimiento tipo;
    private final double monto;
    private final double saldoResultante;
    private final String descripcion;
    private final String referencia; // id de compra o de solicitud de recarga

    public Movimiento(LocalDateTime fecha, TipoMovimiento tipo, double monto, double saldoResultante,
                      String descripcion, String referencia) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.saldoResultante = saldoResultante;
        this.descripcion = descripcion;
        this.referencia = referencia;
    }

    public LocalDateTime getFecha() { return fecha; }
    public TipoMovimiento getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public double getSaldoResultante() { return saldoResultante; }
    public String getDescripcion() { return descripcion; }
    public String getReferencia() { return referencia; }
}