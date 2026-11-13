package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.AdminControlador;
import co.edu.uniquindio.cinemauq.controlador.ReportesControlador;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import co.edu.uniquindio.cinemauq.vista.util.Vistas;
import co.edu.uniquindio.cinemauq.vistacontrolador.NotificacionesVistaControlador;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.util.function.Consumer;

public class PanelAdminVistaControlador {

    @FXML private Label lblSaludo;
    @FXML private Label lblPendientes;
    @FXML private Button btnTablero;
    @FXML private Button btnPeliculas;
    @FXML private Button btnSalas;
    @FXML private Button btnFunciones;
    @FXML private Button btnConfiteria;
    @FXML private Button btnPromociones;
    @FXML private Button btnClientes;
    @FXML private Button btnCompras;
    @FXML private Button btnReportes;
    @FXML private Button btnHistorial;
    @FXML private Button btnNotificaciones;
    @FXML private ScrollPane contenido;

    private AdminControlador controlador;
    private ReportesControlador reportes;

    public void configurar(AdminControlador controlador, ReportesControlador reportes) {
        this.controlador = controlador;
        this.reportes = reportes;
        lblSaludo.setText("Panel de administración · " + controlador.usuario().getNombre());
        refrescar();
        mostrarTablero();
    }

    private void refrescar() {
        long n = controlador.solicitudesPendientes();
        lblPendientes.setText(n == 0 ? "Sin recargas pendientes" : n + " solicitud(es) de recarga pendiente(s)");
        long sinLeer = controlador.usuario().getNotificacionesSinLeer();
        btnNotificaciones.setText("🔔  Notificaciones" + (sinLeer > 0 ? "  (" + sinLeer + ")" : ""));
    }

    private <C> void abrir(Button boton, String fxml, Consumer<C> configurar) {
        Componentes.activar(boton, btnTablero, btnPeliculas, btnSalas, btnFunciones, btnConfiteria, btnPromociones,
                btnClientes, btnCompras, btnReportes, btnHistorial, btnNotificaciones);
        contenido.setContent(Vistas.cargar("/vista/admin/" + fxml, configurar));
    }

    @FXML
    private void mostrarTablero() {
        this.<TableroVistaControlador>abrir(btnTablero, "tablero.fxml", c -> c.configurar(reportes));
    }

    @FXML
    private void mostrarPeliculas() {
        this.<PeliculasVistaControlador>abrir(btnPeliculas, "peliculas.fxml", c -> c.configurar(controlador));
    }

    @FXML
    private void mostrarSalas() {
        this.<SalasVistaControlador>abrir(btnSalas, "salas.fxml", c -> c.configurar(controlador));
    }

    @FXML
    private void mostrarFunciones() {
        this.<FuncionesVistaControlador>abrir(btnFunciones, "funciones.fxml", c -> c.configurar(controlador));
    }

    @FXML
    private void mostrarConfiteria() {
        this.<ConfiteriaVistaControlador>abrir(btnConfiteria, "confiteria.fxml", c -> c.configurar(controlador));
    }

    @FXML
    private void mostrarPromociones() {
        this.<PromocionesVistaControlador>abrir(btnPromociones, "promociones.fxml", c -> c.configurar(controlador));
    }

    @FXML
    private void mostrarClientes() {
        this.<ClientesVistaControlador>abrir(btnClientes, "clientes.fxml", c -> c.configurar(controlador, this::refrescar));
    }

    @FXML
    private void mostrarCompras() {
        this.<ComprasAdminVistaControlador>abrir(btnCompras, "compras.fxml", c -> c.configurar(controlador));
    }

    @FXML
    private void mostrarReportes() {
        this.<ReportesVistaControlador>abrir(btnReportes, "reportes.fxml", c -> c.configurar(reportes));
    }

    @FXML
    private void mostrarHistorial() {
        this.<HistorialVistaControlador>abrir(btnHistorial, "historial.fxml", c -> c.configurar(controlador));
    }

    @FXML
    private void mostrarNotificaciones() {
        Componentes.activar(btnNotificaciones, btnTablero, btnPeliculas, btnSalas, btnFunciones, btnConfiteria,
                btnPromociones, btnClientes, btnCompras, btnReportes, btnHistorial, btnNotificaciones);
        contenido.setContent(Vistas.<NotificacionesVistaControlador>cargar("/vista/notificaciones.fxml",
                c -> c.configurar(controlador.usuario(), () -> {
                    controlador.marcarNotificacionesLeidas();
                    refrescar();
                })));
    }

    @FXML
    private void restaurarDatos() {
        if (Dialogos.confirmar("¿Restaurar los datos iniciales?",
                "Se borrarán todos los cambios y se volverán a generar los datos de demostración.")) {
            Dialogos.intentar(controlador::restaurarDatosIniciales);
        }
    }

    @FXML
    private void cerrarSesion() {
        controlador.cerrarSesion();
    }
}
