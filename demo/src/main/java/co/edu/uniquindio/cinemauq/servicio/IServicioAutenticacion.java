package co.edu.uniquindio.cinemauq.servicio;

import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;

public interface IServicioAutenticacion {

    Usuario iniciarSesion(String email, String password);

    Cliente registrarCliente(String nombre, String documento, String email, String telefono,
                             String password, String confirmacion);
}
