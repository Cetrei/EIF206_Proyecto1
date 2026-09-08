package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotonPrimario implements CambioTemaListener {

    private static final int RADIO_BORDE = 10;

    private JPanel BotonPrimario;
    private JButton btnPrimario;
    private JLabel lblIcon;

    private Color colorBase;
    private Color colorHover;
    private boolean sobreBoton;

    public BotonPrimario() {
        BotonPrimario.setOpaque(false);
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        btnPrimario = new JButton() {
            @Override
            protected void paintComponent(Graphics graficos) {
                Graphics2D graficos2D = (Graphics2D) graficos.create();
                graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graficos2D.setColor(sobreBoton ? colorHover : colorBase);
                graficos2D.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDE, RADIO_BORDE);
                graficos2D.dispose();
                super.paintComponent(graficos);
            }
        };
        btnPrimario.setContentAreaFilled(false);
        btnPrimario.setFocusPainted(false);
        btnPrimario.setBorderPainted(false);
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
        IconoAplicador.aplicar(lblIcon, icono, 13f, GestorTema.obtenerInstancia().temaActivo().colorTexto());
    }

    public void alHacerClick(Runnable accion) {
        btnPrimario.addActionListener(evento -> accion.run());
    }

    public JPanel obtenerPanel() {
        return BotonPrimario;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        colorBase = tema.colorPrimario();
        colorHover = tema.colorPrimarioHover();
        btnPrimario.setForeground(tema.colorTexto());
        btnPrimario.setFont(tema.fuenteTexto());
        lblIcon.setForeground(tema.colorTexto());
        btnPrimario.repaint();
    }
}
