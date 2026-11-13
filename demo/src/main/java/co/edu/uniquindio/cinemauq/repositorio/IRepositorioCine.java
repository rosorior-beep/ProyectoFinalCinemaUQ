package co.edu.uniquindio.cinemauq.repositorio;

import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.promociones.Promocion;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;

import java.util.List;
import java.util.Optional;


public interface IRepositorioCine {

    List<Usuario> getUsuarios();
    List<Pelicula> getPeliculas();
    List<Sala> getSalas();
    List<Funcion> getFunciones();
    List<ComponenteConfiteria> getProductos();
    List<Promocion> getPromociones();
    List<Compra> getCompras();
    List<SolicitudRecarga> getSolicitudesRecarga();

    default List<Cliente> getClientes() {
        return getUsuarios().stream().filter(u -> u instanceof Cliente).map(u -> (Cliente) u).toList();
    }

    Optional<Usuario> buscarUsuarioPorEmail(String email);

    String siguienteId(String prefijo);

    void guardar();

    void limpiar();
}
