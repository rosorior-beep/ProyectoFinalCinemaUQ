package co.edu.uniquindio.cinemauq.servicio.reportes.generadores;

import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReporteOcupacionSalas implements GeneradorReporte {

    @Override
    public Reporte generar(IRepositorioCine repo, LocalDate desde, LocalDate hasta, LocalDateTime ahora) {
        Reporte r = new Reporte(TipoReporte.OCUPACION_SALAS.getTitulo(), ahora, desde, hasta,
                List.of("Sala", "Tipo", "Funciones", "Capacidad ofrecida", "Asientos vendidos", "Ocupación"),
                false, false, true, true, true, true);
        long capacidadTotal = 0, vendidosTotal = 0;
        for (Sala sala : repo.getSalas()) {
            List<Funcion> funciones = repo.getFunciones().stream()
                    .filter(f -> f.getSala().getId().equals(sala.getId()) && !f.estaCancelada()
                            && GeneradorReporte.enPeriodo(f.getFechaHora(), desde, hasta)).toList();
            long capacidad = funciones.size() * sala.getCapacidad();
            long vendidos = funciones.stream().mapToLong(f -> f.getAsientosOcupados().size()).sum();
            double ocupacion = capacidad == 0 ? 0 : vendidos * 100.0 / capacidad;
            r.agregarFila(sala.getNombre(), sala.getTipo().toString(), String.valueOf(funciones.size()),
                    Formato.entero(capacidad), Formato.entero(vendidos), Formato.porcentaje(ocupacion));
            capacidadTotal += capacidad;
            vendidosTotal += vendidos;
        }
        r.agregarTotal("Capacidad total ofrecida", Formato.entero(capacidadTotal));
        r.agregarTotal("Asientos vendidos", Formato.entero(vendidosTotal));
        r.agregarTotal("Ocupación general",
                Formato.porcentaje(capacidadTotal == 0 ? 0 : vendidosTotal * 100.0 / capacidadTotal));
        return r;
    }
}
