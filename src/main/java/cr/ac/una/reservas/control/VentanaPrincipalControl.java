package cr.ac.una.reservas.control;

import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.model.Usuario;
import cr.ac.una.reservas.presentation.LoginPanel;
import cr.ac.una.reservas.presentation.TabActividades;
import cr.ac.una.reservas.presentation.TabCalendarizacion;
import cr.ac.una.reservas.presentation.TabCategorias;
import cr.ac.una.reservas.presentation.TabEstadisticas;
import cr.ac.una.reservas.presentation.TabFuncionarios;
import cr.ac.una.reservas.presentation.TabRecursos;
import cr.ac.una.reservas.presentation.TabReservas;
import cr.ac.una.reservas.presentation.VentanaPrincipal;
import cr.ac.una.reservas.presentation.componentes.PanelCuenta;
import cr.ac.una.reservas.presentation.componentes.Popup;
import cr.ac.una.reservas.presentation.componentes.ScrollBarTematizado;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.service.AutenticacionService;
import cr.ac.una.reservas.service.FuncionarioService;
import cr.ac.una.reservas.util.ReservaAppException;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Frame;

public class VentanaPrincipalControl {
    private final VentanaPrincipal vista;
    private final Frame ventanaPropietaria;
    private final AutenticacionService autenticacionService;
    private final FuncionarioService funcionarioService;

    public VentanaPrincipalControl(VentanaPrincipal vista, Frame ventanaPropietaria) {
        this(vista, ventanaPropietaria, new AutenticacionService(), new FuncionarioService());
    }

