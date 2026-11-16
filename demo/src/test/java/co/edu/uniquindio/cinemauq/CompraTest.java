package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.excepciones.SaldoInsuficienteException;
import co.edu.uniquindio.cinemauq.excepciones.TarjetaInactivaException;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.confiteria.CategoriaProducto;
import co.edu.uniquindio.cinemauq.modelo.confiteria.Combo;
import co.edu.uniquindio.cinemauq.modelo.confiteria.Producto;
import co.edu.uniquindio.cinemauq.modelo.promociones.AplicacionPromocion;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TipoMovimiento;
import co.edu.uniquindio.cinemauq.servicio.dto.PedidoCompra;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompraTest {

    private PedidoCompra pedido(Escenario e, Funcion f, List<String> asientos) {
        return new PedidoCompra(e.cliente, f, asientos, Map.of(), Set.of(), Set.of());
    }

    @Test
    void compraDescuentaSaldoRegistraMovimientoYAcumulaPuntos() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        Funcion f = e.funcionEn(48);
        Compra c = e.compras.comprar(pedido(e, f, List.of("C3", "C4")));

        assertEquals(20_000, c.getTotalAPagar());
        assertEquals(80_000, e.cliente.getTarjeta().getSaldo());
        assertEquals(TipoMovimiento.PAGO, e.cliente.getTarjeta().getMovimientos().get(1).getTipo());
        assertEquals(20, c.getPuntosGanados()); // 1 punto por cada $1.000
        assertEquals("Pagada", c.getNombreEstado());
        assertTrue(f.getAsientosOcupados().containsAll(List.of("C3", "C4")));
    }

    @Test
    void unAsientoNoSePuedeVenderDosVecesEnLaMismaFuncion() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        Funcion f = e.funcionEn(48);
        e.compras.comprar(pedido(e, f, List.of("B2")));
        assertThrows(AsientoNoDisponibleException.class, () -> e.compras.comprar(pedido(e, f, List.of("B2"))));
    }

    @Test
    void sinSaldoSuficienteNoSeModificaNada() {
        Escenario e = new Escenario();
        e.recargar(10_000);
        Funcion f = e.funcionEn(48);
        assertThrows(SaldoInsuficienteException.class, () -> e.compras.comprar(pedido(e, f, List.of("B1", "B2"))));
        assertEquals(10_000, e.cliente.getTarjeta().getSaldo());
        assertTrue(f.getAsientosOcupados().isEmpty());
        assertTrue(e.repo.getCompras().isEmpty());
    }

    @Test
    void conTarjetaBloqueadaNoSePuedeComprar() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        e.cliente.getTarjeta().setEstado(EstadoTarjeta.BLOQUEADA);
        Funcion f = e.funcionEn(48);
        assertThrows(TarjetaInactivaException.class, () -> e.compras.comprar(pedido(e, f, List.of("B1"))));
    }

    @Test
    void asientoPreferencialTieneRecargoYUnaEntradaPuedePagarseConPuntos() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        e.cliente.getCuentaPuntos().acumular(300, Escenario.AHORA.minusDays(10), 1, "C-0");
        Funcion f = e.funcionEn(48);
        Compra c = e.compras.comprar(new PedidoCompra(e.cliente, f, List.of("E1", "B1"), Map.of(),
                Set.of("B1"), Set.of()));
        // E1 es preferencial (10.000 + 6.000); B1 se paga con 250 puntos
        assertEquals(16_000, c.getTotalAPagar());
        assertEquals(250, c.getPuntosRedimidos());
        assertEquals(50 + 16, e.cliente.getCuentaPuntos().getPuntosDisponibles(Escenario.AHORA.toLocalDate()));
    }

    @Test
    void laPromocionSeAplicaComoDecoradorSoloElDiaConfigurado() {
        Escenario e = new Escenario();
        e.recargar(100_000);
        e.admin.crearPromocion("Martes", AplicacionPromocion.ENTRADAS, 30, DayOfWeek.TUESDAY,
                Escenario.AHORA.toLocalDate(), Escenario.AHORA.toLocalDate().plusDays(30));
        Funcion martes = e.funcionEn(24);   // martes 3 de noviembre
        Funcion miercoles = e.funcionEn(48);
        assertEquals(7_000, e.compras.cotizar(pedido(e, martes, List.of("B1"))).getTotalAPagar());
        assertEquals(10_000, e.compras.cotizar(pedido(e, miercoles, List.of("B1"))).getTotalAPagar());
    }

    @Test
    void elPrecioDeUnComboSeCalculaRecursivamente() {
        Producto crispetas = new Producto("1", "Crispetas", CategoriaProducto.CRISPETAS, 10000, 100);
        Producto gaseosa = new Producto("2", "Gaseosa", CategoriaProducto.BEBIDA, 5000, 50);
        Combo pareja = new Combo("3", "Pareja", 10, 200);
        pareja.agregar(crispetas);
        pareja.agregar(gaseosa);
        pareja.agregar(gaseosa);
        Combo familiar = new Combo("4", "Familiar", 0, 400);
        familiar.agregar(pareja);
        familiar.agregar(crispetas);
        assertEquals(18_000, pareja.getPrecio());   // 20.000 - 10 %
        assertEquals(28_000, familiar.getPrecio()); // 18.000 + 10.000
        assertFalse(crispetas.esCombo());
    }
}
