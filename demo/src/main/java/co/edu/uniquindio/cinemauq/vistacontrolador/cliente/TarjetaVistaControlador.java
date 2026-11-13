package co.edu.uniquindio.cinemauq.vistacontrolador.cliente;

import co.edu.uniquindio.cinemauq.controlador.ClienteControlador;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.Movimiento;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TipoMovimiento;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Comparator;

public class TarjetaVistaControlador {

    @FXML private Label lblEstado;
    @FXML private Label lblSaldo;
    @FXML private Label lblNumero;
    @FXML private Label lblTitular;
    @FXML private TextField txtMonto;
    @FXML private TableView<SolicitudRecarga> tablaSolicitudes;
    @FXML private TableColumn<SolicitudRecarga, String> colSolFecha;
    @FXML private TableColumn<SolicitudRecarga, String> colSolMonto;
    @FXML private TableColumn<SolicitudRecarga, String> colSolEstado;
    @FXML private TableColumn<SolicitudRecarga, String> colSolObservacion;
    @FXML private TableView<Movimiento> tablaMovimientos;
    @FXML private TableColumn<Movimiento, String> colMovFecha;
    @FXML private TableColumn<Movimiento, String> colMovTipo;
    @FXML private TableColumn<Movimiento, String> colMovDescripcion;
    @FXML private TableColumn<Movimiento, String> colMovValor;
    @FXML private TableColumn<Movimiento, String> colMovSaldo;

    private ClienteControlador controlador;
    private Runnable alCambiar;

    @FXML
    private void initialize() {
        Componentes.columna(colSolFecha, s -> Formato.fechaHora(s.getFechaSolicitud()));
        Componentes.columna(colSolMonto, s -> Formato.moneda(s.getMonto()));
        Componentes.columna(colSolEstado, s -> s.getEstado().toString());
        Componentes.columna(colSolObservacion, s -> s.getObservacion() == null ? "" : s.getObservacion());
        Componentes.columna(colMovFecha, m -> Formato.fechaHora(m.getFecha()));
        Componentes.columna(colMovTipo, m -> m.getTipo().getEtiqueta());
        Componentes.columna(colMovDescripcion, Movimiento::getDescripcion);
        Componentes.columna(colMovValor, m -> (m.getTipo() == TipoMovimiento.PAGO ? "- " : "+ ") + Formato.moneda(m.getMonto()));
        Componentes.columna(colMovSaldo, m -> Formato.moneda(m.getSaldoResultante()));
    }

    public void configurar(ClienteControlador controlador, Runnable alCambiar) {
        this.controlador = controlador;
        this.alCambiar = alCambiar;
        TarjetaVirtual tarjeta = controlador.cliente().getTarjeta();
        lblSaldo.setText(Formato.moneda(tarjeta.getSaldo()));
        lblNumero.setText(tarjeta.getNumeroEnmascarado());
        lblTitular.setText(controlador.cliente().getNombre().toUpperCase());
        lblEstado.setText(tarjeta.estaActiva() ? "ACTIVA" : "BLOQUEADA");
        lblEstado.getStyleClass().add(tarjeta.estaActiva() ? "estado-activo" : "estado-bloqueado");
        tablaSolicitudes.getItems().setAll(controlador.misSolicitudes());
        tablaMovimientos.getItems().setAll(tarjeta.getMovimientos().stream()
                .sorted(Comparator.comparing(Movimiento::getFecha).reversed()).toList());
    }

    @FXML
    private void solicitarRecarga() {
        if (Dialogos.intentar(() -> controlador.solicitarRecarga(txtMonto.getText()))) {
            Dialogos.info("Solicitud enviada", "El administrador revisará tu solicitud de recarga.");
            txtMonto.clear();
            tablaSolicitudes.getItems().setAll(controlador.misSolicitudes());
            alCambiar.run();
        }
    }
}
