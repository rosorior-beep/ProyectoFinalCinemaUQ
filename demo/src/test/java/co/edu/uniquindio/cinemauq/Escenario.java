package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.modelo.cine.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Genero;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.modelo.cine.TipoSala;
import co.edu.uniquindio.cinemauq.modelo.confiteria.CategoriaProducto;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Administrador;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.repositorio.RepositorioMemoria;
import co.edu.uniquindio.cinemauq.servicio.IServicioPagos;
import co.edu.uniquindio.cinemauq.servicio.estrategias.PuntosPorValorPagadoStrategy;
import co.edu.uniquindio.cinemauq.servicio.eventos.GestorEventos;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioPagos;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioPuntos;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioRecargas;
import co.edu.uniquindio.cinemauq.servicio.tiempo.TiempoSimulado;

import java.time.LocalDateTime;
import java.util.Set;

public class Escenario {

    public static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 2, 10, 0); // lunes

    public final RepositorioMemoria repo = new RepositorioMemoria();
    public final TiempoSimulado reloj = new TiempoSimulado(AHORA);
    public final GestorEventos eventos = new GestorEventos();
    public final IServicioPagos pagos = new ServicioPagos(reloj);
    public final ServicioPuntos puntos = new ServicioPuntos(new PuntosPorValorPagadoStrategy(1000), reloj);
    public final ServicioCompras compras = new ServicioCompras(repo, pagos, puntos, eventos, reloj);
    public final ServicioRecargas recargas = new ServicioRecargas(repo, pagos, eventos, reloj);
    public final ServicioAdministracion admin = new ServicioAdministracion(repo, compras, recargas, eventos, reloj);

    public final Cliente cliente;
    public final Administrador administrador;
    public final Pelicula pelicula;
    public final Sala sala;

    public Escenario() {
        administrador = new Administrador("AD-1", "Admin", "1000", "admin@test.com", "admin123");
        repo.getUsuarios().add(administrador);
        cliente = new Cliente("CL-1", "Ana", "123456", "ana@test.com", "300", "clave123", "4571000000001234");
        repo.getUsuarios().add(cliente);
        pelicula = admin.crearPelicula("Película de prueba", Genero.DRAMA, 120, Clasificacion.TODOS, "");
        sala = admin.crearSala("Sala prueba", TipoSala.ESTANDAR, 5, 6, "E", Set.of("A1"));
        admin.crearProducto("Crispetas", CategoriaProducto.CRISPETAS, 10000, 100);
    }


    public Funcion funcionEn(long horas) {
        return admin.programarFuncion(pelicula.getId(), sala.getId(), AHORA.plusHours(horas), 10000);
    }

    public void recargar(double monto) {
        recargas.recargarDirectamente(cliente.getId(), monto);
    }
}
