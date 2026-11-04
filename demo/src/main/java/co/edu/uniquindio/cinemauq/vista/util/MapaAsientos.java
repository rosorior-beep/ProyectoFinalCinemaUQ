package co.edu.uniquindio.cinemauq.vista.util;

import co.edu.uniquindio.cinemauq.modelo.asientos.Asiento;
import co.edu.uniquindio.cinemauq.modelo.asientos.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;


public class MapaAsientos extends VBox {

    private final Sala sala;
    private final Funcion funcion; // null en el editor de salas del administrador
    private final Set<String> seleccionados = new LinkedHashSet<>();
    private final Consumer<String> alHacerClic;
    private final GridPane grilla = new GridPane();
    private int maximo = Integer.MAX_VALUE;


    public MapaAsientos(Sala sala, Funcion funcion, Consumer<String> alHacerClic) {
        super(14);
        this.sala = sala;
        this.funcion = funcion;
        this.alHacerClic = alHacerClic;
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(10));

        Label pantalla = new Label("PANTALLA");
        pantalla.getStyleClass().add("pantalla");
        pantalla.setMaxWidth(Double.MAX_VALUE);
        pantalla.setAlignment(Pos.CENTER);

        grilla.setHgap(5);
        grilla.setVgap(5);
        grilla.setAlignment(Pos.CENTER);

        getChildren().addAll(pantalla, grilla, leyenda());
        dibujar();
    }


    public void setSoloLectura(boolean soloLectura) {
        grilla.setMouseTransparent(soloLectura);
    }

    public void setMaximo(int maximo) {
        this.maximo = maximo;
    }

    public Set<String> getSeleccionados() {
        return seleccionados;
    }

    public void limpiarSeleccion() {
        seleccionados.clear();
        dibujar();
    }

    public void dibujar() {
        grilla.getChildren().clear();
        for (int c = 1; c <= sala.getColumnas(); c++) {
            Label numero = new Label(String.valueOf(c));
            numero.getStyleClass().add("numero-columna");
            numero.setMinWidth(34);
            numero.setAlignment(Pos.CENTER);
            grilla.add(numero, c, 0);
        }
        for (Asiento a : sala.getAsientos()) {
            int fila = a.getFila() - 'A' + 1;
            if (a.getNumero() == 1) {
                Label letra = new Label(String.valueOf(a.getFila()));
                letra.getStyleClass().add("letra-fila");
                grilla.add(letra, 0, fila);
            }
            grilla.add(boton(a), a.getNumero(), fila);
        }
    }

    private Button boton(Asiento a) {
        String codigo = a.getCodigo();
        Button b = new Button(a.getTipo() == TipoAsiento.ACCESIBLE ? "♿" : String.valueOf(a.getNumero()));
        b.getStyleClass().add("asiento");
        b.setMinSize(34, 30);
        b.setPrefSize(34, 30);
        boolean ocupado = funcion != null && funcion.getAsientosOcupados().contains(codigo);
        String estado;
        if (!a.isHabilitado()) {
            estado = "asiento-deshabilitado";
        } else if (ocupado) {
            estado = "asiento-ocupado";
        } else if (seleccionados.contains(codigo)) {
            estado = "asiento-seleccionado";
        } else {
            estado = switch (a.getTipo()) {
                case GENERAL -> "asiento-general";
                case PREFERENCIAL -> "asiento-preferencial";
                case ACCESIBLE -> "asiento-accesible";
            };
        }
        b.getStyleClass().add(estado);
        String info = codigo + " · " + a.getTipo()
                + (a.getRecargo() > 0 ? " (+" + co.edu.uniquindio.cinemauq.servicio.reportes.Formato.moneda(a.getRecargo()) + ")" : "")
                + (!a.isHabilitado() ? " · deshabilitado" : ocupado ? " · ocupado" : "");
        b.setTooltip(new Tooltip(info));

        boolean modoSeleccion = funcion != null;
        if (modoSeleccion && (ocupado || !a.isHabilitado())) {
            b.setDisable(true);
        }
        b.setOnAction(e -> {
            if (modoSeleccion) {
                if (seleccionados.contains(codigo)) {
                    seleccionados.remove(codigo);
                } else if (seleccionados.size() < maximo) {
                    seleccionados.add(codigo);
                } else {
                    Dialogos.info("Límite de entradas", "Puedes seleccionar máximo " + maximo + " asientos por compra.");
                    return;
                }
                dibujar();
            }
            if (alHacerClic != null) alHacerClic.accept(codigo);
        });
        return b;
    }

    private FlowPane leyenda() {
        FlowPane f = new FlowPane(16, 6);
        f.setAlignment(Pos.CENTER);
        f.getChildren().addAll(
                item("asiento-general", "General"),
                item("asiento-preferencial", "Preferencial"),
                item("asiento-accesible", "Accesible"),
                item("asiento-seleccionado", "Seleccionado"),
                item("asiento-ocupado", "Ocupado"),
                item("asiento-deshabilitado", "Deshabilitado"));
        return f;
    }

    private HBox item(String clase, String texto) {
        Region muestra = new Region();
        muestra.getStyleClass().addAll("asiento", clase, "muestra-leyenda");
        muestra.setMinSize(16, 16);
        muestra.setMaxSize(16, 16);
        Label l = new Label(texto);
        l.getStyleClass().add("texto-leyenda");
        HBox h = new HBox(6, muestra, l);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }
}