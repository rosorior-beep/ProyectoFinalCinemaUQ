package co.edu.uniquindio.cinemauq.servicio.eventos;

import co.edu.uniquindio.cinemauq.modelo.usuarios.Notificacion;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Rol;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;

public class NotificadorAdministradores implements ObservadorCine {

    private final IRepositorioCine repositorio;

    public NotificadorAdministradores(IRepositorioCine repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void actualizar(EventoCine evento) {
        repositorio.getUsuarios().stream()
                .filter(u -> u.getRol() == Rol.ADMINISTRADOR)
                .forEach(a -> a.agregarNotificacion(new Notificacion(evento.fecha(), evento.mensaje())));
    }
}
