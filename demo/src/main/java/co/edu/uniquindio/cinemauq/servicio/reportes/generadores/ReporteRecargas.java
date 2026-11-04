package co.edu.uniquindio.cinemauq.servicio.reportes.generadores;

import co.edu.uniquindio.cinemauq.modelo.tarjeta.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TipoMovimiento;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReporteRecargas implements GeneradorReporte {

    @Override
    public Reporte generar(IRepositorioCine repo, LocalDate desde, LocalDate hasta, LocalDateTime ahora) {
        Reporte r = new Reporte(TipoReporte.RECARGAS.getTitulo(), ahora, desde, hasta,
                List.of("Fecha", "Referencia", "Cliente", "Tarjeta", "Monto", "Saldo resultante"),
                false, false, false, false, true, true);
        record Fila(Cliente c, Movimiento m) { }
        List<Fila> filas = new ArrayList<>();
        for (Cliente c : repo.getClientes()) {
            for (Movimiento m : c.getTarjeta().getMovimientos()) {
                if (m.getTipo() == TipoMovimiento.RECARGA && GeneradorReporte.enPeriodo(m.getFecha(), desde, hasta)) {
                    filas.add(new Fila(c, m));
                }
            }
        }
        filas.sort(Comparator.comparing(f -> f.m().getFecha()));
        double total = 0;
        for (Fila f : filas) {
            r.agregarFila(Formato.fechaHora(f.m().getFecha()), f.m().getReferencia(), f.c().getNombre(),
                    f.c().getTarjeta().getNumeroEnmascarado(), Formato.moneda(f.m().getMonto()),
                    Formato.moneda(f.m().getSaldoResultante()));
            total += f.m().getMonto();
        }
        r.agregarTotal("Recargas realizadas", Formato.entero(filas.size()));
        r.agregarTotal("Valor total recargado", Formato.moneda(total));
        r.agregarTotal("Promedio por recarga", Formato.moneda(filas.isEmpty() ? 0 : total / filas.size()));
        return r;
    }
}
