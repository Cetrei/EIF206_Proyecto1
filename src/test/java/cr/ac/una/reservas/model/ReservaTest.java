package cr.ac.una.reservas.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaTest {

    @Test
    void construirReservaAsignaEstadoActivaPorDefecto() {
        Reserva reserva = new Reserva(
                "RES-000001",
                "111",
                "Reunion de trabajo",
                LocalDate.of(2026, 8, 14),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                List.of("CAT-000001", "CAT-000002")
        );

        assertEquals(EstadoReserva.ACTIVA, reserva.getEstado());
        assertEquals("RES-000001", reserva.getId());
        assertTrue(reserva.getIdsCategoriasRequeridas().contains("CAT-000001"));
    }

    @Test
    void objetosCompletosTransientEmpiezanVacios() {
        Reserva reserva = new Reserva(
                "RES-000002",
                "111",
                "Charla tecnica",
                LocalDate.of(2026, 8, 20),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                List.of("CAT-000002")
        );

        assertTrue(reserva.getCategoriasRequeridas().isEmpty());
        assertTrue(reserva.getRecursosAsignados().isEmpty());
    }
}
