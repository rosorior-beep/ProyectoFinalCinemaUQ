package co.edu.uniquindio.cinemauq.servicio.comandos.core;


public interface Comando {

    void ejecutar();

    void deshacer();

    String obtenerNombre();
}
