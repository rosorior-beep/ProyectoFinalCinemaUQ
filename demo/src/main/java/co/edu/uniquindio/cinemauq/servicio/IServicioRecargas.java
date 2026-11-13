package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;

import java.util.List;

public interface IServicioRecargas {

    SolicitudRecarga solicitarRecarga(Cliente cliente, double monto);

    List<SolicitudRecarga> solicitudesDe(Cliente cliente);

    List<SolicitudRecarga> solicitudesPendientes();

    void aprobarSolicitud(String idSolicitud);

    void rechazarSolicitud(String idSolicitud, String motivo);

    void recargarDirectamente(String idCliente, double monto);
}
