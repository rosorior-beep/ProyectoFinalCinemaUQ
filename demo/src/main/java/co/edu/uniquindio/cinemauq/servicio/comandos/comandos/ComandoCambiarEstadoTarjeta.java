package co.edu.uniquindio.cinemauq.servicio.comandos.comandos;

import co.edu.uniquindio.cinemauq.modelo.tarjeta.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.comandos.core.Comando;


public class ComandoCambiarEstadoTarjeta implements Comando {

    private final IServicioAdministracion receptor;
    private final Cliente cliente;
    private EstadoTarjeta estadoAnterior;

    public ComandoCambiarEstadoTarjeta(IServicioAdministracion receptor, Cliente cliente) {
        this.receptor = receptor;
        this.cliente = cliente;
    }

    @Override
    public void ejecutar() {
        estadoAnterior = cliente.getTarjeta().getEstado();
        EstadoTarjeta nuevo = estadoAnterior == EstadoTarjeta.ACTIVA ? EstadoTarjeta.BLOQUEADA : EstadoTarjeta.ACTIVA;
        receptor.cambiarEstadoTarjeta(cliente.getId(), nuevo);
    }

    @Override
    public void deshacer() {
        receptor.cambiarEstadoTarjeta(cliente.getId(), estadoAnterior);
    }

    @Override
    public String obtenerNombre() {
        return (estadoAnterior == EstadoTarjeta.ACTIVA ? "Bloquear" : "Activar") + " tarjeta de " + cliente.getNombre();
    }
}
