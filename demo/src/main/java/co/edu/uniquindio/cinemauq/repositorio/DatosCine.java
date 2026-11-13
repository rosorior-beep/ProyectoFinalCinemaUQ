package co.edu.uniquindio.cinemauq.repositorio;

import co.edu.uniquindio.cinemauq.modelo.cine.Funcion;
import co.edu.uniquindio.cinemauq.modelo.cine.Pelicula;
import co.edu.uniquindio.cinemauq.modelo.cine.Sala;
import co.edu.uniquindio.cinemauq.modelo.compra.Compra;
import co.edu.uniquindio.cinemauq.modelo.confiteria.ComponenteConfiteria;
import co.edu.uniquindio.cinemauq.modelo.promociones.Promocion;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatosCine implements Serializable {
    final List<Usuario> usuarios = new ArrayList<>();
    final List<Pelicula> peliculas = new ArrayList<>();
    final List<Sala> salas = new ArrayList<>();
    final List<Funcion> funciones = new ArrayList<>();
    final List<ComponenteConfiteria> productos = new ArrayList<>();
    final List<Promocion> promociones = new ArrayList<>();
    final List<Compra> compras = new ArrayList<>();
    final List<SolicitudRecarga> solicitudes = new ArrayList<>();
    final Map<String, Integer> consecutivos = new HashMap<>();
}
