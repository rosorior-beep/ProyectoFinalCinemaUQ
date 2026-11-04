package co.edu.uniquindio.cinemauq.servicio.comandos.comandos;

import co.edu.uniquindio.cinemauq.modelo.promociones.Promocion;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.comandos.core.Comando;


public class ComandoCambiarEstadoPromocion implements Comando {

    private final IServicioAdministracion receptor;
    private final Promocion promocion;
    private boolean estadoAnterior;

    public ComandoCambiarEstadoPromocion(IServicioAdministracion receptor, Promocion promocion) {
        this.receptor = receptor;
        this.promocion = promocion;
    }

    @Override
    public void ejecutar() {
        estadoAnterior = promocion.isActiva();
        receptor.cambiarEstadoPromocion(promocion.getId(), !estadoAnterior);
    }

    @Override
    public void deshacer() {
        receptor.cambiarEstadoPromocion(promocion.getId(), estadoAnterior);
    }

    @Override
    public String obtenerNombre() {
        return (estadoAnterior ? "Desactivar" : "Activar") + " promoción \"" + promocion.getNombre() + "\"";
    }
}
