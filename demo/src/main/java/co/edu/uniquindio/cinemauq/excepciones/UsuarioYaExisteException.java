package co.edu.uniquindio.cinemauq.excepciones;

public class UsuarioYaExisteException extends CinemaUQException {
    public UsuarioYaExisteException(String mensaje) {
        super(mensaje);
    }
}
