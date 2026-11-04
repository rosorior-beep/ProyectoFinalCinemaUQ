package co.edu.uniquindio.cinemauq.vista;

import co.edu.uniquindio.cinemauq.controlador.AdminControlador;
import co.edu.uniquindio.cinemauq.controlador.ClienteControlador;
import co.edu.uniquindio.cinemauq.controlador.LoginControlador;
import co.edu.uniquindio.cinemauq.controlador.ReportesControlador;
import co.edu.uniquindio.cinemauq.fachada.CinemaFacade;
import co.edu.uniquindio.cinemauq.repositorio.RepositorioArchivo;
import co.edu.uniquindio.cinemauq.vista.util.Vistas;
import co.edu.uniquindio.cinemauq.vistacontrolador.LoginVistaControlador;
import co.edu.uniquindio.cinemauq.vistacontrolador.RegistroVistaControlador;
import co.edu.uniquindio.cinemauq.vistacontrolador.admin.PanelAdminVistaControlador;
import co.edu.uniquindio.cinemauq.vistacontrolador.cliente.PanelClienteVistaControlador;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;


public class Navegador {

    private final Stage stage;
    private final CinemaFacade fachada;
    private final RepositorioArchivo repositorio;
    private Scene escena;

    public Navegador(Stage stage, CinemaFacade fachada, RepositorioArchivo repositorio) {
        this.stage = stage;
        this.fachada = fachada;
        this.repositorio = repositorio;
    }

    public void mostrarLogin() {
        mostrar(Vistas.<LoginVistaControlador>cargar("/vista/login.fxml",
                c -> c.configurar(new LoginControlador(fachada, this))), 1100, 700);
    }

    public void mostrarRegistro() {
        mostrar(Vistas.<RegistroVistaControlador>cargar("/vista/registro.fxml",
                c -> c.configurar(new LoginControlador(fachada, this))), 1100, 700);
    }

    public void mostrarPanelCliente() {
        mostrar(Vistas.<PanelClienteVistaControlador>cargar("/vista/cliente/panel_cliente.fxml",
                c -> c.configurar(new ClienteControlador(fachada, this))), 1280, 800);
    }

    public void mostrarPanelAdmin() {
        mostrar(Vistas.<PanelAdminVistaControlador>cargar("/vista/admin/panel_admin.fxml",
                        c -> c.configurar(new AdminControlador(fachada, this, repositorio), new ReportesControlador(fachada))),
                1360, 820);
    }

    private void mostrar(Parent raiz, double ancho, double alto) {
        if (escena == null) {
            escena = new Scene(raiz, ancho, alto);
            escena.getStylesheets().add(Objects.requireNonNull(
                    getClass().getResource("/css/estilos.css")).toExternalForm());
            stage.setScene(escena);
        } else {
            escena.setRoot(raiz);
            if (stage.getWidth() < ancho && !stage.isMaximized()) {
                stage.setWidth(ancho);
                stage.setHeight(alto);
                stage.centerOnScreen();
            }
        }
    }

    public Stage getStage() {
        return stage;
    }
}
