package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.AccesoDenegadoException;
import co.edu.uniquindio.cinemauq.excepciones.FuncionSolapadaException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.asientos.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.proxy.ProxyServicioAdministracion;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdministracionTest {

    @Test
    void elProxyBloqueaAUnClienteYPermiteAlAdministrador() {
        Escenario e = new Escenario();
        AtomicReference<Usuario> sesion = new AtomicReference<>(e.cliente);
        IServicioAdministracion proxy = new ProxyServicioAdministracion(sesion::get, () -> e.admin);
        assertThrows(AccesoDenegadoException.class, proxy::listarSalas);
        assertThrows(AccesoDenegadoException.class, () -> proxy.recargarTarjeta(e.cliente.getId(), 50_000));
        sesion.set(e.administrador);
        assertEquals(1, proxy.listarSalas().size());
    }

    @Test
    void dosFuncionesNoPuedenCruzarseEnLaMismaSala() {
        Escenario e = new Escenario();
        e.funcionEn(24); // dura 120 min + 20 de limpieza
        assertThrows(FuncionSolapadaException.class, () -> e.funcionEn(26));
        assertDoesNotThrow(() -> e.funcionEn(27));
    }

    @Test
    void replicarFuncionUsaPrototypeSinCopiarAsientosVendidos() {
        Escenario e = new Escenario();
        Funcion original = e.funcionEn(24);
        original.ocuparAsiento("A2");
        List<Funcion> copias = e.admin.replicarFuncion(original.getId(), 3);
        assertEquals(3, copias.size());
        assertEquals(original.getFechaHora().plusDays(1), copias.get(0).getFechaHora());
        assertTrue(copias.get(0).getAsientosOcupados().isEmpty());
        assertTrue(!copias.get(0).getId().equals(original.getId()));
    }

    @Test
    void elFactoryMethodCambiaElTipoDeAsiento() {
        Escenario e = new Escenario();
        assertEquals(TipoAsiento.ACCESIBLE, e.sala.buscarAsiento("A1").getTipo());
        assertEquals(TipoAsiento.PREFERENCIAL, e.sala.buscarAsiento("E3").getTipo());
        e.admin.cambiarTipoAsiento(e.sala.getId(), "B3", TipoAsiento.PREFERENCIAL);
        assertEquals(6000, e.sala.buscarAsiento("B3").getRecargo());
    }

    @Test
    void soloElAdministradorRecargaYSeRegistraElMovimiento() {
        Escenario e = new Escenario();
        var solicitud = e.recargas.solicitarRecarga(e.cliente, 50_000);
        assertThrows(OperacionNoPermitidaException.class, () -> e.recargas.solicitarRecarga(e.cliente, 20_000));
        e.admin.aprobarSolicitudRecarga(solicitud.getId());
        assertEquals(50_000, e.cliente.getTarjeta().getSaldo());
        assertEquals(1, e.cliente.getTarjeta().getMovimientos().size());
        assertThrows(OperacionNoPermitidaException.class, () -> e.admin.aprobarSolicitudRecarga(solicitud.getId()));
    }
}
