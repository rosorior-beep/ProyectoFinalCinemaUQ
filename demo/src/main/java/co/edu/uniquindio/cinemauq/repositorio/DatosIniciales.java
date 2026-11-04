package co.edu.uniquindio.cinemauq.repositorio;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.modelo.asientos.Asiento;
import co.edu.uniquindio.cinemauq.modelo.cine.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Genero;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.modelo.cine.TipoSala;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.confiteria.CategoriaProducto;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Administrador;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Notificacion;
import co.edu.uniquindio.cinemauq.modelo.config.ConfiguracionCine;
import co.edu.uniquindio.cinemauq.modelo.promociones.AplicacionPromocion;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.IServicioPagos;
import co.edu.uniquindio.cinemauq.servicio.IServicioPuntos;
import co.edu.uniquindio.cinemauq.servicio.IServicioRecargas;
import co.edu.uniquindio.cinemauq.servicio.estrategias.PuntosPorValorPagadoStrategy;
import co.edu.uniquindio.cinemauq.servicio.eventos.GestorEventos;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioAutenticacion;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioPagos;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioPuntos;
import co.edu.uniquindio.cinemauq.servicio.impl.ServicioRecargas;
import co.edu.uniquindio.cinemauq.servicio.tiempo.TiempoSimulado;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

public final class DatosIniciales {

    private static final LocalTime[] HORARIOS = {LocalTime.of(14, 0), LocalTime.of(17, 30), LocalTime.of(20, 45)};

    private final IRepositorioCine repo;
    private final LocalDateTime ahora;
    private final TiempoSimulado reloj;
    private final Random azar = new Random(2026);
    private final ServicioCompras compras;
    private final IServicioRecargas recargas;
    private final IServicioAdministracion admin;
    private final List<Cliente> clientes = new ArrayList<>();
    private Cliente sofia;

    private DatosIniciales(IRepositorioCine repo, LocalDateTime ahora) {
        this.repo = repo;
        this.ahora = ahora.withMinute(0).withSecond(0).withNano(0);
        this.reloj = new TiempoSimulado(this.ahora.minusDays(40));
        GestorEventos eventos = new GestorEventos(); // sin observadores: no se generan notificaciones masivas
        IServicioPagos pagos = new ServicioPagos(reloj);
        IServicioPuntos puntos = new ServicioPuntos(
                new PuntosPorValorPagadoStrategy(ConfiguracionCine.getInstance().getValorPorPunto()), reloj);
        this.compras = new ServicioCompras(repo, pagos, puntos, eventos, reloj);
        this.recargas = new ServicioRecargas(repo, pagos, eventos, reloj);
        this.admin = new ServicioAdministracion(repo, compras, recargas, eventos, reloj);
    }


    public static void cargar(IRepositorioCine repo, LocalDateTime ahora) {
        repo.limpiar();
        new DatosIniciales(repo, ahora).generar();
        repo.guardar();
    }

    private void generar() {
        crearUsuarios();
        List<Pelicula> peliculas = crearPeliculas();
        List<Sala> salas = crearSalas();
        crearConfiteria();
        crearPromociones();
        historialDePuntosAntiguo(peliculas.get(0), salas.get(0));
        recargasIniciales();
        List<Funcion> funciones = crearFunciones(peliculas, salas);
        simularCompras(funciones);
        escenariosDeDemostracion(funciones);
        bienvenida();
    }



