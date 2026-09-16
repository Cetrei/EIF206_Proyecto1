package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Funcionario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
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
}
