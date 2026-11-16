package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.servicio.dto.PedidoCompra;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CancelacionTest {

    private Compra comprar(Escenario e, Funcion f) {
        return e.compras.comprar(new PedidoCompra(e.cliente, f, List.of("C1", "C2"), Map.of(), Set.of(), Set.of()));
    }

    @Test
    void masDe24HorasDevuelveElCienPorCiento() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        Compra c = comprar(e, e.funcionEn(25));
        e.compras.cancelarPorCliente(e.cliente, c.getId());
        assertEquals(100_000, e.cliente.getTarjeta().getSaldo());
        assertEquals(100, c.getPorcentajeReembolso());
        assertTrue(c.getFuncion().getAsientosOcupados().isEmpty()); // los asientos quedan libres
        assertEquals(0, e.cliente.getCuentaPuntos().getPuntosDisponibles(Escenario.AHORA.toLocalDate()));
    }

    @Test
    void exactamente24HorasYEntre2Y24DevuelvenEl80PorCiento() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        Compra c24 = comprar(e, e.funcionEn(24));
        assertEquals(80, e.compras.porcentajeReembolsoActual(c24));
        e.compras.cancelarPorCliente(e.cliente, c24.getId());
        assertEquals(100_000 - 20_000 + 16_000, e.cliente.getTarjeta().getSaldo());

        Compra c2 = comprar(e, e.funcionEn(2) );
        assertEquals(80, e.compras.porcentajeReembolsoActual(c2));
    }

    @Test
    void conMenosDe2HorasNoSePermiteCancelar() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        Compra c = comprar(e, e.funcionEn(3));
        e.reloj.establecer(Escenario.AHORA.plusHours(1).plusMinutes(1)); // faltan 1 h 59 min
        assertThrows(CancelacionNoPermitidaException.class, () -> e.compras.cancelarPorCliente(e.cliente, c.getId()));
        assertEquals("Pagada", c.getNombreEstado());
    }

    @Test
    void unReembolsoNoSePuedeEjecutarDosVeces() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        Compra c = comprar(e, e.funcionEn(48));
        e.compras.cancelarPorCliente(e.cliente, c.getId());
        double saldo = e.cliente.getTarjeta().getSaldo();
        assertThrows(CancelacionNoPermitidaException.class, () -> e.compras.cancelarPorCliente(e.cliente, c.getId()));
        assertThrows(CancelacionNoPermitidaException.class, () -> e.compras.reembolsarPorFuncionCancelada(c));
        assertEquals(saldo, e.cliente.getTarjeta().getSaldo()); // no se acreditó dos veces
    }

    @Test
    void siElCineCancelaLaFuncionSeDevuelveElCienPorCientoAunqueFaltePoco() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        Funcion f = e.funcionEn(3);
        Compra c = comprar(e, f);
        e.reloj.establecer(Escenario.AHORA.plusHours(2).plusMinutes(30)); // faltan 30 minutos
        int afectadas = e.admin.cancelarFuncion(f.getId());
        assertEquals(1, afectadas);
        assertEquals(100, c.getPorcentajeReembolso());
        assertEquals(100_000, e.cliente.getTarjeta().getSaldo());
        assertFalse(c.permiteCancelacion());
    }
}
