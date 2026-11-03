package co.edu.uniquindio.cinemauq.modelo.asientos;


public class FabricaAsientoPreferencial extends FabricaAsiento {
    @Override
    public Asiento crearAsiento(char fila, int numero) {
        return new AsientoPreferencial(fila, numero);
    }
}