    private void crearUsuarios() {
        repo.getUsuarios().add(new Administrador(repo.siguienteId("AD"), "Laura Gómez", "1094000001",
                "admin@cinemauq.com", "admin123"));
        ServicioAutenticacion auth = new ServicioAutenticacion(repo);
        sofia = auth.registrarCliente("Sofía Ramírez", "1094123456", "cliente@cinemauq.com", "3104567890", "cliente123", "cliente123");
        clientes.add(sofia);
        clientes.add(auth.registrarCliente("Juan Pérez", "1094234567", "juan@correo.com", "3112345678", "juan123", "juan123"));
        clientes.add(auth.registrarCliente("Valentina Ospina", "1094345678", "valentina@correo.com", "3123456789", "vale123", "vale123"));
        clientes.add(auth.registrarCliente("Mateo Cardona", "1094456789", "mateo@correo.com", "3134567890", "mateo123", "mateo123"));
        clientes.add(auth.registrarCliente("Camila Restrepo", "1094567890", "camila@correo.com", "3145678901", "camila123", "camila123"));
        clientes.add(auth.registrarCliente("Santiago López", "1094678901", "santiago@correo.com", "3156789012", "santi123", "santi123"));
        clientes.add(auth.registrarCliente("Daniela Muñoz", "1094789012", "daniela@correo.com", "3167890123", "dani123", "dani123"));
        String[] nombres = {"Andrés Castaño", "Paula Giraldo", "Felipe Arango", "Laura Henao", "Sebastián Ríos",
                "Manuela Quintero", "David Salazar", "Isabel Montoya", "Tomás Valencia", "Natalia Duque",
                "Julián Marín", "Carolina Zapata", "Esteban Londoño", "Mariana Toro", "Alejandro Mejía",
                "Juliana Franco", "Nicolás Aristizábal", "Gabriela Ocampo"};
        for (int i = 0; i < nombres.length; i++) {
            String usuario = nombres[i].toLowerCase().split(" ")[0]
                    .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u");
            clientes.add(auth.registrarCliente(nombres[i], String.valueOf(1095000000L + i * 1373), usuario + (i + 1) + "@correo.com",
                    "320" + String.format("%07d", 1000000 + i * 4711), "123456", "123456"));
        }
    }



    private List<Pelicula> crearPeliculas() {
        admin.crearPelicula("Horizonte Rojo", Genero.CIENCIA_FICCION, 148, Clasificacion.MAYORES_12,
                "Una tripulación viaja al borde del sistema solar para responder una señal desconocida.");
        admin.crearPelicula("La Casa del Eco", Genero.TERROR, 104, Clasificacion.MAYORES_15,
                "Una familia descubre que su nueva casa repite cada palabra... con un día de anticipación.");
        admin.crearPelicula("Pequeños Gigantes", Genero.ANIMACION, 96, Clasificacion.TODOS,
                "Un grupo de hormigas inventoras debe salvar su colonia de una inundación.");
        admin.crearPelicula("Café para Dos", Genero.ROMANCE, 112, Clasificacion.MAYORES_12,
                "Dos baristas rivales del Quindío compiten por el mejor café de la feria.");
        admin.crearPelicula("Operación Cóndor", Genero.ACCION, 131, Clasificacion.MAYORES_15,
                "Una agente encubierta debe impedir un robo en el aeropuerto más alto del mundo.");
        admin.crearPelicula("El Último Acorde", Genero.DRAMA, 125, Clasificacion.MAYORES_7,
                "Un pianista que pierde el oído prepara su último concierto.");
        Pelicula retirada = admin.crearPelicula("Risas de Verano", Genero.COMEDIA, 99, Clasificacion.TODOS,
                "Un campamento de vacaciones fuera de control.");
        admin.actualizarPelicula(retirada.getId(), retirada.getTitulo(), retirada.getGenero(), retirada.getDuracionMinutos(),
                retirada.getClasificacion(), retirada.getSinopsis(), false);
        List<Pelicula> lista = new ArrayList<>(repo.getPeliculas());
        lista.remove(retirada);
        return lista;
    }

    private List<Sala> crearSalas() {
        admin.crearSala("Sala 1", TipoSala.ESTANDAR, 8, 12, "GH", Set.of("A1", "A2", "A11", "A12"));
        admin.crearSala("Sala 2", TipoSala.TRES_D, 8, 12, "FG", Set.of("A1", "A12"));
        admin.crearSala("Sala 3 Premium", TipoSala.PREMIUM, 6, 10, "CDEF", Set.of("A1", "A10"));
        admin.crearSala("Sala 4", TipoSala.ESTANDAR, 10, 14, "HIJ", Set.of("A1", "A2", "A13", "A14"));
        return new ArrayList<>(repo.getSalas());
    }

