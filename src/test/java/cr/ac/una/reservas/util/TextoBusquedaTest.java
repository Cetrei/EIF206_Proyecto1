package cr.ac.una.reservas.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextoBusquedaTest {

    @Test
    void encuentraCoincidenciaParcial() {
        assertTrue(TextoBusqueda.contiene("Sala para 10 personas", "sala"));
    }

    @Test
    void ignoraMayusculasYMinusculas() {
        assertTrue(TextoBusqueda.contiene("Laptop windows 11", "LAPTOP"));
    }

    @Test
    void ignoraAcentos() {
        assertTrue(TextoBusqueda.contiene("Sala de Reunión", "reunion"));
    }

    @Test
    void ignoraEspaciosAlrededorDelTextoBuscado() {
        assertTrue(TextoBusqueda.contiene("Proyector HDMI", "  proyector  "));
    }

    @Test
    void textoBuscadoVacioCoincideConTodo() {
        assertTrue(TextoBusqueda.contiene("Sala de Juntas", ""));
        assertTrue(TextoBusqueda.contiene("Sala de Juntas", null));
    }

    @Test
    void textoFuenteNuloNoCoincide() {
        assertFalse(TextoBusqueda.contiene(null, "sala"));
    }

    @Test
    void normalizaTextoNuloComoCadenaVacia() {
        assertEquals("", TextoBusqueda.normalizar(null));
    }
}
