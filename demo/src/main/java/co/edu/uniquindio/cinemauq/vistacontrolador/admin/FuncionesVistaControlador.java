package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.AdminControlador;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import co.edu.uniquindio.cinemauq.vista.util.MapaAsientos;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

public class FuncionesVistaControlador {

    @FXML private CheckBox chkProximas;
    @FXML private TableView<Funcion> tablaFunciones;
    @FXML private TableColumn<Funcion, String> colId;
    @FXML private TableColumn<Funcion, String> colFecha;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, String> colSala;
    @FXML private TableColumn<Funcion, String> colPrecio;
    @FXML private TableColumn<Funcion, String> colVendidos;
    @FXML private TableColumn<Funcion, String> colOcupacion;
    @FXML private TableColumn<Funcion, String> colEstado;
    @FXML private VBox panelOcupacion;
    @FXML private ComboBox<Pelicula> cbPelicula;
    @FXML private ComboBox<Sala> cbSala;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtPrecio;
    @FXML private Spinner<Integer> spDias;

    private AdminControlador controlador;

    @FXML
    private void initialize() {
        Componentes.columna(colId, Funcion::getId);
        Componentes.columna(colFecha, f -> Formato.fechaHora(f.getFechaHora()));
        Componentes.columna(colPelicula, f -> f.getPelicula().getTitulo());
        Componentes.columna(colSala, f -> f.getSala().getNombre());
        Componentes.columna(colPrecio, f -> Formato.moneda(f.getPrecioBase()));
        Componentes.columna(colVendidos, f -> f.getAsientosOcupados().size() + " / " + f.getSala().getCapacidad());
        Componentes.columna(colOcupacion, f -> Formato.porcentaje(f.getPorcentajeOcupacion()));
        Componentes.columna(colEstado, f -> f.estaCancelada() ? "Cancelada" : "Programada");
        tablaFunciones.getSelectionModel().selectedItemProperty().addListener((o, a, f) -> mostrarOcupacion(f));
        spDias.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 6));
    }

    public void configurar(AdminControlador controlador) {
        this.controlador = controlador;
        cbPelicula.getItems().setAll(controlador.peliculas().stream().filter(Pelicula::isEnCartelera).toList());
        cbSala.getItems().setAll(controlador.salas());
        dpFecha.setValue(controlador.ahora().toLocalDate().plusDays(1));
        cargar();
    }

    @FXML
    private void cargar() {
        List<Funcion> funciones = controlador.funciones();
        if (chkProximas.isSelected()) {
            funciones = funciones.stream().filter(f -> f.getFechaHora().isAfter(controlador.ahora())).toList();
        }
        tablaFunciones.getItems().setAll(funciones);
    }

    @FXML
    private void programar() {
        if (Dialogos.intentar(() -> controlador.programarFuncion(cbPelicula.getValue(), cbSala.getValue(),
                dpFecha.getValue(), txtHora.getText(), txtPrecio.getText()))) {
            Dialogos.info("Función programada", cbPelicula.getValue() + " · " + dpFecha.getValue() + " " + txtHora.getText());
            cargar();
        }
    }

    private Funcion seleccionada() {
        Funcion f = tablaFunciones.getSelectionModel().getSelectedItem();
        if (f == null) Dialogos.info("Selecciona una función", "Primero selecciona una función de la tabla.");
        return f;
    }

    private void mostrarOcupacion(Funcion f) {
        panelOcupacion.getChildren().clear();
        if (f == null) return;
        MapaAsientos mapa = new MapaAsientos(f.getSala(), f, null);
        mapa.setSoloLectura(true);
        VBox tarjeta = new VBox(10, Componentes.subtitulo("Ocupación de " + f.getId() + " · " + f.getPelicula().getTitulo()), mapa);
        tarjeta.getStyleClass().add("tarjeta");
        panelOcupacion.getChildren().add(tarjeta);
    }

    @FXML
    private void replicar() {
        Funcion f = seleccionada();
        if (f == null) return;
        Dialogos.intentar(() -> {
            List<Funcion> creadas = controlador.replicarFuncion(f, spDias.getValue());
            Dialogos.info("Función replicada (Prototype)", "Se crearon " + creadas.size()
                    + " funciones nuevas a partir de " + f.getId() + ". Los días con la sala ocupada se omitieron.");
            cargar();
        });
    }

    @FXML
    private void cancelarFuncion() {
        Funcion f = seleccionada();
        if (f == null) return;
        if (!Dialogos.confirmar("¿Cancelar la función " + f.getId() + "?",
                f.getPelicula().getTitulo() + " · " + Formato.fechaHora(f.getFechaHora()) + "\n"
                        + "Se devolverá el 100 % a todas las compras de esta función.")) return;
        Dialogos.intentar(() -> {
            int compras = controlador.cancelarFuncion(f);
            Dialogos.info("Función cancelada", "Se reembolsaron " + compras + " compra(s) y se notificó a los clientes.");
            cargar();
        });
    }
}
