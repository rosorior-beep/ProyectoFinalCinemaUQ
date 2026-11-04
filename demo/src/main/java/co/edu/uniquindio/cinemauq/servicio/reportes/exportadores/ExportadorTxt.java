package co.edu.uniquindio.cinemauq.servicio.reportes.exportadores;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ExportadorTxt implements ExportadorReporte {

    @Override public String getExtension() { return "txt"; }

    @Override public String getDescripcion() { return "Archivo de texto (*.txt)"; }

    @Override
    public Path exportar(Reporte reporte, Path destino) {
        try {
            if (destino.getParent() != null) Files.createDirectories(destino.getParent());
            Files.writeString(destino, generarTexto(reporte), StandardCharsets.UTF_8);
            return destino;
        } catch (IOException e) {
            throw new CinemaUQException("No se pudo crear el archivo TXT: " + e.getMessage());
        }
    }

    public String generarTexto(Reporte r) {
        List<String> enc = r.getEncabezados();
        int[] anchos = new int[enc.size()];
        for (int i = 0; i < enc.size(); i++) anchos[i] = enc.get(i).length();
        for (List<String> fila : r.getFilas()) {
            for (int i = 0; i < fila.size(); i++) anchos[i] = Math.max(anchos[i], fila.get(i).length());
        }
        int anchoTotal = 0;
        for (int a : anchos) anchoTotal += a + 3;
        anchoTotal = Math.max(anchoTotal, 60);

        StringBuilder sb = new StringBuilder();
        String doble = "=".repeat(anchoTotal);
        sb.append(doble).append('\n');
        sb.append("CinemaUQ - ").append(r.getTitulo().toUpperCase()).append('\n');
        sb.append(doble).append('\n');
        sb.append("Fecha de generación: ").append(Formato.fechaHora(r.getFechaGeneracion())).append('\n');
        sb.append("Periodo consultado:  ").append(Formato.fecha(r.getDesde())).append(" al ")
                .append(Formato.fecha(r.getHasta())).append('\n');
        sb.append("Registros encontrados: ").append(r.getCantidadRegistros()).append("\n\n");

        sb.append(fila(enc, anchos, r)).append('\n');
        sb.append("-".repeat(anchoTotal)).append('\n');
        if (r.getFilas().isEmpty()) {
            sb.append("(No hay registros en el periodo consultado)\n");
        }
        for (List<String> f : r.getFilas()) {
            sb.append(fila(f, anchos, r)).append('\n');
        }
        sb.append("-".repeat(anchoTotal)).append("\n\nTOTALES\n");
        int anchoEtiqueta = r.getTotales().keySet().stream().mapToInt(String::length).max().orElse(10);
        for (Map.Entry<String, String> t : r.getTotales().entrySet()) {
            sb.append("  ").append(String.format("%-" + anchoEtiqueta + "s", t.getKey()))
                    .append(" : ").append(t.getValue()).append('\n');
        }
        sb.append(doble).append('\n');
        return sb.toString();
    }

    private String fila(List<String> valores, int[] anchos, Reporte r) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            String formato = r.esNumerica(i) ? "%" + anchos[i] + "s" : "%-" + anchos[i] + "s";
            sb.append(String.format(formato, valores.get(i)));
            if (i < valores.size() - 1) sb.append("   ");
        }
        return sb.toString().stripTrailing();
    }
}
