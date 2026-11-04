package co.edu.uniquindio.cinemauq.servicio.estrategias;

import co.edu.uniquindio.cinemauq.modelo.compra.Compra;

import java.time.LocalDateTime;

public class ReembolsoFuncionCanceladaStrategy implements EstrategiaReembolso {

    @Override
    public double calcularPorcentaje(Compra compra, LocalDateTime ahora) {
        return 100;
    }

    @Override
    public String getDescripcion() {
        return "Función cancelada por el cine (100%)";
    }
}
