package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.tarjeta.TarjetaVirtual;

public interface IServicioPagos {

    void validarPago(TarjetaVirtual tarjeta, double monto);

    void cobrar(TarjetaVirtual tarjeta, double monto, String descripcion, String referencia);

    void reembolsar(TarjetaVirtual tarjeta, double monto, String descripcion, String referencia);

    void recargar(TarjetaVirtual tarjeta, double monto, String descripcion, String referencia);
}
