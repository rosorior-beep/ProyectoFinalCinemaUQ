package co.edu.uniquindio.cinemauq.servicio.reportes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Reporte {

    private final String titulo;
    private final LocalDateTime fechaGeneracion;
    private final LocalDate desde;
    private final LocalDate hasta;
    private final List<String> encabezados;
    private final List<List<String>> filas = new ArrayList<>();
    private final Map<String, String> totales = new LinkedHashMap<>();
    private final boolean[] columnasNumericas;

    public Reporte(String titulo, LocalDateTime fechaGeneracion, LocalDate desde, LocalDate hasta,
                   List<String> encabezados, boolean... columnasNumericas) {
        this.titulo = titulo;
        this.fechaGeneracion = fechaGeneracion;
        this.desde = desde;
        this.hasta = hasta;
        this.encabezados = List.copyOf(encabezados);
        this.columnasNumericas = columnasNumericas.length == encabezados.size()
                ? columnasNumericas : new boolean[encabezados.size()];
    }

    public void agregarFila(String... valores) {
        filas.add(List.of(valores));
    }

    public void agregarTotal(String etiqueta, String valor) {
        totales.put(etiqueta, valor);
    }

    public boolean esNumerica(int columna) {
        return columna < columnasNumericas.length && columnasNumericas[columna];
    }

    public String getTitulo() { return titulo; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }
    public List<String> getEncabezados() { return encabezados; }
    public List<List<String>> getFilas() { return filas; }
    public Map<String, String> getTotales() { return totales; }
    public int getCantidadRegistros() { return filas.size(); }
}
