package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.fachada.CinemaFacade;
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
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;
import co.edu.uniquindio.cinemauq.repositorio.DatosIniciales;
import co.edu.uniquindio.cinemauq.repositorio.RepositorioArchivo;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;
import co.edu.uniquindio.cinemauq.vista.Navegador;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class AdminControlador {

    private final CinemaFacade fachada;
    private final Navegador navegador;
    private final RepositorioArchivo repositorio;

    public AdminControlador(CinemaFacade fachada, Navegador navegador, RepositorioArchivo repositorio) {
        this.fachada = fachada;
        this.navegador = navegador;
        this.repositorio = repositorio;
    }

    private IServicioAdministracion admin() { return fachada.administracion(); }

    public Usuario usuario() { return fachada.getUsuarioActual(); }

    public LocalDateTime ahora() { return fachada.ahora(); }


    public List<Pelicula> peliculas() { return admin().listarPeliculas(); }

    public void guardarPelicula(Pelicula existente, String titulo, Genero genero, String duracion,
                                Clasificacion clasificacion, String sinopsis, boolean enCartelera) {
        int minutos = Entradas.entero(duracion, "Duración");
        if (existente == null) {
            Pelicula p = admin().crearPelicula(titulo, genero, minutos, clasificacion, sinopsis);
            if (!enCartelera) admin().actualizarPelicula(p.getId(), titulo, genero, minutos, clasificacion, sinopsis, false);
        } else {
            admin().actualizarPelicula(existente.getId(), titulo, genero, minutos, clasificacion, sinopsis, enCartelera);
        }
    }


    public List<Sala> salas() { return admin().listarSalas(); }

    public Sala crearSala(String nombre, TipoSala tipo, String filas, String columnas, String preferenciales,
                          String accesibles) {
        return admin().crearSala(nombre, tipo, Entradas.entero(filas, "Filas"), Entradas.entero(columnas, "Columnas"),
                preferenciales == null ? "" : preferenciales.replaceAll("[^A-Za-z]", ""), Entradas.codigos(accesibles));
    }

    public void cambiarTipoAsiento(Sala sala, String codigo, TipoAsiento tipo) {
        admin().cambiarTipoAsiento(sala.getId(), codigo, tipo);
    }

    public void alternarHabilitado(Sala sala, String codigo) {
        boolean actual = sala.buscarAsiento(codigo).isHabilitado();
        admin().cambiarHabilitacionAsiento(sala.getId(), codigo, !actual);
    }


    public List<Funcion> funciones() { return admin().listarFunciones(); }

    public Funcion programarFuncion(Pelicula pelicula, Sala sala, LocalDate fecha, String hora, String precio) {
        Entradas.requerido(pelicula, "Película");
        Entradas.requerido(sala, "Sala");
        Entradas.requerido(fecha, "Fecha");
        return admin().programarFuncion(pelicula.getId(), sala.getId(), LocalDateTime.of(fecha, Entradas.hora(hora)),
                Entradas.dinero(precio, "Precio base"));
    }

    public List<Funcion> replicarFuncion(Funcion f, int dias) { return admin().replicarFuncion(f.getId(), dias); }

    public int cancelarFuncion(Funcion f) { return admin().cancelarFuncion(f.getId()); }


    public List<ComponenteConfiteria> confiteria() { return admin().listarConfiteria(); }

    public void crearProducto(String nombre, CategoriaProducto categoria, String precio, String puntos) {
        admin().crearProducto(nombre, categoria, Entradas.dinero(precio, "Precio"), Entradas.entero(puntos, "Puntos"));
    }

    public void crearCombo(String nombre, List<ComponenteConfiteria> componentes, String descuento, String puntos) {
        admin().crearCombo(nombre, componentes.stream().map(ComponenteConfiteria::getId).toList(),
                Entradas.decimal(descuento, "Descuento"), Entradas.entero(puntos, "Puntos"));
    }

    public void actualizarProducto(ComponenteConfiteria c, String nombre, String precio, String puntos, boolean disponible) {
        double valor = c.esCombo() ? c.getPrecio() : Entradas.dinero(precio, "Precio");
        admin().actualizarProducto(c.getId(), nombre, valor, Entradas.entero(puntos, "Puntos"), disponible);
    }


    public List<Promocion> promociones() { return admin().listarPromociones(); }

    public void crearPromocion(String nombre, AplicacionPromocion aplica, String porcentaje, DayOfWeek dia,
                               LocalDate inicio, LocalDate fin) {
        admin().crearPromocion(nombre, aplica, Entradas.decimal(porcentaje, "Porcentaje"), dia, inicio, fin);
    }

    public void alternarPromocion(Promocion p) { admin().cambiarEstadoPromocion(p.getId(), !p.isActiva()); }


    public List<Cliente> clientes() { return admin().listarClientes(); }

    public List<Compra> compras() { return admin().listarCompras(); }

    public void recargar(Cliente c, String monto) { admin().recargarTarjeta(c.getId(), Entradas.dinero(monto, "Monto")); }

    public void alternarTarjeta(Cliente c) {
        admin().cambiarEstadoTarjeta(c.getId(), c.getTarjeta().estaActiva() ? EstadoTarjeta.BLOQUEADA : EstadoTarjeta.ACTIVA);
    }

    public List<SolicitudRecarga> solicitudes() { return admin().listarSolicitudesRecarga(); }

    public void aprobar(SolicitudRecarga s) { admin().aprobarSolicitudRecarga(s.getId()); }

    public void rechazar(SolicitudRecarga s, String motivo) { admin().rechazarSolicitudRecarga(s.getId(), motivo); }

    public long solicitudesPendientes() { return fachada.solicitudesRecargaPendientes(); }

    public void marcarNotificacionesLeidas() { fachada.marcarNotificacionesLeidas(); }


    public void restaurarDatosIniciales() {
        if (repositorio == null) throw new DatosInvalidosException("No hay repositorio de archivo configurado.");
        DatosIniciales.cargar(repositorio, fachada.ahora());
        fachada.cerrarSesion();
        navegador.mostrarLogin();
    }

    public void cerrarSesion() {
        fachada.cerrarSesion();
        navegador.mostrarLogin();
    }
}
