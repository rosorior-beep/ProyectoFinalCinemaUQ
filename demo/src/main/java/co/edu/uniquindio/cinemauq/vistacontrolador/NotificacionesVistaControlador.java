package co.edu.uniquindio.cinemauq.vistacontrolador;

import co.edu.uniquindio.cinemauq.modelo.usuarios.Notificacion;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class NotificacionesVistaControlador {

    @FXML private ListView<Notificacion> listaNotificaciones;

    public void configurar(Usuario usuario, Runnable marcarLeidas) {
        listaNotificaciones.setCellFactory(l -> new ListCell<>() {
            @Override
            protected void updateItem(Notificacion n, boolean empty) {
                super.updateItem(n, empty);
                getStyleClass().remove("no-leida");
                if (empty || n == null) {
                    setText(null);
                    return;
                }
                setText(Formato.fechaHora(n.getFecha()) + "   " + n.getMensaje());
                setWrapText(true);
                setPrefWidth(0);
                if (!n.isLeida()) getStyleClass().add("no-leida");
            }
        });
        listaNotificaciones.getItems().setAll(usuario.getNotificaciones());
        marcarLeidas.run();
    }
}
