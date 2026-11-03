package co.edu.uniquindio.cinemauq.modelo.compra;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.compra.estado.EstadoCompra;
import co.edu.uniquindio.cinemauq.modelo.compra.estado.EstadoPendiente;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Cobrable;
import co.edu.uniquindio.cinemauq.modelo.compra.items.Entrada;
import co.edu.uniquindio.cinemauq.modelo.compra.items.ItemConfiteria;
import co.edu.uniquindio.cinemauq.modelo.puntos.ConsumoPuntos;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Compra implements Serializable {

    private final String id;
    private final Cliente cliente;
    private final Funcion funcion;
    private final List<Cobrable> items;
    private final LocalDateTime fechaCompra;
    private final double subtotal;
    private final double totalAPagar;
    private final int puntosRedimidos;
    private int puntosGanados;
    private List<ConsumoPuntos> consumosPuntos = new ArrayList<>();
    private EstadoCompra estado = new EstadoPendiente();


    private double montoReembolsado;
    private double porcentajeReembolso;
    private String motivoCancelacion;
    private LocalDateTime fechaCancelacion;

    private Compra(Builder b) {
        this.id = b.id;
        this.cliente = b.cliente;
        this.funcion = b.funcion;
        this.items = new ArrayList<>(b.items);
        this.fechaCompra = b.fechaCompra;
        this.subtotal = items.stream().mapToDouble(Cobrable::getPrecioBase).sum();
        this.totalAPagar = items.stream().mapToDouble(Cobrable::getPrecio).sum();
        this.puntosRedimidos = items.stream().mapToInt(Cobrable::getPuntosUsados).sum();
    }

    // ---------------------- Transiciones (delegan en el State) ----------------------

    public void confirmarPago() {
        estado.confirmarPago(this);
    }

    public void cancelar(double montoReembolso, double porcentaje, String motivo, LocalDateTime fecha) {
        estado.cancelar(this, montoReembolso, porcentaje, motivo, fecha);
    }

    public boolean permiteCancelacion() {
        return estado.permiteCancelacion();
    }


    public void cambiarEstado(EstadoCompra nuevo) {
        this.estado = nuevo;
    }

    public void registrarCancelacion(double monto, double porcentaje, String motivo, LocalDateTime fecha) {
        this.montoReembolsado = monto;
        this.porcentajeReembolso = porcentaje;
        this.motivoCancelacion = motivo;
        this.fechaCancelacion = fecha;
    }

    // ---------------------- Consultas ----------------------

    public List<Entrada> getEntradas() {
        return items.stream().map(Cobrable::getItemBase)
                .filter(i -> i instanceof Entrada).map(i -> (Entrada) i).toList();
    }

    public List<ItemConfiteria> getItemsConfiteria() {
        return items.stream().map(Cobrable::getItemBase)
                .filter(i -> i instanceof ItemConfiteria).map(i -> (ItemConfiteria) i).toList();
    }

    public double getDescuentos() {
        double sinPuntos = items.stream().filter(i -> i.getPuntosUsados() == 0)
                .mapToDouble(i -> i.getPrecioBase() - i.getPrecio()).sum();
        return Math.max(0, sinPuntos);
    }

    public double getValorRedimidoConPuntos() {
        return items.stream().filter(i -> i.getPuntosUsados() > 0).mapToDouble(Cobrable::getPrecioBase).sum();
    }

    public double getTotalEntradas() {
        return items.stream().filter(Cobrable::esEntrada).mapToDouble(Cobrable::getPrecio).sum();
    }

    public double getTotalConfiteria() {
        return items.stream().filter(i -> !i.esEntrada()).mapToDouble(Cobrable::getPrecio).sum();
    }

    public boolean estaCancelada() {
        return motivoCancelacion != null;
    }

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Funcion getFuncion() { return funcion; }
    public List<Cobrable> getItems() { return Collections.unmodifiableList(items); }
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public double getSubtotal() { return subtotal; }
    public double getTotalAPagar() { return totalAPagar; }
    public int getPuntosRedimidos() { return puntosRedimidos; }
    public int getPuntosGanados() { return puntosGanados; }
    public void setPuntosGanados(int puntosGanados) { this.puntosGanados = puntosGanados; }
    public List<ConsumoPuntos> getConsumosPuntos() { return consumosPuntos; }
    public void setConsumosPuntos(List<ConsumoPuntos> consumos) { this.consumosPuntos = new ArrayList<>(consumos); }
    public EstadoCompra getEstado() { return estado; }
    public String getNombreEstado() { return estado.getNombre(); }
    public double getMontoReembolsado() { return montoReembolsado; }
    public double getPorcentajeReembolso() { return porcentajeReembolso; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public LocalDateTime getFechaCancelacion() { return fechaCancelacion; }


    public static class Builder {
        private String id;
        private Cliente cliente;
        private Funcion funcion;
        private final List<Cobrable> items = new ArrayList<>();
        private LocalDateTime fechaCompra;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder cliente(Cliente cliente) {
            this.cliente = cliente;
            return this;
        }

        public Builder funcion(Funcion funcion) {
            this.funcion = funcion;
            return this;
        }

        public Builder agregarItem(Cobrable item) {
            this.items.add(item);
            return this;
        }

        public Builder fecha(LocalDateTime fecha) {
            this.fechaCompra = fecha;
            return this;
        }


        public Compra build() {
            if (cliente == null) throw new DatosInvalidosException("La compra debe tener un cliente.");
            if (funcion == null) throw new DatosInvalidosException("La compra debe estar asociada a una función.");
            if (items.isEmpty()) throw new DatosInvalidosException("La compra debe tener al menos una entrada o producto.");
            if (fechaCompra == null) fechaCompra = LocalDateTime.now();
            if (id == null) id = "TEMP";
            return new Compra(this);
        }
    }
}
