package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoriaObserverTest {

    private CategoriaDaoFalso categoriaDao;
    private RecursoDaoFalso recursoDao;
    private CategoriaService categoriaService;
    private int notificaciones;

    @BeforeEach
    void prepararService() {
        categoriaDao = new CategoriaDaoFalso();
        recursoDao = new RecursoDaoFalso();
        categoriaService = new CategoriaService(categoriaDao, recursoDao);
        notificaciones = 0;
    }

    @Test
    void notificaAObservadorRegistradoAlCrearCategoria() {
        categoriaService.agregarObservador(() -> notificaciones++);

        categoriaService.crear("Sala de Juntas");

        assertEquals(1, notificaciones);
    }

    @Test
    void notificaAObservadorRegistradoAlModificarCategoria() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");
        categoriaService.agregarObservador(() -> notificaciones++);

        categoria.setDescripcion("Sala de Juntas Principal");
        categoriaService.modificar(categoria);

        assertEquals(1, notificaciones);
    }

    @Test
    void notificaAObservadorRegistradoAlEliminarCategoria() {
        Categoria categoria = categoriaService.crear("Sala de Juntas");
        categoriaService.agregarObservador(() -> notificaciones++);

        categoriaService.eliminar(categoria.getId());

        assertEquals(1, notificaciones);
    }

    @Test
    void dejaDeNotificarAObservadorQuitado() {
        CategoriaObserver observador = () -> notificaciones++;
        categoriaService.agregarObservador(observador);
        categoriaService.quitarObservador(observador);

        categoriaService.crear("Sala de Juntas");

        assertEquals(0, notificaciones);
    }

    @Test
    void notificaATodosLosObservadoresRegistradosSobreLaMismaInstancia() {
        int[] contadorSegundoObservador = {0};
        categoriaService.agregarObservador(() -> notificaciones++);
        categoriaService.agregarObservador(() -> contadorSegundoObservador[0]++);

        categoriaService.crear("Sala de Juntas");

        assertEquals(1, notificaciones);
        assertEquals(1, contadorSegundoObservador[0]);
    }
}
