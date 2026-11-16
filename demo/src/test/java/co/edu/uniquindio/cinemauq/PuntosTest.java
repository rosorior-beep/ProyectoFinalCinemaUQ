package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.excepciones.PuntosInsuficientesException;
import co.edu.uniquindio.cinemauq.modelo.puntos.ConsumoPuntos;
import co.edu.uniquindio.cinemauq.modelo.puntos.CuentaPuntos;
import co.edu.uniquindio.cinemauq.modelo.puntos.LotePuntos;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PuntosTest {

    private final LocalDateTime hoy = LocalDateTime.of(2026, 11, 2, 12, 0);

    @Test
    void losPuntosVencidosNoSeCuentanNiSeUsan() {
        CuentaPuntos cuenta = new CuentaPuntos();
        cuenta.acumular(300, hoy.minusMonths(13), 1, "C-1"); // vencido
        cuenta.acumular(200, hoy.minusMonths(2), 1, "C-2");
        assertEquals(200, cuenta.getPuntosDisponibles(hoy.toLocalDate()));
        assertEquals(300, cuenta.getPuntosVencidos(hoy.toLocalDate()));
        assertThrows(PuntosInsuficientesException.class, () -> cuenta.redimir(250, hoy, "prueba"));
    }

    @Test
    void seConsumenPrimeroLosPuntosMasProximosAVencer() {
        CuentaPuntos cuenta = new CuentaPuntos();
        LotePuntos reciente = cuenta.acumular(500, hoy.minusMonths(1), 1, "C-3");
        LotePuntos antiguo = cuenta.acumular(300, hoy.minusMonths(11), 1, "C-1");
        LotePuntos medio = cuenta.acumular(200, hoy.minusMonths(6), 1, "C-2");

        List<ConsumoPuntos> consumos = cuenta.redimir(400, hoy, "Entrada");

        assertEquals(0, antiguo.getDisponibles());   // 300 del más antiguo
        assertEquals(100, medio.getDisponibles());   // 100 del siguiente
        assertEquals(500, reciente.getDisponibles()); // el más nuevo intacto
        assertEquals(2, consumos.size());
        assertEquals(600, cuenta.getPuntosDisponibles(hoy.toLocalDate()));
    }

    @Test
    void alDevolverPuntosRegresanASusLotesOriginales() {
        CuentaPuntos cuenta = new CuentaPuntos();
        LotePuntos lote = cuenta.acumular(300, hoy.minusMonths(3), 1, "C-1");
        List<ConsumoPuntos> consumos = cuenta.redimir(250, hoy, "Entrada");
        assertEquals(50, lote.getDisponibles());
        cuenta.devolver(consumos, hoy, "Cancelación");
        assertEquals(300, lote.getDisponibles());
    }

    @Test
    void elLoteVenceUnAnioDespuesDeObtenido() {
        CuentaPuntos cuenta = new CuentaPuntos();
        LotePuntos lote = cuenta.acumular(100, LocalDateTime.of(2026, 9, 10, 15, 0), 1, "C-1");
        assertEquals(java.time.LocalDate.of(2027, 9, 10), lote.getFechaVencimiento());
    }
}
