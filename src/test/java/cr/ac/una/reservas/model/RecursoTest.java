package cr.ac.una.reservas.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RecursoTest {

    @Test
    void setCategoriaActualizaTambienElIdCategoriaParaMantenerlosConsistentes() {
        Recurso recurso = new Recurso("34343", "CAT-000001", "Sala 1 primer piso");
        Categoria nuevaCategoria = new Categoria("CAT-000002", "Proyectores");

        recurso.setCategoria(nuevaCategoria);

        assertEquals("CAT-000002", recurso.getIdCategoria());
        assertEquals(nuevaCategoria, recurso.getCategoria());
    }

    @Test
    void setCategoriaConNuloNoModificaElIdCategoriaExistente() {
        Recurso recurso = new Recurso("34343", "CAT-000001", "Sala 1 primer piso");

        recurso.setCategoria(null);

        assertEquals("CAT-000001", recurso.getIdCategoria());
        assertNull(recurso.getCategoria());
    }

    @Test
    void categoriaEmpiezaNulaHastaQueServiceLaResuelve() {
        Recurso recurso = new Recurso("34343", "CAT-000001", "Sala 1 primer piso");

        assertNull(recurso.getCategoria());
    }
}
