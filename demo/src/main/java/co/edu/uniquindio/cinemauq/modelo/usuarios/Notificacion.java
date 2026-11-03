package co.edu.uniquindio.cinemauq.modelo.usuarios;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notificacion implements Serializable {
    private final LocalDateTime fecha;
    private final String mensaje;
    private boolean leida;

    public Notificacion(LocalDateTime fecha, String mensaje) {
        this.fecha = fecha;
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() { return fecha; }
    public String getMensaje() { return mensaje; }
    public boolean isLeida() { return leida; }
    public void marcarLeida() { this.leida = true; }
}
