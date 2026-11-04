package co.edu.uniquindio.cinemauq.servicio.tiempo;

import java.time.LocalDateTime;

public class TiempoSimulado implements ProveedorTiempo {
    private LocalDateTime actual;

    public TiempoSimulado(LocalDateTime inicio) {
        this.actual = inicio;
    }

    public void establecer(LocalDateTime fecha) {
        this.actual = fecha;
    }

    @Override
    public LocalDateTime ahora() {
        return actual;
    }
}
