package cr.ac.una.reservas.model;

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
