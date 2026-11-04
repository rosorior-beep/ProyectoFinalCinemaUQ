package co.edu.uniquindio.cinemauq.servicio.impl;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.EntidadNoEncontradaException;
import co.edu.uniquindio.cinemauq.excepciones.FuncionSolapadaException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.asientos.TipoAsiento;
import co.edu.uniquindio.cinemauq.modelo.cine.Clasificacion;
import co.edu.uniquindio.cinemauq.modelo.cine.EstadoFuncion;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Genero;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.modelo.cine.TipoSala;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.config.ConfiguracionCine;
import co.edu.uniquindio.cinemauq.modelo.confiteria.CategoriaProducto;
import co.edu.uniquindio.cinemauq.modelo.confiteria.Combo;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.confiteria.Producto;
import co.edu.uniquindio.cinemauq.modelo.promociones.AplicacionPromocion;
import co.edu.uniquindio.cinemauq.modelo.promociones.Promocion;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.IServicioAdministracion;
import co.edu.uniquindio.cinemauq.servicio.IServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.IServicioRecargas;
import co.edu.uniquindio.cinemauq.servicio.eventos.EventoCine;
import co.edu.uniquindio.cinemauq.servicio.eventos.GestorEventos;
import co.edu.uniquindio.cinemauq.servicio.eventos.TipoEvento;
import co.edu.uniquindio.cinemauq.servicio.tiempo.ProveedorTiempo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ServicioAdministracion implements IServicioAdministracion {

    private final IRepositorioCine repositorio;
    private final IServicioCompras compras;
    private final IServicioRecargas recargas;
    private final GestorEventos eventos;
    private final ProveedorTiempo tiempo;

    public ServicioAdministracion(IRepositorioCine repositorio, IServicioCompras compras, IServicioRecargas recargas,
                                  GestorEventos eventos, ProveedorTiempo tiempo) {
        this.repositorio = repositorio;
        this.compras = compras;
        this.recargas = recargas;
        this.eventos = eventos;
        this.tiempo = tiempo;
    }


    @Override
    public List<Pelicula> listarPeliculas() {
        return List.copyOf(repositorio.getPeliculas());
    }

    @Override
    public Pelicula crearPelicula(String titulo, Genero genero, int duracion, Clasificacion clasificacion, String sinopsis) {
        validarPelicula(titulo, genero, duracion, clasificacion);
        boolean repetida = repositorio.getPeliculas().stream().anyMatch(p -> p.getTitulo().equalsIgnoreCase(titulo.trim()));
        if (repetida) throw new DatosInvalidosException("Ya existe una película llamada " + titulo.trim() + ".");
        Pelicula p = new Pelicula(repositorio.siguienteId("P"), titulo.trim(), genero, duracion, clasificacion,
                sinopsis == null ? "" : sinopsis.trim());
        repositorio.getPeliculas().add(p);
        repositorio.guardar();
        return p;
    }

    @Override
    public void actualizarPelicula(String id, String titulo, Genero genero, int duracion,
                                   Clasificacion clasificacion, String sinopsis, boolean enCartelera) {
        validarPelicula(titulo, genero, duracion, clasificacion);
        Pelicula p = buscarPelicula(id);
        p.setTitulo(titulo.trim());
        p.setGenero(genero);
        p.setDuracionMinutos(duracion);
        p.setClasificacion(clasificacion);
        p.setSinopsis(sinopsis == null ? "" : sinopsis.trim());
        p.setEnCartelera(enCartelera);
        repositorio.guardar();
    }

    private void validarPelicula(String titulo, Genero genero, int duracion, Clasificacion clasificacion) {
        if (titulo == null || titulo.isBlank()) throw new DatosInvalidosException("El título es obligatorio.");
        if (genero == null || clasificacion == null) throw new DatosInvalidosException("Selecciona género y clasificación.");
        if (duracion < 30 || duracion > 300) throw new DatosInvalidosException("La duración debe estar entre 30 y 300 minutos.");
    }


    @Override
    public List<Sala> listarSalas() {
        return List.copyOf(repositorio.getSalas());
    }

    @Override
    public Sala crearSala(String nombre, TipoSala tipo, int filas, int columnas, String filasPreferenciales,
                          Set<String> asientosAccesibles) {
        if (nombre == null || nombre.isBlank() || tipo == null) {
            throw new DatosInvalidosException("El nombre y el tipo de la sala son obligatorios.");
        }
        boolean repetida = repositorio.getSalas().stream().anyMatch(s -> s.getNombre().equalsIgnoreCase(nombre.trim()));
        if (repetida) throw new DatosInvalidosException("Ya existe una sala llamada " + nombre.trim() + ".");
        Sala sala = new Sala(repositorio.siguienteId("S"), nombre.trim(), tipo, filas, columnas,
                filasPreferenciales, asientosAccesibles);
        repositorio.getSalas().add(sala);
        repositorio.guardar();
        return sala;
    }

    @Override
    public void cambiarTipoAsiento(String idSala, String codigoAsiento, TipoAsiento tipo) {
        buscarSala(idSala).cambiarTipoAsiento(codigoAsiento, tipo);
        repositorio.guardar();
    }

    @Override
    public void cambiarHabilitacionAsiento(String idSala, String codigoAsiento, boolean habilitado) {
        Sala sala = buscarSala(idSala);
        if (!habilitado) {
            boolean vendido = repositorio.getFunciones().stream()
                    .filter(f -> f.getSala().getId().equals(idSala) && !f.estaCancelada()
                            && f.getFechaHora().isAfter(tiempo.ahora()))
                    .anyMatch(f -> f.getAsientosOcupados().contains(codigoAsiento));
            if (vendido) {
                throw new OperacionNoPermitidaException("El asiento " + codigoAsiento
                        + " está vendido en una función futura; no se puede deshabilitar.");
            }
        }
        sala.buscarAsiento(codigoAsiento).setHabilitado(habilitado);
        repositorio.guardar();
    }


    @Override
    public List<Funcion> listarFunciones() {
        return repositorio.getFunciones().stream().sorted(Comparator.comparing(Funcion::getFechaHora)).toList();
    }

    @Override
    public Funcion programarFuncion(String idPelicula, String idSala, LocalDateTime fechaHora, double precioBase) {
        Pelicula pelicula = buscarPelicula(idPelicula);
        Sala sala = buscarSala(idSala);
        if (fechaHora == null || !fechaHora.isAfter(tiempo.ahora())) {
            throw new DatosInvalidosException("La función debe programarse en una fecha y hora futura.");
        }
        if (precioBase < 5000 || precioBase > 100000) {
            throw new DatosInvalidosException("El precio base debe estar entre $5.000 y $100.000.");
        }
        if (!pelicula.isEnCartelera()) throw new OperacionNoPermitidaException("La película no está en cartelera.");
        if (!sala.isActiva()) throw new OperacionNoPermitidaException("La sala no está activa.");
        Funcion funcion = new Funcion(repositorio.siguienteId("F"), pelicula, sala, fechaHora, precioBase);
        validarSolapamiento(funcion);
        repositorio.getFunciones().add(funcion);
        repositorio.guardar();
        return funcion;
    }


    @Override
    public List<Funcion> replicarFuncion(String idFuncion, int dias) {
        if (dias < 1 || dias > 30) throw new DatosInvalidosException("Se puede replicar entre 1 y 30 días.");
        Funcion original = buscarFuncion(idFuncion);
        List<Funcion> creadas = new ArrayList<>();
        for (int i = 1; i <= dias; i++) {
            Funcion copia = original.clonar();
            copia.setFechaHora(original.getFechaHora().plusDays(i));
            if (!copia.getFechaHora().isAfter(tiempo.ahora())) continue;
            try {
                validarSolapamiento(copia);
            } catch (FuncionSolapadaException e) {
                continue; // ese día la sala ya está ocupada
            }
            copia.setId(repositorio.siguienteId("F"));
            repositorio.getFunciones().add(copia);
            creadas.add(copia);
        }
        repositorio.guardar();
        return creadas;
    }

    private void validarSolapamiento(Funcion nueva) {
        int limpieza = ConfiguracionCine.getInstance().getMinutosLimpieza();
        for (Funcion f : repositorio.getFunciones()) {
            if (f.estaCancelada() || !f.getSala().getId().equals(nueva.getSala().getId())) continue;
            LocalDateTime inicioA = nueva.getFechaHora();
            LocalDateTime finA = nueva.getFechaHoraFin().plusMinutes(limpieza);
            LocalDateTime inicioB = f.getFechaHora();
            LocalDateTime finB = f.getFechaHoraFin().plusMinutes(limpieza);
            if (inicioA.isBefore(finB) && inicioB.isBefore(finA)) {
                throw new FuncionSolapadaException("La " + nueva.getSala().getNombre() + " está ocupada: se cruza con "
                        + f.getPelicula().getTitulo() + " (" + f.getFechaHora().toLocalTime() + " - "
                        + f.getFechaHoraFin().toLocalTime() + " + " + limpieza + " min de limpieza).");
            }
        }
    }

    @Override
    public int cancelarFuncion(String idFuncion) {
        Funcion funcion = buscarFuncion(idFuncion);
        if (funcion.estaCancelada()) throw new OperacionNoPermitidaException("La función ya estaba cancelada.");
        if (!funcion.getFechaHora().isAfter(tiempo.ahora())) {
            throw new OperacionNoPermitidaException("No se puede cancelar una función que ya comenzó.");
        }
        funcion.setEstado(EstadoFuncion.CANCELADA);
        List<Compra> afectadas = repositorio.getCompras().stream()
                .filter(c -> c.getFuncion().getId().equals(idFuncion) && c.permiteCancelacion()).toList();
        for (Compra c : afectadas) {
            compras.reembolsarPorFuncionCancelada(c);
            eventos.notificar(new EventoCine(TipoEvento.FUNCION_CANCELADA, tiempo.ahora(), c.getCliente(),
                    "La función de " + funcion.getPelicula().getTitulo() + " del " + funcion.getFechaHora().toLocalDate()
                            + " a las " + funcion.getFechaHora().toLocalTime() + " fue cancelada por el cine. "
                            + "Te devolvimos el 100% de tu compra " + c.getId() + "."));
        }
        repositorio.guardar();
        return afectadas.size();
    }


    @Override
    public List<ComponenteConfiteria> listarConfiteria() {
        return List.copyOf(repositorio.getProductos());
    }

    @Override
    public ComponenteConfiteria crearProducto(String nombre, CategoriaProducto categoria, double precio, int puntos) {
        if (nombre == null || nombre.isBlank() || categoria == null || categoria == CategoriaProducto.COMBO) {
            throw new DatosInvalidosException("Nombre y categoría (distinta de Combo) son obligatorios.");
        }
        validarPrecioYPuntos(precio, puntos);
        Producto p = new Producto(repositorio.siguienteId("PR"), nombre.trim(), categoria, precio, puntos);
        repositorio.getProductos().add(p);
        repositorio.guardar();
        return p;
    }

    @Override
    public ComponenteConfiteria crearCombo(String nombre, List<String> idsComponentes, double descuento, int puntos) {
        if (nombre == null || nombre.isBlank()) throw new DatosInvalidosException("El nombre del combo es obligatorio.");
        if (idsComponentes == null || idsComponentes.size() < 2) {
            throw new DatosInvalidosException("Un combo debe tener al menos 2 productos.");
        }
        validarPrecioYPuntos(1, puntos);
        Combo combo = new Combo(repositorio.siguienteId("CB"), nombre.trim(), descuento, puntos);
        for (String id : idsComponentes) {
            combo.agregar(buscarProducto(id));
        }
        repositorio.getProductos().add(combo);
        repositorio.guardar();
        return combo;
    }

    @Override
    public void actualizarProducto(String id, String nombre, double precio, int puntos, boolean disponible) {
        ComponenteConfiteria c = buscarProducto(id);
        if (nombre == null || nombre.isBlank()) throw new DatosInvalidosException("El nombre es obligatorio.");
        validarPrecioYPuntos(c instanceof Producto ? precio : 1, puntos);
        c.setNombre(nombre.trim());
        c.setPuntosRedencion(puntos);
        c.setDisponible(disponible);
        if (c instanceof Producto p) p.setPrecio(precio);
        repositorio.guardar();
    }

    private void validarPrecioYPuntos(double precio, int puntos) {
        if (precio <= 0) throw new DatosInvalidosException("El precio debe ser mayor que cero.");
        if (puntos < 1) throw new DatosInvalidosException("Los puntos de redención deben ser al menos 1.");
    }


    @Override
    public List<Promocion> listarPromociones() {
        return List.copyOf(repositorio.getPromociones());
    }

    @Override
    public Promocion crearPromocion(String nombre, AplicacionPromocion aplicaA, double porcentaje, DayOfWeek dia,
                                    LocalDate inicio, LocalDate fin) {
        if (nombre == null || nombre.isBlank() || aplicaA == null || inicio == null || fin == null) {
            throw new DatosInvalidosException("Nombre, aplicación y fechas de la promoción son obligatorios.");
        }
        Promocion p = new Promocion(repositorio.siguienteId("PM"), nombre.trim(), aplicaA, porcentaje, dia, inicio, fin);
        repositorio.getPromociones().add(p);
        repositorio.guardar();
        return p;
    }

    @Override
    public void cambiarEstadoPromocion(String id, boolean activa) {
        repositorio.getPromociones().stream().filter(p -> p.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe la promoción " + id))
                .setActiva(activa);
        repositorio.guardar();
    }


    @Override
    public List<Cliente> listarClientes() {
        return repositorio.getClientes();
    }

    @Override
    public List<Compra> listarCompras() {
        return repositorio.getCompras().stream().sorted(Comparator.comparing(Compra::getFechaCompra).reversed()).toList();
    }

    @Override
    public void cambiarEstadoTarjeta(String idCliente, EstadoTarjeta estado) {
        Cliente cliente = repositorio.getClientes().stream().filter(c -> c.getId().equals(idCliente)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el cliente " + idCliente));
        cliente.getTarjeta().setEstado(estado);
        repositorio.guardar();
    }

    @Override
    public List<SolicitudRecarga> listarSolicitudesRecarga() {
        return repositorio.getSolicitudesRecarga().stream()
                .sorted(Comparator.comparing(SolicitudRecarga::getFechaSolicitud).reversed()).toList();
    }

    @Override
    public void aprobarSolicitudRecarga(String idSolicitud) {
        recargas.aprobarSolicitud(idSolicitud);
    }

    @Override
    public void rechazarSolicitudRecarga(String idSolicitud, String motivo) {
        recargas.rechazarSolicitud(idSolicitud, motivo);
    }

    @Override
    public void recargarTarjeta(String idCliente, double monto) {
        recargas.recargarDirectamente(idCliente, monto);
    }


    private Pelicula buscarPelicula(String id) {
        return repositorio.getPeliculas().stream().filter(p -> p.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe la película " + id));
    }

    private Sala buscarSala(String id) {
        return repositorio.getSalas().stream().filter(s -> s.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe la sala " + id));
    }

    private Funcion buscarFuncion(String id) {
        return repositorio.getFunciones().stream().filter(f -> f.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe la función " + id));
    }

    private ComponenteConfiteria buscarProducto(String id) {
        return repositorio.getProductos().stream().filter(p -> p.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el producto " + id));
    }
}