    private void crearConfiteria() {
        ComponenteConfiteria crisP = admin.crearProducto("Crispetas pequeñas", CategoriaProducto.CRISPETAS, 9000, 90);
        ComponenteConfiteria crisM = admin.crearProducto("Crispetas medianas", CategoriaProducto.CRISPETAS, 13000, 130);
        ComponenteConfiteria crisG = admin.crearProducto("Crispetas grandes", CategoriaProducto.CRISPETAS, 16000, 160);
        ComponenteConfiteria gaseosa = admin.crearProducto("Gaseosa 500 ml", CategoriaProducto.BEBIDA, 7000, 70);
        ComponenteConfiteria gaseosaG = admin.crearProducto("Gaseosa grande", CategoriaProducto.BEBIDA, 9000, 90);
        admin.crearProducto("Agua 600 ml", CategoriaProducto.BEBIDA, 5000, 50);
        ComponenteConfiteria nachos = admin.crearProducto("Nachos con queso", CategoriaProducto.COMIDA, 15000, 150);
        admin.crearProducto("Perro caliente", CategoriaProducto.COMIDA, 14000, 140);
        ComponenteConfiteria choco = admin.crearProducto("Chocolatina", CategoriaProducto.DULCES, 5000, 50);
        ComponenteConfiteria gomitas = admin.crearProducto("Gomitas", CategoriaProducto.DULCES, 6000, 60);

        admin.crearCombo("Combo Individual", List.of(crisM.getId(), gaseosa.getId(), choco.getId()), 10, 220);
        ComponenteConfiteria pareja = admin.crearCombo("Combo Pareja",
                List.of(crisG.getId(), gaseosaG.getId(), gaseosaG.getId()), 15, 300);
        // Composite anidado: un combo que contiene otro combo
        admin.crearCombo("Combo Familiar", List.of(pareja.getId(), nachos.getId(), gomitas.getId(), gomitas.getId()), 10, 500);
    }

    private void crearPromociones() {
        LocalDate hoy = ahora.toLocalDate();
        admin.crearPromocion("Martes de cine", AplicacionPromocion.ENTRADAS, 30, DayOfWeek.TUESDAY,
                hoy.minusDays(60), hoy.plusDays(120));
        admin.crearPromocion("Miércoles de crispetas", AplicacionPromocion.CONFITERIA, 20, DayOfWeek.WEDNESDAY,
                hoy.minusDays(60), hoy.plusDays(120));
        var vacaciones = admin.crearPromocion("Vacaciones UQ", AplicacionPromocion.TODO, 10, null,
                hoy.minusDays(90), hoy.minusDays(45));
        admin.cambiarEstadoPromocion(vacaciones.getId(), false);
    }



    private void recargasIniciales() {
        reloj.establecer(ahora.minusDays(39).withHour(9));
        for (Cliente c : clientes) {
            recargas.recargarDirectamente(c.getId(), 300_000);
        }
    }


    private void historialDePuntosAntiguo(Pelicula pelicula, Sala sala) {
        crearCompraAntigua(pelicula, sala, ahora.minusMonths(13), List.of("D5", "D6", "D7", "D8"));
        crearCompraAntigua(pelicula, sala, ahora.minusMonths(11).minusDays(20), List.of("E5", "E6", "E7"));
        crearCompraAntigua(pelicula, sala, ahora.minusMonths(6), List.of("E5", "E6"));
    }

    private void crearCompraAntigua(Pelicula pelicula, Sala sala, LocalDateTime fecha, List<String> asientos) {
        Funcion f = new Funcion(repo.siguienteId("F"), pelicula, sala, fecha.withHour(20).withMinute(45), 15000);
        repo.getFunciones().add(f);
        reloj.establecer(f.getFechaHora().minusDays(1));
        recargas.recargarDirectamente(sofia.getId(), 200_000);
        Map<String, Integer> productos = new HashMap<>();
        productos.put(buscarProducto("Combo Familiar"), 2);
        compras.comprar(new ComandoCambiarCartelera(sofia, f, asientos, productos, Set.of(), Set.of()));
    }


