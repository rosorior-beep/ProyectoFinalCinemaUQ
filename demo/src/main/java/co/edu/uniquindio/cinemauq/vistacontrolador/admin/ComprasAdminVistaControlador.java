package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.AdminControlador;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Cobrable;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

public class ComprasAdminVistaControlador {

    @FXML private TextField txtFiltro;
    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colId;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colCliente;
    @FXML private TableColumn<Compra, String> colPelicula;
    @FXML private TableColumn<Compra, String> colFuncion;
    @FXML private TableColumn<Compra, String> colEntradas;
    @FXML private TableColumn<Compra, Number> colTotal;
    @FXML private TableColumn<Compra, String> colPuntos;
    @FXML private TableColumn<Compra, String> colEstado;
    @FXML private VBox panelDetalle;

    private List<Compra> todas = List.of();

    @FXML
    private void initialize() {
        Componentes.columna(colId, Compra::getId);
        Componentes.columna(colFecha, c -> Formato.fechaHora(c.getFechaCompra()));
        Componentes.columna(colCliente, c -> c.getCliente().getNombre());
        Componentes.columna(colPelicula, c -> c.getFuncion().getPelicula().getTitulo());
        Componentes.columna(colFuncion, c -> Formato.fechaHora(c.getFuncion().getFechaHora()));
        Componentes.columna(colEntradas, c -> String.valueOf(c.getEntradas().size()));
        Componentes.columnaNumero(colTotal, Compra::getTotalAPagar, n -> Formato.moneda(n.doubleValue()));
        Componentes.columna(colPuntos, c -> String.valueOf(c.getPuntosRedimidos()));
        Componentes.columna(colEstado, Compra::getNombreEstado);
        txtFiltro.textProperty().addListener((o, a, t) -> filtrar(t));
        tablaCompras.getSelectionModel().selectedItemProperty().addListener((o, a, c) -> mostrarDetalle(c));
    }

    public void configurar(AdminControlador controlador) {
        todas = controlador.compras();
        tablaCompras.getItems().setAll(todas);
    }

    private void filtrar(String texto) {
        String f = texto == null ? "" : texto.toLowerCase().trim();
        tablaCompras.getItems().setAll(todas.stream().filter(c -> f.isEmpty() || c.getId().toLowerCase().contains(f)
                || c.getCliente().getNombre().toLowerCase().contains(f)
                || c.getFuncion().getPelicula().getTitulo().toLowerCase().contains(f)).toList());
    }

    private void mostrarDetalle(Compra c) {
        panelDetalle.getChildren().setAll(Componentes.subtitulo(c == null ? "Detalle" : "Detalle de " + c.getId()));
        if (c == null) return;
        for (Cobrable item : c.getItems()) {
            panelDetalle.getChildren().add(Componentes.fila(10, new Label("• " + item.getDescripcion()),
                    Componentes.espaciador(), new Label(Formato.moneda(item.getPrecio()))));
        }
        if (c.estaCancelada()) {
            panelDetalle.getChildren().add(Componentes.texto(c.getMotivoCancelacion() + " · reembolso "
                    + Formato.moneda(c.getMontoReembolsado()) + " (" + (int) c.getPorcentajeReembolso() + "%)", "texto-aviso"));
        }
    }
}
