package co.edu.uniquindio.cinemauq.servicio.eventos;

import co.edu.uniquindio.cinemauq.modelo.usuarios.Notificacion;

public class NotificadorUsuario implements ObservadorCine {
    @Override
    public void actualizar(EventoCine evento) {
        if (evento.destinatario() != null) {
            evento.destinatario().agregarNotificacion(new Notificacion(evento.fecha(), evento.mensaje()));
        }
    }
}
