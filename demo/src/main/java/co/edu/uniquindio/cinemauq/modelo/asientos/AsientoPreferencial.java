package co.edu.uniquindio.cinemauq.modelo.asientos;

public class AsientoPreferencial extends Asiento {
    public static final double RECARGO = 6000;

    public AsientoPreferencial(char fila, int numero) { super(fila, numero); }

    @Override public TipoAsiento getTipo() { return TipoAsiento.PREFERENCIAL; }
    @Override public double getRecargo() { return RECARGO; }
}
