package co.edu.uniquindio.cinemauq.modelo.compra.items;


public class PagadoConPuntos extends CobrableDecorator {

    public PagadoConPuntos(Cobrable envuelto) {
        super(envuelto);
    }

    @Override
    public double getPrecio() { return 0; }

    @Override
    public int getPuntosUsados() { return envuelto.getPuntosRedencion(); }

    @Override
    public String getDescripcion() {
        return envuelto.getDescripcion() + " [Redimido: " + envuelto.getPuntosRedencion() + " pts]";
    }
}