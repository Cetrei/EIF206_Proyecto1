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
        Reserva reserva = new Reserva(datosDeEjemplo(
                "RES-000001",
                "Reunion de trabajo",
                LocalDate.of(2026, 8, 14),
                List.of("CAT-000001", "CAT-000002")
        ));

        assertEquals(EstadoReserva.ACTIVA, reserva.getEstado());
        assertEquals("RES-000001", reserva.getId());
        assertTrue(reserva.getIdsCategoriasRequeridas().contains("CAT-000001"));
    }

    @Test
    void objetosCompletosTransientEmpiezanVacios() {
        Reserva reserva = new Reserva(datosDeEjemplo(
                "RES-000002",
                "Charla tecnica",
                LocalDate.of(2026, 8, 20),
                List.of("CAT-000002")
        ));

        assertTrue(reserva.getCategoriasRequeridas().isEmpty());
        assertTrue(reserva.getRecursosAsignados().isEmpty());
    }

    private DatosNuevaReserva datosDeEjemplo(String id, String actividad, LocalDate fecha, List<String> idsCategorias) {
        DatosNuevaReserva datos = new DatosNuevaReserva();
        datos.setId(id);
        datos.setIdFuncionario("111");
        datos.setActividad(actividad);
        datos.setFecha(fecha);
        datos.setHoraInicio(LocalTime.of(8, 0));
        datos.setHoraFin(LocalTime.of(10, 0));
        datos.setIdsCategoriasRequeridas(idsCategorias);
        return datos;
    }
}
