package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Tarjeta implements CambioTemaListener {

    private static final int RADIO_BORDE = 16;

    private JPanel PanelTarjeta;
    private JPanel pnlEncabezado;
    private JLabel lblIconoEncabezado;
    private JLabel lblTituloEncabezado;
    private JLabel lblSubtituloEncabezado;
    private JPanel pnlAccionEncabezado;
    private JPanel pnlContenido;

    public Tarjeta() {
        GestorTema.obtenerInstancia().agregarListener(this);
        pnlEncabezado.setOpaque(false);
        lblTituloEncabezado.setHorizontalAlignment(SwingConstants.LEFT);
        lblSubtituloEncabezado.setHorizontalAlignment(SwingConstants.LEFT);
        lblSubtituloEncabezado.setText("");
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
        pnlEncabezado.setVisible(false);
    }

    private void createUIComponents() {
        PanelTarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics graficos) {
                Graphics2D graficos2D = (Graphics2D) graficos.create();
                graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graficos2D.setColor(getBackground());
                graficos2D.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDE, RADIO_BORDE);
                graficos2D.dispose();
                super.paintComponent(graficos);
            }
        };
        PanelTarjeta.setOpaque(false);

        pnlAccionEncabezado = new JPanel(new BorderLayout());
        pnlAccionEncabezado.setOpaque(false);

        pnlContenido = new JPanel(new BorderLayout());
        pnlContenido.setOpaque(false);
    }


    public void setTitulo(String titulo) {
        pnlEncabezado.setVisible(true);
        lblTituloEncabezado.setText(titulo);
        lblTituloEncabezado.setHorizontalAlignment(SwingConstants.LEFT);
    }

    public void setSubtitulo(String subtitulo) {
        pnlEncabezado.setVisible(true);
        lblSubtituloEncabezado.setText(subtitulo);
        lblSubtituloEncabezado.setHorizontalAlignment(SwingConstants.LEFT);
    }

    public void setIcono(Icono icono) {
        pnlEncabezado.setVisible(true);
        IconoAplicador.aplicar(lblIconoEncabezado, icono, 17f, GestorTema.obtenerInstancia().temaActivo().colorPrimario());
    }

    public void setAccion(JPanel panelAccion) {
        pnlEncabezado.setVisible(true);
        pnlAccionEncabezado.removeAll();
        pnlAccionEncabezado.add(panelAccion, BorderLayout.CENTER);
        pnlAccionEncabezado.revalidate();
    }

    public JPanel obtenerPanelContenido() {
        return pnlContenido;
    }

    public JPanel obtenerPanel() {
        return PanelTarjeta;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        Color fondo = tema.colorFondoTarjeta();
        PanelTarjeta.setBackground(fondo);
        lblTituloEncabezado.setForeground(tema.colorTexto());
        lblTituloEncabezado.setFont(tema.fuenteTitulo().deriveFont(15f));
        lblSubtituloEncabezado.setForeground(tema.colorTextoSecundario());
        lblSubtituloEncabezado.setFont(tema.fuenteTexto().deriveFont(11.5f));
        PanelTarjeta.repaint();
    }
}
