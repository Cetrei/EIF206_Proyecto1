package cr.ac.una.reservas.model;

/**
 * Fila del resultado de EstadisticaService.recursosReservadosEnPeriodo
 * (ver docs/02_service.md): una categoria y cuantas veces se reservo
 * un recurso de ella en el periodo consultado. Objeto de transporte
 * simple, sin logica de negocio, igual que ResultadoReserva.
 */
public final class EstadisticaCategoria {
    private final Categoria categoria;
    private final long cantidad;

    public EstadisticaCategoria(Categoria categoria, long cantidad) {
        this.categoria = categoria;
        this.cantidad = cantidad;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public long getCantidad() {
        return cantidad;
    }
}
