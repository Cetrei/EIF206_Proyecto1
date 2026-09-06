package cr.ac.una.reservas.model;

import java.time.LocalDate;

/**
 * Fila del resultado de EstadisticaService.actividadesPorSemanaEnPeriodo
 * (ver docs/02_service.md): una semana (representada por su fecha de
 * inicio, lunes) y cuantas actividades se programaron en ella dentro
 * del periodo consultado.
 */
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
