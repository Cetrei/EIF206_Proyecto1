package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.ConfiguracionIa;
import cr.ac.una.reservas.persistence.ConfiguracionIaDao;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfiguracionIaServiceTest {

    private ConfiguracionIaDaoFalso configuracionIaDao;
    private ConfiguracionIaService configuracionIaService;

    @BeforeEach
    void prepararService() {
        configuracionIaDao = new ConfiguracionIaDaoFalso();
        configuracionIaService = new ConfiguracionIaService(configuracionIaDao);
    }

    @Test
    void guardaLaConfiguracionConElIdUnico() {
        configuracionIaService.guardar("llave-de-prueba", "gemini-2.0-flash");

        ConfiguracionIa guardada = configuracionIaService.obtenerConfiguracion().get();
        assertEquals(ConfiguracionIa.ID_UNICO, guardada.getId());
        assertEquals("llave-de-prueba", guardada.getApiKey());
        assertEquals("gemini-2.0-flash", guardada.getModelo());
    }

    @Test
    void recortaLosEspaciosDeLaLlaveYDelModelo() {
        configuracionIaService.guardar("  llave-de-prueba  ", "  gemini-2.0-flash  ");

        ConfiguracionIa guardada = configuracionIaService.obtenerConfiguracion().get();
        assertEquals("llave-de-prueba", guardada.getApiKey());
        assertEquals("gemini-2.0-flash", guardada.getModelo());
    }

    @Test
    void rechazaGuardarSinApiKey() {
        assertThrows(ReglaDeNegocioException.class, () -> configuracionIaService.guardar(" ", "gemini-2.0-flash"));
        assertThrows(ReglaDeNegocioException.class, () -> configuracionIaService.guardar(null, "gemini-2.0-flash"));
    }

    @Test
    void rechazaGuardarSinModelo() {
        assertThrows(ReglaDeNegocioException.class, () -> configuracionIaService.guardar("llave-de-prueba", " "));
        assertThrows(ReglaDeNegocioException.class, () -> configuracionIaService.guardar("llave-de-prueba", null));
    }

    @Test
    void noHayConfiguracionAntesDeGuardarla() {
        assertTrue(configuracionIaService.obtenerConfiguracion().isEmpty());
    }

    @Test
    void reemplazaLaConfiguracionAnteriorAlGuardarDeNuevo() {
        configuracionIaService.guardar("primera", "gemini-2.0-flash");
        configuracionIaService.guardar("segunda", "gemini-2.5-pro");

        assertEquals(1, configuracionIaDao.listarTodos().size());
        assertEquals("segunda", configuracionIaService.obtenerConfiguracion().get().getApiKey());
    }

    private static final class ConfiguracionIaDaoFalso implements ConfiguracionIaDao {
        private ConfiguracionIa configuracion;

        @Override
        public Optional<ConfiguracionIa> buscarPorId(String id) {
            if (configuracion == null || !configuracion.getId().equals(id)) {
                return Optional.empty();
            }
            return Optional.of(configuracion);
        }

        @Override
        public List<ConfiguracionIa> listarTodos() {
            return configuracion == null ? List.of() : List.of(configuracion);
        }

        @Override
        public void guardar(ConfiguracionIa entidad) {
            entidad.setId(ConfiguracionIa.ID_UNICO);
            configuracion = entidad;
        }

        @Override
        public void eliminar(String id) {
            configuracion = null;
        }
    }
}
