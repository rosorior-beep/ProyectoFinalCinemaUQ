package co.edu.uniquindio.cinemauq.vista.util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Function;


public final class Componentes {

    private Componentes() {
    }

    public static Label subtitulo(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("subtitulo");
        return l;
    }

    public static Label texto(String texto, String... clases) {
        Label l = new Label(texto);
        l.getStyleClass().addAll(clases);
        l.setWrapText(true);
        return l;
    }

    public static Region espaciador() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }

    public static HBox fila(double espacio, Node... nodos) {
        HBox h = new HBox(espacio, nodos);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }


    public static <T> void columna(TableColumn<T, String> columna, Function<T, String> valor) {
        columna.setCellValueFactory(d -> new SimpleStringProperty(valor.apply(d.getValue())));
    }


    public static <T> void columnaNumero(TableColumn<T, Number> columna, Function<T, Number> valor,
                                         Function<Number, String> formato) {
        columna.setCellValueFactory(d -> new SimpleObjectProperty<>(valor.apply(d.getValue())));
        columna.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formato.apply(item));
                setAlignment(Pos.CENTER_RIGHT);
            }
        });
    }


    public static void activar(javafx.scene.control.Button activo, javafx.scene.control.Button... todos) {
        for (javafx.scene.control.Button b : todos) b.getStyleClass().remove("nav-activo");
        activo.getStyleClass().add("nav-activo");
    }
}
