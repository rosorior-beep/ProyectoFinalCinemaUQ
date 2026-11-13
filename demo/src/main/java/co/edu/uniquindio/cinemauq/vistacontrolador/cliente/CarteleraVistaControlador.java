package co.edu.uniquindio.cinemauq.vistacontrolador.cliente;

import co.edu.uniquindio.cinemauq.controlador.ClienteControlador;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CarteleraVistaControlador {

    private static final String[] COLORES = {"poster-1", "poster-2", "poster-3", "poster-4", "poster-5", "poster-6"};

    @FXML private FlowPane panelPeliculas;
    @FXML private VBox panelDetalle;

    private ClienteControlador controlador;
    private Consumer<Funcion> alElegirFuncion;

    public void configurar(ClienteControlador controlador, Consumer<Funcion> alElegirFuncion) {
        this.controlador = controlador;
        this.alElegirFuncion = alElegirFuncion;
        List<Pelicula> lista = controlador.cartelera();
        for (int i = 0; i < lista.size(); i++) {
            panelPeliculas.getChildren().add(tarjetaPelicula(lista.get(i), COLORES[i % COLORES.length]));
        }
        if (lista.isEmpty()) {
            panelPeliculas.getChildren().add(new Label("No hay películas con funciones disponibles."));
        } else {
            mostrarFunciones(lista.get(0));
        }
    }

    private VBox tarjetaPelicula(Pelicula p, String color) {
        Label inicial = new Label(p.getTitulo().substring(0, 1));
        inicial.getStyleClass().add("poster-inicial");
        Label titulo = new Label(p.getTitulo());
        titulo.getStyleClass().add("poster-titulo");
        titulo.setWrapText(true);
        VBox contenidoPoster = new VBox(4, inicial, titulo);
        contenidoPoster.setAlignment(Pos.BOTTOM_LEFT);
        contenidoPoster.setPadding(new Insets(12));
        StackPane poster = new StackPane(contenidoPoster);
        poster.getStyleClass().addAll("poster", color);
        poster.setPrefSize(170, 200);

        Label datos = Componentes.texto(p.getGenero() + " · " + p.getDuracionMinutos() + " min · " + p.getClasificacion(),
                "dato-pelicula");
        VBox tarjeta = new VBox(8, poster, datos);
        tarjeta.getStyleClass().add("tarjeta-pelicula");
        tarjeta.setPrefWidth(170);
        tarjeta.setOnMouseClicked(e -> mostrarFunciones(p));
        return tarjeta;
    }

    private void mostrarFunciones(Pelicula p) {
        panelDetalle.getChildren().clear();
        panelDetalle.getChildren().addAll(Componentes.subtitulo(p.getTitulo()),
                Componentes.texto(p.getGenero() + "  ·  " + p.getDuracionMinutos() + " minutos  ·  Clasificación "
                        + p.getClasificacion(), "dato-pelicula"),
                Componentes.texto(p.getSinopsis(), "descripcion"));

        Map<LocalDate, List<Funcion>> porDia = controlador.funciones(p).stream()
                .collect(Collectors.groupingBy(f -> f.getFechaHora().toLocalDate(), TreeMap::new, Collectors.toList()));
        if (porDia.isEmpty()) {
            panelDetalle.getChildren().add(new Label("No hay funciones disponibles."));
        }
        porDia.forEach((dia, funciones) -> {
            Label etiquetaDia = new Label(nombreDia(dia));
            etiquetaDia.getStyleClass().add("etiqueta-dia");
            etiquetaDia.setMinWidth(150);
            FlowPane horarios = new FlowPane(10, 8);
            for (Funcion f : funciones) {
                Button b = new Button(f.getFechaHora().format(Formato.HORA) + "\n" + f.getSala().getNombre()
                        + " · " + Formato.moneda(f.getPrecioBase() + f.getSala().getTipo().getRecargo())
                        + "\n" + f.getAsientosDisponibles() + " libres");
                b.getStyleClass().add("btn-horario");
                b.setOnAction(e -> alElegirFuncion.accept(f));
                horarios.getChildren().add(b);
            }
            HBox fila = new HBox(12, etiquetaDia, horarios);
            HBox.setHgrow(horarios, Priority.ALWAYS);
            panelDetalle.getChildren().add(fila);
        });
    }

    private String nombreDia(LocalDate dia) {
        LocalDate hoy = controlador.ahora().toLocalDate();
        String nombre = dia.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-CO"));
        nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
        if (dia.equals(hoy)) nombre = "Hoy";
        else if (dia.equals(hoy.plusDays(1))) nombre = "Mañana";
        return nombre + " " + Formato.fecha(dia).substring(0, 5);
    }
}
