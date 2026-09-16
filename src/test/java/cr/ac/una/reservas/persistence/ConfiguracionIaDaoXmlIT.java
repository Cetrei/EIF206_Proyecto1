package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.ConfiguracionIa;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfiguracionIaDaoXmlIT {

    @TempDir
    Path carpetaTemporal;

    @Test
    void guardaYReleeLaConfiguracionDesdeElArchivo() {
        Path archivo = carpetaTemporal.resolve("configuracion-ia.xml");
        ConfiguracionIaDaoXml dao = new ConfiguracionIaDaoXml(archivo.toString());

        dao.guardar(new ConfiguracionIa("llave-de-prueba", "gemini-2.0-flash"));

        ConfiguracionIa leida = new ConfiguracionIaDaoXml(archivo.toString())
                .buscarPorId(ConfiguracionIa.ID_UNICO).get();

        assertTrue(Files.exists(archivo));
        assertEquals("llave-de-prueba", leida.getApiKey());
        assertEquals("gemini-2.0-flash", leida.getModelo());
    }

    @Test
    void mantieneUnaSolaConfiguracionAlGuardarVariasVeces() {
        Path archivo = carpetaTemporal.resolve("configuracion-ia.xml");
        ConfiguracionIaDaoXml dao = new ConfiguracionIaDaoXml(archivo.toString());

        dao.guardar(new ConfiguracionIa("primera", "gemini-2.0-flash"));
        dao.guardar(new ConfiguracionIa("segunda", "gemini-2.5-pro"));

        ConfiguracionIaDaoXml daoRecargado = new ConfiguracionIaDaoXml(archivo.toString());
        assertEquals(1, daoRecargado.listarTodos().size());
        assertEquals("segunda", daoRecargado.buscarPorId(ConfiguracionIa.ID_UNICO).get().getApiKey());
    }

    @Test
    void borraElArchivoAlEliminarLaConfiguracion() {
        Path archivo = carpetaTemporal.resolve("configuracion-ia.xml");
        ConfiguracionIaDaoXml dao = new ConfiguracionIaDaoXml(archivo.toString());
        dao.guardar(new ConfiguracionIa("llave-de-prueba", "gemini-2.0-flash"));

        dao.eliminar(ConfiguracionIa.ID_UNICO);

        assertFalse(Files.exists(archivo));
        assertTrue(dao.listarTodos().isEmpty());
    }

    @Test
    void noDevuelveConfiguracionCuandoElArchivoNoExiste() {
        Path archivo = carpetaTemporal.resolve("no-existe.xml");

        ConfiguracionIaDaoXml dao = new ConfiguracionIaDaoXml(archivo.toString());

        assertTrue(dao.buscarPorId(ConfiguracionIa.ID_UNICO).isEmpty());
        assertTrue(dao.listarTodos().isEmpty());
    }
}
