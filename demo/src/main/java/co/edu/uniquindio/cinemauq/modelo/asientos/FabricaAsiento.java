package co.edu.uniquindio.cinemauq.modelo.asientos;

import java.io.Serializable;

public abstract class FabricaAsiento implements Serializable {


    public abstract Asiento crearAsiento(char fila, int numero);

    public static FabricaAsiento para(TipoAsiento tipo) {
        return switch (tipo) {
            case GENERAL -> new FabricaAsientoGeneral();
            case PREFERENCIAL -> new FabricaAsientoPreferencial();
            case ACCESIBLE -> new FabricaAsientoAccesible();
        };
    }
}
