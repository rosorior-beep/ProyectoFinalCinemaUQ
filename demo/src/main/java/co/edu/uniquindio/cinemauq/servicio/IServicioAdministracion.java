package co.edu.uniquindio.cinemauq.servicio;

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

public interface IServicioAdministracion {


    List<Pelicula> listarPeliculas();

    Pelicula crearPelicula(String titulo, Genero genero, int duracion, Clasificacion clasificacion, String sinopsis);

    void actualizarPelicula(String id, String titulo, Genero genero, int duracion,
                            Clasificacion clasificacion, String sinopsis, boolean enCartelera);

    List<Sala> listarSalas();

    Sala crearSala(String nombre, TipoSala tipo, int filas, int columnas, String filasPreferenciales,
                   Set<String> asientosAccesibles);

    void cambiarTipoAsiento(String idSala, String codigoAsiento, TipoAsiento tipo);

    void cambiarHabilitacionAsiento(String idSala, String codigoAsiento, boolean habilitado);


    List<Funcion> listarFunciones();

    Funcion programarFuncion(String idPelicula, String idSala, LocalDateTime fechaHora, double precioBase);


    List<Funcion> replicarFuncion(String idFuncion, int dias);


    int cancelarFuncion(String idFuncion);

    List<ComponenteConfiteria> listarConfiteria();

    ComponenteConfiteria crearProducto(String nombre, CategoriaProducto categoria, double precio, int puntos);

    ComponenteConfiteria crearCombo(String nombre, List<String> idsComponentes, double descuento, int puntos);

    void actualizarProducto(String id, String nombre, double precio, int puntos, boolean disponible);

    List<Promocion> listarPromociones();

    Promocion crearPromocion(String nombre, AplicacionPromocion aplicaA, double porcentaje, DayOfWeek dia,
                             LocalDate inicio, LocalDate fin);

    void cambiarEstadoPromocion(String id, boolean activa);

    List<Cliente> listarClientes();

    List<Compra> listarCompras();

    void cambiarEstadoTarjeta(String idCliente, EstadoTarjeta estado);

    List<SolicitudRecarga> listarSolicitudesRecarga();

    void aprobarSolicitudRecarga(String idSolicitud);

    void rechazarSolicitudRecarga(String idSolicitud, String motivo);

    void recargarTarjeta(String idCliente, double monto);
}
