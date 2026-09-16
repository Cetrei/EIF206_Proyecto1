package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.DatosNuevaReserva;
import cr.ac.una.reservas.model.EstadoReserva;
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

class RecursoServiceTest {

    private RecursoDaoFalso recursoDao;
    private CategoriaDaoFalso categoriaDao;
    private ReservaDaoFalso reservaDao;
    private RecursoService recursoService;
    private Categoria categoriaSala;

    @BeforeEach
    void prepararService() {
        recursoDao = new RecursoDaoFalso();
        categoriaDao = new CategoriaDaoFalso();
        reservaDao = new ReservaDaoFalso();
        recursoService = new RecursoService(recursoDao, categoriaDao, reservaDao);

        categoriaSala = new Categoria("CAT-000001", "Sala para 10 personas");
        categoriaDao.guardar(categoriaSala);
    }

    @Test
    void creaRecursoConCategoriaValida() {
        Recurso recurso = new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso");

        recursoService.crear(recurso);

        assertTrue(recursoDao.buscarPorId("34343").isPresent());
    }

    @Test
    void rechazaCrearRecursoSinId() {
        Recurso recurso = new Recurso(" ", categoriaSala.getId(), "Sala 1 primer piso");

        assertThrows(ReglaDeNegocioException.class, () -> recursoService.crear(recurso));
    }

    @Test
    void rechazaCrearRecursoConIdDuplicado() {
        recursoDao.guardar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
        Recurso duplicado = new Recurso("34343", categoriaSala.getId(), "Otra descripcion");

        assertThrows(ReglaDeNegocioException.class, () -> recursoService.crear(duplicado));
    }

    @Test
    void rechazaCrearRecursoConCategoriaInexistente() {
        Recurso recurso = new Recurso("34343", "CAT-999999", "Sala 1 primer piso");

        assertThrows(ReglaDeNegocioException.class, () -> recursoService.crear(recurso));
    }

    @Test
    void modificaRecursoExistente() {
        recursoDao.guardar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
        Recurso modificado = new Recurso("34343", categoriaSala.getId(), "Sala 1 segundo piso");

        recursoService.modificar(modificado);

        assertEquals("Sala 1 segundo piso", recursoDao.buscarPorId("34343").get().getDescripcion());
    }

    @Test
    void rechazaModificarRecursoInexistente() {
        Recurso recurso = new Recurso("999999", categoriaSala.getId(), "No existe");

        assertThrows(ReglaDeNegocioException.class, () -> recursoService.modificar(recurso));
    }

    @Test
    void listaRecursosPorCategoriaConCategoriaResuelta() {
        recursoDao.guardar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));

        List<Recurso> resultado = recursoService.listarPorCategoria(categoriaSala.getId());

        assertEquals(1, resultado.size());
        assertEquals(categoriaSala.getId(), resultado.get(0).getCategoria().getId());
    }

    @Test
    void filtraPorCategoriaYDescripcion() {
        recursoDao.guardar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
        recursoDao.guardar(new Recurso("45238", categoriaSala.getId(), "Sala 2 segundo piso"));

        List<Recurso> resultado = recursoService.filtrar(categoriaSala.getId(), "primer");

        assertEquals(1, resultado.size());
        assertEquals("34343", resultado.get(0).getId());
    }

    @Test
    void eliminaRecurso() {
        recursoDao.guardar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));

        recursoService.eliminar("34343");

        assertTrue(recursoDao.buscarPorId("34343").isEmpty());
    }

    @Test
    void rechazaEliminarRecursoInexistente() {
        assertThrows(ReglaDeNegocioException.class, () -> recursoService.eliminar("999999"));
    }

    @Test
    void rechazaEliminarRecursoAsignadoAUnaReservaActiva() {
        recursoDao.guardar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
        reservaDao.guardar(reservaConRecurso("RES-000001", "34343", EstadoReserva.ACTIVA));

        assertThrows(ReglaDeNegocioException.class, () -> recursoService.eliminar("34343"));
        assertTrue(recursoDao.buscarPorId("34343").isPresent());
    }

    @Test
    void permiteEliminarRecursoCuyaReservaFueCancelada() {
        recursoDao.guardar(new Recurso("34343", categoriaSala.getId(), "Sala 1 primer piso"));
        reservaDao.guardar(reservaConRecurso("RES-000001", "34343", EstadoReserva.CANCELADA));

        recursoService.eliminar("34343");

        assertTrue(recursoDao.buscarPorId("34343").isEmpty());
    }

    @Test
    void filtraTambienPorNumeroDeActivo() {
        recursoDao.guardar(new Recurso("238715", categoriaSala.getId(), "Laptop principal"));
        recursoDao.guardar(new Recurso("45238", categoriaSala.getId(), "Laptop secundaria"));

        List<Recurso> resultado = recursoService.filtrar(null, "238715");

        assertEquals(1, resultado.size());
        assertEquals("238715", resultado.get(0).getId());
    }

    private Reserva reservaConRecurso(String id, String idRecurso, EstadoReserva estado) {
        DatosNuevaReserva datos = new DatosNuevaReserva(
                id,
                "111",
                "Reunion de trabajo",
                LocalDate.of(2026, 8, 5),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                List.of(categoriaSala.getId())
        );
        Reserva reserva = new Reserva(datos);
        reserva.setIdsRecursosAsignados(List.of(idRecurso));
        reserva.setEstado(estado);
        return reserva;
    }
}