    private List<Funcion> crearFunciones(List<Pelicula> peliculas, List<Sala> salas) {
        List<Funcion> creadas = new ArrayList<>();
        int indice = 0;
        for (int dia = -30; dia <= 7; dia++) {
            LocalDate fecha = ahora.toLocalDate().plusDays(dia);
            boolean finDeSemana = fecha.getDayOfWeek().getValue() >= 5;
            for (Sala sala : salas) {
                for (LocalTime hora : HORARIOS) {
                    Pelicula p = peliculas.get(indice++ % peliculas.size());
                    double precio = finDeSemana ? 15000 : 12000;
                    Funcion f = new Funcion(repo.siguienteId("F"), p, sala, LocalDateTime.of(fecha, hora), precio);
                    repo.getFunciones().add(f);
                    creadas.add(f);
                }
            }
            indice++;
        }
        return creadas;
    }


    private record Accion(LocalDateTime momento, Runnable tarea) { }


    private void simularCompras(List<Funcion> funciones) {
        List<String> confiteria = repo.getProductos().stream().map(ComponenteConfiteria::getId).toList();
        PriorityQueue<Accion> agenda = new PriorityQueue<>(Comparator.comparing(Accion::momento));
        for (Funcion f : funciones) {
            boolean futura = f.getFechaHora().isAfter(ahora);
            boolean finDeSemana = f.getFechaHora().getDayOfWeek().getValue() >= 5;
            int cantidadCompras = futura ? azar.nextInt(4) : 1 + azar.nextInt(finDeSemana ? 9 : 6);
            for (int i = 0; i < cantidadCompras; i++) {
                LocalDateTime momento = f.getFechaHora().minusHours(1 + azar.nextInt(70)).withMinute(azar.nextInt(60));
                if (!momento.isBefore(ahora)) momento = ahora.minusHours(4 + azar.nextInt(6));
                if (!momento.isBefore(f.getFechaHora())) continue;
                LocalDateTime m = momento;
                agenda.add(new Accion(m, () -> compraAleatoria(f, m, confiteria, agenda)));
            }
        }
        while (!agenda.isEmpty()) {
            Accion a = agenda.poll();
            reloj.establecer(a.momento());
            try {
                a.tarea().run();
            } catch (CinemaUQException e) {
                // Si una acción aleatoria no cumple alguna regla (ej. asiento ocupado) simplemente se omite
            }
        }
    }

    private void compraAleatoria(Funcion f, LocalDateTime momento, List<String> confiteria, PriorityQueue<Accion> agenda) {
        Cliente cliente = clientes.get(azar.nextInt(clientes.size()));
        if (cliente.getTarjeta().getEstado() != EstadoTarjeta.ACTIVA) return;
        List<String> asientos = asientosLibres(f, 1 + azar.nextInt(4));
        if (asientos.isEmpty()) return;
        Map<String, Integer> productos = new HashMap<>();
        if (azar.nextDouble() < 0.6) {
            productos.put(confiteria.get(azar.nextInt(confiteria.size())), 1 + azar.nextInt(2));
        }
        Set<String> conPuntos = new HashSet<>();
        // Sofía no redime aleatoriamente: así conserva sus lotes "por vencer" para la demostración
        if (cliente != sofia && cliente.getCuentaPuntos().getPuntosDisponibles(momento.toLocalDate()) >= 400
                && azar.nextDouble() < 0.3) {
            conPuntos.add(asientos.get(0));
        }
        asegurarSaldo(cliente, momento);
        Compra compra = compras.comprar(new ComandoCambiarCartelera(cliente, f, asientos, productos, conPuntos, Set.of()));
        if (azar.nextDouble() < 0.07) {
            LocalDateTime funcion = f.getFechaHora();
            LocalDateTime cancelacion = azar.nextBoolean() ? funcion.minusHours(30) : funcion.minusHours(5);
            if (cancelacion.isAfter(momento) && cancelacion.isBefore(ahora)) {
                agenda.add(new Accion(cancelacion, () -> compras.cancelarPorCliente(cliente, compra.getId())));
            }
        }
    }

