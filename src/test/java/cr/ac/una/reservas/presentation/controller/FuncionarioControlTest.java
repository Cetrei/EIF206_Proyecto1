package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.presentation.model.FuncionarioModel;
import cr.ac.una.reservas.service.FuncionarioDaoFalso;
import cr.ac.una.reservas.service.FuncionarioService;
import cr.ac.una.reservas.service.ReservaDaoFalso;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FuncionarioControlTest {

    @AfterEach
    void cerrarSesion() {
        SesionControl.obtenerInstancia().cerrarSesion();
    }

    @Test
    void rechazaConstruirseSiElUsuarioNoEsAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Funcionario("111", "111", "Juan Perez", "3323"));
        FuncionarioService funcionarioService =
                new FuncionarioService(new FuncionarioDaoFalso(), new ReservaDaoFalso());

        assertThrows(
                ReglaDeNegocioException.class,
                () -> new FuncionarioControl(new FuncionarioModel(), null, null, funcionarioService)
        );
    }

    @Test
    void noRechazaPorRolSiElUsuarioEsAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        FuncionarioService funcionarioService =
                new FuncionarioService(new FuncionarioDaoFalso(), new ReservaDaoFalso());

        try {
            new FuncionarioControl(new FuncionarioModel(), null, null, funcionarioService);
        } catch (ReglaDeNegocioException excepcionDeRol) {
            throw new AssertionError("No deberia rechazar por rol a un administrador.", excepcionDeRol);
        } catch (RuntimeException otraExcepcion) {
        }
    }
}
