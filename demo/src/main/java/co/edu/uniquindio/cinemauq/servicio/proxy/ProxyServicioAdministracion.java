package co.edu.uniquindio.cinemauq.servicio.proxy;

import co.edu.uniquindio.cinemauq.modelo.asientos.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.cine.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Genero;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.modelo.cine.TipoSala;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.confiteria.CategoriaProducto;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.promociones.AplicacionPromocion;
import co.edu.uniquindio.cinemauq.modelo.promociones.Promocion;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import co.edu.uniquindio.cinemauq.excepciones.AccesoDenegadoException;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Rol;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;

import java.util.function.Supplier;

public class ProxyServicioAdministracion implements IServicioAdministracion {

    private final Supplier<Usuario> usuarioSesion;
    private final Supplier<IServicioAdministracion> creadorServicioReal;
    private IServicioAdministracion servicioReal;

    public ProxyServicioAdministracion(Supplier<Usuario> usuarioSesion,
                                       Supplier<IServicioAdministracion> creadorServicioReal) {
        this.usuarioSesion = usuarioSesion;
        this.creadorServicioReal = creadorServicioReal;
    }

    private void verificarAdministrador() {
        Usuario usuario = usuarioSesion.get();
        if (usuario == null || usuario.getRol() != Rol.ADMINISTRADOR) {
            throw new AccesoDenegadoException("Acceso denegado: esta operación es exclusiva del administrador.");
        }
    }

    private IServicioAdministracion real() {
        if (servicioReal == null) {
            servicioReal = creadorServicioReal.get();
        }
        return servicioReal;
    }

    @Override
    public List<Pelicula> listarPeliculas() {
        verificarAdministrador();
        return real().listarPeliculas();
    }

    @Override
    public Pelicula crearPelicula(String titulo, Genero genero, int duracion, Clasificacion clasificacion, String sinopsis) {
        verificarAdministrador();
        return real().crearPelicula(titulo, genero, duracion, clasificacion, sinopsis);
    }

    @Override
    public void actualizarPelicula(String id, String titulo, Genero genero, int duracion,
                                   Clasificacion clasificacion, String sinopsis, boolean enCartelera) {
        verificarAdministrador();
        real().actualizarPelicula(id, titulo, genero, duracion, clasificacion, sinopsis, enCartelera);
    }

    @Override
    public List<Sala> listarSalas() {
        verificarAdministrador();
        return real().listarSalas();
    }

    @Override
    public Sala crearSala(String nombre, TipoSala tipo, int filas, int columnas, String filasPreferenciales,
                          Set<String> asientosAccesibles) {
        verificarAdministrador();
        return real().crearSala(nombre, tipo, filas, columnas, filasPreferenciales, asientosAccesibles);
    }

    @Override
    public void cambiarTipoAsiento(String idSala, String codigoAsiento, TipoAsiento tipo) {
        verificarAdministrador();
        real().cambiarTipoAsiento(idSala, codigoAsiento, tipo);
    }

    @Override
    public void cambiarHabilitacionAsiento(String idSala, String codigoAsiento, boolean habilitado) {
        verificarAdministrador();
        real().cambiarHabilitacionAsiento(idSala, codigoAsiento, habilitado);
    }

    @Override
    public List<Funcion> listarFunciones() {
        verificarAdministrador();
        return real().listarFunciones();
    }

    @Override
    public Funcion programarFuncion(String idPelicula, String idSala, LocalDateTime fechaHora, double precioBase) {
        verificarAdministrador();
        return real().programarFuncion(idPelicula, idSala, fechaHora, precioBase);
    }

    @Override
    public List<Funcion> replicarFuncion(String idFuncion, int dias) {
        verificarAdministrador();
        return real().replicarFuncion(idFuncion, dias);
    }

    @Override
    public int cancelarFuncion(String idFuncion) {
        verificarAdministrador();
        return real().cancelarFuncion(idFuncion);
    }

    @Override
    public List<ComponenteConfiteria> listarConfiteria() {
        verificarAdministrador();
        return real().listarConfiteria();
    }

    @Override
    public ComponenteConfiteria crearProducto(String nombre, CategoriaProducto categoria, double precio, int puntos) {
        verificarAdministrador();
        return real().crearProducto(nombre, categoria, precio, puntos);
    }

    @Override
    public ComponenteConfiteria crearCombo(String nombre, List<String> idsComponentes, double descuento, int puntos) {
        verificarAdministrador();
        return real().crearCombo(nombre, idsComponentes, descuento, puntos);
    }

    @Override
    public void actualizarProducto(String id, String nombre, double precio, int puntos, boolean disponible) {
        verificarAdministrador();
        real().actualizarProducto(id, nombre, precio, puntos, disponible);
    }

    @Override
    public List<Promocion> listarPromociones() {
        verificarAdministrador();
        return real().listarPromociones();
    }

    @Override
    public Promocion crearPromocion(String nombre, AplicacionPromocion aplicaA, double porcentaje, DayOfWeek dia,
                                    LocalDate inicio, LocalDate fin) {
        verificarAdministrador();
        return real().crearPromocion(nombre, aplicaA, porcentaje, dia, inicio, fin);
    }

    @Override
    public void cambiarEstadoPromocion(String id, boolean activa) {
        verificarAdministrador();
        real().cambiarEstadoPromocion(id, activa);
    }

    @Override
    public List<Cliente> listarClientes() {
        verificarAdministrador();
        return real().listarClientes();
    }

    @Override
    public List<Compra> listarCompras() {
        verificarAdministrador();
        return real().listarCompras();
    }

    @Override
    public void cambiarEstadoTarjeta(String idCliente, EstadoTarjeta estado) {
        verificarAdministrador();
        real().cambiarEstadoTarjeta(idCliente, estado);
    }

    @Override
    public List<SolicitudRecarga> listarSolicitudesRecarga() {
        verificarAdministrador();
        return real().listarSolicitudesRecarga();
    }

    @Override
    public void aprobarSolicitudRecarga(String idSolicitud) {
        verificarAdministrador();
        real().aprobarSolicitudRecarga(idSolicitud);
    }

    @Override
    public void rechazarSolicitudRecarga(String idSolicitud, String motivo) {
        verificarAdministrador();
        real().rechazarSolicitudRecarga(idSolicitud, motivo);
    }

    @Override
    public void recargarTarjeta(String idCliente, double monto) {
        verificarAdministrador();
        real().recargarTarjeta(idCliente, monto);
    }
}
