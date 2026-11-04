package co.edu.uniquindio.cinemauq.servicio.impl;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Cobrable;
import co.edu.uniquindio.cinemauq.modelo.compra.items.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TipoMovimiento;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.IServicioReportes;
import co.edu.uniquindio.cinemauq.servicio.reportes.Indicadores;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.exportadores.FabricaExportadores;
import co.edu.uniquindio.cinemauq.servicio.reportes.generadores.GeneradorReporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.generadores.ReporteCancelaciones;
import co.edu.uniquindio.cinemauq.servicio.reportes.generadores.ReporteEntradasPorFuncion;
import co.edu.uniquindio.cinemauq.servicio.reportes.generadores.ReporteEntradasPorPelicula;
import co.edu.uniquindio.cinemauq.servicio.reportes.generadores.ReporteOcupacionSalas;
import co.edu.uniquindio.cinemauq.servicio.reportes.generadores.ReporteProductosVendidos;
import co.edu.uniquindio.cinemauq.servicio.reportes.generadores.ReportePuntos;
import co.edu.uniquindio.cinemauq.servicio.reportes.generadores.ReporteRecargas;
import co.edu.uniquindio.cinemauq.servicio.reportes.generadores.ReporteVentas;
import co.edu.uniquindio.cinemauq.servicio.tiempo.ProveedorTiempo;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ServicioReportes implements IServicioReportes {

    private final IRepositorioCine repositorio;
    private final ProveedorTiempo tiempo;
    private final Map<TipoReporte, GeneradorReporte> generadores = new EnumMap<>(TipoReporte.class);

    public ServicioReportes(IRepositorioCine repositorio, ProveedorTiempo tiempo) {
        this.repositorio = repositorio;
        this.tiempo = tiempo;
        generadores.put(TipoReporte.VENTAS_PERIODO, new ReporteVentas());
        generadores.put(TipoReporte.ENTRADAS_POR_PELICULA, new ReporteEntradasPorPelicula());
        generadores.put(TipoReporte.ENTRADAS_POR_FUNCION, new ReporteEntradasPorFuncion());
        generadores.put(TipoReporte.OCUPACION_SALAS, new ReporteOcupacionSalas());
        generadores.put(TipoReporte.PRODUCTOS_VENDIDOS, new ReporteProductosVendidos());
        generadores.put(TipoReporte.RECARGAS, new ReporteRecargas());
        generadores.put(TipoReporte.CANCELACIONES, new ReporteCancelaciones());
        generadores.put(TipoReporte.PUNTOS, new ReportePuntos());
    }

    @Override
    public Reporte generar(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        return generadores.get(tipo).generar(repositorio, desde, hasta, tiempo.ahora());
    }

    @Override
    public Path exportar(Reporte reporte, String extension, Path destino) {
        return FabricaExportadores.paraExtension(extension).exportar(reporte, destino);
    }

    private void validarPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) throw new DatosInvalidosException("Selecciona el rango de fechas.");
        if (hasta.isBefore(desde)) throw new DatosInvalidosException("La fecha final no puede ser anterior a la inicial.");
    }



    private List<Compra> comprasValidas(LocalDate desde, LocalDate hasta) {
        return repositorio.getCompras().stream()
                .filter(c -> !c.estaCancelada() && GeneradorReporte.enPeriodo(c.getFechaCompra(), desde, hasta))
                .toList();
    }

    private List<Funcion> funcionesDelPeriodo(LocalDate desde, LocalDate hasta) {
        return repositorio.getFunciones().stream()
                .filter(f -> !f.estaCancelada() && GeneradorReporte.enPeriodo(f.getFechaHora(), desde, hasta))
                .toList();
    }

    @Override
    public Map<LocalDate, Double> ventasPorDia(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        Map<LocalDate, Double> mapa = diasVacios(desde, hasta);
        comprasValidas(desde, hasta).forEach(c -> mapa.merge(c.getFechaCompra().toLocalDate(), c.getTotalAPagar(), Double::sum));
        return mapa;
    }

    @Override
    public Map<LocalDate, Double> recargasPorDia(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        Map<LocalDate, Double> mapa = diasVacios(desde, hasta);
        for (Cliente c : repositorio.getClientes()) {
            for (Movimiento m : c.getTarjeta().getMovimientos()) {
                if (m.getTipo() == TipoMovimiento.RECARGA && GeneradorReporte.enPeriodo(m.getFecha(), desde, hasta)) {
                    mapa.merge(m.getFecha().toLocalDate(), m.getMonto(), Double::sum);
                }
            }
        }
        return mapa;
    }

    private Map<LocalDate, Double> diasVacios(LocalDate desde, LocalDate hasta) {
        Map<LocalDate, Double> mapa = new TreeMap<>();
        for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) mapa.put(d, 0.0);
        return mapa;
    }

    @Override
    public Map<String, Long> entradasPorPelicula(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        Map<String, Long> mapa = comprasValidas(desde, hasta).stream().collect(Collectors.groupingBy(
                c -> c.getFuncion().getPelicula().getTitulo(), Collectors.summingLong(c -> c.getEntradas().size())));
        return ordenarDesc(mapa, Integer.MAX_VALUE);
    }

    @Override
    public Map<String, Double> ocupacionPorSala(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        Map<String, Double> mapa = new LinkedHashMap<>();
        List<Funcion> funciones = funcionesDelPeriodo(desde, hasta);
        for (Sala s : repositorio.getSalas()) {
            List<Funcion> deSala = funciones.stream().filter(f -> f.getSala().getId().equals(s.getId())).toList();
            long capacidad = deSala.size() * s.getCapacidad();
            long vendidos = deSala.stream().mapToLong(f -> f.getAsientosOcupados().size()).sum();
            mapa.put(s.getNombre(), capacidad == 0 ? 0 : vendidos * 100.0 / capacidad);
        }
        return mapa;
    }

    @Override
    public Map<String, Double> ventasPorCategoria(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        Map<String, Double> mapa = new LinkedHashMap<>();
        mapa.put("Entradas", 0.0);
        for (Compra c : comprasValidas(desde, hasta)) {
            mapa.merge("Entradas", c.getTotalEntradas(), Double::sum);
            for (Cobrable item : c.getItems()) {
                if (item.getItemBase() instanceof ItemConfiteria ic) {
                    mapa.merge(ic.getCategoria().toString(), item.getPrecio(), Double::sum);
                }
            }
        }
        return mapa;
    }

    @Override
    public Map<String, Long> productosMasVendidos(LocalDate desde, LocalDate hasta, int limite) {
        validarPeriodo(desde, hasta);
        Map<String, Long> mapa = comprasValidas(desde, hasta).stream()
                .flatMap(c -> c.getItemsConfiteria().stream())
                .collect(Collectors.groupingBy(ItemConfiteria::getNombre, Collectors.summingLong(ItemConfiteria::getCantidad)));
        return ordenarDesc(mapa, limite);
    }

    @Override
    public Indicadores indicadores(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        List<Compra> todas = repositorio.getCompras().stream()
                .filter(c -> GeneradorReporte.enPeriodo(c.getFechaCompra(), desde, hasta)).toList();
        double bruto = todas.stream().mapToDouble(Compra::getTotalAPagar).sum();
        List<Compra> canceladas = repositorio.getCompras().stream()
                .filter(c -> c.estaCancelada() && GeneradorReporte.enPeriodo(c.getFechaCancelacion(), desde, hasta)).toList();
        double reembolsado = canceladas.stream().mapToDouble(Compra::getMontoReembolsado).sum();
        long entradas = comprasValidas(desde, hasta).stream().mapToLong(c -> c.getEntradas().size()).sum();

        List<Funcion> funciones = funcionesDelPeriodo(desde, hasta);
        long capacidad = funciones.stream().mapToLong(f -> f.getSala().getCapacidad()).sum();
        long vendidos = funciones.stream().mapToLong(f -> f.getAsientosOcupados().size()).sum();

        String pelicula = entradasPorPelicula(desde, hasta).keySet().stream().findFirst().orElse("-");
        String producto = productosMasVendidos(desde, hasta, 1).keySet().stream().findFirst().orElse("-");
        double recargado = recargasPorDia(desde, hasta).values().stream().mapToDouble(Double::doubleValue).sum();

        return new Indicadores(bruto - todas.stream().mapToDouble(Compra::getMontoReembolsado).sum(), entradas,
                capacidad == 0 ? 0 : vendidos * 100.0 / capacidad, pelicula, producto, reembolsado,
                canceladas.size(), recargado);
    }

    private <K> Map<K, Long> ordenarDesc(Map<K, Long> mapa, int limite) {
        return mapa.entrySet().stream()
                .sorted(Map.Entry.<K, Long>comparingByValue().reversed())
                .limit(limite)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }
}
