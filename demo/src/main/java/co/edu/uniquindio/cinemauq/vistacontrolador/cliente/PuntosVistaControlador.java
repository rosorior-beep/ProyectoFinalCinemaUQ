package co.edu.uniquindio.cinemauq.vistacontrolador.cliente;

import co.edu.uniquindio.cinemauq.controlador.ClienteControlador;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.puntos.CuentaPuntos;
import co.edu.uniquindio.cinemauq.modelo.puntos.LotePuntos;
import co.edu.uniquindio.cinemauq.modelo.puntos.MovimientoPuntos;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.Comparator;

public class PuntosVistaControlador {

    @FXML private Label lblRegla;
    @FXML private Label lblVigentes;
    @FXML private Label lblPorVencer;
    @FXML private Label lblVencidos;
    @FXML private TableView<LotePuntos> tablaLotes;
    @FXML private TableColumn<LotePuntos, String> colLoteObtenidos;
    @FXML private TableColumn<LotePuntos, String> colLoteVencen;
    @FXML private TableColumn<LotePuntos, String> colLotePuntos;
    @FXML private TableColumn<LotePuntos, String> colLoteDisponibles;
    @FXML private TableColumn<LotePuntos, String> colLoteEstado;
    @FXML private TableColumn<LotePuntos, String> colLoteOrigen;
    @FXML private TableView<String[]> tablaEquivalencias;
    @FXML private TableColumn<String[], String> colEqBeneficio;
    @FXML private TableColumn<String[], String> colEqPuntos;
    @FXML private TableColumn<String[], String> colEqValor;
    @FXML private TableView<MovimientoPuntos> tablaMovimientos;
    @FXML private TableColumn<MovimientoPuntos, String> colMovFecha;
    @FXML private TableColumn<MovimientoPuntos, String> colMovTipo;
    @FXML private TableColumn<MovimientoPuntos, String> colMovPuntos;
    @FXML private TableColumn<MovimientoPuntos, String> colMovDetalle;

    public void configurar(ClienteControlador controlador) {
        CuentaPuntos cuenta = controlador.cliente().getCuentaPuntos();
        LocalDate hoy = controlador.ahora().toLocalDate();

        lblRegla.setText("Regla de acumulación: " + controlador.reglaPuntos() + ". Los puntos vencen un año después de "
                + "obtenidos y al redimir se usan primero los más próximos a vencer.");
        lblVigentes.setText(Formato.entero(controlador.puntosDisponibles()));
        lblPorVencer.setText(Formato.entero(controlador.puntosPorVencer()));
        lblVencidos.setText(Formato.entero(cuenta.getPuntosVencidos(hoy)));

        Componentes.columna(colLoteObtenidos, l -> Formato.fecha(l.getFechaObtencion()));
        Componentes.columna(colLoteVencen, l -> Formato.fecha(l.getFechaVencimiento()));
        Componentes.columna(colLotePuntos, l -> Formato.entero(l.getCantidadInicial()));
        Componentes.columna(colLoteDisponibles, l -> Formato.entero(l.getDisponibles()));
        Componentes.columna(colLoteEstado, l -> l.getEstado(hoy));
        Componentes.columna(colLoteOrigen, LotePuntos::getOrigen);
        tablaLotes.setRowFactory(t -> new TableRow<>() {
            @Override
            protected void updateItem(LotePuntos l, boolean empty) {
                super.updateItem(l, empty);
                getStyleClass().removeAll("fila-vencida", "fila-por-vencer");
                if (!empty && l != null) {
                    String estado = l.getEstado(hoy);
                    if (estado.startsWith("Vencido")) getStyleClass().add("fila-vencida");
                    else if (estado.equals("Por vencer")) getStyleClass().add("fila-por-vencer");
                }
            }
        });
        tablaLotes.getItems().setAll(cuenta.getLotes().stream()
                .sorted(Comparator.comparing(LotePuntos::getFechaVencimiento)).toList());

        Componentes.columna(colEqBeneficio, f -> f[0]);
        Componentes.columna(colEqPuntos, f -> f[1]);
        Componentes.columna(colEqValor, f -> f[2]);
        tablaEquivalencias.getItems().add(new String[]{"Entrada (cualquier asiento)",
                String.valueOf(controlador.puntosPorEntrada()), "Desde $12.000"});
        for (ComponenteConfiteria c : controlador.confiteria()) {
            tablaEquivalencias.getItems().add(new String[]{c.getNombre(), String.valueOf(c.getPuntosRedencion()),
                    Formato.moneda(c.getPrecio())});
        }

        Componentes.columna(colMovFecha, m -> Formato.fechaHora(m.getFecha()));
        Componentes.columna(colMovTipo, m -> m.getTipo().getEtiqueta());
        Componentes.columna(colMovPuntos, m -> Formato.entero(m.getCantidad()));
        Componentes.columna(colMovDetalle, MovimientoPuntos::getDescripcion);
        tablaMovimientos.getItems().setAll(cuenta.getMovimientos().stream()
                .sorted(Comparator.comparing(MovimientoPuntos::getFecha).reversed()).toList());
    }
}
