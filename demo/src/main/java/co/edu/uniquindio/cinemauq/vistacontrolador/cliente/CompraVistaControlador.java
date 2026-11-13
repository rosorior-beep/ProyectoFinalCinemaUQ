package co.edu.uniquindio.cinemauq.vistacontrolador.cliente;

import co.edu.uniquindio.cinemauq.controlador.ClienteControlador;
import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Cobrable;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Entrada;
import co.edu.uniquindio.cinemauq.modelo.compra.items.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.vista.util.Componentes;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import co.edu.uniquindio.cinemauq.vista.util.MapaAsientos;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompraVistaControlador {

    @FXML private Label lblFuncion;
    @FXML private StackPane contenedorMapa;
    @FXML private Label lblPrecios;
    @FXML private GridPane gridConfiteria;
    @FXML private VBox panelResumen;

    private ClienteControlador controlador;
    private Funcion funcion;
    private Runnable alTerminar;
    private Runnable alCambiarSaldo;
    private MapaAsientos mapa;
    private final Map<String, Integer> cantidades = new LinkedHashMap<>();
    private final Set<String> asientosConPuntos = new HashSet<>();
    private final Set<String> productosConPuntos = new HashSet<>();

    public void configurar(ClienteControlador controlador, Funcion funcion, Runnable alTerminar, Runnable alCambiarSaldo) {
        this.controlador = controlador;
        this.funcion = funcion;
        this.alTerminar = alTerminar;
        this.alCambiarSaldo = alCambiarSaldo;

        lblFuncion.setText(funcion.getPelicula().getTitulo() + "  ·  " + Formato.fechaHora(funcion.getFechaHora())
                + "  ·  " + funcion.getSala());
        double recargoSala = funcion.getSala().getTipo().getRecargo();
        lblPrecios.setText("Precio base " + Formato.moneda(funcion.getPrecioBase())
                + (recargoSala > 0 ? " + sala " + funcion.getSala().getTipo() + " " + Formato.moneda(recargoSala) : "")
                + ". Preferencial +$6.000. Máximo 10 entradas.");

        mapa = new MapaAsientos(funcion.getSala(), funcion, codigo -> {
            asientosConPuntos.retainAll(mapa.getSeleccionados());
            recalcular();
        });
        mapa.setMaximo(10);
        contenedorMapa.getChildren().setAll(mapa);
        llenarConfiteria();
        recalcular();
    }

    @FXML
    private void volver() {
        alTerminar.run();
    }

    private void llenarConfiteria() {
        int fila = 0;
        for (ComponenteConfiteria c : controlador.confiteria()) {
            Label nombre = new Label(c.getNombre());
            nombre.getStyleClass().add("nombre-producto");
            Label detalle = Componentes.texto(c.esCombo() ? c.getDetalle() : c.getCategoria().toString(), "detalle-producto");
            VBox texto = new VBox(1, nombre, detalle);
            texto.setMaxWidth(200);
            Label precio = new Label(Formato.moneda(c.getPrecio()));
            precio.getStyleClass().add("precio");
            Spinner<Integer> cantidad = new Spinner<>(0, 10, 0);
            cantidad.setPrefWidth(72);
            cantidad.valueProperty().addListener((obs, antes, ahora) -> {
                if (ahora == 0) {
                    cantidades.remove(c.getId());
                    productosConPuntos.remove(c.getId());
                } else {
                    cantidades.put(c.getId(), ahora);
                }
                recalcular();
            });
            gridConfiteria.add(texto, 0, fila);
            gridConfiteria.add(precio, 1, fila);
            gridConfiteria.add(cantidad, 2, fila);
            GridPane.setHgrow(texto, Priority.ALWAYS);
            fila++;
        }
    }

    private void recalcular() {
        panelResumen.getChildren().clear();
        List<String> asientos = new ArrayList<>(mapa.getSeleccionados());
        if (asientos.isEmpty() && cantidades.isEmpty()) {
            panelResumen.getChildren().add(Componentes.texto("Selecciona al menos un asiento o un producto.", "descripcion"));
            return;
        }
        Compra cotizacion;
        try {
            cotizacion = controlador.cotizar(funcion, asientos, cantidades, asientosConPuntos, productosConPuntos);
        } catch (CinemaUQException e) {
            panelResumen.getChildren().add(Componentes.texto(e.getMessage(), "texto-error"));
            return;
        }

        int disponibles = controlador.puntosDisponibles();
        for (Cobrable item : cotizacion.getItems()) {
            panelResumen.getChildren().add(filaItem(item, disponibles));
        }
        panelResumen.getChildren().add(new Separator());

        GridPane totales = new GridPane();
        totales.setHgap(10);
        totales.setVgap(4);
        int f = 0;
        f = total(totales, f, "Subtotal", Formato.moneda(cotizacion.getSubtotal()), false);
        if (cotizacion.getDescuentos() > 0) {
            f = total(totales, f, "Promociones", "- " + Formato.moneda(cotizacion.getDescuentos()), false);
        }
        if (cotizacion.getPuntosRedimidos() > 0) {
            f = total(totales, f, "Pagado con " + Formato.entero(cotizacion.getPuntosRedimidos()) + " pts",
                    "- " + Formato.moneda(cotizacion.getValorRedimidoConPuntos()), false);
        }
        f = total(totales, f, "Total a pagar", Formato.moneda(cotizacion.getTotalAPagar()), true);
        total(totales, f, "Puntos que ganarás", "+" + controlador.puntosAGanar(cotizacion) + " pts", false);
        panelResumen.getChildren().add(totales);

        double saldo = controlador.cliente().getTarjeta().getSaldo();
        panelResumen.getChildren().add(Componentes.texto("Saldo de tu tarjeta: " + Formato.moneda(saldo)
                + "   ·   Puntos vigentes: " + Formato.entero(disponibles), "descripcion"));
        if (cotizacion.getPuntosRedimidos() > disponibles) {
            panelResumen.getChildren().add(Componentes.texto(
                    "No tienes puntos suficientes para todas las redenciones marcadas.", "texto-error"));
        }
        if (cotizacion.getTotalAPagar() > saldo) {
            panelResumen.getChildren().add(Componentes.texto(
                    "Saldo insuficiente: solicita una recarga en \"Mi tarjeta\".", "texto-error"));
        }
        Button confirmar = new Button("Confirmar y pagar " + Formato.moneda(cotizacion.getTotalAPagar()));
        confirmar.getStyleClass().add("btn-primario");
        confirmar.setMaxWidth(Double.MAX_VALUE);
        confirmar.setOnAction(e -> confirmar(asientos));
        panelResumen.getChildren().add(confirmar);
    }

    private HBox filaItem(Cobrable item, int disponibles) {
        Label descripcion = Componentes.texto(item.getDescripcion(), "item-resumen");
        descripcion.setMaxWidth(230);
        String precioTexto = item.getPrecio() < item.getPrecioBase()
                ? Formato.moneda(item.getPrecio()) + "  (antes " + Formato.moneda(item.getPrecioBase()) + ")"
                : Formato.moneda(item.getPrecio());
        Label precio = new Label(precioTexto);
        precio.getStyleClass().add("precio");
        VBox izquierda = new VBox(2, descripcion, precio);

        String clave = claveDe(item);
        boolean esEntrada = item.getItemBase() instanceof Entrada;
        CheckBox puntos = new CheckBox("Usar " + item.getPuntosRedencion() + " pts");
        puntos.getStyleClass().add("check-puntos");
        puntos.setSelected(esEntrada ? asientosConPuntos.contains(clave) : productosConPuntos.contains(clave));
        puntos.setDisable(!puntos.isSelected() && item.getPuntosRedencion() > disponibles);
        puntos.selectedProperty().addListener((o, a, marcado) -> {
            Set<String> conjunto = esEntrada ? asientosConPuntos : productosConPuntos;
            if (marcado) conjunto.add(clave);
            else conjunto.remove(clave);
            recalcular();
        });
        HBox h = new HBox(8, izquierda, Componentes.espaciador(), puntos);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(2, 0, 2, 0));
        return h;
    }

    private String claveDe(Cobrable item) {
        Cobrable base = item.getItemBase();
        if (base instanceof Entrada e) return e.getCodigoAsiento();
        if (base instanceof ItemConfiteria i) return i.getIdProducto();
        return "";
    }

    private int total(GridPane g, int fila, String etiqueta, String valor, boolean destacado) {
        Label e = new Label(etiqueta);
        Label v = new Label(valor);
        if (destacado) {
            e.getStyleClass().add("total-destacado");
            v.getStyleClass().add("total-destacado");
        }
        g.add(e, 0, fila);
        g.add(v, 1, fila);
        GridPane.setHgrow(e, Priority.ALWAYS);
        return fila + 1;
    }

    private void confirmar(List<String> asientos) {
        try {
            Compra compra = controlador.comprar(funcion, asientos, cantidades, asientosConPuntos, productosConPuntos);
            alCambiarSaldo.run();
            Dialogos.info("¡Compra confirmada! " + compra.getId(),
                    compra.getEntradas().size() + " entrada(s) para " + funcion.getPelicula().getTitulo() + "\n"
                            + Formato.fechaHora(funcion.getFechaHora()) + " · " + funcion.getSala().getNombre() + "\n"
                            + "Asientos: " + String.join(", ", compra.getEntradas().stream()
                            .map(Entrada::getCodigoAsiento).toList()) + "\n\n"
                            + "Pagado con la tarjeta: " + Formato.moneda(compra.getTotalAPagar()) + "\n"
                            + (compra.getPuntosRedimidos() > 0 ? "Puntos redimidos: " + compra.getPuntosRedimidos() + "\n" : "")
                            + "Puntos ganados: " + compra.getPuntosGanados());
            alTerminar.run();
        } catch (CinemaUQException e) {
            Dialogos.error(e);
            mapa.limpiarSeleccion(); // otro cliente pudo haber tomado un asiento
            asientosConPuntos.clear();
            recalcular();
        }
    }
}
