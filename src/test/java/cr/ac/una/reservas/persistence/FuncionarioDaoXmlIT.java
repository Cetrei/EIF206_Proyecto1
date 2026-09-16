package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.model.RolUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuncionarioDaoXmlIT {

    @TempDir
    Path carpetaTemporal;

    @Test
    void guardarYLeerFuncionarioDeVueltaDesdeXml() {
        Path archivo = carpetaTemporal.resolve("funcionarios.xml");

        FuncionarioDao dao = new FuncionarioDaoXml(archivo.toString());
        Funcionario original = new Funcionario("111", "111", "Juan Perez", "3323");

        dao.guardar(original);
        Optional<Funcionario> leido = dao.buscarPorId("111");

        assertTrue(leido.isPresent());
        assertEquals("Juan Perez", leido.get().getNombre());
        assertTrue(Files.exists(archivo));
    }

    @Test
    void conservaTelefonoYRolAlReleerDesdeElArchivo() {
        Path archivo = carpetaTemporal.resolve("funcionarios.xml");
        FuncionarioDao dao = new FuncionarioDaoXml(archivo.toString());
        dao.guardar(new Funcionario("111", "111", "Juan Perez", "3323"));

        Funcionario leido = new FuncionarioDaoXml(archivo.toString()).buscarPorId("111").get();

        assertEquals("3323", leido.getTelefono());
        assertEquals("111", leido.getClave());
        assertEquals(RolUsuario.FUNCIONARIO, leido.getRol());
    }

    @Test
    void buscaPorNombreParcialSinDistinguirMayusculasNiAcentos() {
        Path archivo = carpetaTemporal.resolve("funcionarios.xml");
        FuncionarioDao dao = new FuncionarioDaoXml(archivo.toString());
        dao.guardar(new Funcionario("111", "111", "Juan Perez", "3323"));
        dao.guardar(new Funcionario("222", "222", "María Pérez", "222222"));
        dao.guardar(new Funcionario("333", "333", "Carlos Mora", "333333"));

        List<Funcionario> porApellido = dao.buscarPorNombre("perez");

        assertEquals(2, porApellido.size());
        assertEquals(1, dao.buscarPorNombre("MARIA").size());
        assertTrue(dao.buscarPorNombre("Zuniga").isEmpty());
    }

    @Test
    void actualizaYEliminaFuncionariosEnElArchivo() {
        Path archivo = carpetaTemporal.resolve("funcionarios.xml");
        FuncionarioDao dao = new FuncionarioDaoXml(archivo.toString());
        dao.guardar(new Funcionario("111", "111", "Juan Perez", "3323"));

        dao.guardar(new Funcionario("111", "111", "Juan Perez Mora", "9999"));
        assertEquals(1, new FuncionarioDaoXml(archivo.toString()).listarTodos().size());
        assertEquals("9999", new FuncionarioDaoXml(archivo.toString()).buscarPorId("111").get().getTelefono());

        dao.eliminar("111");
        assertTrue(new FuncionarioDaoXml(archivo.toString()).listarTodos().isEmpty());
    }
}
