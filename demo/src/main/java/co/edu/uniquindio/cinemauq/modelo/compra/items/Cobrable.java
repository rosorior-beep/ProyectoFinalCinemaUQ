package co.edu.uniquindio.cinemauq.modelo.compra.items;

import java.io.Serializable;

public interface Cobrable extends Serializable {

    String getDescripcion();


    double getPrecioBase();


    double getPrecio();


    int getPuntosRedencion();


    default int getPuntosUsados() { return 0; }

    boolean esEntrada();


    default Cobrable getItemBase() { return this; }
}

