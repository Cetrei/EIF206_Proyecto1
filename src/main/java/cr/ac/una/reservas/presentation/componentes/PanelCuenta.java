package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

/**
 * Panel de cuenta del usuario, pensado para reciclarse en dos contextos
 * (ver aclaracion del equipo):
 * <ul>
 *     <li>Modo propio ("Mi Perfil"): el usuario logueado ve sus datos,
 *     solo puede editar el telefono y guardar, y tiene ambos botones de
 *     accion (cambiar contrasena y cerrar sesion).</li>
 *     <li>Modo administrador ("Editando Perfil"): un administrador abre
 *     el perfil de otro funcionario; ahi si puede editar nombre y
 *     cedula ademas del telefono, pero "Cerrar sesion" no tiene sentido
 *     (no es su propia sesion) asi que se oculta. "Cambiar contrasena"
 *     si aplica (el admin puede sobreescribir la clave de otro).</li>
 * </ul>
 * No se agregan componentes nuevos para esto: el propio panel decide
 * que campos son editables y que botones se muestran segun el modo.
 * <p>
 * "Cambiar contraseña" es una SEGUNDA VISTA dentro de este mismo panel
 * (CardLayout en pnlVistas), no un JDialog nuevo: quien contenga a
 * PanelCuenta (por ejemplo VentanaPrincipalControl) abre un unico
 * dialogo modal para todo "Mi Perfil", y este panel decide internamente
 * que vista mostrar. Esto evita apilar un dialogo modal encima de otro
 * (dialogo > dialogo > dialogo), que es dificil de seguir para quien
 * usa la app y facil de dejar en un estado inconsistente (por ejemplo,
 * cerrar el de atras sin cerrar el de adelante).
 * <p>
 * TarjetaDatos y TarjetaAcciones son hojas custom-create="true" en el
 * .form (sin hijos declarados en el XML, igual que txtID/btnIngresar en
 * LoginPanel.form): todo su contenido interno lo arma este archivo. La
 * tarjeta de "Cambiar contraseña" (TarjetaClave) no existe en el .form:
 * se agrega enteramente por codigo dentro de pnlVistas, ya que el
 * Designer no soporta bien CardLayout con paneles that se intercambian.
 */
public class PanelCuenta implements CambioTemaListener {

    private static final String VISTA_DATOS = "datos";
    private static final String VISTA_CLAVE = "clave";

    private JPanel PanelCuenta;
    private JPanel TarjetaDatos;
    private JPanel TarjetaAcciones;
    private JLabel lblTitulo;
    private JPanel BarraSuperior;

    private BarraSuperior barraSuperiorReal;

    private JLabel lblNombre;
    private JLabel lblID;
    private JLabel lblTelefono;
    private JTextField txtNombre;
    private JTextField txtID;
    private JTextField txtTelefono;

    private Tarjeta tarjetaDatosReal;
    private Tarjeta tarjetaAccionesReal;
    private BotonPrimario botonGuardarReal;
    private BotonSecundario botonCambiarContrasenaReal;
    private BotonSecundario botonCerrarSesionReal;

    // Segunda vista ("Cambiar contraseña"), armada enteramente por
    // codigo dentro de pnlVistas (ver arriba).
    private JPanel pnlVistas;
    private CardLayout layoutVistas;
    private Tarjeta tarjetaClaveReal;
    private CampoTexto campoClaveActualReal;
    private CampoTexto campoClaveNuevaReal;
    private BotonPrimario botonConfirmarClaveReal;
    private BotonSecundario botonVolverReal;

    private boolean modoAdmin;

