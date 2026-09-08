package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecursoServiceTest {

    private RecursoDaoFalso recursoDao;
    private CategoriaDaoFalso categoriaDao;
    private RecursoService recursoService;
    private Categoria categoriaSala;

    @BeforeEach
    void prepararService() {
        recursoDao = new RecursoDaoFalso();
        categoriaDao = new CategoriaDaoFalso();
        recursoService = new RecursoService(recursoDao, categoriaDao);

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
}
