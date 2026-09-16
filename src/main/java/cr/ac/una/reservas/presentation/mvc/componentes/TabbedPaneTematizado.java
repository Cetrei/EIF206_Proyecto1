package cr.ac.una.reservas.presentation.mvc.componentes;

import cr.ac.una.reservas.presentation.mvc.tema.Tema;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

public final class TabbedPaneTematizado {
    private static final int ALTO_INDICADOR = 2;

    private TabbedPaneTematizado() {
    }

    public static void aplicar(JTabbedPane panelPestanas, Tema tema) {
        if (panelPestanas == null) {
            return;
        }
        panelPestanas.setOpaque(true);
        panelPestanas.setBackground(tema.colorFondoVentana());
        panelPestanas.setForeground(tema.colorTexto());
        panelPestanas.setFont(tema.fuenteTexto());
        panelPestanas.setUI(new UiTabbedPaneTematizado(tema));

        for (int indice = 0; indice < panelPestanas.getTabCount(); indice++) {
            panelPestanas.setBackgroundAt(indice, tema.colorFondoVentana());
            panelPestanas.setForegroundAt(indice, tema.colorTexto());
            Component contenido = panelPestanas.getComponentAt(indice);
            if (contenido instanceof JComponent) {
                contenido.setBackground(tema.colorFondoVentana());
                ((JComponent) contenido).setOpaque(true);
            }
        }
        panelPestanas.revalidate();
        panelPestanas.repaint();
    }

    private static final class UiTabbedPaneTematizado extends BasicTabbedPaneUI {

        private final Tema tema;

        private UiTabbedPaneTematizado(Tema tema) {
            this.tema = tema;
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(7, 16, 7, 16);
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            tabAreaInsets = new Insets(0, 0, 0, 0);
            contentBorderInsets = new Insets(1, 0, 0, 0);
            highlight = tema.colorBorde();
            lightHighlight = tema.colorBorde();
            shadow = tema.colorBorde();
            darkShadow = tema.colorBorde();
            focus = tema.colorPrimario();
        }

        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + 2;
        }

        @Override
        protected void paintTabArea(Graphics graficos, int tabPlacement, int seleccionada) {
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setColor(tema.colorFondoVentana());
            graficos2D.fillRect(0, 0, tabPane.getWidth(), calculateTabAreaHeight(
                    tabPlacement, runCount, maxTabHeight
            ));
            graficos2D.dispose();
            super.paintTabArea(graficos, tabPlacement, seleccionada);
        }

        @Override
        protected void paintTabBackground(
                Graphics graficos, int tabPlacement, int tabIndex,
                int x, int y, int ancho, int alto, boolean seleccionada
        ) {
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graficos2D.setColor(seleccionada ? tema.colorFondoTarjeta() : tema.colorFondoVentana());
            graficos2D.fillRect(x, y, ancho, alto);
            if (seleccionada) {
                graficos2D.setColor(tema.colorPrimario());
                graficos2D.fillRect(x, y + alto - ALTO_INDICADOR, ancho, ALTO_INDICADOR);
            }
            graficos2D.dispose();
        }

        @Override
        protected void paintTabBorder(
                Graphics graficos, int tabPlacement, int tabIndex,
                int x, int y, int ancho, int alto, boolean seleccionada
        ) {
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setColor(tema.colorBorde());
            graficos2D.drawLine(x + ancho - 1, y + 4, x + ancho - 1, y + alto - 5);
            graficos2D.dispose();
        }

        @Override
        protected void paintContentBorder(Graphics graficos, int tabPlacement, int seleccionada) {
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            Insets insets = tabPane.getInsets();
            int alturaPestanas = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
            graficos2D.setColor(tema.colorBorde());
            graficos2D.fillRect(
                    insets.left,
                    insets.top + alturaPestanas,
                    tabPane.getWidth() - insets.left - insets.right,
                    1
            );
            graficos2D.dispose();
        }

        @Override
        protected void paintText(
                Graphics graficos, int tabPlacement, Font fuente, FontMetrics metricas,
                int tabIndex, String titulo, Rectangle rectanguloTexto, boolean seleccionada
        ) {
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );
            graficos2D.setFont(fuente);
            Color colorTexto = seleccionada ? tema.colorTexto() : tema.colorTextoSecundario();
            graficos2D.setColor(tabPane.isEnabledAt(tabIndex) ? colorTexto : tema.colorBorde());
            graficos2D.drawString(titulo, rectanguloTexto.x, rectanguloTexto.y + metricas.getAscent());
            graficos2D.dispose();
        }

        @Override
        protected void paintFocusIndicator(
                Graphics graficos, int tabPlacement, Rectangle[] rectangulos, int tabIndex,
                Rectangle rectanguloIcono, Rectangle rectanguloTexto, boolean seleccionada
        ) {
        }
    }
}
