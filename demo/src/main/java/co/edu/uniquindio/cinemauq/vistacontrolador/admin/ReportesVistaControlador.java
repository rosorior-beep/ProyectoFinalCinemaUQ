package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.ReportesControlador;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ReportesVistaControlador {

    @FXML private ComboBox<TipoReporte> cbTipo;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private Label lblEncabezado;
    @FXML private TableView<List<String>> tablaReporte;
    @FXML private GridPane gridTotales;

    private ReportesControlador controlador;
    private Reporte reporte;

    @FXML
    private void initialize() {
        cbTipo.getItems().setAll(TipoReporte.values());
        cbTipo.getSelectionModel().selectFirst();
    }

    public void configurar(ReportesControlador controlador) {
        this.controlador = controlador;
        dpDesde.setValue(controlador.hoy().minusDays(29));
        dpHasta.setValue(controlador.hoy());
    }

    @FXML
    private void generar() {
        Dialogos.intentar(() -> mostrar(controlador.generar(cbTipo.getValue(), dpDesde.getValue(), dpHasta.getValue())));
    }

    private void mostrar(Reporte r) {
        this.reporte = r;
        lblEncabezado.setText(r.getTitulo() + "  ·  Generado " + Formato.fechaHora(r.getFechaGeneracion())
                + "  ·  Periodo " + Formato.fecha(r.getDesde()) + " al " + Formato.fecha(r.getHasta())
                + "  ·  " + r.getCantidadRegistros() + " registros");
        tablaReporte.getColumns().clear();
        for (int i = 0; i < r.getEncabezados().size(); i++) {
            final int indice = i;
            TableColumn<List<String>, String> col = new TableColumn<>(r.getEncabezados().get(i));
            col.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get(indice)));
            if (r.esNumerica(i)) {
                col.setCellFactory(c -> new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : item);
                        setAlignment(Pos.CENTER_RIGHT);
                    }
                });
            }
            tablaReporte.getColumns().add(col);
        }
        tablaReporte.getItems().setAll(r.getFilas());
        gridTotales.getChildren().clear();
        int fila = 0;
        for (Map.Entry<String, String> t : r.getTotales().entrySet()) {
            Label valor = new Label(t.getValue());
            valor.getStyleClass().add("total-destacado");
            gridTotales.add(new Label(t.getKey()), 0, fila);
            gridTotales.add(valor, 1, fila++);
        }
    }

    @FXML
    private void exportarPdf() {
        exportar("pdf");
    }

    @FXML
    private void exportarTxt() {
        exportar("txt");
    }

    private void exportar(String extension) {
        if (reporte == null) {
            Dialogos.info("Sin reporte", "Primero genera un reporte.");
            return;
        }
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte");
        selector.setInitialFileName("CinemaUQ_" + reporte.getTitulo().replaceAll("[^A-Za-zÁÉÍÓÚáéíóúñÑ]+", "_")
                + "_" + reporte.getDesde() + "_" + reporte.getHasta() + "." + extension);
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter(extension.toUpperCase(), "*." + extension));
        File archivo = selector.showSaveDialog(tablaReporte.getScene().getWindow());
        if (archivo == null) return;
        Dialogos.intentar(() -> {
            Path ruta = controlador.exportar(reporte, extension, archivo.toPath());
            Dialogos.info("Reporte exportado", "Se guardó en:\n" + ruta.toAbsolutePath());
        });
    }
}
