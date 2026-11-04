package co.edu.uniquindio.cinemauq.servicio.reportes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Formato {

    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private Formato() {
    }

    public static String moneda(double valor) {
        return "$" + String.format("%,.0f", valor).replace(',', '.');
    }

    public static String entero(long valor) {
        return String.format("%,d", valor).replace(',', '.');
    }

    public static String porcentaje(double valor) {
        return String.format("%.1f%%", valor);
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(FECHA_HORA);
    }
}
