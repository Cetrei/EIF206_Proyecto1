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

    // No se construye la Vista real (TabFuncionarios) porque hoy depende de $$$setupUI$$$(),
    // generado por el GUI Designer de IntelliJ solo mientras el .form exista: fuera de ese
    // runtime, createUIComponents() nunca se llama y sus campos quedan null. Esto se resuelve
    // exportando los .form a codigo plano (ver TODO.md); mientras tanto, la excepcion de la
    // Vista al llegar a null no puede confundirse con un rechazo de ReglaDeNegocioException,
    // que es lo unico que esta prueba necesita verificar.
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
            // Se espera NullPointerException por la Vista null; no es lo que esta prueba verifica.
        }
    }
}
