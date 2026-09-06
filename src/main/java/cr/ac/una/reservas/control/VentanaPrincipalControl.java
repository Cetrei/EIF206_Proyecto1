package cr.ac.una.reservas.control;

import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.model.Usuario;
import cr.ac.una.reservas.presentation.LoginPanel;
import cr.ac.una.reservas.presentation.TabCategorias;
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

/**
 * Controlador de la ventana principal (post-login). Ver docs/06_control_presentation.md.
 * <p>
 * Responsabilidades, deliberadamente delgadas:
 * <ul>
 *     <li>Mostrar en el encabezado el nombre y rol del usuario que hay
 *     en SesionControl.</li>
 *     <li>Ocultar las pestanas exclusivas de administrador cuando el
 *     usuario logueado es un funcionario.</li>
 *     <li>Conectar el boton de cuenta del encabezado con un UNICO
 *     dialogo modal que contiene PanelCuenta en modo propio ("Mi
 *     Perfil"), incluyendo el enganche de "Cerrar sesion" (que delega
 *     en SesionControl y vuelve a la pantalla de Login).</li>
 * </ul>
 * No contiene logica de negocio propia: guardar el telefono delega en
 * FuncionarioService.modificar(...) y cambiar la clave delega en
 * AutenticacionService.cambiarClave(...) (ver docs/02_service.md).
 * <p>
 * Deliberadamente, este controlador abre como maximo un JDialog a la
 * vez sobre la ventana principal: "Cambiar contraseña" NO es un
 * segundo dialogo apilado encima de "Mi Perfil", es una vista distinta
 * dentro del mismo PanelCuenta (ver PanelCuenta.mostrarVistaClave /
 * mostrarVistaDatos), para no forzar a quien usa la app a navegar
 * dialogo > dialogo > dialogo.
 */
public class VentanaPrincipalControl {

    private final VentanaPrincipal vista;
    private final Frame ventanaPropietaria;
    private final AutenticacionService autenticacionService;
    private final FuncionarioService funcionarioService;

    public VentanaPrincipalControl(VentanaPrincipal vista, Frame ventanaPropietaria) {
        this(vista, ventanaPropietaria, new AutenticacionService(), new FuncionarioService());
    }

    // Constructor para pruebas: permite inyectar servicios construidos
    // con Dao falsos en vez de los reales de DaoFactory.
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

    /**
     * Pide confirmacion antes de cerrar la ventana principal (la raiz
     * de toda la aplicacion, a diferencia de la X de PanelCuenta que
     * solo cierra un dialogo secundario), para no perder trabajo sin
     * querer con un click accidental en la barra superior. Si el
     * usuario confirma, sale de la aplicacion igual que si hubiera
     * cerrado la ventana con el boton nativo del sistema operativo.
     */
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

    /**
     * Construye la vista+control de cada pestana principal y la coloca
     * dentro del panel vacio que expone VentanaPrincipal (ver
     * VentanaPrincipal.obtenerPanelX()), siguiendo "un controlador por
     * cada pantalla principal" (docs/06_control_presentation.md).
     * <p>
     * Solo se inicializan aqui las pestanas cuya vista+control ya
     * existen; el resto (Reservas, Funcionarios, Recursos,
     * Calendarizacion, Actividades, Estadisticas) sigue pendiente (ver
     * docs/06_control_presentation.md, seccion "Notas de continuidad")
     * y sus paneles quedan vacios por ahora, sin fallar por eso: cada
     * pestana se arma de forma independiente.
     */
    private void inicializarPestanas() {
        TabCategorias tabCategorias = new TabCategorias();
        new CategoriaControl(tabCategorias, ventanaPropietaria);
        colocarEnPestana(vista.obtenerPanelCategorias(), tabCategorias.obtenerPanel());
    }

