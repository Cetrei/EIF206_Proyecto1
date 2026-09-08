package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.DatosNuevaReserva;
import cr.ac.una.reservas.model.EstadisticaCategoria;
import cr.ac.una.reservas.model.EstadisticaSemana;
import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadisticaServiceTest {

    private ReservaDaoFalso reservaDao;
    private RecursoDaoFalso recursoDao;
    private CategoriaDaoFalso categoriaDao;
    private EstadisticaService estadisticaService;
    private Categoria categoriaSala;

    @BeforeEach
    void prepararService() {
        reservaDao = new ReservaDaoFalso();
        recursoDao = new RecursoDaoFalso();
        categoriaDao = new CategoriaDaoFalso();
        estadisticaService = new EstadisticaService(reservaDao, recursoDao, categoriaDao);

        categoriaSala = new Categoria("CAT-000001", "Sala para 10 personas");
        categoriaDao.guardar(categoriaSala);
        recursoDao.guardar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
    }

    @Test
    void contabilizaRecursosReservadosEnElPeriodo() {
        LocalDate fecha = LocalDate.of(2026, 8, 5);
        guardarReservaActiva("RES-000001", fecha, List.of("34343"));

        List<EstadisticaCategoria> resultado = estadisticaService.recursosReservadosEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        );

        assertEquals(1, resultado.size());
        assertEquals(categoriaSala.getId(), resultado.get(0).getCategoria().getId());
        assertEquals(1L, resultado.get(0).getCantidad());
    }

    @Test
    void ignoraReservasFueraDelPeriodo() {
        guardarReservaActiva("RES-000001", LocalDate.of(2026, 8, 20), List.of("34343"));

        List<EstadisticaCategoria> resultado = estadisticaService.recursosReservadosEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        );

        assertTrue(resultado.isEmpty());
    }

    @Test
    void rechazaRangoDeRecursosSinFechaDesde() {
        assertThrows(ReglaDeNegocioException.class,
                () -> estadisticaService.recursosReservadosEnPeriodo(null, LocalDate.of(2026, 8, 13)));
    }

    @Test
    void rechazaRangoDeRecursosConHastaAntesDeDesde() {
        assertThrows(ReglaDeNegocioException.class, () -> estadisticaService.recursosReservadosEnPeriodo(
                LocalDate.of(2026, 8, 13), LocalDate.of(2026, 8, 1)
        ));
    }

    @Test
    void agrupaActividadesPorSemanaEnElPeriodo() {
        LocalDate lunes = LocalDate.of(2026, 8, 3);
        guardarReservaActiva("RES-000001", lunes, List.of("34343"));
        guardarReservaActiva("RES-000002", lunes.plusDays(1), List.of("34343"));

        List<EstadisticaSemana> resultado = estadisticaService.actividadesPorSemanaEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        );

        assertEquals(1, resultado.size());
        assertEquals(lunes, resultado.get(0).getInicioSemana());
        assertEquals(2L, resultado.get(0).getCantidad());
    }

    @Test
    void ordenaEstadisticasDeActividadesPorSemanaAscendente() {
        LocalDate primeraSemana = LocalDate.of(2026, 8, 3);
        LocalDate segundaSemana = LocalDate.of(2026, 8, 10);
        guardarReservaActiva("RES-000001", segundaSemana, List.of("34343"));
        guardarReservaActiva("RES-000002", primeraSemana, List.of("34343"));

        List<EstadisticaSemana> resultado = estadisticaService.actividadesPorSemanaEnPeriodo(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 13)
        );

        assertEquals(primeraSemana, resultado.get(0).getInicioSemana());
        assertEquals(segundaSemana, resultado.get(1).getInicioSemana());
    }

    @Test
    void rechazaRangoDeActividadesConFechasNulas() {
        assertThrows(ReglaDeNegocioException.class,
                () -> estadisticaService.actividadesPorSemanaEnPeriodo(null, null));
    }

    private void guardarReservaActiva(String id, LocalDate fecha, List<String> idsRecursos) {
        DatosNuevaReserva datos = new DatosNuevaReserva();
        datos.setId(id);
        datos.setIdFuncionario("111");
        datos.setActividad("Reunion de trabajo");
        datos.setFecha(fecha);
        datos.setHoraInicio(LocalTime.of(8, 0));
        datos.setHoraFin(LocalTime.of(10, 0));
        datos.setIdsCategoriasRequeridas(List.of(categoriaSala.getId()));

        Reserva reserva = new Reserva(datos);
        reserva.setIdsRecursosAsignados(idsRecursos);
        reservaDao.guardar(reserva);
    }
}
