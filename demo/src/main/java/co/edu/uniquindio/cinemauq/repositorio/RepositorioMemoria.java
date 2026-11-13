package co.edu.uniquindio.cinemauq.repositorio;

import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.promociones.Promocion;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;

import java.util.List;
import java.util.Optional;

public class RepositorioMemoria implements IRepositorioCine {

    protected DatosCine datos = new DatosCine();

    @Override public List<Usuario> getUsuarios() { return datos.usuarios; }
    @Override public List<Pelicula> getPeliculas() { return datos.peliculas; }
    @Override public List<Sala> getSalas() { return datos.salas; }
    @Override public List<Funcion> getFunciones() { return datos.funciones; }
    @Override public List<ComponenteConfiteria> getProductos() { return datos.productos; }
    @Override public List<Promocion> getPromociones() { return datos.promociones; }
    @Override public List<Compra> getCompras() { return datos.compras; }
    @Override public List<SolicitudRecarga> getSolicitudesRecarga() { return datos.solicitudes; }

    @Override
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return datos.usuarios.stream().filter(u -> u.getEmail().equalsIgnoreCase(email.trim())).findFirst();
    }

    @Override
    public String siguienteId(String prefijo) {
        int siguiente = datos.consecutivos.merge(prefijo, 1, Integer::sum);
        return prefijo + "-" + String.format("%04d", siguiente);
    }

    @Override
    public void guardar() {

    }

    @Override
    public void limpiar() {
        datos = new DatosCine();
    }
}
