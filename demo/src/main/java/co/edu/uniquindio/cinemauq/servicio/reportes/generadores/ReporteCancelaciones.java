package co.edu.uniquindio.cinemauq.servicio.reportes.generadores;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ReporteCancelaciones implements GeneradorReporte {

    @Override
    public Reporte generar(IRepositorioCine repo, LocalDate desde, LocalDate hasta, LocalDateTime ahora) {
        Reporte r = new Reporte(TipoReporte.CANCELACIONES.getTitulo(), ahora, desde, hasta,
                List.of("Fecha cancelación", "Compra", "Cliente", "Película", "Motivo", "Pagado", "%", "Reembolsado"),
                false, false, false, false, false, true, true, true);
        List<Compra> canceladas = repo.getCompras().stream()
                .filter(c -> c.estaCancelada() && GeneradorReporte.enPeriodo(c.getFechaCancelacion(), desde, hasta))
                .sorted(Comparator.comparing(Compra::getFechaCancelacion)).toList();
        double pagado = 0, reembolsado = 0;
        long porCine = 0;
        for (Compra c : canceladas) {
            r.agregarFila(Formato.fechaHora(c.getFechaCancelacion()), c.getId(), c.getCliente().getNombre(),
                    c.getFuncion().getPelicula().getTitulo(), c.getMotivoCancelacion(),
                    Formato.moneda(c.getTotalAPagar()), (int) c.getPorcentajeReembolso() + "%",
                    Formato.moneda(c.getMontoReembolsado()));
            pagado += c.getTotalAPagar();
            reembolsado += c.getMontoReembolsado();
            if (c.getMotivoCancelacion().contains("cine")) porCine++;
        }
        r.agregarTotal("Cancelaciones", Formato.entero(canceladas.size()));
        r.agregarTotal("Por función cancelada (cine)", Formato.entero(porCine));
        r.agregarTotal("Por solicitud del cliente", Formato.entero(canceladas.size() - porCine));
        r.agregarTotal("Valor pagado de esas compras", Formato.moneda(pagado));
        r.agregarTotal("Total reembolsado", Formato.moneda(reembolsado));
        r.agregarTotal("Retenido por el cine", Formato.moneda(pagado - reembolsado));
        return r;
    }
}
