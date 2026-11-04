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

public class ReporteVentas implements GeneradorReporte {

    @Override
    public Reporte generar(IRepositorioCine repo, LocalDate desde, LocalDate hasta, LocalDateTime ahora) {
        Reporte r = new Reporte(TipoReporte.VENTAS_PERIODO.getTitulo(), ahora, desde, hasta,
                List.of("Fecha", "Compra", "Cliente", "Película", "Entradas", "Entradas $", "Confitería $",
                        "Total pagado", "Estado"),
                false, false, false, false, true, true, true, true, false);
        double total = 0, reembolsos = 0, entradasValor = 0, confiteria = 0;
        long entradas = 0;
        List<Compra> compras = repo.getCompras().stream()
                .filter(c -> GeneradorReporte.enPeriodo(c.getFechaCompra(), desde, hasta))
                .sorted(Comparator.comparing(Compra::getFechaCompra)).toList();
        for (Compra c : compras) {
            r.agregarFila(Formato.fechaHora(c.getFechaCompra()), c.getId(), c.getCliente().getNombre(),
                    c.getFuncion().getPelicula().getTitulo(), String.valueOf(c.getEntradas().size()),
                    Formato.moneda(c.getTotalEntradas()), Formato.moneda(c.getTotalConfiteria()),
                    Formato.moneda(c.getTotalAPagar()), c.getNombreEstado());
            total += c.getTotalAPagar();
            entradasValor += c.getTotalEntradas();
            confiteria += c.getTotalConfiteria();
            entradas += c.getEntradas().size();
            reembolsos += c.getMontoReembolsado();
        }
        r.agregarTotal("Compras registradas", Formato.entero(compras.size()));
        r.agregarTotal("Entradas vendidas", Formato.entero(entradas));
        r.agregarTotal("Ventas de entradas", Formato.moneda(entradasValor));
        r.agregarTotal("Ventas de confitería", Formato.moneda(confiteria));
        r.agregarTotal("Total ventas (bruto)", Formato.moneda(total));
        r.agregarTotal("Reembolsos de estas compras", Formato.moneda(reembolsos));
        r.agregarTotal("Total ventas (neto)", Formato.moneda(total - reembolsos));
        return r;
    }
}
