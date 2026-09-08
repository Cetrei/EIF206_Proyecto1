package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class SpinnerTematizado {
    private SpinnerTematizado() {
    }

    public static void aplicar(JSpinner spinner, Tema tema) {
        if (spinner == null) return;
        spinner.setOpaque(true);
        spinner.setBackground(tema.colorFondoCampo());
        spinner.setFont(tema.fuenteTexto());
        spinner.setBorder(BorderFactory.createLineBorder(tema.colorBorde(), 1, true));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField campoTexto = ((JSpinner.DefaultEditor) editor).getTextField();
            campoTexto.setOpaque(true);
            campoTexto.setBackground(tema.colorFondoCampo());
            campoTexto.setForeground(tema.colorTexto());
            campoTexto.setCaretColor(tema.colorTexto());
            campoTexto.setFont(tema.fuenteTexto());
            campoTexto.setBorder(new EmptyBorder(4, 8, 4, 4));
        }

        spinner.setUI(new UiSpinnerTematizado(tema));
    }

    private static final class UiSpinnerTematizado extends BasicSpinnerUI {

        private final Tema tema;

        private UiSpinnerTematizado(Tema tema) {
            this.tema = tema;
        }

        @Override
        protected Component createNextButton() {
            Component boton = new BotonFlechaSpinner(tema, true);
            installNextButtonListeners(boton);
            return boton;
        }

        @Override
        protected Component createPreviousButton() {
            Component boton = new BotonFlechaSpinner(tema, false);
            installPreviousButtonListeners(boton);
            return boton;
        }
    }

    private static final class BotonFlechaSpinner extends javax.swing.JButton {

        private final Tema tema;
        private final boolean apuntaHaciaArriba;

        private BotonFlechaSpinner(Tema tema, boolean apuntaHaciaArriba) {
            this.tema = tema;
            this.apuntaHaciaArriba = apuntaHaciaArriba;
            setOpaque(true);
            setFocusable(false);
            setBorder(BorderFactory.createEmptyBorder());
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(true); // anim al pasar/click del mouse
            getModel().addChangeListener(evento -> repaint());
        }

        @Override
        public java.awt.Dimension getPreferredSize() {
            return new java.awt.Dimension(16, super.getPreferredSize().height);
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color colorFondo = tema.colorFondoCampo();
            if (getModel().isPressed()) {
                colorFondo = tema.colorSuperficie();
            } else if (getModel().isRollover()) {
                colorFondo = tema.colorBorde();
            }
            graficos2D.setColor(colorFondo);
            graficos2D.fillRect(0, 0, getWidth(), getHeight());

            Color colorFlecha = tema.colorTextoSecundario();
            graficos2D.setColor(colorFlecha);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int mitadAncho = 3;
            int alto = 2;

            java.awt.Polygon flecha = new java.awt.Polygon();
            if (apuntaHaciaArriba) {
                flecha.addPoint(cx - mitadAncho, cy + alto / 2);
                flecha.addPoint(cx + mitadAncho, cy + alto / 2);
                flecha.addPoint(cx, cy - alto);
            } else {
                flecha.addPoint(cx - mitadAncho, cy - alto / 2);
                flecha.addPoint(cx + mitadAncho, cy - alto / 2);
                flecha.addPoint(cx, cy + alto);
            }
            graficos2D.fillPolygon(flecha);
            graficos2D.dispose();
        }
    }
}
