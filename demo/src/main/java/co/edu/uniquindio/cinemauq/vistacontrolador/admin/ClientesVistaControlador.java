package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.AdminControlador;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.EstadoSolicitud;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ClientesVistaControlador {

    @FXML private TextField txtFiltro;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDocumento;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colTarjeta;
    @FXML private TableColumn<Cliente, Number> colSaldo;
    @FXML private TableColumn<Cliente, String> colEstado;
    @FXML private TableColumn<Cliente, Number> colPuntos;
    @FXML private TextField txtMonto;
    @FXML private CheckBox chkPendientes;
    @FXML private TableView<SolicitudRecarga> tablaSolicitudes;
    @FXML private TableColumn<SolicitudRecarga, String> colSolId;
    @FXML private TableColumn<SolicitudRecarga, String> colSolFecha;
    @FXML private TableColumn<SolicitudRecarga, String> colSolCliente;
    @FXML private TableColumn<SolicitudRecarga, String> colSolMonto;
    @FXML private TableColumn<SolicitudRecarga, String> colSolEstado;
    @FXML private TableColumn<SolicitudRecarga, String> colSolRespuesta;

    private AdminControlador controlador;
    private Runnable alCambiar;

    @FXML
    private void initialize() {
        Componentes.columna(colId, Cliente::getId);
        Componentes.columna(colNombre, Cliente::getNombre);
        Componentes.columna(colDocumento, Cliente::getDocumento);
        Componentes.columna(colCorreo, Cliente::getEmail);
        Componentes.columna(colTarjeta, c -> c.getTarjeta().getNumeroEnmascarado());
        Componentes.columnaNumero(colSaldo, c -> c.getTarjeta().getSaldo(), n -> Formato.moneda(n.doubleValue()));
        Componentes.columna(colEstado, c -> c.getTarjeta().getEstado().toString());
        Componentes.columna(colSolId, SolicitudRecarga::getId);
        Componentes.columna(colSolFecha, s -> Formato.fechaHora(s.getFechaSolicitud()));
        Componentes.columna(colSolCliente, s -> s.getCliente().getNombre());
        Componentes.columna(colSolMonto, s -> Formato.moneda(s.getMonto()));
        Componentes.columna(colSolEstado, s -> s.getEstado().toString());
        Componentes.columna(colSolRespuesta, s -> s.getObservacion() == null ? "" : s.getObservacion());
        txtFiltro.textProperty().addListener((o, a, t) -> cargarClientes());
    }

    public void configurar(AdminControlador controlador, Runnable alCambiar) {
        this.controlador = controlador;
        this.alCambiar = alCambiar;
        Componentes.columnaNumero(colPuntos,
                c -> c.getCuentaPuntos().getPuntosDisponibles(controlador.ahora().toLocalDate()),
                n -> Formato.entero(n.longValue()));
        cargarClientes();
        cargarSolicitudes();
    }

    private void cargarClientes() {
        String f = txtFiltro.getText() == null ? "" : txtFiltro.getText().toLowerCase().trim();
        tablaClientes.getItems().setAll(controlador.clientes().stream()
                .filter(c -> f.isEmpty() || c.getNombre().toLowerCase().contains(f) || c.getDocumento().contains(f)
                        || c.getEmail().toLowerCase().contains(f)).toList());
    }

    @FXML
    private void cargarSolicitudes() {
        tablaSolicitudes.getItems().setAll(controlador.solicitudes().stream()
                .filter(s -> !chkPendientes.isSelected() || s.getEstado() == EstadoSolicitud.PENDIENTE).toList());
    }

    private Cliente clienteSeleccionado() {
        Cliente c = tablaClientes.getSelectionModel().getSelectedItem();
        if (c == null) Dialogos.info("Selecciona un cliente", "Primero selecciona un cliente de la tabla.");
        return c;
    }

    @FXML
    private void recargar() {
        Cliente c = clienteSeleccionado();
        if (c == null) return;
        if (Dialogos.intentar(() -> controlador.recargar(c, txtMonto.getText()))) {
            Dialogos.info("Recarga realizada", "Nuevo saldo de " + c.getNombre() + ": " + Formato.moneda(c.getTarjeta().getSaldo()));
            txtMonto.clear();
            tablaClientes.refresh();
        }
    }

    @FXML
    private void alternarTarjeta() {
        Cliente c = clienteSeleccionado();
        if (c != null && Dialogos.intentar(() -> controlador.alternarTarjeta(c))) tablaClientes.refresh();
    }

    @FXML
    private void aprobar() {
        responder(true);
    }

    @FXML
    private void rechazar() {
        responder(false);
    }

    private void responder(boolean aprobar) {
        SolicitudRecarga s = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (s == null) {
            Dialogos.info("Selecciona una solicitud", "Primero selecciona una solicitud de la tabla.");
            return;
        }
        boolean ok;
        if (aprobar) {
            ok = Dialogos.intentar(() -> controlador.aprobar(s));
        } else {
            String motivo = Dialogos.pedirTexto("Rechazar solicitud " + s.getId(), "Motivo:", "").orElse(null);
            if (motivo == null) return;
            ok = Dialogos.intentar(() -> controlador.rechazar(s, motivo));
        }
        if (ok) {
            cargarSolicitudes();
            tablaClientes.refresh();
            alCambiar.run();
        }
    }
}
