package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.model.Usuario;
import cr.ac.una.reservas.presentation.mvc.LoginPanel;
import cr.ac.una.reservas.presentation.mvc.TabActividades;
import cr.ac.una.reservas.presentation.mvc.TabCalendarizacion;
import cr.ac.una.reservas.presentation.mvc.TabCategorias;
import cr.ac.una.reservas.presentation.mvc.TabEstadisticas;
import cr.ac.una.reservas.presentation.mvc.TabFuncionarios;
import cr.ac.una.reservas.presentation.mvc.TabRecursos;
import cr.ac.una.reservas.presentation.mvc.TabReservas;
import cr.ac.una.reservas.presentation.mvc.VentanaPrincipal;
import cr.ac.una.reservas.presentation.mvc.componentes.PanelCuenta;
import cr.ac.una.reservas.presentation.mvc.componentes.Popup;
import cr.ac.una.reservas.presentation.mvc.componentes.ScrollBarTematizado;
import cr.ac.una.reservas.presentation.model.LoginModel;
import cr.ac.una.reservas.presentation.model.ActividadModel;
import cr.ac.una.reservas.presentation.model.CalendarizacionModel;
import cr.ac.una.reservas.presentation.model.CategoriaModel;
import cr.ac.una.reservas.presentation.model.EstadisticaModel;
import cr.ac.una.reservas.presentation.model.FuncionarioModel;
import cr.ac.una.reservas.presentation.model.RecursoModel;
import cr.ac.una.reservas.presentation.model.ReservaModel;
import cr.ac.una.reservas.presentation.model.VentanaPrincipalModel;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.service.AutenticacionService;
import cr.ac.una.reservas.service.FuncionarioService;
import cr.ac.una.reservas.service.ServiceFactory;
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
    private final VentanaPrincipalModel modelo;
    private final VentanaPrincipal vista;
    private final Frame ventanaPropietaria;
    private final AutenticacionService autenticacionService;
    private final FuncionarioService funcionarioService;

    public VentanaPrincipalControl(VentanaPrincipalModel modelo, VentanaPrincipal vista, Frame ventanaPropietaria) {
        this(
                modelo,
                vista,
                ventanaPropietaria,
                ServiceFactory.obtenerAutenticacionService(),
                ServiceFactory.obtenerFuncionarioService()
        );
    }

    public VentanaPrincipalControl(
            VentanaPrincipalModel modelo,
            VentanaPrincipal vista,
            Frame ventanaPropietaria,
            AutenticacionService autenticacionService,
            FuncionarioService funcionarioService
    ) {
        this.modelo = modelo;
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
        if (SesionControl.obtenerInstancia().esAdministrador()) {
            FuncionarioModel funcionarioModelo = new FuncionarioModel();
            TabFuncionarios tabFuncionarios = new TabFuncionarios(funcionarioModelo);
            new FuncionarioControl(funcionarioModelo, tabFuncionarios, ventanaPropietaria);
            colocarEnPestana(vista.obtenerPanelFuncionarios(), tabFuncionarios.obtenerPanel());

            CategoriaModel categoriaModelo = new CategoriaModel();
            TabCategorias tabCategorias = new TabCategorias(categoriaModelo);
            new CategoriaControl(categoriaModelo, tabCategorias, ventanaPropietaria);
            colocarEnPestana(vista.obtenerPanelCategorias(), tabCategorias.obtenerPanel());

            RecursoModel recursoModelo = new RecursoModel();
            TabRecursos tabRecursos = new TabRecursos(recursoModelo);
            new RecursoControl(recursoModelo, tabRecursos, ventanaPropietaria);
            colocarEnPestana(vista.obtenerPanelRecursos(), tabRecursos.obtenerPanel());
        }

        CalendarizacionModel calendarizacionModelo = new CalendarizacionModel();
        TabCalendarizacion tabCalendarizacion = new TabCalendarizacion(calendarizacionModelo);
        new CalendarizacionControl(calendarizacionModelo, tabCalendarizacion, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelCalendarizacion(), tabCalendarizacion.obtenerPanel());

        ActividadModel actividadModelo = new ActividadModel();
        TabActividades tabActividades = new TabActividades(actividadModelo);
        new ActividadControl(actividadModelo, tabActividades, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelActividades(), tabActividades.obtenerPanel());

        EstadisticaModel estadisticaModelo = new EstadisticaModel();

        TabEstadisticas tabEstadisticas = new TabEstadisticas(estadisticaModelo);
        new EstadisticaControl(estadisticaModelo, tabEstadisticas, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelEstadisticas(), tabEstadisticas.obtenerPanel());

        ReservaModel reservaModelo = new ReservaModel();
        TabReservas tabReservas = new TabReservas(reservaModelo);
        new ReservaControl(reservaModelo, tabReservas, ventanaPropietaria);
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
        modelo.setUsuario(nombre, rolTexto);

        if (!SesionControl.obtenerInstancia().esAdministrador()) {
            vista.mostrarSoloPestanasDeFuncionario();
        } else {
            vista.mostrarSoloPestanasDeAdministrador();
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

        LoginModel loginModelo = new LoginModel();
        LoginPanel loginPanel = new LoginPanel(loginModelo);
        new LoginControl(loginModelo, loginPanel, ventana);

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
            VentanaPrincipalModel modelo = new VentanaPrincipalModel();
            VentanaPrincipal vista = new VentanaPrincipal(modelo);
            new VentanaPrincipalControl(modelo, vista, ventana);

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
