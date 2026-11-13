package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.dto.PedidoCompra;

import java.util.List;

public interface IServicioCompras {

    List<Pelicula> peliculasEnCartelera();

    List<Funcion> funcionesDisponibles(Pelicula pelicula);

    List<ComponenteConfiteria> confiteriaDisponible();

    Compra cotizar(PedidoCompra pedido);

    Compra comprar(PedidoCompra pedido);

    List<Compra> comprasDe(Cliente cliente);

    double porcentajeReembolsoActual(Compra compra);

    Compra cancelarPorCliente(Cliente cliente, String idCompra);

    Compra reembolsarPorFuncionCancelada(Compra compra);
}
