package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.fachada.CinemaFacade;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.config.ConfiguracionCine;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.vista.Navegador;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ClienteControlador {

    private final CinemaFacade fachada;
    private final Navegador navegador;

    public ClienteControlador(CinemaFacade fachada, Navegador navegador) {
        this.fachada = fachada;
        this.navegador = navegador;
    }

    public Cliente cliente() { return fachada.getClienteActual(); }

    public LocalDateTime ahora() { return fachada.ahora(); }

    public List<Pelicula> cartelera() { return fachada.peliculasEnCartelera(); }

    public List<Funcion> funciones(Pelicula p) { return fachada.funcionesDisponibles(p); }

    public List<ComponenteConfiteria> confiteria() { return fachada.confiteriaDisponible(); }

    public int puntosPorEntrada() { return ConfiguracionCine.getInstance().getPuntosPorEntrada(); }

    public Compra cotizar(Funcion f, List<String> asientos, Map<String, Integer> productos,
                          Set<String> asientosPuntos, Set<String> productosPuntos) {
        return fachada.cotizar(f, asientos, productos, asientosPuntos, productosPuntos);
    }

    public Compra comprar(Funcion f, List<String> asientos, Map<String, Integer> productos,
                          Set<String> asientosPuntos, Set<String> productosPuntos) {
        return fachada.comprar(f, asientos, productos, asientosPuntos, productosPuntos);
    }

    public List<Compra> misCompras() { return fachada.misCompras(); }

    public double porcentajeReembolso(Compra c) { return fachada.porcentajeReembolso(c); }

    public Compra cancelar(Compra c) { return fachada.cancelarCompra(c.getId()); }

    public SolicitudRecarga solicitarRecarga(String monto) {
        return fachada.solicitarRecarga(Entradas.dinero(monto, "Monto"));
    }

    public List<SolicitudRecarga> misSolicitudes() { return fachada.misSolicitudesRecarga(); }

    public int puntosDisponibles() { return fachada.puntosDisponibles(); }

    public int puntosPorVencer() { return fachada.puntosPorVencer(30); }

    public String reglaPuntos() { return fachada.descripcionReglaPuntos(); }

    public int puntosAGanar(Compra c) { return fachada.puntosAGanar(c); }

    public void marcarNotificacionesLeidas() { fachada.marcarNotificacionesLeidas(); }

    public void cerrarSesion() {
        fachada.cerrarSesion();
        navegador.mostrarLogin();
    }
}
