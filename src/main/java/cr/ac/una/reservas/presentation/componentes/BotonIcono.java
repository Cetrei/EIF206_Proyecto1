package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotonIcono implements CambioTemaListener {
    public enum Variante {
        NEUTRO,
        PELIGRO
    }

    private static final int RADIO_BORDE = 8;

    private JPanel BotonIcono;
    private JButton btnIcono;

    private Icono icono = Icono.AJUSTES;
    private Variante variante = Variante.NEUTRO;
    private Color colorFondo;
    private Color colorFondoHover;
    private Color colorIcono;
    private boolean sobreBoton;
    private JPanel envoltorio;

    public BotonIcono() {
        GestorTema.obtenerInstancia().agregarListener(this);
        BotonIcono.setOpaque(false);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        btnIcono = new JButton() {
            @Override
            protected void paintComponent(Graphics graficos) {
                Graphics2D graficos2D = (Graphics2D) graficos.create();
                graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graficos2D.setColor(sobreBoton ? colorFondoHover : colorFondo);
                graficos2D.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDE, RADIO_BORDE);
                graficos2D.dispose();
                super.paintComponent(graficos);
            }
        };
        btnIcono.setContentAreaFilled(false);
        btnIcono.setFocusPainted(false);
        btnIcono.setBorderPainted(false);
        btnIcono.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        btnIcono.setFocusable(false);
        btnIcono.setOpaque(false);
        btnIcono.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                sobreBoton = true;
                btnIcono.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                sobreBoton = false;
                btnIcono.repaint();
            }
        });
    }

    public void setIcono(Icono icono) {
        this.icono = icono;
        aplicarIcono();
    }

    public void setTamano(int ancho, int alto) {
        Dimension tamano = new Dimension(ancho, alto);
        if (envoltorio == null) {
            envoltorio = new JPanel(new BorderLayout());
            envoltorio.setOpaque(false);
            envoltorio.add(BotonIcono, BorderLayout.CENTER);
        }
        envoltorio.setPreferredSize(tamano);
        envoltorio.setMinimumSize(tamano);
        envoltorio.setMaximumSize(tamano);
        envoltorio.revalidate();
    }

    public void setVariante(Variante variante) {
        this.variante = variante;
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    public void alHacerClick(Runnable accion) {
        btnIcono.addActionListener(evento -> accion.run());
    }

    public JPanel obtenerPanel() {
        if (envoltorio != null) {
            return envoltorio;
        }
        return BotonIcono;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (variante == Variante.PELIGRO) {
            colorFondo = tema.colorPeligro();
            colorFondoHover = tema.colorPeligroHover();
            colorIcono = tema.colorTextoSobrePeligro();
        } else {
            colorFondo = tema.colorFondoCampo();
            colorFondoHover = tema.colorBorde();
            colorIcono = tema.colorTextoSecundario();
        }
        aplicarIcono();
        btnIcono.repaint();
    }

    private void aplicarIcono() {
        IconoAplicador.aplicar(btnIcono, icono, 14f, colorIcono);
    }
}
