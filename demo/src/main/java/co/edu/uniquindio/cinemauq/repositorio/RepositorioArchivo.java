package co.edu.uniquindio.cinemauq.repositorio;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public final class RepositorioArchivo extends RepositorioMemoria {

    private static RepositorioArchivo instancia;

    private final Path archivo;

    private RepositorioArchivo(Path archivo) {
        this.archivo = archivo;
    }

    public static RepositorioArchivo getInstance() {
        if (instancia == null) {
            instancia = new RepositorioArchivo(Paths.get("datos", "cinemauq.dat"));
        }
        return instancia;
    }

    public boolean existeArchivo() {
        return Files.exists(archivo);
    }


    public boolean cargar() {
        if (!existeArchivo()) return false;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(archivo))) {
            datos = (DatosCine) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("No se pudieron cargar los datos (" + e.getMessage() + "). Se usarán datos iniciales.");
            datos = new DatosCine();
            return false;
        }
    }

    @Override
    public void guardar() {
        try {
            Files.createDirectories(archivo.getParent());
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(archivo))) {
                out.writeObject(datos);
            }
        } catch (IOException e) {
            throw new CinemaUQException("No fue posible guardar los datos: " + e.getMessage());
        }
    }
}
