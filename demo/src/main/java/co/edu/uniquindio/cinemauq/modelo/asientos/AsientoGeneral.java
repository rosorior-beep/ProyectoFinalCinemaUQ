package co.edu.uniquindio.cinemauq.modelo.asientos;

public class AsientoGeneral extends Asiento {
    public AsientoGeneral(char fila, int numero) { super(fila, numero); }

    @Override public TipoAsiento getTipo() { return TipoAsiento.GENERAL; }
    @Override public double getRecargo() { return 0; }
}
