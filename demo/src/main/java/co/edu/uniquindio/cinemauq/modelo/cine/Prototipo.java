package co.edu.uniquindio.cinemauq.modelo.cine;

public interface Prototipo<T extends Prototipo<T>> {
    T clonar();
}
