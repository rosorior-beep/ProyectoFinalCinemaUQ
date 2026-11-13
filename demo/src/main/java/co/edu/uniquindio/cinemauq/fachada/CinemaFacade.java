package co.edu.uniquindio.cinemauq.fachada;

import co.edu.uniquindio.cinemauq.excepciones.AccesoDenegadoException;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.config.ConfiguracionCine;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.EstadoSolicitud;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Rol;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.IServicioAutenticacion;
import co.edu.uniquindio.cinemauq.servicio.IServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.IServicioPagos;
import co.edu.uniquindio.cinemauq.servicio.IServicioPuntos;
import co.edu.uniquindio.cinemauq.servicio.IServicioRecargas;
import co.edu.uniquindio.cinemauq.servicio.IServicioReportes;
import co.edu.uniquindio.cinemauq.servicio.dto.PedidoCompra;
import co.edu.uniquindio.cinemauq.servicio.estrategias.PuntosPorValorPagadoStrategy;
import co.edu.uniquindio.cinemauq.servicio.eventos.GestorEventos;
import co.edu.uniquindio.cinemauq.servicio.eventos.NotificadorAdministradores;
import co.edu.uniquindio.cinemauq.servicio.eventos.NotificadorUsuario;
import co.edu.uniquindio.cinemauq.servicio.eventos.ObservadorCine;
import co.edu.uniquindio.cinemauq.servicio.eventos.TipoEvento;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioAutenticacion;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioPagos;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioPuntos;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioRecargas;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioReportes;
import co.edu.uniquindio.cinemauq.servicio.proxy.ProxyServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.reportes.Indicadores;
import co.edu.uniquindio.cinemauq.servicio.reportes.Reporte;
import co.edu.uniquindio.cinemauq.servicio.reportes.TipoReporte;
import co.edu.uniquindio.cinemauq.servicio.tiempo.ProveedorTiempo;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CinemaFacade {

    private final IRepositorioCine repositorio;
    private final ProveedorTiempo tiempo;
    private final GestorEventos eventos;
    private final IServicioAutenticacion autenticacion;
    private final IServicioPuntos puntos;
    private final IServicioCompras compras;
    private final IServicioRecargas recargas;
    private final IServicioAdministracion administracion;
    private final IServicioReportes reportes;

    private Usuario usuarioActual;

    public CinemaFacade(IRepositorioCine repositorio, ProveedorTiempo tiempo, List<ObservadorCine> observadoresExtra) {
        this.repositorio = repositorio;
        this.tiempo = tiempo;


        this.eventos = new GestorEventos();
        eventos.suscribirATodos(new NotificadorUsuario());
        eventos.suscribir(TipoEvento.RECARGA_SOLICITADA, new NotificadorAdministradores(repositorio));
        observadoresExtra.forEach(eventos::suscribirATodos);


        IServicioPagos pagos = new ServicioPagos(tiempo);
        this.autenticacion = new ServicioAutenticacion(repositorio);
        this.puntos = new ServicioPuntos(new PuntosPorValorPagadoStrategy(ConfiguracionCine.getInstance().getValorPorPunto()), tiempo);
        this.compras = new ServicioCompras(repositorio, pagos, puntos, eventos, tiempo);
        this.recargas = new ServicioRecargas(repositorio, pagos, eventos, tiempo);
        this.reportes = new ServicioReportes(repositorio, tiempo);
        // Proxy: el servicio real de administración solo se crea y usa si el usuario es administrador
        this.administracion = new ProxyServicioAdministracion(() -> usuarioActual,
                () -> new ServicioAdministracion(repositorio, compras, recargas, eventos, tiempo));
    }



    public Usuario iniciarSesion(String email, String password) {
        usuarioActual = autenticacion.iniciarSesion(email, password);
        return usuarioActual;
    }

    public Cliente registrarCliente(String nombre, String documento, String email, String telefono,
                                    String password, String confirmacion) {
        return autenticacion.registrarCliente(nombre, documento, email, telefono, password, confirmacion);
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Cliente getClienteActual() {
        if (usuarioActual instanceof Cliente c) return c;
        throw new AccesoDenegadoException("Debes iniciar sesión como cliente.");
    }

    private void verificarAdministrador() {
        if (usuarioActual == null || usuarioActual.getRol() != Rol.ADMINISTRADOR) {
            throw new AccesoDenegadoException("Acceso denegado: opción exclusiva del administrador.");
        }
    }

    public LocalDateTime ahora() {
        return tiempo.ahora();
    }

    public void marcarNotificacionesLeidas() {
        if (usuarioActual != null) {
            usuarioActual.getNotificaciones().forEach(n -> n.marcarLeida());
            repositorio.guardar();
        }
    }



    public List<Pelicula> peliculasEnCartelera() {
        return compras.peliculasEnCartelera();
    }

    public List<Funcion> funcionesDisponibles(Pelicula pelicula) {
        return compras.funcionesDisponibles(pelicula);
    }

    public List<ComponenteConfiteria> confiteriaDisponible() {
        return compras.confiteriaDisponible();
    }

    public Compra cotizar(Funcion funcion, List<String> asientos, Map<String, Integer> productos,
                          Set<String> asientosConPuntos, Set<String> productosConPuntos) {
        return compras.cotizar(new PedidoCompra(getClienteActual(), funcion, asientos, productos,
                asientosConPuntos, productosConPuntos));
    }

    public Compra comprar(Funcion funcion, List<String> asientos, Map<String, Integer> productos,
                          Set<String> asientosConPuntos, Set<String> productosConPuntos) {
        return compras.comprar(new PedidoCompra(getClienteActual(), funcion, asientos, productos,
                asientosConPuntos, productosConPuntos));
    }

    public List<Compra> misCompras() {
        return compras.comprasDe(getClienteActual());
    }

    public double porcentajeReembolso(Compra compra) {
        return compras.porcentajeReembolsoActual(compra);
    }

    public Compra cancelarCompra(String idCompra) {
        return compras.cancelarPorCliente(getClienteActual(), idCompra);
    }

    public SolicitudRecarga solicitarRecarga(double monto) {
        return recargas.solicitarRecarga(getClienteActual(), monto);
    }

    public List<SolicitudRecarga> misSolicitudesRecarga() {
        return recargas.solicitudesDe(getClienteActual());
    }

    public int puntosDisponibles() {
        return puntos.puntosDisponibles(getClienteActual());
    }

    public int puntosPorVencer(int dias) {
        return getClienteActual().getCuentaPuntos().getPuntosPorVencer(ahora().toLocalDate(), dias);
    }

    public int puntosAGanar(Compra compra) {
        return puntos.calcularPuntosGanados(compra);
    }

    public String descripcionReglaPuntos() {
        return puntos.getDescripcionRegla();
    }



    public IServicioAdministracion administracion() {
        return administracion;
    }

    public Reporte generarReporte(TipoReporte tipo, LocalDate desde, LocalDate hasta) {
        verificarAdministrador();
        return reportes.generar(tipo, desde, hasta);
    }

    public Path exportarReporte(Reporte reporte, String extension, Path destino) {
        verificarAdministrador();
        return reportes.exportar(reporte, extension, destino);
    }

    public IServicioReportes estadisticas() {
        verificarAdministrador();
        return reportes;
    }

    public Indicadores indicadores(LocalDate desde, LocalDate hasta) {
        verificarAdministrador();
        return reportes.indicadores(desde, hasta);
    }

    public long solicitudesRecargaPendientes() {
        return repositorio.getSolicitudesRecarga().stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
                .count();
    }
}
