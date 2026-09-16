package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Recurso;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecursoDaoXmlIT {

    @TempDir
    Path carpetaTemporal;

    @Test
    void guardaYReleeRecursosDesdeElArchivo() {
        Path archivo = carpetaTemporal.resolve("recursos.xml");
        RecursoDaoXml dao = new RecursoDaoXml(archivo.toString());

        dao.guardar(new Recurso("238715", "CAT-000002", "Laptop #238715"));

        RecursoDaoXml daoRecargado = new RecursoDaoXml(archivo.toString());

        assertTrue(Files.exists(archivo));
        assertEquals("Laptop #238715", daoRecargado.buscarPorId("238715").get().getDescripcion());
        assertEquals("CAT-000002", daoRecargado.buscarPorId("238715").get().getIdCategoria());
    }

    @Test
    void listaSoloLosRecursosDeLaCategoriaPedida() {
        Path archivo = carpetaTemporal.resolve("recursos.xml");
        RecursoDaoXml dao = new RecursoDaoXml(archivo.toString());
        dao.guardar(new Recurso("238715", "CAT-000002", "Laptop #238715"));
        dao.guardar(new Recurso("45238", "CAT-000002", "Laptop #45238"));
        dao.guardar(new Recurso("34343", "CAT-000001", "Sala 1 primer piso"));

        List<Recurso> resultado = dao.listarPorCategoria("CAT-000002");

        assertEquals(2, resultado.size());
        assertEquals(1, dao.listarPorCategoria("CAT-000001").size());
        assertTrue(dao.listarPorCategoria("CAT-999999").isEmpty());
    }

    @Test
    void actualizaYEliminaRecursosEnElArchivo() {
        Path archivo = carpetaTemporal.resolve("recursos.xml");
        RecursoDaoXml dao = new RecursoDaoXml(archivo.toString());
        dao.guardar(new Recurso("34343", "CAT-000001", "Sala 1 primer piso"));

        dao.guardar(new Recurso("34343", "CAT-000001", "Sala 1 segundo piso"));
        assertEquals("Sala 1 segundo piso", new RecursoDaoXml(archivo.toString())
                .buscarPorId("34343").get().getDescripcion());

        dao.eliminar("34343");
        assertTrue(new RecursoDaoXml(archivo.toString()).listarTodos().isEmpty());
    }
}
