package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.presentation.model.LoginModel;
import cr.ac.una.reservas.presentation.mvc.LoginPanel;
import cr.ac.una.reservas.presentation.mvc.componentes.CampoTexto;
import cr.ac.una.reservas.service.AdministradorDaoFalso;
import cr.ac.una.reservas.service.AutenticacionService;
import cr.ac.una.reservas.service.FuncionarioDaoFalso;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.JDialog;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Container;
import java.awt.Frame;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginControlTest {

    private static final String ID_ADMIN = "admin";
    private static final String CLAVE_ADMIN = "123";

    @AfterEach
    void cerrarSesion() {
        SesionControl.obtenerInstancia().cerrarSesion();
    }

    @Test
    void recortaEspaciosDelIdAlIngresar() {
        AdministradorDaoFalso administradorDao = new AdministradorDaoFalso();
        administradorDao.guardar(new Administrador(ID_ADMIN, CLAVE_ADMIN));
        AutenticacionService autenticacionService = new AutenticacionService(administradorDao, new FuncionarioDaoFalso());

        LoginModel modelo = new LoginModel();
        LoginPanel vista = new LoginPanel(modelo);
        Frame ventanaPropietaria = new Frame();
        new LoginControl(modelo, vista, ventanaPropietaria, autenticacionService);

        campoTexto(vista.obtenerPanel()).setText(" " + ID_ADMIN + " ");
        campoClave(vista.obtenerPanel()).setText(CLAVE_ADMIN);
        campoClave(vista.obtenerPanel()).postActionEvent();

        assertTrue(SesionControl.obtenerInstancia().haySesionActiva());
        assertNull(modelo.getMensajeError());
    }

    @Test
    void recortaEspaciosDelIdAlCambiarClave() throws Exception {
        AdministradorDaoFalso administradorDao = new AdministradorDaoFalso();
        administradorDao.guardar(new Administrador(ID_ADMIN, CLAVE_ADMIN));
        AutenticacionService autenticacionService = new AutenticacionService(administradorDao, new FuncionarioDaoFalso());

        LoginModel modelo = new LoginModel();
        LoginPanel vista = new LoginPanel(modelo);
        Frame ventanaPropietaria = new Frame();
        LoginControl control = new LoginControl(modelo, vista, ventanaPropietaria, autenticacionService);

        CampoTexto campoId = new CampoTexto(false);
        campoId.mostrarValor(" " + ID_ADMIN + " ");
        CampoTexto campoClaveActual = new CampoTexto(true);
        campoClaveActual.mostrarValor(CLAVE_ADMIN);
        CampoTexto campoClaveNueva = new CampoTexto(true);
        campoClaveNueva.mostrarValor("nueva-clave");
        JDialog dialogo = new JDialog(ventanaPropietaria, "Cambiar contraseña", false);

        invocarConfirmarCambioClave(control, dialogo, campoId, campoClaveActual, campoClaveNueva);

        assertEquals("nueva-clave", administradorDao.buscarPorId(ID_ADMIN).get().getClave());
    }

    private static void invocarConfirmarCambioClave(
            LoginControl control, JDialog dialogo, CampoTexto campoId, CampoTexto campoClaveActual, CampoTexto campoClaveNueva
    ) throws Exception {
        Method metodo = LoginControl.class.getDeclaredMethod(
                "confirmarCambioClave", JDialog.class, CampoTexto.class, CampoTexto.class, CampoTexto.class
        );
        metodo.setAccessible(true);
        metodo.invoke(control, dialogo, campoId, campoClaveActual, campoClaveNueva);
    }

    private static JTextField campoTexto(Container contenedor) {
        for (java.awt.Component componente : contenedor.getComponents()) {
            if (componente instanceof JPasswordField) continue;
            if (componente instanceof JTextField) return (JTextField) componente;
            if (componente instanceof Container) {
                JTextField encontrado = campoTexto((Container) componente);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private static JPasswordField campoClave(Container contenedor) {
        for (java.awt.Component componente : contenedor.getComponents()) {
            if (componente instanceof JPasswordField) return (JPasswordField) componente;
            if (componente instanceof Container) {
                JPasswordField encontrado = campoClave((Container) componente);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }
}
