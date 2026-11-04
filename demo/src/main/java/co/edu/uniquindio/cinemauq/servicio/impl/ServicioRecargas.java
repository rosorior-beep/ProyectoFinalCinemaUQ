package co.edu.uniquindio.cinemauq.servicio.impl;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.EntidadNoEncontradaException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.config.ConfiguracionCine;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.EstadoSolicitud;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.IServicioPagos;
import co.edu.uniquindio.cinemauq.servicio.IServicioRecargas;
import co.edu.uniquindio.cinemauq.servicio.eventos.EventoCine;
import co.edu.uniquindio.cinemauq.servicio.eventos.GestorEventos;
import co.edu.uniquindio.cinemauq.servicio.eventos.TipoEvento;
import co.edu.uniquindio.cinemauq.servicio.tiempo.ProveedorTiempo;

import java.util.Comparator;
import java.util.List;

public class ServicioRecargas implements IServicioRecargas {

    private final IRepositorioCine repositorio;
    private final IServicioPagos pagos;
    private final GestorEventos eventos;
    private final ProveedorTiempo tiempo;

    public ServicioRecargas(IRepositorioCine repositorio, IServicioPagos pagos, GestorEventos eventos,
                            ProveedorTiempo tiempo) {
        this.repositorio = repositorio;
        this.pagos = pagos;
        this.eventos = eventos;
        this.tiempo = tiempo;
    }

    @Override
    public SolicitudRecarga solicitarRecarga(Cliente cliente, double monto) {
        validarMonto(monto);
        boolean tienePendiente = repositorio.getSolicitudesRecarga().stream()
                .anyMatch(s -> s.getCliente().getId().equals(cliente.getId()) && s.getEstado() == EstadoSolicitud.PENDIENTE);
        if (tienePendiente) {
            throw new OperacionNoPermitidaException("Ya tienes una solicitud de recarga pendiente.");
        }
        SolicitudRecarga solicitud = new SolicitudRecarga(repositorio.siguienteId("RC"), cliente, monto, tiempo.ahora());
        repositorio.getSolicitudesRecarga().add(solicitud);
        eventos.notificar(new EventoCine(TipoEvento.RECARGA_SOLICITADA, tiempo.ahora(), null,
                String.format("%s solicitó una recarga de $%,.0f (%s).", cliente.getNombre(), monto, solicitud.getId())));
        repositorio.guardar();
        return solicitud;
    }

    @Override
    public List<SolicitudRecarga> solicitudesDe(Cliente cliente) {
        return repositorio.getSolicitudesRecarga().stream()
                .filter(s -> s.getCliente().getId().equals(cliente.getId()))
                .sorted(Comparator.comparing(SolicitudRecarga::getFechaSolicitud).reversed()).toList();
    }

    @Override
    public List<SolicitudRecarga> solicitudesPendientes() {
        return repositorio.getSolicitudesRecarga().stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE).toList();
    }

    @Override
    public void aprobarSolicitud(String idSolicitud) {
        SolicitudRecarga s = buscarPendiente(idSolicitud);
        pagos.recargar(s.getCliente().getTarjeta(), s.getMonto(), "Recarga aprobada (" + s.getId() + ")", s.getId());
        s.responder(EstadoSolicitud.APROBADA, tiempo.ahora(), "Aprobada");
        eventos.notificar(new EventoCine(TipoEvento.RECARGA_REALIZADA, tiempo.ahora(), s.getCliente(),
                String.format("Tu recarga de $%,.0f fue aprobada. Nuevo saldo: $%,.0f.",
                        s.getMonto(), s.getCliente().getTarjeta().getSaldo())));
        repositorio.guardar();
    }

    @Override
    public void rechazarSolicitud(String idSolicitud, String motivo) {
        SolicitudRecarga s = buscarPendiente(idSolicitud);
        String texto = (motivo == null || motivo.isBlank()) ? "Sin motivo" : motivo.trim();
        s.responder(EstadoSolicitud.RECHAZADA, tiempo.ahora(), texto);
        eventos.notificar(new EventoCine(TipoEvento.RECARGA_RECHAZADA, tiempo.ahora(), s.getCliente(),
                String.format("Tu solicitud de recarga por $%,.0f fue rechazada: %s", s.getMonto(), texto)));
        repositorio.guardar();
    }

    @Override
    public void recargarDirectamente(String idCliente, double monto) {
        validarMonto(monto);
        Cliente cliente = repositorio.getClientes().stream().filter(c -> c.getId().equals(idCliente)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el cliente " + idCliente));
        String referencia = repositorio.siguienteId("RC");
        pagos.recargar(cliente.getTarjeta(), monto, "Recarga realizada por el administrador", referencia);
        eventos.notificar(new EventoCine(TipoEvento.RECARGA_REALIZADA, tiempo.ahora(), cliente,
                String.format("Se recargaron $%,.0f a tu tarjeta. Nuevo saldo: $%,.0f.", monto,
                        cliente.getTarjeta().getSaldo())));
        repositorio.guardar();
    }

    private SolicitudRecarga buscarPendiente(String id) {
        SolicitudRecarga s = repositorio.getSolicitudesRecarga().stream().filter(x -> x.getId().equals(id))
                .findFirst().orElseThrow(() -> new EntidadNoEncontradaException("No existe la solicitud " + id));
        if (s.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new OperacionNoPermitidaException("La solicitud " + id + " ya fue respondida.");
        }
        return s;
    }

    private void validarMonto(double monto) {
        ConfiguracionCine cfg = ConfiguracionCine.getInstance();
        if (monto < cfg.getRecargaMinima() || monto > cfg.getRecargaMaxima()) {
            throw new DatosInvalidosException(String.format("La recarga debe estar entre $%,.0f y $%,.0f.",
                    cfg.getRecargaMinima(), cfg.getRecargaMaxima()));
        }
    }
}
