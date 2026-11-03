package co.edu.uniquindio.cinemauq.modelo.puntos;

import java.io.Serializable;

public record ConsumoPuntos(String loteId, int cantidad) implements Serializable {
}