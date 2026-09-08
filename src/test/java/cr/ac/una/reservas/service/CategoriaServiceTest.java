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

class CategoriaServiceTest {

    private CategoriaDaoFalso categoriaDao;
    private RecursoDaoFalso recursoDao;
    private CategoriaService categoriaService;

    @BeforeEach
    void prepararService() {
        categoriaDao = new CategoriaDaoFalso();
        recursoDao = new RecursoDaoFalso();
        categoriaService = new CategoriaService(categoriaDao, recursoDao);
    }

    @Test
    void creaCategoriaConIdAutogenerado() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");

        assertEquals("CAT-000001", categoria.getId());
        assertEquals("Sala de Juntas", categoria.getDescripcion());
    }

    @Test
    void generaConsecutivosDiferentesParaCategoriasSucesivas() {
        Categoria primera = categoriaService.crear("Sala de Juntas");
        Categoria segunda = categoriaService.crear("Laptop windows 11");

        assertEquals("CAT-000001", primera.getId());
        assertEquals("CAT-000002", segunda.getId());
    }

    @Test
    void rechazaCrearConDescripcionVacia() {
        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.crear(" "));
    }

    @Test
    void rechazaCrearConDescripcionNula() {
        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.crear(null));
    }

    @Test
    void modificaCategoriaExistente() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");
        categoria.setDescripcion("Sala de Juntas Principal");

        categoriaService.modificar(categoria);

        assertEquals("Sala de Juntas Principal", categoriaDao.buscarPorId(categoria.getId()).get().getDescripcion());
    }

    @Test
    void rechazaModificarCategoriaInexistente() {
        Categoria categoria = new Categoria("CAT-999999", "No existe");

        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.modificar(categoria));
    }

    @Test
    void eliminaCategoriaSinRecursosAsociados() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");

        categoriaService.eliminar(categoria.getId());

        assertTrue(categoriaDao.buscarPorId(categoria.getId()).isEmpty());
    }

    @Test
    void rechazaEliminarCategoriaConRecursosAsociados() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");
        recursoDao.guardar(new Recurso("238715", categoria.getId(), "Sala 1 primer piso"));

        assertThrows(ReglaDeNegocioException.class, () -> categoriaService.eliminar(categoria.getId()));
    }

    @Test
    void buscaCategoriasPorDescripcion() {
        categoriaDao.guardar(new Categoria("CAT-000001", "Sala de Juntas"));
        categoriaDao.guardar(new Categoria("CAT-000002", "Laptop windows 11"));

        List<Categoria> resultado = categoriaService.buscarPorDescripcion("Sala");

        assertEquals(1, resultado.size());
        assertEquals("CAT-000001", resultado.get(0).getId());
    }

    @Test
    void listaTodasLasCategorias() {
        categoriaDao.guardar(new Categoria("CAT-000001", "Sala de Juntas"));
        categoriaDao.guardar(new Categoria("CAT-000002", "Laptop windows 11"));

        assertEquals(2, categoriaService.listarTodas().size());
    }
}
