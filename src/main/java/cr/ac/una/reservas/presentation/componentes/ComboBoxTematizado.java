package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

public final class ComboBoxTematizado {

    private ComboBoxTematizado() {
    }

    public static <T> JComboBox<T> crear(Tema tema) {
        JComboBox<T> combo = new JComboBox<>();
        aplicar(combo, tema);
        return combo;
    }

    public static <T> JComboBox<T> crear(ComboBoxModel<T> modelo, Tema tema) {
        JComboBox<T> combo = new JComboBox<>(modelo);
        aplicar(combo, tema);
        return combo;
    }

    public static void aplicar(JComboBox<?> combo, Tema tema) {
        if (combo == null) {
            return;
        }
        combo.setOpaque(true);
        combo.setBackground(tema.colorFondoCampo());
        combo.setForeground(tema.colorTexto());
        combo.setFont(tema.fuenteTexto());
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(tema.colorBorde(), 1, true),
                new EmptyBorder(4, 8, 4, 4)
        ));
        aplicarConTipoCapturado(combo, tema);
    }


    @SuppressWarnings("unchecked")
    private static <T> void aplicarConTipoCapturado(JComboBox<T> combo, Tema tema) {
        ListCellRenderer<Object> rendererPrevio = (ListCellRenderer<Object>) combo.getRenderer();
        combo.setUI(new UiComboBoxTematizado(tema));
        combo.setRenderer((ListCellRenderer<T>) (ListCellRenderer<?>) new RendererTematizado(tema, rendererPrevio));
    }

    private static final class RendererTematizado implements ListCellRenderer<Object> {

        private final Tema tema;
        private final ListCellRenderer<Object> rendererBase;

        @SuppressWarnings("unchecked")
        private RendererTematizado(Tema tema, ListCellRenderer<Object> rendererPrevio) {
            this.tema = tema;
            this.rendererBase = rendererPrevio != null
                    ? rendererPrevio
                    : (ListCellRenderer<Object>) (ListCellRenderer<?>) new DefaultListCellRenderer();
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> lista, Object valor, int indice, boolean seleccionado, boolean conFoco
        ) {
            Component componente = rendererBase.getListCellRendererComponent(lista, valor, indice, seleccionado, conFoco);
            componente.setFont(tema.fuenteTexto());
            if (seleccionado) {
                componente.setBackground(tema.colorPrimario());
                componente.setForeground(tema.colorTextoSobrePrimario());
            } else {
                componente.setBackground(tema.colorFondoCampo());
                componente.setForeground(tema.colorTexto());
            }
            if (componente instanceof JPanel) {
                ((JPanel) componente).setOpaque(true);
            }
            return componente;
        }
    }

    private static final class UiComboBoxTematizado extends BasicComboBoxUI {

        private final Tema tema;

        private UiComboBoxTematizado(Tema tema) {
            this.tema = tema;
        }

        @Override
        protected JButton createArrowButton() {
            return new BotonFlecha(tema);
        }

        @Override
        protected ComboPopup createPopup() {
            return new PopupTematizado(comboBox, tema);
        }

        @Override
        public void paintCurrentValueBackground(Graphics graficos, java.awt.Rectangle bounds, boolean hasFocus) {
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setColor(comboBox.isEnabled() ? tema.colorFondoCampo() : tema.colorFondoTarjeta());
            graficos2D.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            graficos2D.dispose();
        }
    }

    private static final class PopupTematizado extends javax.swing.plaf.basic.BasicComboPopup {

        private final Tema tema;

        private PopupTematizado(JComboBox<Object> combo, Tema tema) {
            super(combo);
            this.tema = tema;
            setBorder(BorderFactory.createLineBorder(tema.colorBorde(), 1));
            aplicarTemaAPiezas();
        }

        private void aplicarTemaAPiezas() {
            list.setBackground(tema.colorFondoCampo());
            list.setForeground(tema.colorTexto());
            list.setFont(tema.fuenteTexto());
            list.setSelectionBackground(tema.colorPrimario());
            list.setSelectionForeground(tema.colorTextoSobrePrimario());

            scroller.setOpaque(true);
            scroller.setBackground(tema.colorFondoCampo());
            scroller.getViewport().setOpaque(true);
            scroller.getViewport().setBackground(tema.colorFondoCampo());
            scroller.setBorder(BorderFactory.createEmptyBorder());
            ScrollBarTematizado.aplicar(scroller.getVerticalScrollBar(), tema);

            setOpaque(true);
            setBackground(tema.colorFondoCampo());
        }
    }

    private static final class BotonFlecha extends JButton {

        private final Tema tema;

        private BotonFlecha(Tema tema) {
            this.tema = tema;
            setOpaque(true);
            setFocusable(false);
            setBorder(BorderFactory.createEmptyBorder());
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setRolloverEnabled(false);
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graficos2D.setColor(tema.colorFondoCampo());
            graficos2D.fillRect(0, 0, getWidth(), getHeight());

            Color colorFlecha = tema.colorTextoSecundario();
            graficos2D.setColor(colorFlecha);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int tam = 4;

            Polygon flecha = new Polygon();
            flecha.addPoint(cx - tam, cy - tam / 2);
            flecha.addPoint(cx + tam, cy - tam / 2);
            flecha.addPoint(cx, cy + tam / 2);
            graficos2D.fillPolygon(flecha);
            graficos2D.dispose();
        }
    }
}
