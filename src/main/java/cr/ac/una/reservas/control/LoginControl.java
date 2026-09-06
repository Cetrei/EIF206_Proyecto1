package cr.ac.una.reservas.control;

import cr.ac.una.reservas.model.Usuario;
import cr.ac.una.reservas.presentation.LoginPanel;
import cr.ac.una.reservas.presentation.componentes.BarraSuperior;
import cr.ac.una.reservas.presentation.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.componentes.Popup;
import cr.ac.una.reservas.presentation.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.service.AutenticacionService;
import cr.ac.una.reservas.util.ReservaAppException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

/**
 * Controlador de la pantalla de Login (ver docs/06_control_presentation.md).
 * Deliberadamente delgado: toma el evento del boton Ingresar, valida que
 * los campos minimos esten presentes, delega la autenticacion real a
 * AutenticacionService y actualiza SesionControl o muestra el error.
 *
 * No debe existir logica de negocio (verificar la clave, etc.) aqui,
 * eso vive en service.
 */
public class LoginControl {

    private final LoginPanel vista;
    private final Frame ventanaPropietaria;
    private final AutenticacionService autenticacionService;

    public LoginControl(LoginPanel vista, Frame ventanaPropietaria) {
        this(vista, ventanaPropietaria, new AutenticacionService());
    }

    // Constructor para pruebas: permite inyectar un AutenticacionService
    // construido con Dao falsos en vez del real de DaoFactory.
    public LoginControl(LoginPanel vista, Frame ventanaPropietaria, AutenticacionService autenticacionService) {
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.autenticacionService = autenticacionService;
        this.vista.alIngresar(this::intentarIngresar);
        this.vista.alCambiarClave(this::abrirCambioClave);
    }

    private void intentarIngresar() {
        String id = vista.obtenerId();
        String clave = vista.obtenerContrasena();

        // Validacion de formato basico (control), segun docs/07_convenciones.md:
        // que los campos obligatorios no esten vacios. La validacion de la
        // regla de negocio (credenciales correctas) vive en service.
        if (id == null || id.isBlank() || clave == null || clave.isBlank()) {
            mostrarError("Debe ingresar el ID y la contraseña.");
            return;
        }

        try {
            Usuario usuario = autenticacionService.autenticar(id, clave);
            SesionControl.obtenerInstancia().iniciarSesion(usuario);
            navegarAVentanaPrincipal();
        } catch (ReservaAppException excepcion) {
            mostrarError(excepcion.getMessage());
        }
    }

    /**
     * Reemplaza el contenido de la ventana actual por VentanaPrincipal
     * (encabezado + pestanas), ya con la sesion iniciada en
     * SesionControl. Requiere que ventanaPropietaria sea el JFrame raiz
     * de la aplicacion (como lo arma Main), no un dialogo secundario.
     */
    private void navegarAVentanaPrincipal() {
        if (ventanaPropietaria instanceof JFrame) {
            VentanaPrincipalControl.mostrarEn((JFrame) ventanaPropietaria);
        }
    }

    /**
     * Dialogo de "Cambiar contraseña" desde la pantalla de Login (antes
     * de iniciar sesion), por lo que a diferencia de
     * VentanaPrincipalControl.abrirCambiarContrasenaPropia (que ya
     * conoce al usuario logueado via SesionControl) aqui hace falta
     * pedir tambien el ID. Delega la regla de negocio real en
     * AutenticacionService.cambiarClave, que ya existe (ver
     * docs/02_service.md); este metodo solo arma el formulario con los
     * mismos componentes de diseno que el resto de la app (Tarjeta,
     * CampoTexto, BotonPrimario), igual que el dialogo equivalente ya
     * construido en VentanaPrincipalControl.
     */
    private void abrirCambioClave() {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setTitulo("Cambiar contraseña");

        CampoTexto campoId = new CampoTexto(false);
        campoId.setEtiqueta("ID");
        campoId.setIcono(Icono.USUARIO);

        CampoTexto campoClaveActual = new CampoTexto(true);
        campoClaveActual.setEtiqueta("CONTRASEÑA ACTUAL");
        campoClaveActual.setIcono(Icono.CANDADO);

        CampoTexto campoClaveNueva = new CampoTexto(true);
        campoClaveNueva.setEtiqueta("CONTRASEÑA NUEVA");
        campoClaveNueva.setIcono(Icono.CANDADO);

        BotonPrimario botonConfirmar = new BotonPrimario();
        botonConfirmar.setTexto("Confirmar");
        botonConfirmar.setIcono(Icono.CHECK);

        JPanel contenido = tarjeta.obtenerPanelContenido();
        contenido.setLayout(new GridLayout(4, 1, 0, 10));
        contenido.add(campoId.obtenerPanel());
        contenido.add(campoClaveActual.obtenerPanel());
        contenido.add(campoClaveNueva.obtenerPanel());
        contenido.add(botonConfirmar.obtenerPanel());

        JDialog dialogo = new JDialog(ventanaPropietaria, "Cambiar contraseña", true);
        // Igual que el dialogo de "Mi Perfil" (ver
        // VentanaPrincipalControl.abrirMiCuenta): sin setUndecorated,
        // el dialogo conserva la barra de titulo nativa del sistema
        // operativo, que se ve como un marco blanco grueso sin
        // relacion con el tema oscuro/claro de la app, y sin boton de
        // cerrar propio del sistema de diseno.
        dialogo.setUndecorated(true);
        dialogo.setResizable(false);
        dialogo.getContentPane().setBackground(GestorTema.obtenerInstancia().temaActivo().colorFondoVentana());

        BarraSuperior barraSuperior = new BarraSuperior();
        barraSuperior.alCerrar(dialogo::dispose);

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.setBorder(new EmptyBorder(0, 16, 16, 16));
        envoltorio.add(barraSuperior.obtenerPanel(), BorderLayout.NORTH);
        envoltorio.add(tarjeta.obtenerPanel(), BorderLayout.CENTER);
        dialogo.setContentPane(envoltorio);

        botonConfirmar.alHacerClick(() -> {
            String id = campoId.obtenerTexto();
            if (id == null || id.isBlank()) {
                Popup.mostrarAviso(
                        ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo cambiar la contraseña",
                        "Debe ingresar el ID del usuario."
                );
                return;
            }
            try {
                autenticacionService.cambiarClave(
                        id,
                        campoClaveActual.obtenerTexto(),
                        campoClaveNueva.obtenerTexto()
                );
                dialogo.dispose();
                Popup.mostrarAviso(
                        ventanaPropietaria,
                        Popup.Tipo.CONFIRMACION,
                        "Cambiar contraseña",
                        "La contraseña se actualizó correctamente."
                );
            } catch (ReservaAppException excepcion) {
                Popup.mostrarAviso(
                        ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo cambiar la contraseña", excepcion.getMessage()
                );
            }
        });

        dialogo.pack();
        dialogo.setLocationRelativeTo(ventanaPropietaria);
        dialogo.setVisible(true);
    }

    private void mostrarError(String mensaje) {
        Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo ingresar", mensaje);
    }
}
