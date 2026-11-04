package co.edu.uniquindio.cinemauq.servicio.eventos;

import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;

import java.time.LocalDateTime;

public record EventoCine(TipoEvento tipo, LocalDateTime fecha, Usuario destinatario, String mensaje) {
}
