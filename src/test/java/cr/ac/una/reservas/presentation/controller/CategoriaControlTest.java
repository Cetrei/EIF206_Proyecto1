package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.presentation.model.CategoriaModel;
import cr.ac.una.reservas.presentation.mvc.TabCategorias;
import cr.ac.una.reservas.service.CategoriaDaoFalso;
import cr.ac.una.reservas.service.CategoriaService;
import cr.ac.una.reservas.service.RecursoDaoFalso;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void recortaEspaciosDeLaDescripcionAlCrear() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();
        CategoriaService categoriaService = new CategoriaService(categoriaDao, new RecursoDaoFalso());

        CategoriaModel modelo = new CategoriaModel();
        TabCategorias vista = new TabCategorias(modelo);
        new CategoriaControl(modelo, vista, null, categoriaService);

        vista.mostrarDescripcion("  Sala de Juntas  ");
        clickearBoton(vista.obtenerPanel(), "Guardar");

        assertEquals(1, categoriaDao.listarTodos().size());
        assertEquals("Sala de Juntas", categoriaDao.listarTodos().get(0).getDescripcion());
    }

    @Test
    void recortaEspaciosDeLaDescripcionAlModificar() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "admin"));
        CategoriaDaoFalso categoriaDao = new CategoriaDaoFalso();
        Categoria existente = new Categoria("CAT-000001", "Sala de Juntas");
        categoriaDao.guardar(existente);
        CategoriaService categoriaService = new CategoriaService(categoriaDao, new RecursoDaoFalso());

        CategoriaModel modelo = new CategoriaModel();
        TabCategorias vista = new TabCategorias(modelo);
        new CategoriaControl(modelo, vista, null, categoriaService);
        modelo.setCategoriaSeleccionada(existente);

        vista.mostrarDescripcion("  Sala de Juntas Principal  ");
        clickearBoton(vista.obtenerPanel(), "Guardar");

        assertEquals("Sala de Juntas Principal", categoriaDao.buscarPorId("CAT-000001").get().getDescripcion());
    }

    private static void clickearBoton(JComponent raiz, String texto) {
        AbstractButton boton = buscarBoton(raiz, texto);
        boton.doClick();
    }

    private static AbstractButton buscarBoton(java.awt.Container contenedor, String texto) {
        for (java.awt.Component componente : contenedor.getComponents()) {
            if (componente instanceof AbstractButton && texto.equals(((AbstractButton) componente).getText())) {
                return (AbstractButton) componente;
            }
            if (componente instanceof java.awt.Container) {
                AbstractButton encontrado = buscarBoton((java.awt.Container) componente, texto);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }
}
