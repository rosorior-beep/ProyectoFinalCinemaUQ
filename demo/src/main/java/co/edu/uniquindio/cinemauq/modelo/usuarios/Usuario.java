package co.edu.uniquindio.cinemauq.modelo.usuarios;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Usuario implements Serializable {

    private final String id;
    private String nombre;
    private final String documento;
    private String email;
    private String password;
    private final List<Notificacion> notificaciones = new ArrayList<>();

    protected Usuario(String id, String nombre, String documento, String email, String password) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.email = email;
        this.password = password;
    }

    public abstract Rol getRol();

    public boolean verificarPassword(String intento) {
        return password != null && password.equals(intento);
    }

    public void agregarNotificacion(Notificacion n) {
        notificaciones.add(0, n);
    }

    public long getNotificacionesSinLeer() {
        return notificaciones.stream().filter(n -> !n.isLeida()).count();
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDocumento() { return documento; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Notificacion> getNotificaciones() { return notificaciones; }

    @Override
    public String toString() {
        return nombre + " (" + email + ")";
    }
}
