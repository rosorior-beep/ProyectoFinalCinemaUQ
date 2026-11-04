package co.edu.uniquindio.cinemauq.servicio.comandos.comandos;

import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.comandos.core.Comando;

public class ComandoCambiarCartelera implements Comando {

    private final IServicioAdministracion receptor;
    private final Pelicula pelicula;
    private boolean estadoAnterior;

    public ComandoCambiarCartelera(IServicioAdministracion receptor, Pelicula pelicula) {
        this.receptor = receptor;
        this.pelicula = pelicula;
    }

    @Override
    public void ejecutar() {
        estadoAnterior = pelicula.isEnCartelera();
        aplicar(!estadoAnterior);
    }

    @Override
    public void deshacer() {
        aplicar(estadoAnterior);
    }

    private void aplicar(boolean enCartelera) {
        receptor.actualizarPelicula(pelicula.getId(), pelicula.getTitulo(), pelicula.getGenero(),
                pelicula.getDuracionMinutos(), pelicula.getClasificacion(), pelicula.getSinopsis(), enCartelera);
    }

    @Override
    public String obtenerNombre() {
        return (estadoAnterior ? "Retirar de cartelera" : "Poner en cartelera") + " \"" + pelicula.getTitulo() + "\"";
    }
}
