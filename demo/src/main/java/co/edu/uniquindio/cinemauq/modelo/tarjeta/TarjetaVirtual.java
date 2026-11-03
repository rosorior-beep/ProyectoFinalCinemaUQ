package co.edu.uniquindio.cinemauq.modelo.tarjeta;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.cinemauq.excepciones.TarjetaInactivaException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TarjetaVirtual implements Serializable {

    private final String numero;
    private double saldo;
    private EstadoTarjeta estado = EstadoTarjeta.ACTIVA;
    private final List<Movimiento> movimientos = new ArrayList<>();

    public TarjetaVirtual(String numero) {
        this.numero = numero;
    }

    public boolean estaActiva() {
        return estado == EstadoTarjeta.ACTIVA;
    }


    public void validarPago(double monto) {
        if (!estaActiva()) {
            throw new TarjetaInactivaException("La tarjeta " + getNumeroEnmascarado() + " está bloqueada.");
        }
        if (monto > saldo) {
            throw new SaldoInsuficienteException(String.format(
                    "Saldo insuficiente: el total es $%,.0f y el saldo disponible es $%,.0f. Solicita una recarga.",
                    monto, saldo));
        }
    }

    public void debitar(double monto, LocalDateTime fecha, String descripcion, String referencia) {
        if (monto < 0) throw new DatosInvalidosException("El monto a debitar no puede ser negativo.");
        validarPago(monto);
        saldo -= monto;
        movimientos.add(new Movimiento(fecha, TipoMovimiento.PAGO, monto, saldo, descripcion, referencia));
    }

    public void acreditar(double monto, TipoMovimiento tipo, LocalDateTime fecha, String descripcion, String referencia) {
        if (monto <= 0) throw new DatosInvalidosException("El monto a acreditar debe ser mayor que cero.");
        if (tipo == TipoMovimiento.PAGO) throw new DatosInvalidosException("Un pago no es una acreditación.");
        saldo += monto;
        movimientos.add(new Movimiento(fecha, tipo, monto, saldo, descripcion, referencia));
    }

    public String getNumeroEnmascarado() {
        return "**** **** **** " + numero.substring(Math.max(0, numero.length() - 4));
    }

    public String getNumero() { return numero; }
    public double getSaldo() { return saldo; }
    public EstadoTarjeta getEstado() { return estado; }
    public void setEstado(EstadoTarjeta estado) { this.estado = estado; }
    public List<Movimiento> getMovimientos() { return Collections.unmodifiableList(movimientos); }
}