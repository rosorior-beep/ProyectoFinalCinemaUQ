package co.edu.uniquindio.cinemauq.servicio.reportes.generadores;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Cobrable;
import co.edu.uniquindio.cinemauq.modelo.compra.items.ItemConfiteria;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReporteProductosVendidos implements GeneradorReporte {

    @Override
    public Reporte generar(IRepositorioCine repo, LocalDate desde, LocalDate hasta, LocalDateTime ahora) {
        Reporte r = new Reporte(TipoReporte.PRODUCTOS_VENDIDOS.getTitulo(), ahora, desde, hasta,
                List.of("Producto / Combo", "Categoría", "Unidades", "Unid. con puntos", "Ingresos"),
                false, false, true, true, true);
        Map<String, Object[]> datos = new LinkedHashMap<>(); // nombre -> [categoria, unidades, conPuntos, ingresos]
        for (Compra c : repo.getCompras()) {
            if (c.estaCancelada() || !GeneradorReporte.enPeriodo(c.getFechaCompra(), desde, hasta)) continue;
            for (Cobrable item : c.getItems()) {
                if (!(item.getItemBase() instanceof ItemConfiteria base)) continue;
                Object[] fila = datos.computeIfAbsent(base.getNombre(),
                        k -> new Object[]{base.getCategoria().toString(), 0L, 0L, 0.0});
                fila[1] = (long) fila[1] + base.getCantidad();
                if (item.getPuntosUsados() > 0) fila[2] = (long) fila[2] + base.getCantidad();
                fila[3] = (double) fila[3] + item.getPrecio(); // lo realmente pagado (con descuentos)
            }
        }
        long unidades = 0;
        double ingresos = 0;
        List<String> orden = datos.keySet().stream()
                .sorted((a, b) -> Long.compare((long) datos.get(b)[1], (long) datos.get(a)[1])).toList();
        for (String nombre : orden) {
            Object[] f = datos.get(nombre);
            r.agregarFila(nombre, (String) f[0], Formato.entero((long) f[1]), Formato.entero((long) f[2]),
                    Formato.moneda((double) f[3]));
            unidades += (long) f[1];
            ingresos += (double) f[3];
        }
        r.agregarTotal("Unidades vendidas", Formato.entero(unidades));
        r.agregarTotal("Ingresos de confitería", Formato.moneda(ingresos));
        if (!orden.isEmpty()) r.agregarTotal("Más vendido", orden.get(0));
        return r;
    }
}
