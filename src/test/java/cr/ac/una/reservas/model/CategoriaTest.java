package cr.ac.una.reservas.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CategoriaTest {

    @Test
    void dosCategoriasConElMismoIdSonIgualesAunqueLaDescripcionDifiera() {
        Categoria primera = new Categoria("CAT-000001", "Sala de Juntas");
        Categoria segunda = new Categoria("CAT-000001", "Otra descripcion");

        assertEquals(primera, segunda);
        assertEquals(primera.hashCode(), segunda.hashCode());
    }

    @Test
    void dosCategoriasConIdDiferenteNoSonIguales() {
        Categoria primera = new Categoria("CAT-000001", "Sala de Juntas");
        Categoria segunda = new Categoria("CAT-000002", "Sala de Juntas");

        assertNotEquals(primera, segunda);
    }

    @Test
    void noEsIgualAUnObjetoDeOtraClase() {
        Categoria categoria = new Categoria("CAT-000001", "Sala de Juntas");

        assertNotEquals(categoria, "CAT-000001");
    }

    @Test
    void esIgualASiMisma() {
        Categoria categoria = new Categoria("CAT-000001", "Sala de Juntas");

        assertEquals(categoria, categoria);
    }
}
