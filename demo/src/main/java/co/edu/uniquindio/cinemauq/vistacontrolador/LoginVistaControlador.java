package co.edu.uniquindio.cinemauq.vistacontrolador;

import co.edu.uniquindio.cinemauq.controlador.LoginControlador;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginVistaControlador {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;

    private LoginControlador controlador;

    public void configurar(LoginControlador controlador) {
        this.controlador = controlador;
    }

    @FXML
    private void iniciarSesion() {
        Dialogos.intentar(() -> controlador.iniciarSesion(txtEmail.getText(), txtPassword.getText()));
    }

    @FXML
    private void irARegistro() {
        controlador.irARegistro();
    }
}