    private void asegurarSaldo(Cliente cliente, LocalDateTime momento) {
        if (cliente.getTarjeta().getSaldo() < 150_000) {
            reloj.establecer(momento.minusMinutes(30));
            recargas.recargarDirectamente(cliente.getId(), 200_000 + 50_000 * azar.nextInt(4));
            reloj.establecer(momento);
        }
    }

    private List<String> asientosLibres(Funcion f, int cantidad) {
        List<Asiento> libres = new ArrayList<>(f.getSala().getAsientos().stream()
                .filter(a -> f.estaDisponible(a.getCodigo())).toList());
        if (libres.size() < cantidad) return List.of();
        int inicio = azar.nextInt(libres.size() - cantidad + 1);
        return libres.subList(inicio, inicio + cantidad).stream().map(Asiento::getCodigo).toList();
    }



    private void escenariosDeDemostracion(List<Funcion> funciones) {
        List<Funcion> futuras = funciones.stream().filter(f -> f.getFechaHora().isAfter(ahora.plusHours(3))).toList();


        Funcion lejana = futuras.stream().filter(f -> f.getFechaHora().isAfter(ahora.plusDays(3))).findFirst().orElseThrow();
        Funcion cercana = futuras.stream().filter(f -> f.getFechaHora().isBefore(ahora.plusHours(24))).findFirst()
                .orElse(futuras.get(0));
        reloj.establecer(ahora.minusHours(2));
        asegurarSaldo(sofia, ahora.minusHours(2));
        compras.comprar(new ComandoCambiarCartelera(sofia, lejana, asientosLibres(lejana, 2),
                Map.of(buscarProducto("Combo Pareja"), 1), Set.of(), Set.of()));
        compras.comprar(new ComandoCambiarCartelera(sofia, cercana, asientosLibres(cercana, 2), Map.of(), Set.of(), Set.of()));


        Funcion aCancelar = futuras.get(futuras.size() / 2);
        reloj.establecer(ahora.minusHours(3));
        for (int i = 1; i <= 3; i++) {
            Cliente c = clientes.get(i);
            asegurarSaldo(c, ahora.minusHours(3));
            List<String> asientos = asientosLibres(aCancelar, 2);
            if (!asientos.isEmpty()) {
                compras.comprar(new ComandoCambiarCartelera(c, aCancelar, asientos, Map.of(), Set.of(), Set.of()));
            }
        }
        reloj.establecer(ahora.minusHours(1));
        admin.cancelarFuncion(aCancelar.getId());

        // 3. Solicitudes de recarga pendientes y tarjeta bloqueada
        reloj.establecer(ahora.minusHours(1));
        recargas.solicitarRecarga(clientes.get(2), 100_000);
        recargas.solicitarRecarga(clientes.get(4), 50_000);
        Cliente santiago = clientes.get(5);
        admin.cambiarEstadoTarjeta(santiago.getId(), EstadoTarjeta.BLOQUEADA);
    }

    private void bienvenida() {
        for (Cliente c : clientes) {
            c.agregarNotificacion(new Notificacion(ahora, "¡Bienvenido(a) a CinemaUQ, " + c.getNombre()
                    + "! Acumulas 1 punto por cada $1.000 pagados."));
        }
        sofia.agregarNotificacion(new Notificacion(ahora, "Tienes puntos próximos a vencer. ¡Úsalos antes de que expiren!"));
    }

    private String buscarProducto(String nombre) {
        return repo.getProductos().stream().filter(p -> p.getNombre().equals(nombre)).findFirst()
                .orElseThrow().getId();
    }
}
