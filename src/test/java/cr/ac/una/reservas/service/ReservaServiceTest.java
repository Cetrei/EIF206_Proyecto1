package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.DatosNuevaReserva;
import cr.ac.una.reservas.model.EstadoReserva;
import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.model.ResultadoReserva;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaServiceTest {

    private ReservaDaoFalso reservaDao;
    private RecursoDaoFalso recursoDao;
    private CategoriaDaoFalso categoriaDao;
    private ReservaService reservaService;
    private Categoria categoriaSala;

    @BeforeEach
    void prepararService() {
        reservaDao = new ReservaDaoFalso();
        recursoDao = new RecursoDaoFalso();
        categoriaDao = new CategoriaDaoFalso();
        reservaService = new ReservaService(reservaDao, recursoDao, categoriaDao);

        categoriaSala = new Categoria("CAT-000001", "Sala para 10 personas");
        categoriaDao.guardar(categoriaSala);
        recursoDao.guardar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
    }

    @Test
    void reservaConExitoCuandoHayRecursoDisponible() {
        ResultadoReserva resultado = reservaService.intentarReservar(datosDeEjemplo(
                "111", LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));

        assertTrue(resultado.isExitoso());
        assertEquals(EstadoReserva.ACTIVA, resultado.getReserva().getEstado());
        assertTrue(resultado.getReserva().getIdsRecursosAsignados().contains("34343"));
    }

    @Test
    void generaIdConsecutivoParaCadaReservaExitosa() {
        ResultadoReserva primera = reservaService.intentarReservar(datosDeEjemplo(
                "111", LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(9, 0)
        ));

        assertEquals("RES-000001", primera.getReserva().getId());
    }

    @Test
    void fallaCuandoNoHayRecursoDisponibleEnElHorario() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        reservaService.intentarReservar(datosDeEjemplo("111", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0)));

        ResultadoReserva resultado = reservaService.intentarReservar(datosDeEjemplo(
                "222", fecha, LocalTime.of(9, 0), LocalTime.of(11, 0)
        ));

        assertFalse(resultado.isExitoso());
        assertEquals(1, resultado.getCategoriasNoDisponibles().size());
        assertEquals(categoriaSala.getId(), resultado.getCategoriasNoDisponibles().get(0).getId());
    }

    @Test
    void permiteReservarMismaCategoriaEnHorariosQueNoSeSolapan() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        reservaService.intentarReservar(datosDeEjemplo("111", fecha, LocalTime.of(8, 0), LocalTime.of(9, 0)));

        ResultadoReserva resultado = reservaService.intentarReservar(datosDeEjemplo(
                "222", fecha, LocalTime.of(9, 0), LocalTime.of(10, 0)
        ));

        assertTrue(resultado.isExitoso());
    }

    @Test
    void rechazaReservaSinActividad() {
        DatosNuevaReserva datos = datosDeEjemplo("111", LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(10, 0));
        datos.setActividad(" ");

        assertThrows(ReglaDeNegocioException.class, () -> reservaService.intentarReservar(datos));
    }

    @Test
    void rechazaReservaConFechaEnElPasado() {
        DatosNuevaReserva datos = datosDeEjemplo("111", LocalDate.now().minusDays(1), LocalTime.of(8, 0), LocalTime.of(10, 0));

        assertThrows(ReglaDeNegocioException.class, () -> reservaService.intentarReservar(datos));
    }

    @Test
    void rechazaReservaConHoraFinAntesQueHoraInicio() {
        DatosNuevaReserva datos = datosDeEjemplo("111", LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(8, 0));

        assertThrows(ReglaDeNegocioException.class, () -> reservaService.intentarReservar(datos));
    }

    @Test
    void rechazaReservaSinCategorias() {
        DatosNuevaReserva datos = datosDeEjemplo("111", LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(10, 0));
        datos.setIdsCategoriasRequeridas(List.of());

        assertThrows(ReglaDeNegocioException.class, () -> reservaService.intentarReservar(datos));
    }

    @Test
    void cancelaReservaActivaFutura() {
        ResultadoReserva creada = reservaService.intentarReservar(datosDeEjemplo(
                "111", LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));

        reservaService.cancelarReserva(creada.getReserva().getId());

        assertEquals(EstadoReserva.CANCELADA, reservaDao.buscarPorId(creada.getReserva().getId()).get().getEstado());
    }

    @Test
    void rechazaCancelarReservaInexistente() {
        assertThrows(ReglaDeNegocioException.class, () -> reservaService.cancelarReserva("RES-999999"));
    }

    @Test
    void rechazaCancelarReservaYaCancelada() {
        ResultadoReserva creada = reservaService.intentarReservar(datosDeEjemplo(
                "111", LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));
        reservaService.cancelarReserva(creada.getReserva().getId());

        assertThrows(ReglaDeNegocioException.class, () -> reservaService.cancelarReserva(creada.getReserva().getId()));
    }

    @Test
    void notificaAObservadoresAlCrearYCancelarReserva() {
        ContadorEventosReserva contador = new ContadorEventosReserva();
        reservaService.agregarObservador(contador);

        ResultadoReserva creada = reservaService.intentarReservar(datosDeEjemplo(
                "111", LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));
        reservaService.cancelarReserva(creada.getReserva().getId());

        assertEquals(1, contador.creadas);
        assertEquals(1, contador.canceladas);
    }

    @Test
    void modificaReservaExistenteConservandoSuId() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        ResultadoReserva creada = reservaService.intentarReservar(datosDeEjemplo(
                "111", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));

        DatosNuevaReserva cambios = datosDeEjemplo("111", fecha, LocalTime.of(13, 0), LocalTime.of(15, 0));
        cambios.setId(creada.getReserva().getId());
        cambios.setActividad("Sesion de Junta Directiva");

        ResultadoReserva modificada = reservaService.intentarModificar(cambios);

        assertTrue(modificada.isExitoso());
        assertEquals(creada.getReserva().getId(), modificada.getReserva().getId());
        assertEquals("Sesion de Junta Directiva", modificada.getReserva().getActividad());
        assertEquals(LocalTime.of(13, 0), modificada.getReserva().getHoraInicio());
        assertEquals(1, reservaDao.listarTodos().size());
    }

    @Test
    void permiteModificarSinPerderElRecursoQueLaPropiaReservaOcupa() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        ResultadoReserva creada = reservaService.intentarReservar(datosDeEjemplo(
                "111", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));

        DatosNuevaReserva cambios = datosDeEjemplo("111", fecha, LocalTime.of(9, 0), LocalTime.of(11, 0));
        cambios.setId(creada.getReserva().getId());

        ResultadoReserva modificada = reservaService.intentarModificar(cambios);

        assertTrue(modificada.isExitoso());
        assertTrue(modificada.getReserva().getIdsRecursosAsignados().contains("34343"));
    }

    @Test
    void fallaAlModificarCuandoOtraReservaOcupaElHorario() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        ResultadoReserva primera = reservaService.intentarReservar(datosDeEjemplo(
                "111", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));
        reservaService.intentarReservar(datosDeEjemplo("222", fecha, LocalTime.of(14, 0), LocalTime.of(16, 0)));

        DatosNuevaReserva cambios = datosDeEjemplo("111", fecha, LocalTime.of(15, 0), LocalTime.of(17, 0));
        cambios.setId(primera.getReserva().getId());

        ResultadoReserva modificada = reservaService.intentarModificar(cambios);

        assertFalse(modificada.isExitoso());
        assertEquals(categoriaSala.getId(), modificada.getCategoriasNoDisponibles().get(0).getId());
    }

    @Test
    void rechazaModificarReservaCancelada() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        ResultadoReserva creada = reservaService.intentarReservar(datosDeEjemplo(
                "111", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));
        reservaService.cancelarReserva(creada.getReserva().getId());

        DatosNuevaReserva cambios = datosDeEjemplo("111", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0));
        cambios.setId(creada.getReserva().getId());

        assertThrows(ReglaDeNegocioException.class, () -> reservaService.intentarModificar(cambios));
    }

    @Test
    void rechazaModificarReservaInexistente() {
        DatosNuevaReserva cambios = datosDeEjemplo(
                "111", LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(10, 0)
        );
        cambios.setId("RES-999999");

        assertThrows(ReglaDeNegocioException.class, () -> reservaService.intentarModificar(cambios));
    }

    @Test
    void rechazaModificarLaReservaDeOtroFuncionario() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        ResultadoReserva creada = reservaService.intentarReservar(datosDeEjemplo(
                "111", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));

        DatosNuevaReserva cambios = datosDeEjemplo("222", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0));
        cambios.setId(creada.getReserva().getId());

        assertThrows(ReglaDeNegocioException.class, () -> reservaService.intentarModificar(cambios));
    }

    @Test
    void notificaAObservadoresAlModificarUnaReserva() {
        ContadorEventosReserva contador = new ContadorEventosReserva();
        LocalDate fecha = LocalDate.now().plusDays(1);
        ResultadoReserva creada = reservaService.intentarReservar(datosDeEjemplo(
                "111", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));
        reservaService.agregarObservador(contador);

        DatosNuevaReserva cambios = datosDeEjemplo("111", fecha, LocalTime.of(13, 0), LocalTime.of(15, 0));
        cambios.setId(creada.getReserva().getId());
        reservaService.intentarModificar(cambios);

        assertEquals(1, contador.modificadas);
    }

    @Test
    void elIdDeLaSiguienteReservaSigueAlMayorExistente() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        reservaDao.guardar(new Reserva(new DatosNuevaReserva(
                "RES-000007", "111", "Reunion previa", fecha,
                LocalTime.of(6, 0), LocalTime.of(7, 0), List.of(categoriaSala.getId())
        )));

        ResultadoReserva nueva = reservaService.intentarReservar(datosDeEjemplo(
                "111", fecha, LocalTime.of(8, 0), LocalTime.of(10, 0)
        ));

        assertEquals("RES-000008", nueva.getReserva().getId());
    }

    @Test
    void listaReservasActivasEnUnaFechaEspecifica() {
        LocalDate fecha = LocalDate.now().plusDays(1);
        reservaService.intentarReservar(datosDeEjemplo("111", fecha, LocalTime.of(8, 0), LocalTime.of(9, 0)));

        List<Reserva> resultado = reservaService.listarReservasActivasEnFecha(fecha);

        assertEquals(1, resultado.size());
    }

    private DatosNuevaReserva datosDeEjemplo(String idFuncionario, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        DatosNuevaReserva datos = new DatosNuevaReserva();
        datos.setIdFuncionario(idFuncionario);
        datos.setActividad("Reunion de trabajo");
        datos.setFecha(fecha);
        datos.setHoraInicio(horaInicio);
        datos.setHoraFin(horaFin);
        datos.setIdsCategoriasRequeridas(List.of(categoriaSala.getId()));
        return datos;
    }

    private static final class ContadorEventosReserva implements ReservaObserver {
        private int creadas;
        private int canceladas;
        private int modificadas;

        @Override
        public void onReservaCreada(Reserva reserva) {
            creadas++;
        }

        @Override
        public void onReservaCancelada(Reserva reserva) {
            canceladas++;
        }

        @Override
        public void onReservaModificada(Reserva reserva) {
            modificadas++;
        }
    }
}
