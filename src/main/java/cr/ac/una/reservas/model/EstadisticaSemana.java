package cr.ac.una.reservas.model;

import java.time.LocalDate;

public final class EstadisticaSemana {
    private final LocalDate inicioSemana;
    private final long cantidad;

    public EstadisticaSemana(LocalDate inicioSemana, long cantidad) {
        this.inicioSemana = inicioSemana;
        this.cantidad = cantidad;
    }

    public LocalDate getInicioSemana() {
        return inicioSemana;
    }

    public long getCantidad() {
        return cantidad;
    }
}
