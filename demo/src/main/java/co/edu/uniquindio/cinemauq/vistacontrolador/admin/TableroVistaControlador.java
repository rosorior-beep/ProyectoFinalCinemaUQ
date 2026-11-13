package co.edu.uniquindio.cinemauq.vistacontrolador.admin;

import co.edu.uniquindio.cinemauq.controlador.ReportesControlador;
import co.edu.uniquindio.cinemauq.servicio.IServicioReportes;
import co.edu.uniquindio.cinemauq.servicio.reportes.Formato;
import co.edu.uniquindio.cinemauq.servicio.reportes.Indicadores;
import co.edu.uniquindio.cinemauq.vista.util.Dialogos;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.Map;

public class TableroVistaControlador {

    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private Label lblVentas;
    @FXML private Label lblEntradas;
    @FXML private Label lblOcupacion;
    @FXML private Label lblRecargado;
    @FXML private Label lblPelicula;
    @FXML private Label lblProducto;
    @FXML private Label lblCancelaciones;
    @FXML private Label lblReembolsado;
    @FXML private LineChart<String, Number> graficoVentas;
    @FXML private LineChart<String, Number> graficoRecargas;
    @FXML private BarChart<String, Number> graficoPeliculas;
    @FXML private PieChart graficoCategorias;
    @FXML private BarChart<String, Number> graficoOcupacion;
    @FXML private BarChart<String, Number> graficoProductos;

    private ReportesControlador controlador;

    public void configurar(ReportesControlador controlador) {
        this.controlador = controlador;
        ultimos30();
    }

    @FXML
    private void ultimos7() {
        rango(6);
    }

    @FXML
    private void ultimos30() {
        rango(29);
    }

    private void rango(int dias) {
        dpHasta.setValue(controlador.hoy());
        dpDesde.setValue(controlador.hoy().minusDays(dias));
        actualizar();
    }

    @FXML
    private void actualizar() {
        Dialogos.intentar(() -> {
            LocalDate d = dpDesde.getValue();
            LocalDate h = dpHasta.getValue();
            Indicadores ind = controlador.indicadores(d, h);
            IServicioReportes est = controlador.estadisticas();

            lblVentas.setText(Formato.moneda(ind.ventasNetas()));
            lblEntradas.setText(Formato.entero(ind.entradasVendidas()));
            lblOcupacion.setText(Formato.porcentaje(ind.ocupacionPromedio()));
            lblRecargado.setText(Formato.moneda(ind.totalRecargado()));
            lblPelicula.setText(ind.peliculaMasVista());
            lblProducto.setText(ind.productoMasVendido());
            lblCancelaciones.setText(Formato.entero(ind.cancelaciones()));
            lblReembolsado.setText(Formato.moneda(ind.totalReembolsado()));

            lineas(graficoVentas, est.ventasPorDia(d, h));
            lineas(graficoRecargas, est.recargasPorDia(d, h));
            barras(graficoPeliculas, est.entradasPorPelicula(d, h));
            pastel(graficoCategorias, est.ventasPorCategoria(d, h));
            barras(graficoOcupacion, est.ocupacionPorSala(d, h));
            barras(graficoProductos, est.productosMasVendidos(d, h, 8));
        });
    }

    private void lineas(LineChart<String, Number> grafico, Map<LocalDate, Double> datos) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        datos.forEach((fecha, valor) -> serie.getData().add(new XYChart.Data<>(Formato.fecha(fecha).substring(0, 5), valor)));
        grafico.getData().setAll(serie);
        grafico.setCreateSymbols(datos.size() <= 15);
    }

    private void barras(BarChart<String, Number> grafico, Map<String, ? extends Number> datos) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        datos.forEach((k, v) -> serie.getData().add(new XYChart.Data<>(k, v)));
        grafico.getData().setAll(serie);
    }

    private void pastel(PieChart grafico, Map<String, Double> datos) {
        grafico.getData().clear();
        double total = datos.values().stream().mapToDouble(Double::doubleValue).sum();
        datos.forEach((k, v) -> {
            if (v > 0) {
                grafico.getData().add(new PieChart.Data(k + " " + Formato.porcentaje(total == 0 ? 0 : v * 100 / total), v));
            }
        });
    }
}
