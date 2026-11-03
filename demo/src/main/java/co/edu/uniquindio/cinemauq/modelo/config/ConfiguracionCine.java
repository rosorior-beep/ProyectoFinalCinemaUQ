package co.edu.uniquindio.cinemauq.modelo.config;


public final class ConfiguracionCine {

    private static ConfiguracionCine instancia;

    private double valorPorPunto = 1000;

    private int puntosPorEntrada = 250;

    private int minutosLimpieza = 20;
    private double recargaMinima = 10_000;
    private double recargaMaxima = 1_000_000;

    private int maxEntradasPorCompra = 10;

    private int aniosVigenciaPuntos = 1;

    private ConfiguracionCine() {
    }

    public static ConfiguracionCine getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracionCine();
        }
        return instancia;
    }

    public double getValorPorPunto() { return valorPorPunto; }
    public int getPuntosPorEntrada() { return puntosPorEntrada; }
    public int getMinutosLimpieza() { return minutosLimpieza; }
    public double getRecargaMinima() { return recargaMinima; }
    public double getRecargaMaxima() { return recargaMaxima; }
    public int getMaxEntradasPorCompra() { return maxEntradasPorCompra; }
    public int getAniosVigenciaPuntos() { return aniosVigenciaPuntos; }
}