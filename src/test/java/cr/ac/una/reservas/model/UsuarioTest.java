package cr.ac.una.reservas.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    @Test
    void funcionarioNaceConRolFuncionario() {
        Funcionario funcionario = new Funcionario("111", "111", "Juan Perez", "3323");

        assertEquals(RolUsuario.FUNCIONARIO, funcionario.getRol());
        assertEquals("111", funcionario.getId());
        assertEquals("Juan Perez", funcionario.getNombre());
        assertEquals("3323", funcionario.getTelefono());
    }

    @Test
    void administradorNaceConRolAdministrador() {
        Administrador administrador = new Administrador("admin", "123");

        assertEquals(RolUsuario.ADMINISTRADOR, administrador.getRol());
        assertEquals("admin", administrador.getId());
        assertEquals("123", administrador.getClave());
    }

    @Test
    void permiteCambiarLaClaveDelUsuario() {
        Usuario usuario = new Funcionario("111", "111", "Juan Perez", "3323");

        usuario.setClave("nueva");

        assertEquals("nueva", usuario.getClave());
    }

    @Test
    void constructorVacioDejaLosCamposSinValor() {
        Funcionario funcionario = new Funcionario();

        assertNull(funcionario.getId());
        assertNull(funcionario.getClave());
        assertNull(funcionario.getRol());
        assertNull(funcionario.getNombre());
    }

    @Test
    void elTextoDelFuncionarioIncluyeSuNombre() {
        Funcionario funcionario = new Funcionario("111", "111", "Juan Perez", "3323");

        assertTrue(funcionario.toString().contains("Juan Perez"));
    }
}
