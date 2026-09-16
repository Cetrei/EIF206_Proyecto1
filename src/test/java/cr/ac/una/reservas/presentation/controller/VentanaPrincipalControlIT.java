package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.presentation.model.VentanaPrincipalModel;
import cr.ac.una.reservas.presentation.mvc.VentanaPrincipal;
import cr.ac.una.reservas.presentation.mvc.componentes.PanelCuenta;
import cr.ac.una.reservas.service.FuncionarioService;
import cr.ac.una.reservas.service.ServiceFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.awt.Frame;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VentanaPrincipalControlIT {

    private static final FuncionarioService FUNCIONARIO_SERVICE = ServiceFactory.obtenerFuncionarioService();

    @AfterEach
    void cerrarSesion() {
        SesionControl.obtenerInstancia().cerrarSesion();
    }

    @Test
    void recortaEspaciosDelTelefonoAlGuardarMiCuenta() throws Exception {
        String id = idUnico();
        Funcionario funcionario = new Funcionario(id, id, "Funcionario De Prueba", "0000-0000");
        FUNCIONARIO_SERVICE.crear(funcionario);
        SesionControl.obtenerInstancia().iniciarSesion(funcionario);

        VentanaPrincipalControl control = construirControl();
        PanelCuenta panelCuenta = new PanelCuenta();
        panelCuenta.cargarDatos(funcionario.getNombre(), funcionario.getId(), "  8888-8888  ");

        invocarGuardarMiCuenta(control, panelCuenta);

        assertEquals("8888-8888", FUNCIONARIO_SERVICE.buscarPorId(id).get().getTelefono());
    }

    @Test
    void rechazaTelefonoConCaracteresInvalidosSinModificarElFuncionario() throws Exception {
        String id = idUnico();
        Funcionario funcionario = new Funcionario(id, id, "Funcionario De Prueba", "0000-0000");
        FUNCIONARIO_SERVICE.crear(funcionario);
        SesionControl.obtenerInstancia().iniciarSesion(funcionario);

        VentanaPrincipalControl control = construirControl();
        PanelCuenta panelCuenta = new PanelCuenta();
        panelCuenta.cargarDatos(funcionario.getNombre(), funcionario.getId(), "no-es-un-telefono");

        invocarGuardarMiCuenta(control, panelCuenta);

        assertEquals("0000-0000", FUNCIONARIO_SERVICE.buscarPorId(id).get().getTelefono());
    }

    @Test
    void rechazaTelefonoVacioSinModificarElFuncionario() throws Exception {
        String id = idUnico();
        Funcionario funcionario = new Funcionario(id, id, "Funcionario De Prueba", "0000-0000");
        FUNCIONARIO_SERVICE.crear(funcionario);
        SesionControl.obtenerInstancia().iniciarSesion(funcionario);

        VentanaPrincipalControl control = construirControl();
        PanelCuenta panelCuenta = new PanelCuenta();
        panelCuenta.cargarDatos(funcionario.getNombre(), funcionario.getId(), "   ");

        invocarGuardarMiCuenta(control, panelCuenta);

        assertEquals("0000-0000", FUNCIONARIO_SERVICE.buscarPorId(id).get().getTelefono());
    }

    private static String idUnico() {
        return "IT-" + System.nanoTime();
    }

    private static VentanaPrincipalControl construirControl() {
        VentanaPrincipalModel modelo = new VentanaPrincipalModel();
        VentanaPrincipal vista = new VentanaPrincipal(modelo);
        Frame ventanaPropietaria = new Frame();
        return new VentanaPrincipalControl(modelo, vista, ventanaPropietaria);
    }

    private static void invocarGuardarMiCuenta(VentanaPrincipalControl control, PanelCuenta panelCuenta) throws Exception {
        Method metodo = VentanaPrincipalControl.class.getDeclaredMethod("guardarMiCuenta", PanelCuenta.class);
        metodo.setAccessible(true);
        metodo.invoke(control, panelCuenta);
    }
}
