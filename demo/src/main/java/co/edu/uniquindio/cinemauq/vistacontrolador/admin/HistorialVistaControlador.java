package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.AdminControlador;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class HistorialVistaControlador {

    @FXML private Button btnDeshacer;
    @FXML private Label lblMensaje;
    @FXML private ListView<String> listaHistorial;

    private AdminControlador controlador;

    public void configurar(AdminControlador controlador) {
        this.controlador = controlador;
        cargar();
    }

    private void cargar() {
        listaHistorial.getItems().setAll(controlador.historialAcciones());
        btnDeshacer.setDisable(!controlador.puedeDeshacer());
    }

    @FXML
    private void deshacer() {
        Dialogos.intentar(() -> {
            String accion = controlador.deshacerUltimaAccion();
            lblMensaje.setText("Se deshizo: " + accion);
            cargar();
        });
    }
}
