package co.edu.uniquindio.cinemauq.modelo.puntos;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.PuntosInsuficientesException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CuentaPuntos implements Serializable {

    private final List<LotePuntos> lotes = new ArrayList<>();
    private final List<MovimientoPuntos> movimientos = new ArrayList<>();
    private int consecutivoLote = 1;

    public LotePuntos acumular(int cantidad, LocalDateTime fecha, int aniosVigencia, String origen) {
        if (cantidad <= 0) {
            throw new DatosInvalidosException("La cantidad de puntos a acumular debe ser positiva.");
        }
        LocalDate obtencion = fecha.toLocalDate();
        LotePuntos lote = new LotePuntos("L" + (consecutivoLote++), cantidad, obtencion,
                obtencion.plusYears(aniosVigencia), origen);
        lotes.add(lote);
        movimientos.add(new MovimientoPuntos(fecha, TipoMovimientoPuntos.ACUMULACION, cantidad,
                "Puntos por la compra " + origen));
        return lote;
    }

    public int getPuntosDisponibles(LocalDate hoy) {
        return lotes.stream().mapToInt(l -> l.disponiblesVigentes(hoy)).sum();
    }

    public int getPuntosVencidos(LocalDate hoy) {
        return lotes.stream().filter(l -> !l.estaVigente(hoy)).mapToInt(LotePuntos::getDisponibles).sum();
    }

    public int getPuntosPorVencer(LocalDate hoy, int dias) {
        return lotes.stream()
                .filter(l -> l.estaVigente(hoy) && !hoy.plusDays(dias).isBefore(l.getFechaVencimiento()))
                .mapToInt(LotePuntos::getDisponibles).sum();
    }

      public List<ConsumoPuntos> redimir(int cantidad, LocalDateTime fecha, String descripcion) {
        if (cantidad <= 0) {
            throw new DatosInvalidosException("La cantidad de puntos a redimir debe ser positiva.");
        }
        LocalDate hoy = fecha.toLocalDate();
        int disponibles = getPuntosDisponibles(hoy);
        if (disponibles < cantidad) {
            throw new PuntosInsuficientesException("Puntos insuficientes: se requieren " + cantidad
                    + " y tienes " + disponibles + " puntos vigentes.");
        }
        List<LotePuntos> ordenados = lotes.stream()
                .filter(l -> l.disponiblesVigentes(hoy) > 0)
                .sorted(Comparator.comparing(LotePuntos::getFechaVencimiento))
                .toList();
        List<ConsumoPuntos> consumos = new ArrayList<>();
        int pendiente = cantidad;
        for (LotePuntos lote : ordenados) {
            if (pendiente == 0) break;
            int tomados = lote.consumir(pendiente);
            consumos.add(new ConsumoPuntos(lote.getId(), tomados));
            pendiente -= tomados;
        }
        movimientos.add(new MovimientoPuntos(fecha, TipoMovimientoPuntos.REDENCION, cantidad, descripcion));
        return consumos;
    }

    public int devolver(List<ConsumoPuntos> consumos, LocalDateTime fecha, String descripcion) {
        int total = 0;
        for (ConsumoPuntos c : consumos) {
            LotePuntos lote = buscarLote(c.loteId());
            if (lote != null) {
                lote.reponer(c.cantidad());
                total += c.cantidad();
            }
        }
        if (total > 0) {
            movimientos.add(new MovimientoPuntos(fecha, TipoMovimientoPuntos.DEVOLUCION, total, descripcion));
        }
        return total;
    }


    public int revertirAcumulacion(String origen, LocalDateTime fecha) {
        int total = 0;
        for (LotePuntos lote : lotes) {
            if (origen.equals(lote.getOrigen())) {
                total += lote.consumir(lote.getDisponibles());
            }
        }
        if (total > 0) {
            movimientos.add(new MovimientoPuntos(fecha, TipoMovimientoPuntos.REVERSION, total,
                    "Puntos retirados por cancelación de " + origen));
        }
        return total;
    }

    private LotePuntos buscarLote(String id) {
        return lotes.stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);
    }

    public List<LotePuntos> getLotes() { return Collections.unmodifiableList(lotes); }
    public List<MovimientoPuntos> getMovimientos() { return Collections.unmodifiableList(movimientos); }
}