package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.AdminControlador;
import co.edu.uniquindio.cinemauq.modelo.promociones.AplicacionPromocion;
import co.edu.uniquindio.cinemauq.modelo.promociones.Promocion;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class PromocionesVistaControlador {

    private static final Locale ES = Locale.forLanguageTag("es-CO");

    @FXML private TableView<Promocion> tablaPromociones;
    @FXML private TableColumn<Promocion, String> colId;
    @FXML private TableColumn<Promocion, String> colNombre;
    @FXML private TableColumn<Promocion, String> colAplica;
    @FXML private TableColumn<Promocion, String> colDescuento;
    @FXML private TableColumn<Promocion, String> colDia;
    @FXML private TableColumn<Promocion, String> colVigencia;
    @FXML private TableColumn<Promocion, String> colActiva;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<AplicacionPromocion> cbAplica;
    @FXML private TextField txtPorcentaje;
    @FXML private ComboBox<String> cbDia;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;

    private AdminControlador controlador;

    @FXML
    private void initialize() {
        Componentes.columna(colId, Promocion::getId);
        Componentes.columna(colNombre, Promocion::getNombre);
        Componentes.columna(colAplica, p -> p.getAplicaA().toString());
        Componentes.columna(colDescuento, p -> (int) p.getPorcentaje() + "%");
        Componentes.columna(colDia, p -> p.getDiaSemana() == null ? "Todos" : dia(p.getDiaSemana()));
        Componentes.columna(colVigencia, p -> Formato.fecha(p.getFechaInicio()) + " - " + Formato.fecha(p.getFechaFin()));
        Componentes.columna(colActiva, p -> p.isActiva() ? "Sí" : "No");
        cbAplica.getItems().setAll(AplicacionPromocion.values());
        cbDia.getItems().add("Todos los días");
        for (DayOfWeek d : DayOfWeek.values()) cbDia.getItems().add(dia(d));
        cbDia.getSelectionModel().selectFirst();
    }

    public void configurar(AdminControlador controlador) {
        this.controlador = controlador;
        dpInicio.setValue(controlador.ahora().toLocalDate());
        dpFin.setValue(controlador.ahora().toLocalDate().plusMonths(1));
        cargar();
    }

    private void cargar() {
        tablaPromociones.getItems().setAll(controlador.promociones());
    }

    @FXML
    private void crearPromocion() {
        int indice = cbDia.getSelectionModel().getSelectedIndex();
        DayOfWeek d = indice <= 0 ? null : DayOfWeek.of(indice);
        if (Dialogos.intentar(() -> controlador.crearPromocion(txtNombre.getText(), cbAplica.getValue(),
                txtPorcentaje.getText(), d, dpInicio.getValue(), dpFin.getValue()))) {
            cargar();
            txtNombre.clear();
            txtPorcentaje.clear();
        }
    }

    @FXML
    private void alternarPromocion() {
        Promocion p = tablaPromociones.getSelectionModel().getSelectedItem();
        if (p == null) {
            Dialogos.info("Selecciona una promoción", "Primero selecciona una promoción de la tabla.");
            return;
        }
        if (Dialogos.intentar(() -> controlador.alternarPromocion(p))) cargar();
    }

    private static String dia(DayOfWeek d) {
        String n = d.getDisplayName(TextStyle.FULL, ES);
        return n.substring(0, 1).toUpperCase() + n.substring(1);
    }
}
