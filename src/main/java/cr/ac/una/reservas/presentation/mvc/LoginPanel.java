package cr.ac.una.reservas.presentation.mvc;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cr.ac.una.reservas.presentation.mvc.componentes.BarraSuperior;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.mvc.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.mvc.componentes.Popup;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.model.LoginModel;
import cr.ac.una.reservas.presentation.mvc.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.presentation.mvc.tema.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LoginPanel implements CambioTemaListener, PropertyChangeListener {
    private static final int RADIO_BORDE_TARJETA = 16;
    private boolean accionesConfiguradas = false;

    private JPanel VentanaPrincipal;
    private JPanel pnlTarjeta;
    private JPanel btnIngresar;
    private JPanel btnCambiarClave;
    private JLabel iconTitulo;
    private JLabel lblTitulo;
    private JLabel lblSubTitulo;
    private JPanel txtID;
    private JPanel txtContrasena;
    private JLabel lblPiePagina;
    private JLabel lblIntegrantes;

    private BotonPrimario botonIngresarReal;
    private BotonSecundario botonCambiarClaveReal;
    private CampoTexto campoIdReal;
    private CampoTexto campoContrasenaReal;
    private BarraSuperior barraSuperiorReal;
    private JPanel envoltorio;

    public LoginPanel(LoginModel modelo) {
        $$$setupUI$$$();
        barraSuperiorReal = new BarraSuperior();
        modelo.addPropertyChangeListener(this);
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        VentanaPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics graficos) {
                Graphics2D graficos2D = (Graphics2D) graficos.create();
                graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graficos2D.setColor(getBackground());
                graficos2D.fillRect(0, 0, getWidth(), getHeight());
                graficos2D.dispose();
                super.paintComponent(graficos);
            }
        };

        pnlTarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics graficos) {
                Graphics2D graficos2D = (Graphics2D) graficos.create();
                graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graficos2D.setColor(getBackground());
                graficos2D.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDE_TARJETA, RADIO_BORDE_TARJETA);
                graficos2D.dispose();
                super.paintComponent(graficos);
            }
        };
        pnlTarjeta.setOpaque(false);

        botonIngresarReal = new BotonPrimario();
        botonIngresarReal.setTexto("Ingresar");
        btnIngresar = botonIngresarReal.obtenerPanel();

        botonCambiarClaveReal = new BotonSecundario();
        botonCambiarClaveReal.setTexto("Cambiar contraseña");
        btnCambiarClave = botonCambiarClaveReal.obtenerPanel();

        campoIdReal = new CampoTexto();
        campoIdReal.setEtiqueta("IDENTIFICACIÓN (ID)");
        campoIdReal.setIcono(IconoSemantico.USUARIO.icono());
        txtID = campoIdReal.obtenerPanel();

        campoContrasenaReal = new CampoTexto(true);
        campoContrasenaReal.setEtiqueta("CONTRASEÑA");
        campoContrasenaReal.setIcono(IconoSemantico.CONTRASENA.icono());
        txtContrasena = campoContrasenaReal.obtenerPanel();
    }

    public void alIngresar(Runnable accion) {
        if (accionesConfiguradas) return;
        botonIngresarReal.alHacerClick(accion);
        campoContrasenaReal.alConfirmar(accion);
        accionesConfiguradas = true;
    }

    public void alCambiarClave(Runnable accion) {
        botonCambiarClaveReal.alHacerClick(accion);
    }

    public void alCerrar(Runnable accion) {
        barraSuperiorReal.alCerrar(accion);
    }

    public String obtenerId() {
        return campoIdReal.obtenerTexto();
    }

    public String obtenerContrasena() {
        return campoContrasenaReal.obtenerTexto();
    }

    public JPanel obtenerPanel() {
        if (envoltorio == null) {
            envoltorio = new JPanel(new BorderLayout());
            envoltorio.setOpaque(true);
            envoltorio.setBackground(GestorTema.obtenerInstancia().temaActivo().colorFondoVentana());
            envoltorio.add(barraSuperiorReal.obtenerPanel(), BorderLayout.NORTH);
            envoltorio.add(VentanaPrincipal, BorderLayout.CENTER);
        }
        return envoltorio;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evento) {
        if (LoginModel.PROP_MENSAJE_ERROR.equals(evento.getPropertyName())) {
            String mensaje = (String) evento.getNewValue();
            if (mensaje != null && !mensaje.isBlank()) {
                Frame propietario = (Frame) SwingUtilities.getWindowAncestor(VentanaPrincipal);
                Popup.mostrarAviso(propietario, Popup.Tipo.ERROR, "No se pudo ingresar", mensaje);
            }
        }
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (envoltorio != null) {
            envoltorio.setBackground(tema.colorFondoVentana());
        }

        VentanaPrincipal.setBackground(tema.colorFondoVentana());

        pnlTarjeta.setBackground(tema.colorFondoTarjeta());
        pnlTarjeta.repaint();

        IconoAplicador.aplicar(iconTitulo, IconoSemantico.LOGO_APP.icono(), 32f, tema.colorPrimario());

        lblTitulo.setForeground(tema.colorTexto());
        lblTitulo.setFont(tema.fuenteTitulo());

        lblSubTitulo.setForeground(tema.colorTextoSecundario());
        lblSubTitulo.setFont(tema.fuenteTexto());

        lblPiePagina.setForeground(tema.colorTextoSecundario());
        lblPiePagina.setFont(tema.fuenteTexto().deriveFont(Font.PLAIN, 11f));

        lblIntegrantes.setForeground(tema.colorTextoSecundario());
        lblIntegrantes.setFont(tema.fuenteTexto().deriveFont(Font.PLAIN, 11f));
        lblIntegrantes.setBorder(new EmptyBorder(0, 16, 12, 16));

        VentanaPrincipal.repaint();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        VentanaPrincipal.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        pnlTarjeta.setLayout(new GridLayoutManager(8, 1, new Insets(28, 32, 24, 32), -1, 6));
        VentanaPrincipal.add(pnlTarjeta, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(420, -1), null, 0, false));
        pnlTarjeta.add(btnIngresar, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblTitulo = new JLabel();
        lblTitulo.setHorizontalAlignment(0);
        lblTitulo.setText("SISTEMA DE RESERVAS ");
        pnlTarjeta.add(lblTitulo, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        iconTitulo = new JLabel();
        iconTitulo.setHorizontalAlignment(0);
        iconTitulo.setText("");
        pnlTarjeta.add(iconTitulo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblSubTitulo = new JLabel();
        lblSubTitulo.setHorizontalAlignment(0);
        lblSubTitulo.setText("Universidad Nacional de Costa Rica  ");
        pnlTarjeta.add(lblSubTitulo, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlTarjeta.add(txtID, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        pnlTarjeta.add(txtContrasena, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lblPiePagina = new JLabel();
        lblPiePagina.setHorizontalAlignment(0);
        lblPiePagina.setText("EIF206 Programación 3 | Proyecto #1 XML  MVC");
        lblPiePagina.setDisplayedMnemonic(' ');
        lblPiePagina.setDisplayedMnemonicIndex(40);
        pnlTarjeta.add(lblPiePagina, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlTarjeta.add(btnCambiarClave, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        VentanaPrincipal.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        VentanaPrincipal.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        lblIntegrantes = new JLabel();
        lblIntegrantes.setText("Joanfer Hidalgo Chaves, Santiago Hernandez Chaves, Justin Angulo Artavia");
        VentanaPrincipal.add(lblIntegrantes, new GridConstraints(3, 0, 1, 3, 12, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        VentanaPrincipal.add(spacer3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        VentanaPrincipal.add(spacer4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return VentanaPrincipal;
    }

}
