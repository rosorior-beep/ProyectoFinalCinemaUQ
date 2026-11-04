package co.edu.uniquindio.cinemauq.servicio.reportes.generadores;

import co.edu.uniquindio.cinemauq.modelo.puntos.MovimientoPuntos;
import co.edu.uniquindio.cinemauq.modelo.puntos.TipoMovimientoPuntos;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReportePuntos implements GeneradorReporte {

    @Override
    public Reporte generar(IRepositorioCine repo, LocalDate desde, LocalDate hasta, LocalDateTime ahora) {
        Reporte r = new Reporte(TipoReporte.PUNTOS.getTitulo(), ahora, desde, hasta,
                List.of("Cliente", "Acumulados", "Redimidos", "Devueltos", "Revertidos", "Disponibles hoy", "Vencidos"),
                false, true, true, true, true, true, true);
        long acum = 0, red = 0, disp = 0, venc = 0;
        LocalDate hoy = ahora.toLocalDate();
        for (Cliente c : repo.getClientes()) {
            long[] t = new long[TipoMovimientoPuntos.values().length];
            for (MovimientoPuntos m : c.getCuentaPuntos().getMovimientos()) {
                if (GeneradorReporte.enPeriodo(m.getFecha(), desde, hasta)) t[m.getTipo().ordinal()] += m.getCantidad();
            }
            int disponibles = c.getCuentaPuntos().getPuntosDisponibles(hoy);
            int vencidos = c.getCuentaPuntos().getPuntosVencidos(hoy);
            long a = t[TipoMovimientoPuntos.ACUMULACION.ordinal()];
            long rd = t[TipoMovimientoPuntos.REDENCION.ordinal()];
            if (a == 0 && rd == 0 && disponibles == 0 && vencidos == 0) continue;
            r.agregarFila(c.getNombre(), Formato.entero(a), Formato.entero(rd),
                    Formato.entero(t[TipoMovimientoPuntos.DEVOLUCION.ordinal()]),
                    Formato.entero(t[TipoMovimientoPuntos.REVERSION.ordinal()]),
                    Formato.entero(disponibles), Formato.entero(vencidos));
            acum += a;
            red += rd;
            disp += disponibles;
            venc += vencidos;
        }
        r.agregarTotal("Puntos acumulados en el periodo", Formato.entero(acum));
        r.agregarTotal("Puntos redimidos en el periodo", Formato.entero(red));
        r.agregarTotal("Puntos vigentes de todos los clientes", Formato.entero(disp));
        r.agregarTotal("Puntos vencidos sin usar", Formato.entero(venc));
        return r;
    }
}
