package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Categoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoriaDaoXmlIT {

    @TempDir
    Path carpetaTemporal;

    @Test
    void guardaYReleeCategoriasDesdeElArchivo() {
        Path archivo = carpetaTemporal.resolve("categorias.xml");
        CategoriaDaoXml dao = new CategoriaDaoXml(archivo.toString());

        dao.guardar(new Categoria("CAT-000001", "Sala para 10 personas"));
        dao.guardar(new Categoria("CAT-000002", "Laptop windows 11"));

        CategoriaDaoXml daoRecargado = new CategoriaDaoXml(archivo.toString());

        assertTrue(Files.exists(archivo));
        assertEquals(2, daoRecargado.listarTodos().size());
        assertEquals("Sala para 10 personas", daoRecargado.buscarPorId("CAT-000001").get().getDescripcion());
    }

    @Test
    void buscaPorDescripcionParcialSinDistinguirMayusculasNiAcentos() {
        Path archivo = carpetaTemporal.resolve("categorias.xml");
        CategoriaDaoXml dao = new CategoriaDaoXml(archivo.toString());
        dao.guardar(new Categoria("CAT-000001", "Sala de Reunión"));
        dao.guardar(new Categoria("CAT-000002", "Laptop windows 11"));
        dao.guardar(new Categoria("CAT-000003", "Sala para 10 personas"));

        List<Categoria> resultado = dao.buscarPorDescripcion("sala");

        assertEquals(2, resultado.size());
        assertEquals(1, dao.buscarPorDescripcion("REUNION").size());
    }

    @Test
    void devuelveListaVaciaCuandoNingunaCategoriaCoincide() {
        Path archivo = carpetaTemporal.resolve("categorias.xml");
        CategoriaDaoXml dao = new CategoriaDaoXml(archivo.toString());
        dao.guardar(new Categoria("CAT-000001", "Sala para 10 personas"));

        assertTrue(dao.buscarPorDescripcion("proyector").isEmpty());
    }

    @Test
    void actualizaLaCategoriaExistenteSinDuplicarla() {
        Path archivo = carpetaTemporal.resolve("categorias.xml");
        CategoriaDaoXml dao = new CategoriaDaoXml(archivo.toString());
        dao.guardar(new Categoria("CAT-000001", "Sala para 10 personas"));

        dao.guardar(new Categoria("CAT-000001", "Sala para 20 personas"));

        CategoriaDaoXml daoRecargado = new CategoriaDaoXml(archivo.toString());
        assertEquals(1, daoRecargado.listarTodos().size());
        assertEquals("Sala para 20 personas", daoRecargado.buscarPorId("CAT-000001").get().getDescripcion());
    }

    @Test
    void eliminaLaCategoriaDelArchivo() {
        Path archivo = carpetaTemporal.resolve("categorias.xml");
        CategoriaDaoXml dao = new CategoriaDaoXml(archivo.toString());
        dao.guardar(new Categoria("CAT-000001", "Sala para 10 personas"));

        dao.eliminar("CAT-000001");

        assertTrue(new CategoriaDaoXml(archivo.toString()).listarTodos().isEmpty());
    }

    @Test
    void arrancaVacioCuandoElArchivoNoExiste() {
        Path archivo = carpetaTemporal.resolve("no-existe.xml");

        CategoriaDaoXml dao = new CategoriaDaoXml(archivo.toString());

        assertTrue(dao.listarTodos().isEmpty());
        assertTrue(dao.buscarPorId("CAT-000001").isEmpty());
    }
}