    public PanelCuenta() {
        GestorTema.obtenerInstancia().agregarListener(this);
        setModoAdmin(false);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    /**
     * TarjetaDatos/TarjetaAcciones se declaran custom-create="true" en
     * el .form (como JPanel sin contenido), asi que aqui se reemplazan
     * por instancias reales de Tarjeta (fondo con esquinas redondeadas,
     * igual estilo que el resto de la app) y se arma su contenido a
     * mano con los campos y botones reales del sistema de diseño.
     * <p>
     * Ademas, PanelCuenta se envuelve aqui mismo dentro de un
     * CardLayout (pnlVistas) para poder alternar entre la vista de
     * datos y la vista de cambio de contraseña sin abrir un segundo
     * dialogo (ver javadoc de la clase).
     */
    private void createUIComponents() {
        barraSuperiorReal = new BarraSuperior();
        BarraSuperior = barraSuperiorReal.obtenerPanel();

        tarjetaDatosReal = new Tarjeta();
        TarjetaDatos = tarjetaDatosReal.obtenerPanel();

        tarjetaAccionesReal = new Tarjeta();
        TarjetaAcciones = tarjetaAccionesReal.obtenerPanel();

        lblNombre = new JLabel("Nombre");
        lblID = new JLabel("Identificación");
        lblTelefono = new JLabel("Teléfono");
        txtNombre = new JTextField();
        txtID = new JTextField();
        txtTelefono = new JTextField();

        botonGuardarReal = new BotonPrimario();
        botonGuardarReal.setTexto("Guardar");
        botonGuardarReal.setIcono(Icono.GUARDAR);

        botonCambiarContrasenaReal = new BotonSecundario();
        botonCambiarContrasenaReal.setTexto("Cambiar contraseña");
        botonCambiarContrasenaReal.setIcono(Icono.CANDADO);

        botonCerrarSesionReal = new BotonSecundario();
        botonCerrarSesionReal.setVariante(BotonSecundario.Variante.PELIGRO);
        botonCerrarSesionReal.setTexto("Cerrar sesión");
        botonCerrarSesionReal.setIcono(Icono.SALIR);

        armarTarjetaDatos();
        armarTarjetaAcciones();
        armarTarjetaClave();

        // pnlVistas sustituye a PanelCuenta como "raiz visual": el
        // contenido original (titulo + TarjetaDatos + TarjetaAcciones)
        // se agrupa en pnlVistaDatos y se registra como una tarjeta del
        // CardLayout, junto a pnlVistaClave.
        layoutVistas = new CardLayout();
        pnlVistas = new JPanel(layoutVistas);
        pnlVistas.setOpaque(false);
    }

    /**
     * Arma la segunda vista ("Cambiar contraseña"): mismos componentes
     * de diseño que el dialogo que antes se abria por separado
     * (Tarjeta + CampoTexto + BotonPrimario), mas un BotonSecundario
     * "Volver" para regresar a la vista de datos sin cerrar el
     * dialogo contenedor.
     */
    private void armarTarjetaClave() {
        tarjetaClaveReal = new Tarjeta();
        tarjetaClaveReal.setTitulo("Cambiar contraseña");

        campoClaveActualReal = new CampoTexto(true);
        campoClaveActualReal.setEtiqueta("CONTRASEÑA ACTUAL");
        campoClaveActualReal.setIcono(Icono.CANDADO);

        campoClaveNuevaReal = new CampoTexto(true);
        campoClaveNuevaReal.setEtiqueta("CONTRASEÑA NUEVA");
        campoClaveNuevaReal.setIcono(Icono.CANDADO);

        botonConfirmarClaveReal = new BotonPrimario();
        botonConfirmarClaveReal.setTexto("Confirmar");
        botonConfirmarClaveReal.setIcono(Icono.CHECK);

        botonVolverReal = new BotonSecundario();
        botonVolverReal.setTexto("Volver");

        JPanel contenido = tarjetaClaveReal.obtenerPanelContenido();
        contenido.setLayout(new GridLayout(4, 1, 0, 10));
        contenido.add(campoClaveActualReal.obtenerPanel());
        contenido.add(campoClaveNuevaReal.obtenerPanel());
        contenido.add(botonConfirmarClaveReal.obtenerPanel());
        contenido.add(botonVolverReal.obtenerPanel());

        // Volver siempre regresa a la vista de datos y limpia los
        // campos, para no dejar una clave a medio escribir si el
        // usuario entra de nuevo mas tarde.
        botonVolverReal.alHacerClick(this::mostrarVistaDatos);
    }

    private void armarTarjetaDatos() {
        JPanel contenido = tarjetaDatosReal.obtenerPanelContenido();
        contenido.setLayout(new GridBagLayout());
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.weightx = 1;
        restricciones.gridx = 0;

        restricciones.gridy = 0;
        restricciones.insets = new Insets(0, 0, 4, 0);
        contenido.add(lblNombre, restricciones);
        restricciones.gridy = 1;
        restricciones.insets = new Insets(0, 0, 10, 0);
        contenido.add(txtNombre, restricciones);
        restricciones.gridy = 2;
        restricciones.insets = new Insets(0, 0, 4, 0);
        contenido.add(lblID, restricciones);
        restricciones.gridy = 3;
        restricciones.insets = new Insets(0, 0, 10, 0);
        contenido.add(txtID, restricciones);
        restricciones.gridy = 4;
        restricciones.insets = new Insets(0, 0, 4, 0);
        contenido.add(lblTelefono, restricciones);
        restricciones.gridy = 5;
        restricciones.insets = new Insets(0, 0, 16, 0);
        contenido.add(txtTelefono, restricciones);
        restricciones.gridy = 6;
        restricciones.insets = new Insets(0, 0, 0, 0);
        contenido.add(botonGuardarReal.obtenerPanel(), restricciones);
    }

    /**
     * Fila de acciones con un poco de aire entre botones (antes iban
     * pegados con BorderLayout.WEST/EAST sin espacio), para que el
     * icono de cada boton no quede apretado contra el otro boton.
     */
    private void armarTarjetaAcciones() {
        JPanel contenido = tarjetaAccionesReal.obtenerPanelContenido();
        contenido.setLayout(new BorderLayout(12, 0));
        contenido.add(botonCambiarContrasenaReal.obtenerPanel(), BorderLayout.WEST);
        contenido.add(botonCerrarSesionReal.obtenerPanel(), BorderLayout.EAST);
    }

    /**
     * @param nombre   nombre completo a mostrar (y a editar si esModoAdmin).
     * @param id       identificacion del funcionario/administrador.
     * @param telefono telefono actual (siempre editable).
     */
    public void cargarDatos(String nombre, String id, String telefono) {
        txtNombre.setText(nombre);
        txtID.setText(id);
        txtTelefono.setText(telefono);
    }

    /**
     * Activa el modo "Editando Perfil" (un administrador editando el
     * perfil de otro funcionario) o el modo "Mi Perfil" (el usuario
     * logueado viendo/editando su propia cuenta).
     * <p>
     * En modo propio: nombre e identificacion son de solo lectura (solo
     * se puede cambiar el telefono) y se muestra "Cerrar sesion".
     * En modo administrador: nombre e identificacion son editables y
     * "Cerrar sesion" se oculta porque no es la sesion del admin.
     */
    public void setModoAdmin(boolean modoAdmin) {
        this.modoAdmin = modoAdmin;
        lblTitulo.setText(modoAdmin ? "Editando Perfil" : "Mi Perfil");

        txtNombre.setEditable(modoAdmin);
        txtID.setEditable(modoAdmin);
        txtTelefono.setEditable(true);

        botonCerrarSesionReal.obtenerPanel().setVisible(!modoAdmin);

        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    public boolean esModoAdmin() {
        return modoAdmin;
    }

    public String obtenerNombre() {
        return txtNombre.getText();
    }

    public String obtenerId() {
        return txtID.getText();
    }

    public String obtenerTelefono() {
        return txtTelefono.getText();
    }

    public void alGuardar(Runnable accion) {
        botonGuardarReal.alHacerClick(accion);
    }

    /**
     * Registra la accion a ejecutar cuando se hace click en el boton
     * "Cambiar contraseña" de la tarjeta de acciones (vista de datos).
     * El uso tipico (ver VentanaPrincipalControl) es pasar
     * this::mostrarVistaClave, para cambiar de vista dentro del mismo
     * dialogo en vez de abrir uno nuevo.
     */
    public void alCambiarContrasena(Runnable accion) {
        botonCambiarContrasenaReal.alHacerClick(accion);
    }

    /**
     * Registra la accion a ejecutar cuando la vista de "Cambiar
     * contraseña" confirma el cambio (boton Confirmar de esa vista).
     * Quien contenga a PanelCuenta llama a limpiarCamposClave() y
     * mostrarVistaDatos() dentro de esta accion si el cambio tuvo
     * exito, o deja la vista de clave abierta si hubo un error (por
     * ejemplo, mostrando un Popup con el mensaje).
     */
    public void alConfirmarCambioClave(Runnable accion) {
        botonConfirmarClaveReal.alHacerClick(accion);
    }

    public void alCerrarSesion(Runnable accion) {
        botonCerrarSesionReal.alHacerClick(accion);
    }

    /**
     * Registra la accion a ejecutar al presionar la X de BarraSuperior
     * (arriba del todo, encima del titulo). Quien construya el JDialog
     * de "Mi Perfil"/"Editando Perfil" (ver VentanaPrincipalControl)
     * conecta esto con dialogo.dispose(), igual que un boton de cerrar
     * nativo de la ventana pero respetando el tema de la aplicacion.
     */
    public void alCerrar(Runnable accion) {
        barraSuperiorReal.alCerrar(accion);
    }

    public String obtenerClaveActual() {
        return campoClaveActualReal.obtenerTexto();
    }

    public String obtenerClaveNueva() {
        return campoClaveNuevaReal.obtenerTexto();
    }

    public void limpiarCamposClave() {
        campoClaveActualReal.obtenerCampoTexto().setText("");
        campoClaveNuevaReal.obtenerCampoTexto().setText("");
    }

    /**
     * Cambia a la vista de "Cambiar contraseña" dentro de este mismo
     * panel/dialogo, en vez de abrir un dialogo nuevo. El boton
     * "Cambiar contraseña" de la tarjeta de acciones llama a esto (ver
     * quien construye PanelCuenta), y el propio panel se encarga de
     * volver con el boton "Volver" de la vista de clave.
     */
    public void mostrarVistaClave() {
        layoutVistas.show(pnlVistas, VISTA_CLAVE);
    }

    public void mostrarVistaDatos() {
        limpiarCamposClave();
        layoutVistas.show(pnlVistas, VISTA_DATOS);
    }

    /**
     * Panel raiz a usar como contentPane del dialogo unico de "Mi
     * Perfil"/"Editando Perfil". Contiene ambas vistas (datos y cambio
     * de clave) superpuestas via CardLayout; solo una es visible a la
     * vez, y cambiar entre ellas no abre ni cierra ningun dialogo.
     * <p>
     * La vista de datos es el propio PanelCuenta tal como lo arma el
     * .form (titulo + TarjetaDatos + TarjetaAcciones, con su
     * GridLayoutManager original), sin desarmarlo: solo se envuelve
     * como una tarjeta mas de pnlVistas, junto a tarjetaClaveReal.
     */
    public JPanel obtenerPanel() {
        if (pnlVistas.getComponentCount() == 0) {
            pnlVistas.add(PanelCuenta, VISTA_DATOS);
            pnlVistas.add(tarjetaClaveReal.obtenerPanel(), VISTA_CLAVE);
        }
        return pnlVistas;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (lblTitulo == null || txtNombre == null) {
            // createUIComponents ya corrio (lo exige el GUI Designer),
            // pero este metodo puede dispararse antes por el listener
            // de tema que registran Tarjeta/BotonPrimario/BotonSecundario
            // en sus propios constructores, ejecutados dentro de
            // createUIComponents() de este mismo panel.
            return;
        }

        PanelCuenta.setOpaque(true);
        PanelCuenta.setBackground(tema.colorFondoVentana());

        lblTitulo.setForeground(tema.colorTexto());
        lblTitulo.setFont(tema.fuenteTitulo().deriveFont(18f));

        lblNombre.setForeground(tema.colorTextoSecundario());
        lblID.setForeground(tema.colorTextoSecundario());
        lblTelefono.setForeground(tema.colorTextoSecundario());
        lblNombre.setFont(tema.fuenteTexto());
        lblID.setFont(tema.fuenteTexto());
        lblTelefono.setFont(tema.fuenteTexto());

        estilizarSegunEditable(txtNombre, tema);
        estilizarSegunEditable(txtID, tema);
        estilizarSegunEditable(txtTelefono, tema);
    }

    private void estilizarSegunEditable(JTextField campo, Tema tema) {
        boolean soloLectura = !campo.isEditable();
        Color fondo = soloLectura ? tema.colorFondoVentana() : tema.colorFondoCampo();
        Color texto = soloLectura ? tema.colorTextoSecundario() : tema.colorTexto();

        campo.setOpaque(true);
        campo.setBackground(fondo);
        campo.setForeground(texto);
        campo.setCaretColor(tema.colorTexto());
        campo.setFont(tema.fuenteTexto());
        campo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(tema.colorBorde(), 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }
}
