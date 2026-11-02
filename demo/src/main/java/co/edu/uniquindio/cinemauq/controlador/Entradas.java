package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


final class Entradas {

    private Entradas() {
    }

    static double dinero(String texto, String campo) {
        try {
            String limpio = texto == null ? "" : texto.replace("$", "").replace(".", "").replace(",", "").trim();
            return Double.parseDouble(limpio);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("El campo \"" + campo + "\" debe ser un valor numérico (ej. 50000).");
        }
    }

    static double decimal(String texto, String campo) {
        try {
            return Double.parseDouble(texto.replace(",", ".").replace("%", "").trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new DatosInvalidosException("El campo \"" + campo + "\" debe ser numérico.");
        }
    }

    static int entero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new DatosInvalidosException("El campo \"" + campo + "\" debe ser un número entero.");
        }
    }

    static LocalTime hora(String texto) {
        try {
            return LocalTime.parse(texto.trim().length() == 4 ? "0" + texto.trim() : texto.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new DatosInvalidosException("La hora debe tener el formato HH:mm (ej. 19:30).");
        }
    }

    static Set<String> codigos(String texto) {
        if (texto == null || texto.isBlank()) return Set.of();
        return Arrays.stream(texto.split("[,;\\s]+")).map(String::trim).filter(s -> !s.isEmpty())
                .map(String::toUpperCase).collect(Collectors.toSet());
    }

    static void requerido(Object valor, String campo) {
        if (valor == null || (valor instanceof String s && s.isBlank())) {
            throw new DatosInvalidosException("El campo \"" + campo + "\" es obligatorio.");
        }
    }
}
