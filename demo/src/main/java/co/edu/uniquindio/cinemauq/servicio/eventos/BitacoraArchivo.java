package co.edu.uniquindio.cinemauq.servicio.eventos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class BitacoraArchivo implements ObservadorCine {

    private final Path archivo;

    public BitacoraArchivo(Path archivo) {
        this.archivo = archivo;
    }

    @Override
    public void actualizar(EventoCine evento) {
        String linea = evento.fecha() + " | " + evento.tipo() + " | "
                + (evento.destinatario() == null ? "-" : evento.destinatario().getEmail())
                + " | " + evento.mensaje() + System.lineSeparator();
        try {
            Files.createDirectories(archivo.getParent());
            Files.writeString(archivo, linea, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("No se pudo escribir la bitácora: " + e.getMessage());
        }
    }
}
