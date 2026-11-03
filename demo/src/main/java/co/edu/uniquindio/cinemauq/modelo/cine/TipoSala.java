package co.edu.uniquindio.cinemauq.modelo.cine;


public enum TipoSala {
    ESTANDAR("2D Estándar", 0), TRES_D("3D", 4000), PREMIUM("Premium", 8000);

    private final String etiqueta;
    private final double recargo;

    TipoSala(String etiqueta, double recargo) {
        this.etiqueta = etiqueta;
        this.recargo = recargo;
    }

    public double getRecargo() { return recargo; }

    @Override
    public String toString() { return etiqueta; }
}
