package co.edu.uniquindio.cinemauq.vista.util;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;


public final class Dialogos {

    private Dialogos() {
    }

    public static void error(Throwable e) {
        boolean deNegocio = e instanceof CinemaUQException;
        Alert alerta = new Alert(deNegocio ? Alert.AlertType.WARNING : Alert.AlertType.ERROR);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(deNegocio ? "No se pudo completar la operación" : "Error inesperado");
        alerta.setContentText(e.getMessage() == null ? e.toString() : e.getMessage());
        if (!deNegocio) e.printStackTrace();
        alerta.showAndWait();
    }

    public static void info(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(titulo);
        return alerta.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }

    public static Optional<String> pedirTexto(String titulo, String mensaje, String valorInicial) {
        TextInputDialog dialogo = new TextInputDialog(valorInicial);
        dialogo.setTitle("CinemaUQ");
        dialogo.setHeaderText(titulo);
        dialogo.setContentText(mensaje);
        return dialogo.showAndWait();
    }


    public static boolean intentar(Runnable accion) {
        try {
            accion.run();
            return true;
        } catch (RuntimeException e) {
            error(e);
            return false;
        }
    }
}
