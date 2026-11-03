package co.edu.uniquindio.cinemauq.modelo.compra.items;


public abstract class CobrableDecorator implements Cobrable {

    protected final Cobrable envuelto;

    protected CobrableDecorator(Cobrable envuelto) {
        this.envuelto = envuelto;
    }

    @Override public String getDescripcion() { return envuelto.getDescripcion(); }
    @Override public double getPrecioBase() { return envuelto.getPrecioBase(); }
    @Override public double getPrecio() { return envuelto.getPrecio(); }
    @Override public int getPuntosRedencion() { return envuelto.getPuntosRedencion(); }
    @Override public int getPuntosUsados() { return envuelto.getPuntosUsados(); }
    @Override public boolean esEntrada() { return envuelto.esEntrada(); }
    @Override public Cobrable getItemBase() { return envuelto.getItemBase(); }
}
