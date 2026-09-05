package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Funcionario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Termina en IT, no en Test: Failsafe la corre en "mvn verify", Surefire la ignora.
// Queda comentado hasta que FuncionarioDaoXml exista (companero A, docs/03_persistence.md).
class FuncionarioDaoXmlIT {

    private static final String ARCHIVO_PRUEBA = "data/test-funcionarios.xml";

    @BeforeEach
    void limpiarArchivoPrueba() {
        new File(ARCHIVO_PRUEBA).delete();
    }

    @AfterEach
    void borrarArchivoPrueba() {
        new File(ARCHIVO_PRUEBA).delete();
    }

    @Test
    void guardarYLeerFuncionarioDeVueltaDesdeXml() {
        // FuncionarioDao dao = new FuncionarioDaoXml(ARCHIVO_PRUEBA);
        // Funcionario original = new Funcionario("111", "111", "Juan Perez", "3323");
        //
        // dao.guardar(original);
        // Optional<Funcionario> leido = dao.buscarPorId("111");
        //
        // assertTrue(leido.isPresent());
        // assertEquals("Juan Perez", leido.get().getNombre());
    }
}
