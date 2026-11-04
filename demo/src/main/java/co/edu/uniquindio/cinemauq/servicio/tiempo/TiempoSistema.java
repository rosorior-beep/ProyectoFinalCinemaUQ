package co.edu.uniquindio.cinemauq.servicio.tiempo;

import java.time.LocalDateTime;

public class TiempoSistema implements ProveedorTiempo {
    @Override
    public LocalDateTime ahora() {
        return LocalDateTime.now().withSecond(0).withNano(0);
    }
}
