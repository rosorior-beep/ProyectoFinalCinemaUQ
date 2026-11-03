package co.edu.uniquindio.cinemauq.modelo.confiteria;


public class Producto extends ComponenteConfiteria {

    private double precio;
    private CategoriaProducto categoria;

    public Producto(String id, String nombre, CategoriaProducto categoria, double precio, int puntosRedencion) {
        super(id, nombre, puntosRedencion);
        this.categoria = categoria;
        this.precio = precio;
    }

    @Override
    public double getPrecio() { return precio; }

    public void setPrecio(double precio) { this.precio = precio; }

    @Override
    public CategoriaProducto getCategoria() { return categoria; }

    public void setCategoria(CategoriaProducto categoria) { this.categoria = categoria; }

    @Override
    public String getDetalle() { return categoria.toString(); }
}

