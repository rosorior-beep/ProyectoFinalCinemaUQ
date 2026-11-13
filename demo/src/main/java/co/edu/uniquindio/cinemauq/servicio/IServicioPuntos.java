package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.puntos.ConsumoPuntos;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;

import java.util.List;

public interface IServicioPuntos {

    int calcularPuntosGanados(Compra compra);

    int puntosDisponibles(Cliente cliente);

    void validarRedencion(Cliente cliente, int puntos);

    List<ConsumoPuntos> redimir(Cliente cliente, int puntos, String descripcion);

    int acumular(Cliente cliente, Compra compra);

    void revertirCompra(Cliente cliente, Compra compra);

    String getDescripcionRegla();
}
