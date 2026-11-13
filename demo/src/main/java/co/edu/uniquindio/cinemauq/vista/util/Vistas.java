package co.edu.uniquindio.cinemauq.vista.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;


public final class Vistas {

    private Vistas() {
    }

    public static <C> Parent cargar(String ruta, Consumer<C> configurar) {
        URL url = Vistas.class.getResource(ruta);
        if (url == null) {
            throw new IllegalStateException("No se encontró la vista " + ruta);
        }
        FXMLLoader loader = new FXMLLoader(url);
        try {
            Parent raiz = loader.load();
            C controlador = loader.getController();
            if (configurar != null) {
                configurar.accept(controlador);
            }
            return raiz;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la vista " + ruta + ": " + e.getMessage(), e);
        }
    }
}
