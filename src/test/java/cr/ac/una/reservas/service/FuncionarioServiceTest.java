package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.DatosNuevaReserva;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuncionarioServiceTest {

    private FuncionarioDaoFalso funcionarioDao;
    private ReservaDaoFalso reservaDao;
    private FuncionarioService funcionarioService;

    @BeforeEach
    void prepararService() {
        funcionarioDao = new FuncionarioDaoFalso();
        reservaDao = new ReservaDaoFalso();
        funcionarioService = new FuncionarioService(funcionarioDao, reservaDao);
    }

    @Test
    void creaFuncionarioConClaveInicialIgualAlId() {
        Funcionario funcionario = new Funcionario("111", "clave-cualquiera", "Juan Perez", "3323");

        funcionarioService.crear(funcionario);

        assertEquals("111", funcionarioDao.buscarPorId("111").get().getClave());
    }

    @Test
    void rechazaCrearFuncionarioSinId() {
        Funcionario funcionario = new Funcionario(" ", "clave", "Juan Perez", "3323");

        assertThrows(ReglaDeNegocioException.class, () -> funcionarioService.crear(funcionario));
    }

    @Test
    void rechazaCrearFuncionarioConIdDuplicado() {
        funcionarioDao.guardar(new Funcionario("111", "111", "Juan Perez", "3323"));
        Funcionario duplicado = new Funcionario("111", "111", "Otro Nombre", "0000");

        assertThrows(ReglaDeNegocioException.class, () -> funcionarioService.crear(duplicado));
    }

    @Test
    void modificaFuncionarioExistente() {
        funcionarioDao.guardar(new Funcionario("111", "111", "Juan Perez", "3323"));
        Funcionario modificado = new Funcionario("111", "111", "Juan Perez Mora", "4444");

        funcionarioService.modificar(modificado);

        assertEquals("Juan Perez Mora", funcionarioDao.buscarPorId("111").get().getNombre());
    }

    @Test
    void rechazaModificarFuncionarioInexistente() {
        Funcionario funcionario = new Funcionario("999", "999", "No existe", "0000");

        assertThrows(ReglaDeNegocioException.class, () -> funcionarioService.modificar(funcionario));
    }

    @Test
    void eliminaFuncionarioSinReservasActivas() {
        funcionarioDao.guardar(new Funcionario("111", "111", "Juan Perez", "3323"));

        funcionarioService.eliminar("111");

        assertTrue(funcionarioDao.buscarPorId("111").isEmpty());
    }

    @Test
    void rechazaEliminarFuncionarioConReservaActiva() {
        funcionarioDao.guardar(new Funcionario("111", "111", "Juan Perez", "3323"));
        reservaDao.guardar(new Reserva(datosDeEjemplo("RES-000001", "111")));

        assertThrows(ReglaDeNegocioException.class, () -> funcionarioService.eliminar("111"));
    }

    @Test
    void buscaFuncionariosPorNombre() {
        funcionarioDao.guardar(new Funcionario("111", "111", "Juan Perez", "3323"));
        funcionarioDao.guardar(new Funcionario("222", "222", "Maria Perez", "4444"));

        List<Funcionario> resultado = funcionarioService.buscarPorNombre("Maria");

        assertEquals(1, resultado.size());
        assertEquals("222", resultado.get(0).getId());
    }

    private DatosNuevaReserva datosDeEjemplo(String id, String idFuncionario) {
        DatosNuevaReserva datos = new DatosNuevaReserva();
        datos.setId(id);
        datos.setIdFuncionario(idFuncionario);
        datos.setActividad("Reunion de trabajo");
        datos.setFecha(LocalDate.now().plusDays(1));
        datos.setHoraInicio(LocalTime.of(8, 0));
        datos.setHoraFin(LocalTime.of(10, 0));
        datos.setIdsCategoriasRequeridas(List.of("CAT-000001"));
        return datos;
    }
}
