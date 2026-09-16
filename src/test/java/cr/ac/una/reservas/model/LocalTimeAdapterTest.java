package cr.ac.una.reservas.model;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalTimeAdapterTest {

    private final LocalTimeAdapter adapter = new LocalTimeAdapter();

    @Test
    void unmarshalConvierteTextoIsoAHora() {
        assertEquals(LocalTime.of(8, 30), adapter.unmarshal("08:30"));
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
    void unmarshalLanzaExcepcionSiElTextoNoEsUnaHoraValida() {
        assertThrows(java.time.format.DateTimeParseException.class, () -> adapter.unmarshal("no-es-hora"));
    }

    @Test
    void marshalConvierteHoraATextoIso() {
        assertEquals("08:30", adapter.marshal(LocalTime.of(8, 30)));
    }

    @Test
    void marshalRetornaNuloSiLaHoraEsNula() {
        assertNull(adapter.marshal(null));
    }

    @Test
    void marshalYUnmarshalSonInversos() {
        LocalTime horaOriginal = LocalTime.of(23, 59);

        assertEquals(horaOriginal, adapter.unmarshal(adapter.marshal(horaOriginal)));
    }
}
