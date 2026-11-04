package co.edu.uniquindio.cinemauq.servicio.reportes.generadores;

import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ReporteEntradasPorFuncion implements GeneradorReporte {

    @Override
    public Reporte generar(IRepositorioCine repo, LocalDate desde, LocalDate hasta, LocalDateTime ahora) {
        Reporte r = new Reporte(TipoReporte.ENTRADAS_POR_FUNCION.getTitulo(), ahora, desde, hasta,
                List.of("Función", "Fecha y hora", "Película", "Sala", "Estado", "Entradas", "Total"),
                false, false, false, false, false, true, true);
        long totalEntradas = 0;
        double totalVentas = 0;
        List<Funcion> funciones = repo.getFunciones().stream()
                .filter(f -> GeneradorReporte.enPeriodo(f.getFechaHora(), desde, hasta))
                .sorted(Comparator.comparing(Funcion::getFechaHora)).toList();
        for (Funcion f : funciones) {
            List<Compra> compras = repo.getCompras().stream()
                    .filter(c -> !c.estaCancelada() && c.getFuncion().getId().equals(f.getId())).toList();
            long entradas = compras.stream().mapToLong(c -> c.getEntradas().size()).sum();
            double ventas = compras.stream().mapToDouble(Compra::getTotalEntradas).sum();
            r.agregarFila(f.getId(), Formato.fechaHora(f.getFechaHora()), f.getPelicula().getTitulo(),
                    f.getSala().getNombre(), f.estaCancelada() ? "Cancelada" : "Programada",
                    Formato.entero(entradas), Formato.moneda(ventas));
            totalEntradas += entradas;
            totalVentas += ventas;
        }
        r.agregarTotal("Funciones en el periodo", Formato.entero(funciones.size()));
        r.agregarTotal("Total entradas", Formato.entero(totalEntradas));
        r.agregarTotal("Total ventas de entradas", Formato.moneda(totalVentas));
        return r;
    }
}
