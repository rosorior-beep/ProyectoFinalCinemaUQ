package co.edu.uniquindio.cinemauq.modelo.asientos;

public class AsientoAccesible extends Asiento {
    public AsientoAccesible(char fila, int numero) { super(fila, numero); }

    @Override public TipoAsiento getTipo() { return TipoAsiento.ACCESIBLE; }
    @Override public double getRecargo() { return 0; }
}
