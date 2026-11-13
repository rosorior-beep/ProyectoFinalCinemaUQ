package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.AdminControlador;
import co.edu.uniquindio.cinemauq.modelo.asientos.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.modelo.cine.TipoSala;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import co.edu.uniquindio.cinemauq.vista.util.MapaAsientos;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

public class SalasVistaControlador {

    @FXML private TableView<Sala> tablaSalas;
    @FXML private TableColumn<Sala, String> colId;
    @FXML private TableColumn<Sala, String> colNombre;
    @FXML private TableColumn<Sala, String> colTipo;
    @FXML private TableColumn<Sala, String> colTamano;
    @FXML private TableColumn<Sala, String> colCapacidad;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<TipoSala> cbTipo;
    @FXML private TextField txtFilas;
    @FXML private TextField txtColumnas;
    @FXML private TextField txtPreferenciales;
    @FXML private TextField txtAccesibles;
    @FXML private Label lblEditor;
    @FXML private ToggleGroup grupoHerramienta;
    @FXML private RadioButton rbGeneral;
    @FXML private RadioButton rbPreferencial;
    @FXML private RadioButton rbAccesible;
    @FXML private RadioButton rbHabilitar;
    @FXML private StackPane contenedorMapa;

    private AdminControlador controlador;
    private Sala salaActual;

    @FXML
    private void initialize() {
        Componentes.columna(colId, Sala::getId);
        Componentes.columna(colNombre, Sala::getNombre);
        Componentes.columna(colTipo, s -> s.getTipo().toString());
        Componentes.columna(colTamano, s -> s.getFilas() + " x " + s.getColumnas());
        Componentes.columna(colCapacidad, s -> String.valueOf(s.getCapacidad()));
        cbTipo.getItems().setAll(TipoSala.values());
        cbTipo.setValue(TipoSala.ESTANDAR);
        rbGeneral.setUserData(TipoAsiento.GENERAL);
        rbPreferencial.setUserData(TipoAsiento.PREFERENCIAL);
        rbAccesible.setUserData(TipoAsiento.ACCESIBLE);
        rbHabilitar.setUserData("HABILITAR");
        tablaSalas.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> mostrarEditor(s));
    }

    public void configurar(AdminControlador controlador) {
        this.controlador = controlador;
        tablaSalas.getItems().setAll(controlador.salas());
        if (!tablaSalas.getItems().isEmpty()) tablaSalas.getSelectionModel().selectFirst();
    }

    @FXML
    private void crearSala() {
        if (Dialogos.intentar(() -> controlador.crearSala(txtNombre.getText(), cbTipo.getValue(), txtFilas.getText(),
                txtColumnas.getText(), txtPreferenciales.getText(), txtAccesibles.getText()))) {
            tablaSalas.getItems().setAll(controlador.salas());
            tablaSalas.getSelectionModel().selectLast();
            txtNombre.clear();
        }
    }

    @FXML
    private void deshacer() {
        if (Dialogos.intentar(() -> Dialogos.info("Cambio deshecho", controlador.deshacerUltimaAccion()))) {
            mostrarEditor(salaActual);
            tablaSalas.refresh();
        }
    }

    private void mostrarEditor(Sala sala) {
        contenedorMapa.getChildren().clear();
        salaActual = sala;
        if (sala == null) return;
        lblEditor.setText("Editor de asientos · " + sala.getNombre());
        MapaAsientos[] mapa = new MapaAsientos[1];
        mapa[0] = new MapaAsientos(sala, null, codigo -> {
            Object accion = grupoHerramienta.getSelectedToggle().getUserData();
            boolean ok = Dialogos.intentar(() -> {
                if (accion instanceof TipoAsiento t) controlador.cambiarTipoAsiento(sala, codigo, t);
                else controlador.alternarHabilitado(sala, codigo);
            });
            if (ok) {
                mapa[0].dibujar();
                tablaSalas.refresh();
            }
        });
        contenedorMapa.getChildren().add(mapa[0]);
    }
}