    // Constructor para pruebas
    public VentanaPrincipalControl(
            VentanaPrincipal vista,
            Frame ventanaPropietaria,
            AutenticacionService autenticacionService,
            FuncionarioService funcionarioService
    ) {
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.autenticacionService = autenticacionService;
        this.funcionarioService = funcionarioService;
        inicializarEncabezado();
        inicializarPestanas();
        this.vista.alAbrirCuenta(this::abrirMiCuenta);
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

    private void inicializarPestanas() {
        TabFuncionarios tabFuncionarios = new TabFuncionarios();
        new FuncionarioControl(tabFuncionarios, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelFuncionarios(), tabFuncionarios.obtenerPanel());

        TabCategorias tabCategorias = new TabCategorias();
        new CategoriaControl(tabCategorias, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelCategorias(), tabCategorias.obtenerPanel());

        TabRecursos tabRecursos = new TabRecursos();
        new RecursoControl(tabRecursos, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelRecursos(), tabRecursos.obtenerPanel());

        TabCalendarizacion tabCalendarizacion = new TabCalendarizacion();
        new CalendarizacionControl(tabCalendarizacion, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelCalendarizacion(), tabCalendarizacion.obtenerPanel());

        TabActividades tabActividades = new TabActividades();
        new ActividadControl(tabActividades, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelActividades(), tabActividades.obtenerPanel());

        TabEstadisticas tabEstadisticas = new TabEstadisticas();
        new EstadisticaControl(tabEstadisticas, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelEstadisticas(), tabEstadisticas.obtenerPanel());

        TabReservas tabReservas = new TabReservas();
        new ReservaControl(tabReservas, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelReservas(), tabReservas.obtenerPanel());
    }

    private void colocarEnPestana(JPanel panelPestana, JPanel contenido) {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        ScrollBarTematizado.aplicar(scroll.getVerticalScrollBar(), GestorTema.obtenerInstancia().temaActivo());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelPestana.setLayout(new BorderLayout());
        panelPestana.setBorder(BorderFactory.createEmptyBorder());
        panelPestana.add(scroll, BorderLayout.CENTER);
    }

    private void inicializarEncabezado() {
        Usuario usuario = SesionControl.obtenerInstancia().usuarioActual();
        if (usuario == null) return;

        String nombre = usuario instanceof Funcionario ? ((Funcionario) usuario).getNombre() : usuario.getId();
        String rolTexto = SesionControl.obtenerInstancia().esAdministrador() ? "ADMINISTRADOR" : "FUNCIONARIO";
        vista.mostrarUsuario(nombre, rolTexto);

        if (!SesionControl.obtenerInstancia().esAdministrador()) {
            vista.mostrarSoloPestanasDeFuncionario();
        }
    }

    private void abrirMiCuenta() {
        Usuario usuario = SesionControl.obtenerInstancia().usuarioActual();
        if (usuario == null) return;

        PanelCuenta panelCuenta = new PanelCuenta();
        panelCuenta.setModoAdmin(false);

        String nombre = usuario instanceof Funcionario ? ((Funcionario) usuario).getNombre() : usuario.getId();
        String telefono = usuario instanceof Funcionario ? ((Funcionario) usuario).getTelefono() : "";
        panelCuenta.cargarDatos(nombre, usuario.getId(), telefono);

        JDialog dialogoCuenta = new JDialog(ventanaPropietaria, "Mi Perfil", true);
        dialogoCuenta.setUndecorated(true);
        dialogoCuenta.setResizable(false);
        dialogoCuenta.getContentPane().setBackground(GestorTema.obtenerInstancia().temaActivo().colorFondoVentana());
        dialogoCuenta.setContentPane(panelCuenta.obtenerPanel());

        panelCuenta.alGuardar(() -> guardarMiCuenta(panelCuenta));
        panelCuenta.alCambiarContrasena(panelCuenta::mostrarVistaClave);
        panelCuenta.alConfirmarCambioClave(() -> confirmarCambioClave(panelCuenta, usuario.getId()));
        panelCuenta.alCerrarSesion(() -> {
            dialogoCuenta.dispose();
            cerrarSesionYVolverALogin();
        });
        panelCuenta.alCerrar(dialogoCuenta::dispose);

        dialogoCuenta.pack();
        dialogoCuenta.setLocationRelativeTo(ventanaPropietaria);
        dialogoCuenta.setVisible(true);
    }

    private void guardarMiCuenta(PanelCuenta panelCuenta) {
        Usuario usuario = SesionControl.obtenerInstancia().usuarioActual();
        if (!(usuario instanceof Funcionario)) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.INFORMACION,
                    "Guardar cambios",
                    "No hay datos adicionales que guardar para una cuenta de administrador."
            );
            return;
        }
        try {
            Funcionario funcionario = (Funcionario) usuario;
            funcionario.setTelefono(panelCuenta.obtenerTelefono());
            funcionarioService.modificar(funcionario);
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Guardar cambios",
                    "Los cambios se guardaron correctamente."
            );
        } catch (ReservaAppException excepcion) {
            Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo guardar", excepcion.getMessage());
        }
    }

    private void confirmarCambioClave(PanelCuenta panelCuenta, String idUsuario) {
        try {
            autenticacionService.cambiarClave(
                    idUsuario,
                    panelCuenta.obtenerClaveActual(),
                    panelCuenta.obtenerClaveNueva()
            );
            panelCuenta.mostrarVistaDatos();
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
    }

    private void cerrarSesionYVolverALogin() {
        SesionControl.obtenerInstancia().cerrarSesion();

        if (!(ventanaPropietaria instanceof JFrame)) return;
        JFrame ventana = (JFrame) ventanaPropietaria;

        LoginPanel loginPanel = new LoginPanel();
        new LoginControl(loginPanel, ventana);

        ventana.setContentPane(loginPanel.obtenerPanel());
        ventana.setMinimumSize(null);
        ventana.pack();
        ventana.setMinimumSize(ventana.getSize());
        ventana.setLocationRelativeTo(null);
        ventana.revalidate();
        ventana.repaint();
    }

    public static void mostrarEn(JFrame ventana) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal vista = new VentanaPrincipal();
            new VentanaPrincipalControl(vista, ventana);

            ventana.setContentPane(vista.obtenerPanel());
            ventana.setMinimumSize(null);
            ventana.pack();
            ventana.setMinimumSize(ventana.getSize());
            ventana.setLocationRelativeTo(null);
            ventana.revalidate();
            ventana.repaint();
        });
    }
}
