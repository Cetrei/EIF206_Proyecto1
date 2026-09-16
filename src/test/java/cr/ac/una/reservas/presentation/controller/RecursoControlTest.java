package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.presentation.model.RecursoModel;
import cr.ac.una.reservas.service.CategoriaDaoFalso;
import cr.ac.una.reservas.service.CategoriaService;
import cr.ac.una.reservas.service.RecursoDaoFalso;
import cr.ac.una.reservas.service.RecursoService;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RecursoControlTest {

    @AfterEach
    void cerrarSesion() {
        SesionControl.obtenerInstancia().cerrarSesion();
    }

    @Test
    void rechazaConstruirseSiElUsuarioNoEsAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Funcionario("111", "111", "Juan Perez", "3323"));
        RecursoDaoFalso recursoDao = new RecursoDaoFalso();
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();
        RecursoService recursoService = new RecursoService(recursoDao, categoriaDao);
        CategoriaService categoriaService = new CategoriaService(categoriaDao, recursoDao);

        assertThrows(
                ReglaDeNegocioException.class,
                () -> new RecursoControl(new RecursoModel(), null, null, recursoService, categoriaService)
        );
    }

    @Test
    void noRechazaPorRolSiElUsuarioEsAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        RecursoDaoFalso recursoDao = new RecursoDaoFalso();
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();
        RecursoService recursoService = new RecursoService(recursoDao, categoriaDao);
        CategoriaService categoriaService = new CategoriaService(categoriaDao, recursoDao);

        try {
            new RecursoControl(new RecursoModel(), null, null, recursoService, categoriaService);
        } catch (ReglaDeNegocioException excepcionDeRol) {
            throw new AssertionError("No deberia rechazar por rol a un administrador.", excepcionDeRol);
        } catch (RuntimeException otraExcepcion) {
        }
    }
}
