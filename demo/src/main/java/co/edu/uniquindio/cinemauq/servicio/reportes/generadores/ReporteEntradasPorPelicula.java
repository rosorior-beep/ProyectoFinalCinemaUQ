package co.edu.uniquindio.cinemauq.servicio.reportes.generadores;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReporteEntradasPorPelicula implements GeneradorReporte {

    @Override
    public Reporte generar(IRepositorioCine repo, LocalDate desde, LocalDate hasta, LocalDateTime ahora) {
        Reporte r = new Reporte(TipoReporte.ENTRADAS_POR_PELICULA.getTitulo(), ahora, desde, hasta,
                List.of("Película", "Funciones", "Entradas", "Total"), false, true, true, true);
        Map<String, long[]> entradas = new LinkedHashMap<>();
        Map<String, Double> totales = new LinkedHashMap<>();
        Map<String, Set<String>> funciones = new LinkedHashMap<>();
        for (Compra c : repo.getCompras()) {
            if (c.estaCancelada() || !GeneradorReporte.enPeriodo(c.getFechaCompra(), desde, hasta)) continue;
            String titulo = c.getFuncion().getPelicula().getTitulo();
            entradas.computeIfAbsent(titulo, k -> new long[1])[0] += c.getEntradas().size();
            totales.merge(titulo, c.getTotalEntradas(), Double::sum);
            funciones.computeIfAbsent(titulo, k -> new HashSet<>()).add(c.getFuncion().getId());
        }
        long totalEntradas = 0;
        double totalVentas = 0;
        List<String> orden = entradas.keySet().stream()
                .sorted((a, b) -> Long.compare(entradas.get(b)[0], entradas.get(a)[0])).toList();
        for (String titulo : orden) {
            r.agregarFila(titulo, String.valueOf(funciones.get(titulo).size()),
                    Formato.entero(entradas.get(titulo)[0]), Formato.moneda(totales.get(titulo)));
            totalEntradas += entradas.get(titulo)[0];
            totalVentas += totales.get(titulo);
        }
        r.agregarTotal("Total entradas", Formato.entero(totalEntradas));
        r.agregarTotal("Total ventas de entradas", Formato.moneda(totalVentas));
        return r;
    }
}
