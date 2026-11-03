package co.edu.uniquindio.cinemauq.modelo.cine;

import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.EntidadNoEncontradaException;
import co.edu.uniquindio.cinemauq.modelo.asientos.Asiento;
import co.edu.uniquindio.cinemauq.modelo.asientos.FabricaAsiento;
import co.edu.uniquindio.cinemauq.modelo.asientos.TipoAsiento;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


public class Sala implements Serializable {

    public static final int MAX_FILAS = 15;
    public static final int MAX_COLUMNAS = 20;

    private final String id;
    private String nombre;
    private TipoSala tipo;
    private final int filas;
    private final int columnas;
    private final List<Asiento> asientos = new ArrayList<>();
    private boolean activa = true;


    public Sala(String id, String nombre, TipoSala tipo, int filas, int columnas,
                String filasPreferenciales, Set<String> asientosAccesibles) {
        if (filas < 1 || filas > MAX_FILAS || columnas < 1 || columnas > MAX_COLUMNAS) {
            throw new DatosInvalidosException("Una sala debe tener entre 1 y " + MAX_FILAS
                    + " filas y entre 1 y " + MAX_COLUMNAS + " columnas.");
        }
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.filas = filas;
        this.columnas = columnas;
        String preferenciales = filasPreferenciales == null ? "" : filasPreferenciales.toUpperCase();
        for (int f = 0; f < filas; f++) {
            char letra = (char) ('A' + f);
            for (int c = 1; c <= columnas; c++) {
                String codigo = "" + letra + c;
                TipoAsiento tipoAsiento = TipoAsiento.GENERAL;
                if (asientosAccesibles != null && asientosAccesibles.contains(codigo)) {
                    tipoAsiento = TipoAsiento.ACCESIBLE;
                } else if (preferenciales.indexOf(letra) >= 0) {
                    tipoAsiento = TipoAsiento.PREFERENCIAL;
                }
                asientos.add(FabricaAsiento.para(tipoAsiento).crearAsiento(letra, c));
            }
        }
    }

    public Asiento buscarAsiento(String codigo) {
        return asientos.stream().filter(a -> a.getCodigo().equalsIgnoreCase(codigo)).findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("El asiento " + codigo
                        + " no existe en la " + nombre));
    }

    /** Cambia el tipo de un asiento usando la fábrica correspondiente (Factory Method). */
    public void cambiarTipoAsiento(String codigo, TipoAsiento nuevoTipo) {
        Asiento actual = buscarAsiento(codigo);
        if (actual.getTipo() == nuevoTipo) return;
        Asiento nuevo = FabricaAsiento.para(nuevoTipo).crearAsiento(actual.getFila(), actual.getNumero());
        nuevo.setHabilitado(actual.isHabilitado());
        asientos.set(asientos.indexOf(actual), nuevo);
    }

    public long getCapacidad() {
        return asientos.stream().filter(Asiento::isHabilitado).count();
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoSala getTipo() { return tipo; }
    public void setTipo(TipoSala tipo) { this.tipo = tipo; }
    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
    public List<Asiento> getAsientos() { return Collections.unmodifiableList(asientos); }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    @Override
    public String toString() { return nombre + " - " + tipo; }
}
