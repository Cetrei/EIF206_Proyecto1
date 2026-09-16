package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.DatosNuevaReserva;
import cr.ac.una.reservas.model.EstadoReserva;
import cr.ac.una.reservas.model.Reserva;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaDaoXmlIT {

    @TempDir
    Path carpetaTemporal;

    @Test
    void conservaFechasHorasYListasAlReleerDesdeElArchivo() {
        Path archivo = carpetaTemporal.resolve("reservas.xml");
        ReservaDaoXml dao = new ReservaDaoXml(archivo.toString());

        Reserva reserva = reservaDeEjemplo("RES-000001", "111", LocalDate.of(2026, 8, 5));
        reserva.setIdsRecursosAsignados(List.of("238715", "34343"));
        dao.guardar(reserva);

        Reserva leida = new ReservaDaoXml(archivo.toString()).buscarPorId("RES-000001").get();

        assertTrue(Files.exists(archivo));
        assertEquals(LocalDate.of(2026, 8, 5), leida.getFecha());
        assertEquals(LocalTime.of(9, 0), leida.getHoraInicio());
        assertEquals(LocalTime.of(11, 0), leida.getHoraFin());
        assertEquals(List.of("CAT-000001"), leida.getIdsCategoriasRequeridas());
        assertEquals(List.of("238715", "34343"), leida.getIdsRecursosAsignados());
        assertEquals(EstadoReserva.ACTIVA, leida.getEstado());
    }

    @Test
    void listaSoloLasReservasDelFuncionarioPedido() {
        Path archivo = carpetaTemporal.resolve("reservas.xml");
        ReservaDaoXml dao = new ReservaDaoXml(archivo.toString());
        dao.guardar(reservaDeEjemplo("RES-000001", "111", LocalDate.of(2026, 8, 5)));
        dao.guardar(reservaDeEjemplo("RES-000002", "222", LocalDate.of(2026, 8, 6)));
        dao.guardar(reservaDeEjemplo("RES-000003", "111", LocalDate.of(2026, 8, 7)));

        assertEquals(2, dao.listarPorFuncionario("111").size());
        assertEquals(1, dao.listarPorFuncionario("222").size());
        assertTrue(dao.listarPorFuncionario("333").isEmpty());
    }

    @Test
    void persisteElCambioDeEstadoAlCancelar() {
        Path archivo = carpetaTemporal.resolve("reservas.xml");
        ReservaDaoXml dao = new ReservaDaoXml(archivo.toString());
        Reserva reserva = reservaDeEjemplo("RES-000001", "111", LocalDate.of(2026, 8, 5));
        dao.guardar(reserva);

        reserva.setEstado(EstadoReserva.CANCELADA);
        dao.guardar(reserva);

        ReservaDaoXml daoRecargado = new ReservaDaoXml(archivo.toString());
        assertEquals(1, daoRecargado.listarTodos().size());
        assertEquals(EstadoReserva.CANCELADA, daoRecargado.buscarPorId("RES-000001").get().getEstado());
    }

    private Reserva reservaDeEjemplo(String id, String idFuncionario, LocalDate fecha) {
        DatosNuevaReserva datos = new DatosNuevaReserva(
                id,
                idFuncionario,
                "Sesion de Junta Directiva",
                fecha,
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                List.of("CAT-000001")
        );
        return new Reserva(datos);
    }
}
