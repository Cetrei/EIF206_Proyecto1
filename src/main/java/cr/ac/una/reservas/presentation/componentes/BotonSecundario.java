package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotonSecundario implements CambioTemaListener {

    public enum Variante {
        NEUTRO,
        PELIGRO
    }

    private static final int RADIO_BORDE = 10;
    private static final float GROSOR_BORDE = 1.5f;

    private JPanel BotonSecundario;
    private JButton btnPrimario;
    private JLabel lblIcon;

    private Variante variante = Variante.NEUTRO;
    private Color colorBorde;
    private Color colorTexto;
    private boolean sobreBoton;

    public BotonSecundario() {
        BotonSecundario.setOpaque(false);
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        btnPrimario = new JButton() {
            @Override
            protected void paintComponent(Graphics graficos) {
                Graphics2D graficos2D = (Graphics2D) graficos.create();
                graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (sobreBoton) {
                    graficos2D.setColor(colorConTransparencia(colorBorde, 40));
                    graficos2D.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDE, RADIO_BORDE);
                }
                graficos2D.setStroke(new BasicStroke(GROSOR_BORDE));
                graficos2D.setColor(colorBorde);
                graficos2D.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, RADIO_BORDE, RADIO_BORDE);
                graficos2D.dispose();
                super.paintComponent(graficos);
            }
        };
        btnPrimario.setContentAreaFilled(false);
        btnPrimario.setFocusPainted(false);
        btnPrimario.setBorderPainted(false);
        // IMPORTANTE: no usar un EmptyBorder de 0 (ver BotonPrimario
        // para la explicacion completa). Se deja un padding igual de
        // generoso, con un poco mas de aire en los costados para que
        // el texto no quede pegado al trazo dibujado a mano
        // (drawRoundRect) del borde de este boton.
        btnPrimario.setBorder(new javax.swing.border.EmptyBorder(8, 18, 8, 18));
        btnPrimario.setFocusable(false);
        btnPrimario.setOpaque(false);
        btnPrimario.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                sobreBoton = true;
                btnPrimario.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                sobreBoton = false;
                btnPrimario.repaint();
            }
        });
    }

    public void setTexto(String texto) {
        btnPrimario.setText(texto);
    }

    public void setIcono(Icono icono) {
        IconoAplicador.aplicar(lblIcon, icono, 13f, colorTexto);
    }

    public void setVariante(Variante variante) {
        this.variante = variante;
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    public void alHacerClick(Runnable accion) {
        btnPrimario.addActionListener(evento -> accion.run());
    }

    public JPanel obtenerPanel() {
        return BotonSecundario;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        colorBorde = variante == Variante.PELIGRO ? tema.colorPeligro() : tema.colorBorde();
        colorTexto = variante == Variante.PELIGRO ? tema.colorPeligro() : tema.colorTexto();
        btnPrimario.setForeground(colorTexto);
        btnPrimario.setFont(tema.fuenteTexto());
        lblIcon.setForeground(colorTexto);
        btnPrimario.repaint();
    }

    private static Color colorConTransparencia(Color color, int alfa) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alfa);
    }
}
