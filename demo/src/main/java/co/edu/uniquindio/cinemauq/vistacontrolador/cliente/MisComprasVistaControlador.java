package co.edu.uniquindio.cinemauq.vistacontrolador.cliente;

import co.edu.uniquindio.cinemauq.controlador.ClienteControlador;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Cobrable;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Entrada;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

public class MisComprasVistaControlador {

    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colId;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colPelicula;
    @FXML private TableColumn<Compra, String> colFuncion;
    @FXML private TableColumn<Compra, String> colAsientos;
    @FXML private TableColumn<Compra, Number> colTotal;
    @FXML private TableColumn<Compra, String> colEstado;
    @FXML private VBox panelDetalle;

    private ClienteControlador controlador;
    private Runnable alCambiarSaldo;

    @FXML
    private void initialize() {
        Componentes.columna(colId, Compra::getId);
        Componentes.columna(colFecha, c -> Formato.fechaHora(c.getFechaCompra()));
        Componentes.columna(colPelicula, c -> c.getFuncion().getPelicula().getTitulo());
        Componentes.columna(colFuncion, c -> Formato.fechaHora(c.getFuncion().getFechaHora()));
        Componentes.columna(colAsientos, c -> c.getEntradas().stream().map(Entrada::getCodigoAsiento)
                .collect(Collectors.joining(", ")));
        Componentes.columnaNumero(colTotal, Compra::getTotalAPagar, n -> Formato.moneda(n.doubleValue()));
        Componentes.columna(colEstado, Compra::getNombreEstado);
        tablaCompras.getSelectionModel().selectedItemProperty().addListener((o, a, c) -> mostrarDetalle(c));
    }

    public void configurar(ClienteControlador controlador, Runnable alCambiarSaldo) {
        this.controlador = controlador;
        this.alCambiarSaldo = alCambiarSaldo;
        cargar();
    }

    private void cargar() {
        tablaCompras.getItems().setAll(controlador.misCompras());
    }

    private void mostrarDetalle(Compra c) {
        panelDetalle.getChildren().clear();
        if (c == null) return;
        panelDetalle.getChildren().add(Componentes.subtitulo("Compra " + c.getId() + " · " + c.getNombreEstado()));
        panelDetalle.getChildren().add(Componentes.texto(c.getFuncion().getPelicula().getTitulo() + " · "
                        + Formato.fechaHora(c.getFuncion().getFechaHora()) + " · " + c.getFuncion().getSala().getNombre(),
                "info-funcion"));
        for (Cobrable item : c.getItems()) {
            panelDetalle.getChildren().add(Componentes.fila(10, new Label("• " + item.getDescripcion()),
                    Componentes.espaciador(), new Label(Formato.moneda(item.getPrecio()))));
        }
        panelDetalle.getChildren().add(Componentes.texto("Total pagado: " + Formato.moneda(c.getTotalAPagar())
                        + "   ·   Puntos redimidos: " + c.getPuntosRedimidos() + "   ·   Puntos ganados: " + c.getPuntosGanados(),
                "descripcion"));
        if (c.estaCancelada()) {
            panelDetalle.getChildren().add(Componentes.texto(c.getMotivoCancelacion() + " el "
                    + Formato.fechaHora(c.getFechaCancelacion()) + ". Reembolso: " + Formato.moneda(c.getMontoReembolsado())
                    + " (" + (int) c.getPorcentajeReembolso() + "%).", "texto-aviso"));
            return;
        }
        double porcentaje = controlador.porcentajeReembolso(c);
        if (porcentaje == 0) {
            panelDetalle.getChildren().add(Componentes.texto(
                    "Esta compra ya no se puede cancelar (faltan menos de 2 horas o la función ya pasó).", "texto-aviso"));
            return;
        }
        double monto = Math.round(c.getTotalAPagar() * porcentaje / 100);
        Button cancelar = new Button("Cancelar compra (reembolso " + (int) porcentaje + "% = " + Formato.moneda(monto) + ")");
        cancelar.getStyleClass().add("btn-peligro");
        cancelar.setOnAction(e -> cancelar(c, porcentaje, monto));
        panelDetalle.getChildren().add(cancelar);
    }

    private void cancelar(Compra c, double porcentaje, double monto) {
        String mensaje = "Se devolverán " + Formato.moneda(monto) + " (" + (int) porcentaje + "%) a tu tarjeta."
                + (c.getPuntosRedimidos() > 0 ? "\nLos " + c.getPuntosRedimidos() + " puntos redimidos regresan a tu cuenta." : "")
                + (c.getPuntosGanados() > 0 ? "\nSe retirarán los " + c.getPuntosGanados() + " puntos ganados con esta compra." : "");
        if (!Dialogos.confirmar("¿Cancelar la compra " + c.getId() + "?", mensaje)) return;
        if (Dialogos.intentar(() -> controlador.cancelar(c))) {
            alCambiarSaldo.run();
            Dialogos.info("Compra cancelada", "Se reembolsaron " + Formato.moneda(c.getMontoReembolsado()) + " a tu tarjeta.");
            cargar();
            tablaCompras.getSelectionModel().select(c);
        }
    }
}
