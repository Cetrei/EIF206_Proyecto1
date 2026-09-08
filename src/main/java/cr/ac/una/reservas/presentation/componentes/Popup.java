package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

public class Popup implements CambioTemaListener {

    public enum Tipo {
        INFORMACION,
        ERROR,
        CONFIRMACION
    }

    public static class AccionPopup {
        private final String texto;
        private final boolean esPrincipal;
        private final Runnable accion;

        public AccionPopup(String texto, boolean esPrincipal, Runnable accion) {
            this.texto = texto;
            this.esPrincipal = esPrincipal;
            this.accion = accion;
        }
    }

    private static final int RADIO_BORDE = 14;

    private JDialog dialogoRaiz;
    private JPanel pnlContenidoPopup;
    private JLabel lblIconoPopup;
    private JLabel lblTituloPopup;
    private JLabel lblMensajePopup;
    private JPanel pnlBotonesPopup;

    private Tipo tipo = Tipo.INFORMACION;

    public Popup(Frame propietario) {
        construirDialogo(propietario);
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void construirDialogo(Frame propietario) {
        dialogoRaiz = new JDialog(propietario, true) {
            @Override
            public void paint(Graphics graficos) {
                Graphics2D graficos2D = (Graphics2D) graficos.create();
                graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graficos2D.setColor(getContentPane().getBackground());
                graficos2D.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDE, RADIO_BORDE);
                graficos2D.dispose();
                super.paint(graficos);
            }
        };
        dialogoRaiz.setUndecorated(true);
        dialogoRaiz.setResizable(false);
        dialogoRaiz.setContentPane(pnlContenidoPopup);
    }

    private void createUIComponents() {
        pnlBotonesPopup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBotonesPopup.setOpaque(false);
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    public void setTitulo(String titulo) {
        lblTituloPopup.setText(titulo);
    }

    public void setMensaje(String mensaje) {
        lblMensajePopup.setText("<html><body style='width: 220px'>" + mensaje + "</body></html>");
    }

    /**
     * Reemplaza los botones del popup. El primero marcado esPrincipal usa
     * BotonPrimario, el resto BotonSecundario. Si no se llama, el popup
     * queda sin botones (uso poco comun, normalmente se agrega al menos
     * un boton de cierre).
     */
    public void setAcciones(List<AccionPopup> acciones) {
        pnlBotonesPopup.removeAll();
        for (AccionPopup accionPopup : acciones) {
            pnlBotonesPopup.add(crearBoton(accionPopup));
        }
        pnlBotonesPopup.revalidate();
        pnlBotonesPopup.repaint();
    }

    private Component crearBoton(AccionPopup accionPopup) {
        Runnable accionConCierre = () -> {
            if (accionPopup.accion != null) {
                accionPopup.accion.run();
            }
            cerrar();
        };
        if (accionPopup.esPrincipal) {
            BotonPrimario boton = new BotonPrimario();
            boton.setTexto(accionPopup.texto);
            boton.alHacerClick(accionConCierre);
            return boton.obtenerPanel();
        }
        BotonSecundario boton = new BotonSecundario();
        boton.setTexto(accionPopup.texto);
        boton.alHacerClick(accionConCierre);
        return boton.obtenerPanel();
    }

    /**
     * Atajo para el caso mas comun: un aviso con un unico boton "Aceptar".
     */
    public static void mostrarAviso(Frame propietario, Tipo tipo, String titulo, String mensaje) {
        Popup popup = new Popup(propietario);
        popup.setTipo(tipo);
        popup.setTitulo(titulo);
        popup.setMensaje(mensaje);
        List<AccionPopup> acciones = new ArrayList<>();
        acciones.add(new AccionPopup("Aceptar", true, null));
        popup.setAcciones(acciones);
        popup.mostrar();
    }

    public void mostrar() {
        dialogoRaiz.pack();
        dialogoRaiz.setLocationRelativeTo(dialogoRaiz.getOwner());
        dialogoRaiz.setVisible(true);
    }

    public void cerrar() {
        dialogoRaiz.dispose();
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        Color colorIcono = colorSegunTipo(tema);
        Icono icono = iconoSegunTipo();

        dialogoRaiz.getContentPane().setBackground(tema.colorFondoTarjeta());
        IconoAplicador.aplicar(lblIconoPopup, icono, 28f, colorIcono);

        lblTituloPopup.setForeground(tema.colorTexto());
        lblTituloPopup.setFont(tema.fuenteTitulo().deriveFont(15f));

        lblMensajePopup.setForeground(tema.colorTextoSecundario());
        lblMensajePopup.setFont(tema.fuenteTexto());

        dialogoRaiz.repaint();
    }

    private Color colorSegunTipo(Tema tema) {
        return tipo == Tipo.ERROR ? tema.colorPeligro() : tema.colorPrimario();
    }

    private Icono iconoSegunTipo() {
        switch (tipo) {
            case ERROR:
                return Icono.ALERTA;
            case CONFIRMACION:
                return Icono.CHECK;
            case INFORMACION:
            default:
                return Icono.INFO;
        }
    }
}