    /**
     * Coloca el contenido de una pestana (titulo + formulario + tabla,
     * ver por ejemplo TabCategorias) dentro del panel vacio que expone
     * VentanaPrincipal, envuelto en un JScrollPane vertical.
     * <p>
     * Cada TabXxx (TabCategorias, y en el futuro TabFuncionarios,
     * TabRecursos, etc.) arma su contenido con alturas fijas pensadas
     * para una ventana comoda, pero la ventana principal es
     * redimensionable y nada obliga a un usuario a mantenerla grande:
     * sin un scroll a nivel de toda la pestana, al achicar la ventana
     * (o en pantallas pequenas) el contenido de abajo queda cortado y
     * sin forma de llegar a el. Centralizar el envoltorio aqui, en vez
     * de que cada TabXxx haga su propio JScrollPane, evita repetir el
     * mismo patron en cada pestana nueva que se agregue.
     */
    private void colocarEnPestana(JPanel panelPestana, JPanel contenido) {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        // ScrollBarTematizado instala un ScrollBarUI propio (fondo,
        // thumb y BOTONES DE FLECHA pintados a mano), en vez de dejar
        // el de Metal: sus botones de flecha ignoran las claves
        // ScrollBar.* de UIManager por completo y toman su color de
        // claves genericas "control*" compartidas por todo Metal.
        ScrollBarTematizado.aplicar(scroll.getVerticalScrollBar(), GestorTema.obtenerInstancia().temaActivo());
        // El contenido de cada TabXxx ya trae su propio ancho pensado
        // para llenar la pestana (fill horizontal en su .form); solo
        // interesa poder hacer scroll vertical cuando no entra todo el
        // alto, no limitar tambien el ancho disponible.
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panelPestana.setLayout(new BorderLayout());
        panelPestana.setBorder(BorderFactory.createEmptyBorder());
        panelPestana.add(scroll, BorderLayout.CENTER);
    }

    private void inicializarEncabezado() {
        Usuario usuario = SesionControl.obtenerInstancia().usuarioActual();
        if (usuario == null) {
            return;
        }

        String nombre = usuario instanceof Funcionario ? ((Funcionario) usuario).getNombre() : usuario.getId();
        String rolTexto = SesionControl.obtenerInstancia().esAdministrador() ? "ADMINISTRADOR" : "FUNCIONARIO";
        vista.mostrarUsuario(nombre, rolTexto);

        if (!SesionControl.obtenerInstancia().esAdministrador()) {
            vista.mostrarSoloPestanasDeFuncionario();
        }
    }

    /**
     * Abre "Mi Perfil" en modo propio (no administrador), con los datos
     * del usuario logueado, y engancha Guardar/Cambiar contrasena/Cerrar
     * sesion. Se usa un JDialog simple en vez de Popup porque Popup esta
     * pensado para mensajes con botones, no para un formulario completo
     * (ver Popup.form: pnlContenidoPopup es un grid de icono+titulo+
     * mensaje+botones, sin espacio para un panel como PanelCuenta).
     * <p>
     * Este es el UNICO JDialog que este metodo abre: "Cambiar
     * contraseña" se resuelve cambiando de vista dentro del propio
     * PanelCuenta (ver alCambiarContrasena mas abajo), no abriendo un
     * segundo dialogo.
     */
    private void abrirMiCuenta() {
        Usuario usuario = SesionControl.obtenerInstancia().usuarioActual();
        if (usuario == null) {
            return;
        }

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

    /**
     * Guarda los cambios de "Mi Perfil": el telefono siempre (para
     * cualquier Usuario que sea Funcionario) y, si el usuario logueado
     * es Administrador, no hay nada que persistir por ahora (ver
     * docs/01_model.md: Administrador no tiene nombre/telefono, y su
     * id/clave se manejan por AutenticacionService.cambiarClave, no por
     * este boton).
     */
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

    /**
     * Llama a AutenticacionService.cambiarClave con los datos de la
     * vista de clave de PanelCuenta (ver PanelCuenta.mostrarVistaClave).
     * Si tiene exito, vuelve a la vista de datos del mismo dialogo (sin
     * cerrar ni abrir ningun JDialog); si falla, deja la vista de clave
     * abierta con un Popup de error para que el usuario corrija.
     */
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

    /**
     * Cierra la sesion actual en SesionControl y reemplaza la ventana
     * principal por una nueva pantalla de Login, en la misma ventana
     * (JFrame) para no dejar dos ventanas abiertas. Sigue el mismo
     * patron de arranque que Main.iniciar().
     */
    private void cerrarSesionYVolverALogin() {
        SesionControl.obtenerInstancia().cerrarSesion();

        if (!(ventanaPropietaria instanceof JFrame)) {
            return;
        }
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

    /**
     * Construye la ventana principal completa (vista + control) y la
     * coloca como contentPane del JFrame recibido. Pensado para que
     * LoginControl la use al navegar tras un login exitoso, y para que
     * VentanaPrincipalControl.cerrarSesionYVolverALogin/Main la usen de
     * forma simetrica.
     */
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
