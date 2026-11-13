package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.AdminControlador;
import co.edu.uniquindio.cinemauq.modelo.cine.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.cine.Genero;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PeliculasVistaControlador {

    @FXML private TableView<Pelicula> tablaPeliculas;
    @FXML private TableColumn<Pelicula, String> colId;
    @FXML private TableColumn<Pelicula, String> colTitulo;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, String> colDuracion;
    @FXML private TableColumn<Pelicula, String> colClasificacion;
    @FXML private TableColumn<Pelicula, String> colCartelera;
    @FXML private TextField txtTitulo;
    @FXML private ComboBox<Genero> cbGenero;
    @FXML private TextField txtDuracion;
    @FXML private ComboBox<Clasificacion> cbClasificacion;
    @FXML private TextArea txtSinopsis;
    @FXML private CheckBox chkCartelera;

    private AdminControlador controlador;
    private Pelicula seleccionada;

    @FXML
    private void initialize() {
        Componentes.columna(colId, Pelicula::getId);
        Componentes.columna(colTitulo, Pelicula::getTitulo);
        Componentes.columna(colGenero, p -> p.getGenero().toString());
        Componentes.columna(colDuracion, p -> p.getDuracionMinutos() + " min");
        Componentes.columna(colClasificacion, p -> p.getClasificacion().toString());
        Componentes.columna(colCartelera, p -> p.isEnCartelera() ? "Sí" : "No");
        cbGenero.getItems().setAll(Genero.values());
        cbClasificacion.getItems().setAll(Clasificacion.values());
        tablaPeliculas.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> llenar(p));
    }

    public void configurar(AdminControlador controlador) {
        this.controlador = controlador;
        cargar();
    }

    private void cargar() {
        tablaPeliculas.getItems().setAll(controlador.peliculas());
    }

    private void llenar(Pelicula p) {
        seleccionada = p;
        txtTitulo.setText(p == null ? "" : p.getTitulo());
        cbGenero.setValue(p == null ? null : p.getGenero());
        txtDuracion.setText(p == null ? "" : String.valueOf(p.getDuracionMinutos()));
        cbClasificacion.setValue(p == null ? null : p.getClasificacion());
        txtSinopsis.setText(p == null ? "" : p.getSinopsis());
        chkCartelera.setSelected(p == null || p.isEnCartelera());
    }

    @FXML
    private void guardar() {
        if (Dialogos.intentar(() -> controlador.guardarPelicula(seleccionada, txtTitulo.getText(), cbGenero.getValue(),
                txtDuracion.getText(), cbClasificacion.getValue(), txtSinopsis.getText(), chkCartelera.isSelected()))) {
            Dialogos.info("Película guardada", txtTitulo.getText());
            cargar();
            nueva();
        }
    }

    @FXML
    private void alternarCartelera() {
        Pelicula p = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (p == null) {
            Dialogos.info("Selecciona una película", "Primero selecciona una película de la tabla.");
            return;
        }
        if (Dialogos.intentar(() -> controlador.alternarCartelera(p))) {
            tablaPeliculas.refresh();
            llenar(p);
        }
    }

    @FXML
    private void nueva() {
        tablaPeliculas.getSelectionModel().clearSelection();
        llenar(null);
    }
}
