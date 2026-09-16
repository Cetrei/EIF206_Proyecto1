package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.presentation.model.CategoriaModel;
import cr.ac.una.reservas.service.CategoriaDaoFalso;
import cr.ac.una.reservas.service.CategoriaService;
import cr.ac.una.reservas.service.RecursoDaoFalso;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoriaControlTest {

    @AfterEach
    void cerrarSesion() {
        SesionControl.obtenerInstancia().cerrarSesion();
    }

    @Test
    void rechazaConstruirseSiElUsuarioNoEsAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Funcionario("111", "111", "Juan Perez", "3323"));
        CategoriaService categoriaService = new CategoriaService(new CategoriaDaoFalso(), new RecursoDaoFalso());

        assertThrows(
                ReglaDeNegocioException.class,
                () -> new CategoriaControl(new CategoriaModel(), null, null, categoriaService)
        );
    }

    @Test
    void noRechazaPorRolSiElUsuarioEsAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        CategoriaService categoriaService = new CategoriaService(new CategoriaDaoFalso(), new RecursoDaoFalso());

        try {
            new CategoriaControl(new CategoriaModel(), null, null, categoriaService);
        } catch (ReglaDeNegocioException excepcionDeRol) {
            throw new AssertionError("No deberia rechazar por rol a un administrador.", excepcionDeRol);
        } catch (RuntimeException otraExcepcion) {
        }
    }
}
