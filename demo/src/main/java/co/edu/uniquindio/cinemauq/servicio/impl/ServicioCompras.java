package co.edu.uniquindio.cinemauq.servicio.impl;

import co.edu.uniquindio.cinemauq.excepciones.AsientoNoDisponibleException;
import co.edu.uniquindio.cinemauq.excepciones.CancelacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.EntidadNoEncontradaException;
import co.edu.uniquindio.cinemauq.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.modelo.asientos.Asiento;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Cobrable;
import co.edu.uniquindio.cinemauq.modelo.compra.items.ConDescuentoPromocion;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Entrada;
import co.edu.uniquindio.cinemauq.modelo.compra.items.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.compra.items.PagadoConPuntos;
import co.edu.uniquindio.cinemauq.modelo.config.ConfiguracionCine;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.promociones.Promocion;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.IServicioCompras;
import co.edu.uniquindio.cinemauq.servicio.IServicioPagos;
import co.edu.uniquindio.cinemauq.servicio.IServicioPuntos;
import co.edu.uniquindio.cinemauq.servicio.dto.PedidoCompra;
import co.edu.uniquindio.cinemauq.servicio.estrategias.CalculadoraReembolso;
import co.edu.uniquindio.cinemauq.servicio.estrategias.ReembolsoFuncionCanceladaStrategy;
import co.edu.uniquindio.cinemauq.servicio.estrategias.ReembolsoPorAnticipacionStrategy;
import co.edu.uniquindio.cinemauq.servicio.eventos.EventoCine;
import co.edu.uniquindio.cinemauq.servicio.eventos.GestorEventos;
import co.edu.uniquindio.cinemauq.servicio.eventos.TipoEvento;
import co.edu.uniquindio.cinemauq.servicio.tiempo.ProveedorTiempo;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class ServicioCompras implements IServicioCompras {

    private final IRepositorioCine repositorio;
    private final IServicioPagos pagos;
    private final IServicioPuntos puntos;
    private final GestorEventos eventos;
    private final ProveedorTiempo tiempo;
    private final CalculadoraReembolso calculadoraReembolso =
            new CalculadoraReembolso(new ReembolsoPorAnticipacionStrategy());

    public ServicioCompras(IRepositorioCine repositorio, IServicioPagos pagos, IServicioPuntos puntos,
                           GestorEventos eventos, ProveedorTiempo tiempo) {
        this.repositorio = repositorio;
        this.pagos = pagos;
        this.puntos = puntos;
        this.eventos = eventos;
        this.tiempo = tiempo;
    }


    @Override
    public List<Pelicula> peliculasEnCartelera() {
        return repositorio.getPeliculas().stream()
                .filter(Pelicula::isEnCartelera)
                .filter(p -> !funcionesDisponibles(p).isEmpty())
                .toList();
    }

    @Override
    public List<Funcion> funcionesDisponibles(Pelicula pelicula) {
        return repositorio.getFunciones().stream()
                .filter(f -> f.getPelicula().getId().equals(pelicula.getId()))
                .filter(f -> !f.estaCancelada() && f.getFechaHora().isAfter(tiempo.ahora()))
                .sorted(Comparator.comparing(Funcion::getFechaHora))
                .toList();
    }

    @Override
    public List<ComponenteConfiteria> confiteriaDisponible() {
        return repositorio.getProductos().stream().filter(ComponenteConfiteria::isDisponible).toList();
    }


    @Override
    public Compra cotizar(PedidoCompra pedido) {
        Funcion funcion = pedido.funcion();
        if (funcion == null) throw new DatosInvalidosException("Selecciona una función.");
        if (funcion.estaCancelada()) throw new OperacionNoPermitidaException("La función fue cancelada.");
        if (!funcion.getFechaHora().isAfter(tiempo.ahora())) {
            throw new OperacionNoPermitidaException("La función ya comenzó; no se pueden comprar entradas.");
        }
        List<String> asientos = pedido.asientos() == null ? List.of() : pedido.asientos();
        Map<String, Integer> productos = pedido.productos() == null ? Map.of() : pedido.productos();
        Set<String> asientosConPuntos = pedido.asientosConPuntos() == null ? Set.of() : pedido.asientosConPuntos();
        Set<String> productosConPuntos = pedido.productosConPuntos() == null ? Set.of() : pedido.productosConPuntos();

        int maximo = ConfiguracionCine.getInstance().getMaxEntradasPorCompra();
        if (asientos.size() > maximo) {
            throw new DatosInvalidosException("Máximo " + maximo + " entradas por compra.");
        }
        if (new HashSet<>(asientos).size() != asientos.size()) {
            throw new DatosInvalidosException("Hay asientos repetidos en la selección.");
        }

        LocalDate fechaFuncion = funcion.getFechaHora().toLocalDate();
        Compra.Builder builder = new Compra.Builder()
                .cliente(pedido.cliente())
                .funcion(funcion)
                .fecha(tiempo.ahora());

        for (String codigo : asientos) {
            Asiento asiento = funcion.getSala().buscarAsiento(codigo);
            if (!funcion.estaDisponible(codigo)) {
                throw new AsientoNoDisponibleException("El asiento " + codigo + " ya no está disponible.");
            }
            Cobrable entrada = new Entrada(funcion, asiento, ConfiguracionCine.getInstance().getPuntosPorEntrada());
            builder.agregarItem(decorar(entrada, asientosConPuntos.contains(codigo), fechaFuncion));
        }

        for (Map.Entry<String, Integer> e : productos.entrySet()) {
            if (e.getValue() == null || e.getValue() <= 0) continue;
            ComponenteConfiteria componente = buscarProducto(e.getKey());
            if (!componente.isDisponible()) {
                throw new OperacionNoPermitidaException(componente.getNombre() + " no está disponible.");
            }
            Cobrable item = new ItemConfiteria(componente, e.getValue());
            builder.agregarItem(decorar(item, productosConPuntos.contains(e.getKey()), fechaFuncion));
        }
        return builder.build();
    }


    private Cobrable decorar(Cobrable item, boolean conPuntos, LocalDate fechaFuncion) {
        if (conPuntos) {
            return new PagadoConPuntos(item);
        }
        Optional<Promocion> mejor = repositorio.getPromociones().stream()
                .filter(p -> p.esAplicable(item.esEntrada(), fechaFuncion))
                .max(Comparator.comparingDouble(Promocion::getPorcentaje));
        return mejor.<Cobrable>map(p -> new ConDescuentoPromocion(item, p)).orElse(item);
    }

    @Override
    public Compra comprar(PedidoCompra pedido) {
        Cliente cliente = pedido.cliente();
        Compra previa = cotizar(pedido);

        pagos.validarPago(cliente.getTarjeta(), previa.getTotalAPagar());
        if (previa.getPuntosRedimidos() > 0) {
            puntos.validarRedencion(cliente, previa.getPuntosRedimidos());
        }

        String id = repositorio.siguienteId("C");
        Compra.Builder builder = new Compra.Builder().id(id).cliente(cliente)
                .funcion(previa.getFuncion()).fecha(previa.getFechaCompra());
        previa.getItems().forEach(builder::agregarItem);
        Compra compra = builder.build();

        compra.getEntradas().forEach(e -> compra.getFuncion().ocuparAsiento(e.getCodigoAsiento()));
        String descripcion = "Compra " + id + " - " + compra.getFuncion().getPelicula().getTitulo();
        pagos.cobrar(cliente.getTarjeta(), compra.getTotalAPagar(), descripcion, id);
        if (compra.getPuntosRedimidos() > 0) {
            compra.setConsumosPuntos(puntos.redimir(cliente, compra.getPuntosRedimidos(),
                    "Redención en la compra " + id));
        }
        compra.confirmarPago();
        compra.setPuntosGanados(puntos.acumular(cliente, compra));

        repositorio.getCompras().add(compra);
        eventos.notificar(new EventoCine(TipoEvento.COMPRA_REALIZADA, tiempo.ahora(), cliente,
                String.format("Compra %s confirmada: %d entrada(s) para %s. Pagaste $%,.0f y ganaste %d puntos.",
                        id, compra.getEntradas().size(), compra.getFuncion().getPelicula().getTitulo(),
                        compra.getTotalAPagar(), compra.getPuntosGanados())));
        repositorio.guardar();
        return compra;
    }

    @Override
    public List<Compra> comprasDe(Cliente cliente) {
        return repositorio.getCompras().stream()
                .filter(c -> c.getCliente().getId().equals(cliente.getId()))
                .sorted(Comparator.comparing(Compra::getFechaCompra).reversed())
                .toList();
    }


    @Override
    public double porcentajeReembolsoActual(Compra compra) {
        if (!compra.permiteCancelacion()) return 0;
        calculadoraReembolso.setEstrategia(new ReembolsoPorAnticipacionStrategy());
        return calculadoraReembolso.calcularPorcentaje(compra, tiempo.ahora());
    }

    @Override
    public Compra cancelarPorCliente(Cliente cliente, String idCompra) {
        Compra compra = repositorio.getCompras().stream().filter(c -> c.getId().equals(idCompra)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe la compra " + idCompra));
        if (!compra.getCliente().getId().equals(cliente.getId())) {
            throw new OperacionNoPermitidaException("La compra " + idCompra + " no te pertenece.");
        }

        calculadoraReembolso.setEstrategia(new ReembolsoPorAnticipacionStrategy());
        double porcentaje = compra.permiteCancelacion()
                ? calculadoraReembolso.calcularPorcentaje(compra, tiempo.ahora()) : 100;
        if (porcentaje == 0) {
            throw new CancelacionNoPermitidaException(
                    "No se puede cancelar: faltan menos de 2 horas para la función o ya pasó.");
        }
        ejecutarCancelacion(compra, porcentaje, "Cancelada por el cliente");
        return compra;
    }

    @Override
    public Compra reembolsarPorFuncionCancelada(Compra compra) {
        calculadoraReembolso.setEstrategia(new ReembolsoFuncionCanceladaStrategy());
        double porcentaje = calculadoraReembolso.calcularPorcentaje(compra, tiempo.ahora());
        ejecutarCancelacion(compra, porcentaje, "Función cancelada por el cine");
        return compra;
    }

    private void ejecutarCancelacion(Compra compra, double porcentaje, String motivo) {
        double monto = Math.round(compra.getTotalAPagar() * porcentaje / 100.0);
        compra.cancelar(monto, porcentaje, motivo, tiempo.ahora());

        Cliente cliente = compra.getCliente();
        if (monto > 0) {
            pagos.reembolsar(cliente.getTarjeta(), monto,
                    "Reembolso " + (int) porcentaje + "% de la compra " + compra.getId(), compra.getId());
        }
        puntos.revertirCompra(cliente, compra);
        compra.getEntradas().forEach(e -> compra.getFuncion().liberarAsiento(e.getCodigoAsiento()));

        eventos.notificar(new EventoCine(TipoEvento.COMPRA_CANCELADA, tiempo.ahora(), cliente,
                String.format("Compra %s cancelada (%s). Se reembolsaron $%,.0f (%d%%) a tu tarjeta.",
                        compra.getId(), motivo, monto, (int) porcentaje)));
        repositorio.guardar();
    }

    private ComponenteConfiteria buscarProducto(String id) {
        return repositorio.getProductos().stream().filter(p -> p.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe el producto " + id));
    }
}
