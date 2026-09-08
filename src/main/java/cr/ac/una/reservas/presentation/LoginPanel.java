package cr.ac.una.reservas.presentation;

import cr.ac.una.reservas.presentation.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class LoginPanel implements CambioTemaListener {
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

    public LoginPanel() {
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

    // Enter en el campo de contrasena dispara la misma accion que el boton Ingresar.
    public void alIngresar(Runnable accion) {
        if (accionesConfiguradas) return;
        botonIngresarReal.alHacerClick(accion);
        campoContrasenaReal.alConfirmar(accion);
        accionesConfiguradas = true;
    }

    public void alCambiarClave(Runnable accion) {
        botonCambiarClaveReal.alHacerClick(accion);
    }

    public String obtenerId() {
        return campoIdReal.obtenerTexto();
    }

    public String obtenerContrasena() {
        return campoContrasenaReal.obtenerTexto();
    }

    public JPanel obtenerPanel() {
        return VentanaPrincipal;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
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

        VentanaPrincipal.repaint();
    }
}
