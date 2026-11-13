package co.edu.uniquindio.cinemauq.vistacontrolador.cliente;

import co.edu.uniquindio.cinemauq.controlador.ClienteControlador;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Vistas;
import co.edu.uniquindio.cinemauq.vistacontrolador.NotificacionesVistaControlador;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class PanelClienteVistaControlador {

    @FXML private Label lblSaludo;
    @FXML private Label lblTarjeta;
    @FXML private Label lblSaldo;
    @FXML private Label lblPuntos;
    @FXML private Button btnCartelera;
    @FXML private Button btnCompras;
    @FXML private Button btnTarjeta;
    @FXML private Button btnPuntos;
    @FXML private Button btnNotificaciones;
    @FXML private ScrollPane contenido;

    private ClienteControlador controlador;

    public void configurar(ClienteControlador controlador) {
        this.controlador = controlador;
        lblSaludo.setText("Hola, " + controlador.cliente().getNombre().split(" ")[0]);
        refrescarEncabezado();
        mostrarCartelera();
    }

    public void refrescarEncabezado() {
        Cliente c = controlador.cliente();
        lblSaldo.setText("Saldo  " + Formato.moneda(c.getTarjeta().getSaldo()));
        lblPuntos.setText("⭐ " + Formato.entero(controlador.puntosDisponibles()) + " pts");
        lblTarjeta.setText(c.getTarjeta().estaActiva() ? "Tarjeta activa" : "Tarjeta bloqueada");
        long sinLeer = c.getNotificacionesSinLeer();
        btnNotificaciones.setText("🔔  Notificaciones" + (sinLeer > 0 ? "  (" + sinLeer + ")" : ""));
    }

    private void activar(Button b) {
        Componentes.activar(b, btnCartelera, btnCompras, btnTarjeta, btnPuntos, btnNotificaciones);
    }

    @FXML
    private void mostrarCartelera() {
        activar(btnCartelera);
        contenido.setContent(Vistas.<CarteleraVistaControlador>cargar("/vista/cliente/cartelera.fxml",
                c -> c.configurar(controlador, this::abrirCompra)));
    }

    private void abrirCompra(Funcion funcion) {
        contenido.setContent(Vistas.<CompraVistaControlador>cargar("/vista/cliente/compra.fxml",
                c -> c.configurar(controlador, funcion, () -> {
                    refrescarEncabezado();
                    mostrarCartelera();
                }, this::refrescarEncabezado)));
    }

    @FXML
    private void mostrarCompras() {
        activar(btnCompras);
        contenido.setContent(Vistas.<MisComprasVistaControlador>cargar("/vista/cliente/mis_compras.fxml",
                c -> c.configurar(controlador, this::refrescarEncabezado)));
    }

    @FXML
    private void mostrarTarjeta() {
        activar(btnTarjeta);
        contenido.setContent(Vistas.<TarjetaVistaControlador>cargar("/vista/cliente/tarjeta.fxml",
                c -> c.configurar(controlador, this::refrescarEncabezado)));
    }

    @FXML
    private void mostrarPuntos() {
        activar(btnPuntos);
        contenido.setContent(Vistas.<PuntosVistaControlador>cargar("/vista/cliente/puntos.fxml",
                c -> c.configurar(controlador)));
    }

    @FXML
    private void mostrarNotificaciones() {
        activar(btnNotificaciones);
        contenido.setContent(Vistas.<NotificacionesVistaControlador>cargar("/vista/notificaciones.fxml",
                c -> c.configurar(controlador.cliente(), () -> {
                    controlador.marcarNotificacionesLeidas();
                    refrescarEncabezado();
                })));
    }

    @FXML
    private void cerrarSesion() {
        controlador.cerrarSesion();
    }
}
