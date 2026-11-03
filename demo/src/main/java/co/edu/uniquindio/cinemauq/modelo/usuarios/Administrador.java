package co.edu.uniquindio.cinemauq.modelo.usuarios;

public class Administrador extends Usuario {

    public Administrador(String id, String nombre, String documento, String email, String password) {
        super(id, nombre, documento, email, password);
    }

    @Override
    public Rol getRol() {
        return Rol.ADMINISTRADOR;
    }
}
