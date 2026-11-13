package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.AdminControlador;
import co.edu.uniquindio.cinemauq.modelo.confiteria.CategoriaProducto;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class ConfiteriaVistaControlador {

    @FXML private TableView<ComponenteConfiteria> tablaProductos;
    @FXML private TableColumn<ComponenteConfiteria, String> colId;
    @FXML private TableColumn<ComponenteConfiteria, String> colNombre;
    @FXML private TableColumn<ComponenteConfiteria, String> colCategoria;
    @FXML private TableColumn<ComponenteConfiteria, String> colContenido;
    @FXML private TableColumn<ComponenteConfiteria, String> colPrecio;
    @FXML private TableColumn<ComponenteConfiteria, String> colPuntos;
    @FXML private TableColumn<ComponenteConfiteria, String> colDisponible;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<CategoriaProducto> cbCategoria;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtPuntos;
    @FXML private TextField txtNombreCombo;
    @FXML private TextField txtDescuento;
    @FXML private TextField txtPuntosCombo;
    @FXML private ComboBox<ComponenteConfiteria> cbComponente;
    @FXML private ListView<ComponenteConfiteria> listaComponentes;
    @FXML private TextField txtEditNombre;
    @FXML private TextField txtEditPrecio;
    @FXML private TextField txtEditPuntos;
    @FXML private CheckBox chkEditDisponible;

    private AdminControlador controlador;

    @FXML
    private void initialize() {
        Componentes.columna(colId, ComponenteConfiteria::getId);
        Componentes.columna(colNombre, ComponenteConfiteria::getNombre);
        Componentes.columna(colCategoria, c -> c.getCategoria().toString());
        Componentes.columna(colContenido, ComponenteConfiteria::getDetalle);
        Componentes.columna(colPrecio, c -> Formato.moneda(c.getPrecio()));
        Componentes.columna(colPuntos, c -> String.valueOf(c.getPuntosRedencion()));
        Componentes.columna(colDisponible, c -> c.isDisponible() ? "Sí" : "No");
        cbCategoria.getItems().setAll(CategoriaProducto.values());
        cbCategoria.getItems().remove(CategoriaProducto.COMBO);
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((o, a, c) -> {
            if (c == null) return;
            txtEditNombre.setText(c.getNombre());
            txtEditPrecio.setText(String.valueOf((long) c.getPrecio()));
            txtEditPrecio.setDisable(c.esCombo());
            txtEditPuntos.setText(String.valueOf(c.getPuntosRedencion()));
            chkEditDisponible.setSelected(c.isDisponible());
        });
    }

    public void configurar(AdminControlador controlador) {
        this.controlador = controlador;
        cargar();
    }

    private void cargar() {
        tablaProductos.getItems().setAll(controlador.confiteria());
        cbComponente.getItems().setAll(controlador.confiteria());
    }

    @FXML
    private void crearProducto() {
        if (Dialogos.intentar(() -> controlador.crearProducto(txtNombre.getText(), cbCategoria.getValue(),
                txtPrecio.getText(), txtPuntos.getText()))) {
            txtNombre.clear();
            txtPrecio.clear();
            txtPuntos.clear();
            cargar();
        }
    }

    @FXML
    private void agregarComponente() {
        if (cbComponente.getValue() != null) listaComponentes.getItems().add(cbComponente.getValue());
    }

    @FXML
    private void quitarComponente() {
        int i = listaComponentes.getSelectionModel().getSelectedIndex();
        if (i >= 0) listaComponentes.getItems().remove(i);
    }

    @FXML
    private void crearCombo() {
        if (Dialogos.intentar(() -> controlador.crearCombo(txtNombreCombo.getText(),
                new ArrayList<>(listaComponentes.getItems()), txtDescuento.getText(), txtPuntosCombo.getText()))) {
            txtNombreCombo.clear();
            txtPuntosCombo.clear();
            listaComponentes.getItems().clear();
            cargar();
        }
    }

    @FXML
    private void guardarCambios() {
        ComponenteConfiteria c = tablaProductos.getSelectionModel().getSelectedItem();
        if (c == null) {
            Dialogos.info("Selecciona un producto", "Primero selecciona un producto o combo de la tabla.");
            return;
        }
        if (Dialogos.intentar(() -> controlador.actualizarProducto(c, txtEditNombre.getText(), txtEditPrecio.getText(),
                txtEditPuntos.getText(), chkEditDisponible.isSelected()))) {
            cargar();
        }
    }
}
