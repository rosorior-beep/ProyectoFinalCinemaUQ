package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.asientos.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.promociones.AplicacionPromocion;
import co.edu.uniquindio.cinemauq.modelo.promociones.Promocion;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.servicio.comandos.comandos.ComandoCambiarEstadoPromocion;
import co.edu.uniquindio.cinemauq.servicio.comandos.comandos.ComandoCambiarEstadoTarjeta;
import co.edu.uniquindio.cinemauq.servicio.comandos.comandos.ComandoCambiarTipoAsiento;
import co.edu.uniquindio.cinemauq.servicio.comandos.invocador.GestorComandos;
import co.edu.uniquindio.cinemauq.servicio.dto.PedidoCompra;
import co.edu.uniquindio.cinemauq.servicio.estrategias.CalculadoraReembolso;
import co.edu.uniquindio.cinemauq.servicio.estrategias.ReembolsoFuncionCanceladaStrategy;
import co.edu.uniquindio.cinemauq.servicio.estrategias.ReembolsoPorAnticipacionStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatronesComportamientoTest {



    @Test
    void elComandoCambiaElAsientoYSePuedeDeshacer() {
        Escenario e = new Escenario();
        GestorComandos gestor = new GestorComandos();
        gestor.ejecutar(new ComandoCambiarTipoAsiento(e.admin, e.sala, "B3", TipoAsiento.PREFERENCIAL));
        assertEquals(TipoAsiento.PREFERENCIAL, e.sala.buscarAsiento("B3").getTipo());
        assertEquals(1, gestor.obtenerHistorial().size());

        String deshecho = gestor.deshacer();
        assertEquals(TipoAsiento.GENERAL, e.sala.buscarAsiento("B3").getTipo());
        assertTrue(deshecho.contains("B3"));
        assertFalse(gestor.puedeDeshacer());
    }

    @Test
    void seDeshaceEnOrdenInversoComoUnaPila() {
        Escenario e = new Escenario();
        Promocion promo = e.admin.crearPromocion("Prueba", AplicacionPromocion.TODO, 10, null,
                Escenario.AHORA.toLocalDate(), Escenario.AHORA.toLocalDate().plusDays(5));
        GestorComandos gestor = new GestorComandos();
        gestor.ejecutar(new ComandoCambiarEstadoPromocion(e.admin, promo));
        gestor.ejecutar(new ComandoCambiarEstadoTarjeta(e.admin, e.cliente));
        assertFalse(promo.isActiva());
        assertEquals(EstadoTarjeta.BLOQUEADA, e.cliente.getTarjeta().getEstado());

        gestor.deshacer(); // primero la tarjeta (último en entrar)
        assertEquals(EstadoTarjeta.ACTIVA, e.cliente.getTarjeta().getEstado());
        assertFalse(promo.isActiva());
        gestor.deshacer();
        assertTrue(promo.isActiva());
    }

    @Test
    void sinAccionesNoSePuedeDeshacer() {
        assertThrows(OperacionNoPermitidaException.class, () -> new GestorComandos().deshacer());
    }


    @Test
    void elContextoCambiaDeEstrategiaEnTiempoDeEjecucion() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        Funcion f = e.funcionEn(3);
        Compra c = e.compras.comprar(new PedidoCompra(e.cliente, f, List.of("C1"), Map.of(), Set.of(), Set.of()));
        CalculadoraReembolso calculadora = new CalculadoraReembolso(new ReembolsoPorAnticipacionStrategy());
        assertEquals(80, calculadora.calcularPorcentaje(c, Escenario.AHORA));
        calculadora.setEstrategia(new ReembolsoFuncionCanceladaStrategy());
        assertEquals(100, calculadora.calcularPorcentaje(c, Escenario.AHORA));
        calculadora.setEstrategia(null);
        assertThrows(IllegalStateException.class, () -> calculadora.calcularPorcentaje(c, Escenario.AHORA));
    }
}
