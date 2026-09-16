package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.RolUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdministradorDaoXmlIT {

    @TempDir
    Path carpetaTemporal;

    @Test
    void conservaClaveYRolAlReleerDesdeElArchivo() {
        Path archivo = carpetaTemporal.resolve("administradores.xml");
        AdministradorDaoXml dao = new AdministradorDaoXml(archivo.toString());

        dao.guardar(new Administrador("admin", "123"));

        Administrador leido = new AdministradorDaoXml(archivo.toString()).buscarPorId("admin").get();

        assertEquals("123", leido.getClave());
        assertEquals(RolUsuario.ADMINISTRADOR, leido.getRol());
    }

    @Test
    void persisteElCambioDeClave() {
        Path archivo = carpetaTemporal.resolve("administradores.xml");
        AdministradorDaoXml dao = new AdministradorDaoXml(archivo.toString());
        Administrador administrador = new Administrador("admin", "123");
        dao.guardar(administrador);

        administrador.setClave("nueva");
        dao.guardar(administrador);

        AdministradorDaoXml daoRecargado = new AdministradorDaoXml(archivo.toString());
        assertEquals(1, daoRecargado.listarTodos().size());
        assertEquals("nueva", daoRecargado.buscarPorId("admin").get().getClave());
    }

    @Test
    void eliminaAlAdministradorDelArchivo() {
        Path archivo = carpetaTemporal.resolve("administradores.xml");
        AdministradorDaoXml dao = new AdministradorDaoXml(archivo.toString());
        dao.guardar(new Administrador("admin", "123"));

        dao.eliminar("admin");

        assertTrue(new AdministradorDaoXml(archivo.toString()).listarTodos().isEmpty());
    }
}
