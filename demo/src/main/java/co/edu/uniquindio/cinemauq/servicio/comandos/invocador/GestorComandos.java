package co.edu.uniquindio.cinemauq.servicio.comandos.invocador;

import co.edu.uniquindio.cinemauq.excepciones.OperacionNoPermitidaException;
import co.edu.uniquindio.cinemauq.servicio.comandos.core.Comando;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class GestorComandos {

    private final Stack<Comando> historial = new Stack<>();


    public void ejecutar(Comando comando) {
        comando.ejecutar();
        historial.push(comando);
    }


    public String deshacer() {
        if (historial.isEmpty()) {
            throw new OperacionNoPermitidaException("No hay acciones en el historial para deshacer.");
        }
        Comando ultimo = historial.peek();
        ultimo.deshacer();
        historial.pop();
        return ultimo.obtenerNombre();
    }

    public boolean puedeDeshacer() {
        return !historial.isEmpty();
    }


    public List<String> obtenerHistorial() {
        List<String> nombres = new ArrayList<>();
        for (int i = historial.size() - 1; i >= 0; i--) {
            nombres.add((i + 1) + ". " + historial.get(i).obtenerNombre());
        }
        return nombres;
    }
}
