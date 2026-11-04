package co.edu.uniquindio.cinemauq.servicio.comandos.comandos;

import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.comandos.core.Comando;


public class ComandoCambiarHabilitacionAsiento implements Comando {

    private final IServicioAdministracion receptor;
    private final Sala sala;
    private final String codigo;
    private boolean estadoAnterior;

    public ComandoCambiarHabilitacionAsiento(IServicioAdministracion receptor, Sala sala, String codigo) {
        this.receptor = receptor;
        this.sala = sala;
        this.codigo = codigo;
    }

    @Override
    public void ejecutar() {
        estadoAnterior = sala.buscarAsiento(codigo).isHabilitado();
        receptor.cambiarHabilitacionAsiento(sala.getId(), codigo, !estadoAnterior);
    }

    @Override
    public void deshacer() {
        receptor.cambiarHabilitacionAsiento(sala.getId(), codigo, estadoAnterior);
    }

    @Override
    public String obtenerNombre() {
        return (estadoAnterior ? "Deshabilitar" : "Habilitar") + " asiento " + codigo + " de " + sala.getNombre();
    }
}
