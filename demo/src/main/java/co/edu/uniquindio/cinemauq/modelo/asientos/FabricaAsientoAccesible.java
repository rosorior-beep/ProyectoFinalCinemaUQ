package co.edu.uniquindio.cinemauq.modelo.asientos;


public class FabricaAsientoAccesible extends FabricaAsiento {
    @Override
    public Asiento crearAsiento(char fila, int numero) {
        return new AsientoAccesible(fila, numero);
    }
}
