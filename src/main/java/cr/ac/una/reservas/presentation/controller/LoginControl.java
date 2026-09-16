package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Usuario;
import cr.ac.una.reservas.presentation.mvc.LoginPanel;
import cr.ac.una.reservas.presentation.mvc.componentes.BarraSuperior;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.mvc.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.mvc.componentes.Popup;
import cr.ac.una.reservas.presentation.mvc.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.mvc.iconos.Icono;
import cr.ac.una.reservas.presentation.model.LoginModel;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.service.AutenticacionService;
import cr.ac.una.reservas.service.ServiceFactory;
import cr.ac.una.reservas.util.ReservaAppException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

public class LoginControl {

    private final LoginModel modelo;
    private final LoginPanel vista;
    private final Frame ventanaPropietaria;
    private final AutenticacionService autenticacionService;

    public LoginControl(LoginModel modelo, LoginPanel vista, Frame ventanaPropietaria) {
        this(modelo, vista, ventanaPropietaria, ServiceFactory.obtenerAutenticacionService());
    }

    public LoginControl(
            LoginModel modelo, LoginPanel vista, Frame ventanaPropietaria, AutenticacionService autenticacionService
    ) {
        this.modelo = modelo;
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.autenticacionService = autenticacionService;
        this.vista.alIngresar(this::intentarIngresar);
        this.vista.alCambiarClave(this::abrirCambioClave);
        this.vista.alCerrar(this::confirmarSalidaDeAplicacion);
    }

    private void confirmarSalidaDeAplicacion() {
        Popup popup = new Popup(ventanaPropietaria);
        popup.setTipo(Popup.Tipo.ERROR);
        popup.setTitulo("Salir del sistema");
        popup.setMensaje("¿Seguro desea salir del Sistema de Reserva de Recursos?");
        java.util.List<Popup.AccionPopup> acciones = new java.util.ArrayList<>();
        acciones.add(new Popup.AccionPopup("Cancelar", false, null));
        acciones.add(new Popup.AccionPopup("Salir", true, () -> System.exit(0)));
        popup.setAcciones(acciones);
        popup.mostrar();
    }

    private void intentarIngresar() {
        String id = vista.obtenerId();
        String clave = vista.obtenerContrasena();

        if (id == null || id.isBlank() || clave == null || clave.isBlank()) {
            modelo.setMensajeError("Debe ingresar el ID y la contraseña.");
            return;
        }

        try {
            Usuario usuario = autenticacionService.autenticar(id, clave);
            SesionControl.obtenerInstancia().iniciarSesion(usuario);
            navegarAVentanaPrincipal();
        } catch (ReservaAppException excepcion) {
            modelo.setMensajeError(excepcion.getMessage());
        }
    }

    private void navegarAVentanaPrincipal() {
        if (ventanaPropietaria instanceof JFrame) {
            VentanaPrincipalControl.mostrarEn((JFrame) ventanaPropietaria);
        }
    }

    private void abrirCambioClave() {
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

        Tarjeta tarjeta = armarTarjetaCambioClave(campoId, campoClaveActual, campoClaveNueva, botonConfirmar);
        JDialog dialogo = armarDialogoCambioClave(tarjeta);

        botonConfirmar.alHacerClick(() ->
                confirmarCambioClave(dialogo, campoId, campoClaveActual, campoClaveNueva));

        dialogo.pack();
        dialogo.setLocationRelativeTo(ventanaPropietaria);
        dialogo.setVisible(true);
    }

    private Tarjeta armarTarjetaCambioClave(
            CampoTexto campoId, CampoTexto campoClaveActual, CampoTexto campoClaveNueva, BotonPrimario botonConfirmar
    ) {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setTitulo("Cambiar contraseña");

        JPanel contenido = tarjeta.obtenerPanelContenido();
        contenido.setLayout(new GridLayout(4, 1, 0, 10));
        contenido.add(campoId.obtenerPanel());
        contenido.add(campoClaveActual.obtenerPanel());
        contenido.add(campoClaveNueva.obtenerPanel());
        contenido.add(botonConfirmar.obtenerPanel());
        return tarjeta;
    }

    private JDialog armarDialogoCambioClave(Tarjeta tarjeta) {
        JDialog dialogo = new JDialog(ventanaPropietaria, "Cambiar contraseña", true);
        dialogo.setUndecorated(true);
        dialogo.setResizable(false);

        BarraSuperior barraSuperior = new BarraSuperior();
        barraSuperior.alCerrar(dialogo::dispose);

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(true);
        envoltorio.setBackground(GestorTema.obtenerInstancia().temaActivo().colorFondoVentana());
        envoltorio.setBorder(new EmptyBorder(0, 16, 16, 16));
        envoltorio.add(barraSuperior.obtenerPanel(), BorderLayout.NORTH);
        envoltorio.add(tarjeta.obtenerPanel(), BorderLayout.CENTER);
        dialogo.setContentPane(envoltorio);
        return dialogo;
    }

    private void confirmarCambioClave(
            JDialog dialogo, CampoTexto campoId, CampoTexto campoClaveActual, CampoTexto campoClaveNueva
    ) {
        String id = campoId.obtenerTexto();
        if (id == null || id.isBlank()) {
            Popup.mostrarAviso(
                    ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo cambiar la contraseña",
                    "Debe ingresar el ID del usuario."
            );
            return;
        }
        try {
            autenticacionService.cambiarClave(id, campoClaveActual.obtenerTexto(), campoClaveNueva.obtenerTexto());
            dialogo.dispose();
            Popup.mostrarAviso(
                    ventanaPropietaria, Popup.Tipo.CONFIRMACION, "Cambiar contraseña",
                    "La contraseña se actualizó correctamente."
            );
        } catch (ReservaAppException excepcion) {
            Popup.mostrarAviso(
                    ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo cambiar la contraseña", excepcion.getMessage()
            );
        }
    }
}
