package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.model.RolUsuario;
import cr.ac.una.reservas.model.Usuario;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Prueba de unidad de AutenticacionService (ver docs/07_convenciones.md,
 * seccion Pruebas), usando Dao falsos en memoria en vez de la
 * implementacion real de persistence (Companero A, todavia no
 * entregada).
 * <p>
 * Ademas de cubrir la logica de autenticar/cambiarClave, esta clase
 * siembra los dos usuarios de prueba pedidos para probar la aplicacion
 * de forma manual mientras persistence no esta lista:
 * <ul>
 *     <li>Funcionario, id "funcionario", clave "123".</li>
 *     <li>Administrador, id "admin", clave "123".</li>
 * </ul>
 * Estos usuarios solo existen en memoria durante esta prueba (no se
 * guardan en ningun XML real); en cuanto el Companero A entregue
 * FuncionarioDaoXml/AdministradorDaoXml, la forma de tener estos mismos
 * usuarios disponibles para probar la app de verdad es agregarlos una
 * vez a los archivos data/funcionarios.xml y data/administradores.xml
 * (a mano o con un pequeno script), con los mismos id/clave usados aqui.
 */
class AutenticacionServiceTest {

    static final String ID_FUNCIONARIO_PRUEBA = "funcionario";
    static final String ID_ADMIN_PRUEBA = "admin";
    static final String CLAVE_PRUEBA = "123";

    private FuncionarioDaoFalso funcionarioDao;
    private AdministradorDaoFalso administradorDao;
    private AutenticacionService autenticacionService;

    @BeforeEach
    void prepararUsuariosDePrueba() {
        funcionarioDao = new FuncionarioDaoFalso();
        administradorDao = new AdministradorDaoFalso();
        autenticacionService = new AutenticacionService(administradorDao, funcionarioDao);

        // Usuario de prueba: funcionario / 123
        funcionarioDao.guardar(new Funcionario(ID_FUNCIONARIO_PRUEBA, CLAVE_PRUEBA, "Funcionario de Prueba", "0000-0000"));

        // Usuario de prueba: admin / 123
        administradorDao.guardar(new Administrador(ID_ADMIN_PRUEBA, CLAVE_PRUEBA));
    }

    @Test
    void autenticaAlFuncionarioDePruebaConCredencialesCorrectas() {
        Usuario usuario = autenticacionService.autenticar(ID_FUNCIONARIO_PRUEBA, CLAVE_PRUEBA);

        assertEquals(ID_FUNCIONARIO_PRUEBA, usuario.getId());
        assertEquals(RolUsuario.FUNCIONARIO, usuario.getRol());
    }

    @Test
    void autenticaAlAdminDePruebaConCredencialesCorrectas() {
        Usuario usuario = autenticacionService.autenticar(ID_ADMIN_PRUEBA, CLAVE_PRUEBA);

        assertEquals(ID_ADMIN_PRUEBA, usuario.getId());
        assertEquals(RolUsuario.ADMINISTRADOR, usuario.getRol());
    }

    @Test
    void rechazaClaveIncorrecta() {
        assertThrows(ReglaDeNegocioException.class,
                () -> autenticacionService.autenticar(ID_FUNCIONARIO_PRUEBA, "clave-incorrecta"));
    }

    @Test
    void rechazaIdInexistente() {
        assertThrows(ReglaDeNegocioException.class,
                () -> autenticacionService.autenticar("no-existe", CLAVE_PRUEBA));
    }

    @Test
    void cambiaClaveDelFuncionarioDePruebaCuandoLaClaveActualEsCorrecta() {
        autenticacionService.cambiarClave(ID_FUNCIONARIO_PRUEBA, CLAVE_PRUEBA, "nueva-clave");

        Usuario usuario = autenticacionService.autenticar(ID_FUNCIONARIO_PRUEBA, "nueva-clave");
        assertEquals(ID_FUNCIONARIO_PRUEBA, usuario.getId());
    }

    @Test
    void rechazaCambioDeClaveConClaveActualIncorrecta() {
        assertThrows(ReglaDeNegocioException.class,
                () -> autenticacionService.cambiarClave(ID_ADMIN_PRUEBA, "clave-incorrecta", "otra-clave"));
    }
}
