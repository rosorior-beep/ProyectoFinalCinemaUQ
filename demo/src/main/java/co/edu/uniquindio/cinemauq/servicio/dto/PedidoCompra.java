package co.edu.uniquindio.cinemauq.servicio.dto;

import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;

import java.util.List;
import java.util.Map;
import java.util.Set;


public record PedidoCompra(Cliente cliente, Funcion funcion, List<String> asientos,
                           Map<String, Integer> productos, Set<String> asientosConPuntos,
                           Set<String> productosConPuntos) {
}
