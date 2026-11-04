package co.edu.uniquindio.cinemauq.servicio.impl;

import co.edu.uniquindio.cinemauq.excepciones.CredencialesInvalidasException;
import co.edu.uniquindio.cinemauq.excepciones.DatosInvalidosException;
import co.edu.uniquindio.cinemauq.excepciones.UsuarioYaExisteException;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Cliente;
import co.edu.uniquindio.cinemauq.modelo.usuarios.Usuario;
import co.edu.uniquindio.cinemauq.repositorio.IRepositorioCine;
import co.edu.uniquindio.cinemauq.servicio.IServicioAutenticacion;

import java.util.regex.Pattern;

public class ServicioAutenticacion implements IServicioAutenticacion {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final IRepositorioCine repositorio;

    public ServicioAutenticacion(IRepositorioCine repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Usuario iniciarSesion(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new DatosInvalidosException("Ingresa el correo y la contraseña.");
        }
        return repositorio.buscarUsuarioPorEmail(email)
                .filter(u -> u.verificarPassword(password))
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos."));
    }

    @Override
    public Cliente registrarCliente(String nombre, String documento, String email, String telefono,
                                    String password, String confirmacion) {
        if (vacio(nombre) || vacio(documento) || vacio(email) || vacio(password)) {
            throw new DatosInvalidosException("Nombre, documento, correo y contraseña son obligatorios.");
        }
        if (!EMAIL.matcher(email.trim()).matches()) {
            throw new DatosInvalidosException("El correo no tiene un formato válido.");
        }
        if (!documento.trim().matches("\\d{6,12}")) {
            throw new DatosInvalidosException("El documento debe tener entre 6 y 12 dígitos.");
        }
        if (password.length() < 6) {
            throw new DatosInvalidosException("La contraseña debe tener al menos 6 caracteres.");
        }
        if (!password.equals(confirmacion)) {
            throw new DatosInvalidosException("Las contraseñas no coinciden.");
        }
        if (repositorio.buscarUsuarioPorEmail(email).isPresent()) {
            throw new UsuarioYaExisteException("Ya existe una cuenta con el correo " + email.trim() + ".");
        }
        boolean documentoRepetido = repositorio.getUsuarios().stream()
                .anyMatch(u -> u.getDocumento().equals(documento.trim()));
        if (documentoRepetido) {
            throw new UsuarioYaExisteException("Ya existe una cuenta con el documento " + documento.trim() + ".");
        }
        String id = repositorio.siguienteId("CL");
        String numeroTarjeta = "4571" + String.format("%012d", Math.abs((long) (documento.trim() + id).hashCode()));
        Cliente cliente = new Cliente(id, nombre.trim(), documento.trim(), email.trim().toLowerCase(),
                telefono == null ? "" : telefono.trim(), password, numeroTarjeta);
        repositorio.getUsuarios().add(cliente);
        repositorio.guardar();
        return cliente;
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}
