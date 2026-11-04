package co.edu.uniquindio.cinemauq.servicio.reportes.exportadores;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class AdaptadorPdfBox implements ExportadorReporte {

    private static final float MARGEN = 40;
    private static final float ALTO_FILA = 16;
    private static final float TAM_TEXTO = 8.5f;
    private static final Color MORADO = new Color(76, 29, 149);
    private static final Color GRIS_CLARO = new Color(243, 240, 250);

    private final PDFont normal = PDType1Font.HELVETICA;
    private final PDFont negrita = PDType1Font.HELVETICA_BOLD;

    @Override public String getExtension() { return "pdf"; }

    @Override public String getDescripcion() { return "Documento PDF (*.pdf)"; }

    @Override
    public Path exportar(Reporte reporte, Path destino) {
        try (PDDocument documento = new PDDocument()) {
            if (destino.getParent() != null) Files.createDirectories(destino.getParent());
            new Escritor(documento, reporte).escribir();
            documento.save(destino.toFile());
            return destino;
        } catch (IOException e) {
            throw new CinemaUQException("No se pudo crear el PDF: " + e.getMessage());
        }
    }

    private class Escritor {
        private final PDDocument doc;
        private final Reporte r;
        private final PDRectangle tamano;
        private final float[] anchos;
        private PDPageContentStream cs;
        private float y;
        private int pagina = 0;

        Escritor(PDDocument doc, Reporte r) throws IOException {
            this.doc = doc;
            this.r = r;
            this.tamano = r.getEncabezados().size() > 6
                    ? new PDRectangle(PDRectangle.LETTER.getHeight(), PDRectangle.LETTER.getWidth())
                    : PDRectangle.LETTER;
            this.anchos = calcularAnchos();
        }

        void escribir() throws IOException {
            nuevaPagina(true);
            encabezadoTabla();
            if (r.getFilas().isEmpty()) {
                texto("(No hay registros en el periodo consultado)", MARGEN + 4, y - 11, normal, TAM_TEXTO, Color.DARK_GRAY);
                y -= ALTO_FILA;
            }
            int i = 0;
            for (List<String> fila : r.getFilas()) {
                if (y < MARGEN + ALTO_FILA * 2) {
                    nuevaPagina(false);
                    encabezadoTabla();
                }
                if (i++ % 2 == 0) rectangulo(MARGEN, y - ALTO_FILA, anchoUtil(), ALTO_FILA, GRIS_CLARO);
                celdas(fila, normal, Color.BLACK);
                y -= ALTO_FILA;
            }
            totales();
            cs.close();
        }

        private void nuevaPagina(boolean primera) throws IOException {
            if (cs != null) cs.close();
            PDPage page = new PDPage(tamano);
            doc.addPage(page);
            pagina++;
            cs = new PDPageContentStream(doc, page);
            y = tamano.getHeight() - MARGEN;
            if (primera) {
                rectangulo(MARGEN, y - 34, anchoUtil(), 34, MORADO);
                texto("CinemaUQ", MARGEN + 10, y - 22, negrita, 16, Color.WHITE);
                texto(r.getTitulo(), MARGEN + 110, y - 22, negrita, 13, Color.WHITE);
                y -= 52;
                texto("Fecha de generación: " + Formato.fechaHora(r.getFechaGeneracion()), MARGEN, y, normal, 9, Color.DARK_GRAY);
                y -= 13;
                texto("Periodo consultado: " + Formato.fecha(r.getDesde()) + " al " + Formato.fecha(r.getHasta()),
                        MARGEN, y, normal, 9, Color.DARK_GRAY);
                y -= 13;
                texto("Registros encontrados: " + r.getCantidadRegistros(), MARGEN, y, normal, 9, Color.DARK_GRAY);
                y -= 18;
            }
            texto("Página " + pagina, tamano.getWidth() - MARGEN - 40, MARGEN / 2, normal, 8, Color.GRAY);
        }

        private void encabezadoTabla() throws IOException {
            rectangulo(MARGEN, y - ALTO_FILA, anchoUtil(), ALTO_FILA, MORADO);
            celdas(r.getEncabezados(), negrita, Color.WHITE);
            y -= ALTO_FILA;
        }

        private void celdas(List<String> valores, PDFont fuente, Color color) throws IOException {
            float x = MARGEN;
            for (int i = 0; i < valores.size(); i++) {
                String valor = recortar(limpiar(valores.get(i)), fuente, anchos[i] - 6);
                float ancho = fuente.getStringWidth(valor) / 1000 * TAM_TEXTO;
                float xTexto = r.esNumerica(i) ? x + anchos[i] - 4 - ancho : x + 4;
                texto(valor, xTexto, y - 11, fuente, TAM_TEXTO, color);
                x += anchos[i];
            }
        }

        private void totales() throws IOException {
            y -= 10;
            if (y < MARGEN + 20 + r.getTotales().size() * 14) nuevaPagina(false);
            texto("TOTALES", MARGEN, y - 10, negrita, 10, MORADO);
            y -= 26;
            for (Map.Entry<String, String> t : r.getTotales().entrySet()) {
                texto(t.getKey() + ":", MARGEN + 10, y, normal, 9.5f, Color.BLACK);
                texto(t.getValue(), MARGEN + 240, y, negrita, 9.5f, Color.BLACK);
                y -= 14;
            }
        }

        private float[] calcularAnchos() throws IOException {
            int n = r.getEncabezados().size();
            float[] medidos = new float[n];
            for (int i = 0; i < n; i++) {
                medidos[i] = negrita.getStringWidth(limpiar(r.getEncabezados().get(i))) / 1000 * TAM_TEXTO + 10;
            }
            for (List<String> fila : r.getFilas()) {
                for (int i = 0; i < n; i++) {
                    float w = normal.getStringWidth(limpiar(fila.get(i))) / 1000 * TAM_TEXTO + 10;
                    medidos[i] = Math.max(medidos[i], Math.min(w, 220));
                }
            }
            float suma = 0;
            for (float m : medidos) suma += m;
            float factor = anchoUtil() / suma; // se reparte el ancho de la página en proporción
            for (int i = 0; i < n; i++) medidos[i] *= factor;
            return medidos;
        }

        private float anchoUtil() {
            return tamano.getWidth() - 2 * MARGEN;
        }

        private void texto(String t, float x, float yTexto, PDFont fuente, float tam, Color color) throws IOException {
            cs.beginText();
            cs.setNonStrokingColor(color);
            cs.setFont(fuente, tam);
            cs.newLineAtOffset(x, yTexto);
            cs.showText(limpiar(t));
            cs.endText();
        }

        private void rectangulo(float x, float yR, float w, float h, Color color) throws IOException {
            cs.setNonStrokingColor(color);
            cs.addRect(x, yR, w, h);
            cs.fill();
        }

        private String recortar(String t, PDFont fuente, float max) throws IOException {
            if (fuente.getStringWidth(t) / 1000 * TAM_TEXTO <= max) return t;
            while (t.length() > 1 && fuente.getStringWidth(t + "...") / 1000 * TAM_TEXTO > max) {
                t = t.substring(0, t.length() - 1);
            }
            return t + "...";
        }
    }

    private static String limpiar(String texto) {
        if (texto == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : texto.toCharArray()) {
            sb.append(c >= 32 && c <= 255 ? c : '?');
        }
        return sb.toString();
    }
}
