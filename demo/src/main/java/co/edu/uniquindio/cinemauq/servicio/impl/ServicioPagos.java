package co.edu.uniquindio.cinemauq.servicio.impl;

import co.edu.uniquindio.cinemauq.modelo.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TipoMovimiento;
import co.edu.uniquindio.cinemauq.servicio.IServicioPagos;
import co.edu.uniquindio.cinemauq.servicio.tiempo.ProveedorTiempo;

public class ServicioPagos implements IServicioPagos {

    private final ProveedorTiempo tiempo;

    public ServicioPagos(ProveedorTiempo tiempo) {
        this.tiempo = tiempo;
    }

    @Override
    public void validarPago(TarjetaVirtual tarjeta, double monto) {
        tarjeta.validarPago(monto);
    }

    @Override
    public void cobrar(TarjetaVirtual tarjeta, double monto, String descripcion, String referencia) {
        tarjeta.debitar(monto, tiempo.ahora(), descripcion, referencia);
    }

    @Override
    public void reembolsar(TarjetaVirtual tarjeta, double monto, String descripcion, String referencia) {
        tarjeta.acreditar(monto, TipoMovimiento.REEMBOLSO, tiempo.ahora(), descripcion, referencia);
    }

    @Override
    public void recargar(TarjetaVirtual tarjeta, double monto, String descripcion, String referencia) {
        tarjeta.acreditar(monto, TipoMovimiento.RECARGA, tiempo.ahora(), descripcion, referencia);
    }
}
