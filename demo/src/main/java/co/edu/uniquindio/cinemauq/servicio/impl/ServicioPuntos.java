package co.edu.uniquindio.cinemauq.servicio.impl;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.config.ConfiguracionCine;
import co.edu.uniquindio.cinemauq.modelo.puntos.ConsumoPuntos;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.excepciones.PuntosInsuficientesException;
import co.edu.uniquindio.cinemauq.servicio.IServicioPuntos;
import co.edu.uniquindio.cinemauq.servicio.estrategias.CalculadoraPuntos;
import co.edu.uniquindio.cinemauq.servicio.estrategias.EstrategiaPuntos;
import co.edu.uniquindio.cinemauq.servicio.tiempo.ProveedorTiempo;

import java.util.List;

public class ServicioPuntos implements IServicioPuntos {

    private final CalculadoraPuntos calculadora; // contexto del patrón Strategy
    private final ProveedorTiempo tiempo;

    public ServicioPuntos(EstrategiaPuntos estrategia, ProveedorTiempo tiempo) {
        this.calculadora = new CalculadoraPuntos(estrategia);
        this.tiempo = tiempo;
    }

    public void setEstrategia(EstrategiaPuntos estrategia) {
        calculadora.setEstrategia(estrategia);
    }

    @Override
    public int calcularPuntosGanados(Compra compra) {
        return calculadora.calcularPuntos(compra);
    }

    @Override
    public int puntosDisponibles(Cliente cliente) {
        return cliente.getCuentaPuntos().getPuntosDisponibles(tiempo.ahora().toLocalDate());
    }

    @Override
    public void validarRedencion(Cliente cliente, int puntos) {
        int disponibles = puntosDisponibles(cliente);
        if (puntos > disponibles) {
            throw new PuntosInsuficientesException("Se necesitan " + puntos + " puntos y tienes "
                    + disponibles + " puntos vigentes.");
        }
    }

    @Override
    public List<ConsumoPuntos> redimir(Cliente cliente, int puntos, String descripcion) {
        return cliente.getCuentaPuntos().redimir(puntos, tiempo.ahora(), descripcion);
    }

    @Override
    public int acumular(Cliente cliente, Compra compra) {
        int puntos = calcularPuntosGanados(compra);
        if (puntos > 0) {
            cliente.getCuentaPuntos().acumular(puntos, tiempo.ahora(),
                    ConfiguracionCine.getInstance().getAniosVigenciaPuntos(), compra.getId());
        }
        return puntos;
    }

    @Override
    public void revertirCompra(Cliente cliente, Compra compra) {
        if (!compra.getConsumosPuntos().isEmpty()) {
            cliente.getCuentaPuntos().devolver(compra.getConsumosPuntos(), tiempo.ahora(),
                    "Devolución de puntos de la compra " + compra.getId());
        }
        cliente.getCuentaPuntos().revertirAcumulacion(compra.getId(), tiempo.ahora());
    }

    @Override
    public String getDescripcionRegla() {
        return calculadora.getDescripcion();
    }
}
