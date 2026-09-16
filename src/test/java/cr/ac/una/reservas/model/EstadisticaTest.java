package cr.ac.una.reservas.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstadisticaTest {

    @Test
    void laEstadisticaDeCategoriaGuardaCategoriaYCantidad() {
        Categoria categoria = new Categoria("CAT-000001", "Sala para 10 personas");

        EstadisticaCategoria estadistica = new EstadisticaCategoria(categoria, 3L);

        assertEquals(categoria, estadistica.getCategoria());
        assertEquals(3L, estadistica.getCantidad());
    }

    @Test
    void laEstadisticaDeSemanaGuardaElLunesDeInicioYLaCantidad() {
        LocalDate lunes = LocalDate.of(2026, 8, 3);

        EstadisticaSemana estadistica = new EstadisticaSemana(lunes, 2L);

        assertEquals(lunes, estadistica.getInicioSemana());
        assertEquals(2L, estadistica.getCantidad());
    }
}
