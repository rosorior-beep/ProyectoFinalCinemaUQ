package co.edu.uniquindio.cinemauq.vistacontrolador;

import co.edu.uniquindio.cinemauq.controlador.LoginControlador;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroVistaControlador {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmacion;

    private LoginControlador controlador;

    public void configurar(LoginControlador controlador) {
        this.controlador = controlador;
    }

    @FXML
    private void registrar() {
        Dialogos.intentar(() -> controlador.registrar(txtNombre.getText(), txtDocumento.getText(), txtEmail.getText(),
                txtTelefono.getText(), txtPassword.getText(), txtConfirmacion.getText()));
    }

    @FXML
    private void volver() {
        controlador.irALogin();
    }
}
