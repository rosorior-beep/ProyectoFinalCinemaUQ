package co.edu.uniquindio.cinemauq.modelo.asientos;

public class FabricaAsientoGeneral extends FabricaAsiento {
    @Override
    public Asiento crearAsiento(char fila, int numero) {
        return new AsientoGeneral(fila, numero);
    }
}
