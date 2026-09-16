package cr.ac.una.reservas.presentation.mvc;

import cr.ac.una.reservas.model.DatosNuevaReserva;
import cr.ac.una.reservas.model.EstadoReserva;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.presentation.model.ReservaModel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TabReservasTest {

    @Test
    void arrancaFueraDeModoEdicion() {
        TabReservas vista = new TabReservas(new ReservaModel());

        assertFalse(vista.estaEnModoEdicion());
        assertNull(vista.obtenerReservaEnEdicion());
    }

    @Test
    void seleccionarUnaReservaEntraEnModoEdicion() {
        TabReservas vista = new TabReservas(new ReservaModel());
        Reserva reserva = reservaDeEjemplo();

        vista.mostrarReserva(reserva);

        assertTrue(vista.estaEnModoEdicion());
        assertEquals(reserva, vista.obtenerReservaEnEdicion());
    }

    @Test
    void editarCualquierCampoNoSaleDelModoEdicion() {
        TabReservas vista = new TabReservas(new ReservaModel());
        Reserva reserva = reservaDeEjemplo();
        vista.mostrarReserva(reserva);

        vista.mostrarActividad("Nombre completamente distinto");
        vista.mostrarFecha(LocalDate.of(2026, 9, 1));
        vista.mostrarHoraInicio(LocalTime.of(14, 0));
        vista.mostrarHoraFin(LocalTime.of(16, 0));

        assertTrue(vista.estaEnModoEdicion());
        assertEquals(reserva, vista.obtenerReservaEnEdicion());
    }

    @Test
    void seleccionarOtraReservaReemplazaLaEdicionActual() {
        TabReservas vista = new TabReservas(new ReservaModel());
        Reserva primera = reservaDeEjemplo();
        Reserva segunda = new Reserva(new DatosNuevaReserva(
                "RES-000002", "222", "Otra actividad",
                LocalDate.of(2026, 8, 6), LocalTime.of(10, 0), LocalTime.of(11, 0), List.of("CAT-000002")
        ));

        vista.mostrarReserva(primera);
        vista.mostrarReserva(segunda);

        assertEquals(segunda, vista.obtenerReservaEnEdicion());
    }

    @Test
    void limpiarFormularioSaleDelModoEdicion() {
        TabReservas vista = new TabReservas(new ReservaModel());
        vista.mostrarReserva(reservaDeEjemplo());

        vista.limpiarFormulario();

        assertFalse(vista.estaEnModoEdicion());
        assertNull(vista.obtenerReservaEnEdicion());
    }

    @Test
    void salirDeModoEdicionExplicitoLimpiaLaReferencia() {
        TabReservas vista = new TabReservas(new ReservaModel());
        vista.mostrarReserva(reservaDeEjemplo());

        vista.salirDeModoEdicion();

        assertFalse(vista.estaEnModoEdicion());
        assertNull(vista.obtenerReservaEnEdicion());
    }

    @Test
    void unaReservaCanceladaTambienEntraEnModoEdicionPorSerSeleccionExplicita() {
        TabReservas vista = new TabReservas(new ReservaModel());
        Reserva reserva = reservaDeEjemplo();
        reserva.setEstado(EstadoReserva.CANCELADA);

        vista.mostrarReserva(reserva);

        assertTrue(vista.estaEnModoEdicion());
        assertEquals(reserva, vista.obtenerReservaEnEdicion());
    }

    private static Reserva reservaDeEjemplo() {
        return new Reserva(new DatosNuevaReserva(
                "RES-000001",
                "111",
                "Sesion de Junta Directiva",
                LocalDate.of(2026, 8, 5),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                List.of("CAT-000001")
        ));
    }
}
