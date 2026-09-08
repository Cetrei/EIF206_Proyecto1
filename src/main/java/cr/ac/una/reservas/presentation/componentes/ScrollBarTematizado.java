package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

public final class ScrollBarTematizado {
    private static final int GROSOR = 10;

    private ScrollBarTematizado() {
    }

    public static void aplicar(JScrollBar scrollBar, Tema tema) {
        if (scrollBar == null) return;
        scrollBar.setOpaque(true);
        scrollBar.setBackground(tema.colorFondoTarjeta());
        scrollBar.setUnitIncrement(16);
        scrollBar.setUI(new UiScrollBarTematizado(tema));
    }

    private static final class UiScrollBarTematizado extends BasicScrollBarUI {
        private final Tema tema;

        private UiScrollBarTematizado(Tema tema) {
            this.tema = tema;
        }

        @Override
        protected void configureScrollBarColors() {
            trackColor = tema.colorFondoTarjeta();
            thumbColor = tema.colorSuperficie();
        }

        @Override
        public void paint(Graphics graficos, javax.swing.JComponent c) {
            graficos.setColor(tema.colorFondoTarjeta());
            graficos.fillRect(0, 0, c.getWidth(), c.getHeight());
            super.paint(graficos, c);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return new BotonFlecha(orientation, tema);
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return new BotonFlecha(orientation, tema);
        }

        @Override
        protected void paintTrack(Graphics graficos, javax.swing.JComponent c, Rectangle trackBounds) {
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setColor(tema.colorFondoTarjeta());
            graficos2D.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            graficos2D.dispose();
        }

        @Override
        protected void paintThumb(Graphics graficos, javax.swing.JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graficos2D.setColor(tema.colorSuperficie());
            int margen = 2;
            graficos2D.fillRoundRect(
                    thumbBounds.x + margen,
                    thumbBounds.y + margen,
                    thumbBounds.width - margen * 2,
                    thumbBounds.height - margen * 2,
                    6, 6
            );
            graficos2D.dispose();
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(GROSOR, GROSOR * 2);
        }
    }

    private static final class BotonFlecha extends JButton {
        private final int orientacion;
        private final Tema tema;

        private BotonFlecha(int orientacion, Tema tema) {
            this.orientacion = orientacion;
            this.tema = tema;
            setOpaque(true);
            setFocusable(false);
            setBorder(javax.swing.BorderFactory.createEmptyBorder());
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(false);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(GROSOR, GROSOR);
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graficos2D.setColor(tema.colorFondoTarjeta());
            graficos2D.fillRect(0, 0, getWidth(), getHeight());

            Color colorFlecha = tema.colorTextoSecundario();
            graficos2D.setColor(colorFlecha);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int tam = 3;

            java.awt.Polygon flecha = new java.awt.Polygon();
            switch (orientacion) {
                case javax.swing.SwingConstants.NORTH:
                    flecha.addPoint(cx - tam, cy + tam / 2);
                    flecha.addPoint(cx + tam, cy + tam / 2);
                    flecha.addPoint(cx, cy - tam / 2);
                    break;
                case javax.swing.SwingConstants.SOUTH:
                    flecha.addPoint(cx - tam, cy - tam / 2);
                    flecha.addPoint(cx + tam, cy - tam / 2);
                    flecha.addPoint(cx, cy + tam / 2);
                    break;
                case javax.swing.SwingConstants.WEST:
                    flecha.addPoint(cx + tam / 2, cy - tam);
                    flecha.addPoint(cx + tam / 2, cy + tam);
                    flecha.addPoint(cx - tam / 2, cy);
                    break;
                case javax.swing.SwingConstants.EAST:
                default:
                    flecha.addPoint(cx - tam / 2, cy - tam);
                    flecha.addPoint(cx - tam / 2, cy + tam);
                    flecha.addPoint(cx + tam / 2, cy);
                    break;
            }
            graficos2D.fillPolygon(flecha);
            graficos2D.dispose();
        }
    }
}
