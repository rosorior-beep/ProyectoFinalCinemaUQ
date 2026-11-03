package co.edu.uniquindio.cinemauq.modelo.usuarios;

import co.edu.uniquindio.cinemauq.modelo.puntos.CuentaPuntos;
import co.edu.uniquindio.cinemauq.modelo.tarjeta.TarjetaVirtual;

public class Cliente extends Usuario {

    private String telefono;
    private final TarjetaVirtual tarjeta;
    private final CuentaPuntos cuentaPuntos;

    public Cliente(String id, String nombre, String documento, String email, String telefono,
                   String password, String numeroTarjeta) {
        super(id, nombre, documento, email, password);
        this.telefono = telefono;
        this.tarjeta = new TarjetaVirtual(numeroTarjeta);
        this.cuentaPuntos = new CuentaPuntos();
    }

    @Override
    public Rol getRol() {
        return Rol.CLIENTE;
    }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public TarjetaVirtual getTarjeta() { return tarjeta; }
    public CuentaPuntos getCuentaPuntos() { return cuentaPuntos; }
}
