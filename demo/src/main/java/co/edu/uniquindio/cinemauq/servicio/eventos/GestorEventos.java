package co.edu.uniquindio.cinemauq.servicio.eventos;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class GestorEventos {

    private final Map<TipoEvento, List<ObservadorCine>> observadores = new EnumMap<>(TipoEvento.class);

    public GestorEventos() {
        for (TipoEvento tipo : TipoEvento.values()) {
            observadores.put(tipo, new ArrayList<>());
        }
    }

    public void suscribir(TipoEvento tipo, ObservadorCine observador) {
        observadores.get(tipo).add(observador);
    }

    public void suscribirATodos(ObservadorCine observador) {
        for (TipoEvento tipo : TipoEvento.values()) {
            suscribir(tipo, observador);
        }
    }

    public void desuscribir(TipoEvento tipo, ObservadorCine observador) {
        observadores.get(tipo).remove(observador);
    }

    public void notificar(EventoCine evento) {
        for (ObservadorCine o : List.copyOf(observadores.get(evento.tipo()))) {
            o.actualizar(evento);
        }
    }
}
