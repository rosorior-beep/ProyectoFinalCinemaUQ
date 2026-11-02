package co.edu.uniquindio.cinemauq.controlador;

import co.edu.uniquindio.cinemauq.fachada.CinemaFacade;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Rol;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;
import co.edu.uniquindio.cinemauq.vista.Navegador;

public class LoginControlador {

    private final CinemaFacade fachada;
    private final Navegador navegador;

    public LoginControlador(CinemaFacade fachada, Navegador navegador) {
        this.fachada = fachada;
        this.navegador = navegador;
    }

    public void iniciarSesion(String email, String password) {
        Usuario usuario = fachada.iniciarSesion(email, password);
        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            navegador.mostrarPanelAdmin();
        } else {
            navegador.mostrarPanelCliente();
        }
    }

    public void registrar(String nombre, String documento, String email, String telefono,
                          String password, String confirmacion) {
        fachada.registrarCliente(nombre, documento, email, telefono, password, confirmacion);
        fachada.iniciarSesion(email, password);
        navegador.mostrarPanelCliente();
    }

    public void irARegistro() {
        navegador.mostrarRegistro();
    }

    public void irALogin() {
        navegador.mostrarLogin();
    }
}

