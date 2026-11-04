package co.edu.uniquindio.cinemauq.servicio.tiempo;

import java.time.LocalDateTime;

@FunctionalInterface
public interface ProveedorTiempo {
    LocalDateTime ahora();
}
