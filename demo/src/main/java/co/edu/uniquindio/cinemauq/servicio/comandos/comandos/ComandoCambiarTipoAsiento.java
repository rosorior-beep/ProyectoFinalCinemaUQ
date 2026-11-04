package co.edu.uniquindio.cinemauq.servicio.comandos.comandos;

import co.edu.uniquindio.cinemauq.modelo.asientos.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.comandos.core.Comando;


public class ComandoCambiarTipoAsiento implements Comando {

    private final IServicioAdministracion receptor;
    private final Sala sala;
    private final String codigo;
    private final TipoAsiento nuevoTipo;
    private TipoAsiento tipoAnterior;

    public ComandoCambiarTipoAsiento(IServicioAdministracion receptor, Sala sala, String codigo, TipoAsiento nuevoTipo) {
        this.receptor = receptor;
        this.sala = sala;
        this.codigo = codigo;
        this.nuevoTipo = nuevoTipo;
    }

    @Override
    public void ejecutar() {
        tipoAnterior = sala.buscarAsiento(codigo).getTipo();
        receptor.cambiarTipoAsiento(sala.getId(), codigo, nuevoTipo);
    }

    @Override
    public void deshacer() {
        receptor.cambiarTipoAsiento(sala.getId(), codigo, tipoAnterior);
    }

    @Override
    public String obtenerNombre() {
        return "Asiento " + codigo + " de " + sala.getNombre() + ": " + tipoAnterior + " → " + nuevoTipo;
    }
}
