package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.IconoSemantico;
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
        botonGuardarReal.setIcono(IconoSemantico.GUARDAR.icono());

        botonCambiarContrasenaReal = new BotonSecundario();
        botonCambiarContrasenaReal.setTexto("Cambiar contraseña");
        botonCambiarContrasenaReal.setIcono(IconoSemantico.CONTRASENA.icono());

        botonCerrarSesionReal = new BotonSecundario();
        botonCerrarSesionReal.setVariante(BotonSecundario.Variante.PELIGRO);
        botonCerrarSesionReal.setTexto("Cerrar sesión");
        botonCerrarSesionReal.setIcono(IconoSemantico.CERRAR_SESION.icono());

        armarTarjetaDatos();
        armarTarjetaAcciones();
        armarTarjetaClave();

        // pnlVistas sustituye a PanelCuenta como raiz visual
        layoutVistas = new CardLayout();
        pnlVistas = new JPanel(layoutVistas);
        pnlVistas.setOpaque(false);
    }

    private void armarTarjetaClave() {
        tarjetaClaveReal = new Tarjeta();
        tarjetaClaveReal.setTitulo("Cambiar contraseña");

        campoClaveActualReal = new CampoTexto(true);
        campoClaveActualReal.setEtiqueta("CONTRASEÑA ACTUAL");
        campoClaveActualReal.setIcono(IconoSemantico.CONTRASENA.icono());

        campoClaveNuevaReal = new CampoTexto(true);
        campoClaveNuevaReal.setEtiqueta("CONTRASEÑA NUEVA");
        campoClaveNuevaReal.setIcono(IconoSemantico.CONTRASENA.icono());

        botonConfirmarClaveReal = new BotonPrimario();
        botonConfirmarClaveReal.setTexto("Confirmar");
        botonConfirmarClaveReal.setIcono(IconoSemantico.CONFIRMAR.icono());

        botonVolverReal = new BotonSecundario();
        botonVolverReal.setTexto("Volver");

        JPanel contenido = tarjetaClaveReal.obtenerPanelContenido();
        contenido.setLayout(new GridLayout(4, 1, 0, 10));
        contenido.add(campoClaveActualReal.obtenerPanel());
        contenido.add(campoClaveNuevaReal.obtenerPanel());
        contenido.add(botonConfirmarClaveReal.obtenerPanel());
        contenido.add(botonVolverReal.obtenerPanel());

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

    private void armarTarjetaAcciones() {
        JPanel contenido = tarjetaAccionesReal.obtenerPanelContenido();
        contenido.setLayout(new BorderLayout(12, 0));
        contenido.add(botonCambiarContrasenaReal.obtenerPanel(), BorderLayout.WEST);
        contenido.add(botonCerrarSesionReal.obtenerPanel(), BorderLayout.EAST);
    }

    public void cargarDatos(String nombre, String id, String telefono) {
        txtNombre.setText(nombre);
        txtID.setText(id);
        txtTelefono.setText(telefono);
    }

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

    public void alCambiarContrasena(Runnable accion) {
        botonCambiarContrasenaReal.alHacerClick(accion);
    }

    public void alConfirmarCambioClave(Runnable accion) {
        botonConfirmarClaveReal.alHacerClick(accion);
    }

    public void alCerrarSesion(Runnable accion) {
        botonCerrarSesionReal.alHacerClick(accion);
    }

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

    public void mostrarVistaClave() {
        layoutVistas.show(pnlVistas, VISTA_CLAVE);
    }

    public void mostrarVistaDatos() {
        limpiarCamposClave();
        layoutVistas.show(pnlVistas, VISTA_DATOS);
    }

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
            // Puede dispararse antes de tiempo por el listener de tema que Tarjeta/BotonPrimario/BotonSecundario registran en sus propios constructores, ejecutados dentro de createUIComponents() de este panel.
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
