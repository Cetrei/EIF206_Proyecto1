package cr.ac.una.reservas.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalDateAdapterTest {

    private final LocalDateAdapter adapter = new LocalDateAdapter();

    @Test
    void unmarshalConvierteTextoIsoAFecha() {
        assertEquals(LocalDate.of(2026, 9, 15), adapter.unmarshal("2026-09-15"));
    }

    @Test
    void unmarshalRetornaNuloSiElTextoEsNulo() {
        assertNull(adapter.unmarshal(null));
    }

    @Test
    void unmarshalRetornaNuloSiElTextoEstaVacio() {
        assertNull(adapter.unmarshal(""));
    }

    @Test
    void unmarshalLanzaExcepcionSiElTextoNoEsUnaFechaValida() {
        assertThrows(java.time.format.DateTimeParseException.class, () -> adapter.unmarshal("no-es-fecha"));
    }

    @Test
    void marshalConvierteFechaATextoIso() {
        assertEquals("2026-09-15", adapter.marshal(LocalDate.of(2026, 9, 15)));
    }

    @Test
    void marshalRetornaNuloSiLaFechaEsNula() {
        assertNull(adapter.marshal(null));
    }

    @Test
    void marshalYUnmarshalSonInversos() {
        LocalDate fechaOriginal = LocalDate.of(2025, 1, 31);

        assertEquals(fechaOriginal, adapter.unmarshal(adapter.marshal(fechaOriginal)));
    }
}
