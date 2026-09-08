package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

public abstract class PanelConScrollAnidado {
    protected final JScrollPane scroll;

    protected PanelConScrollAnidado(JScrollPane scroll) {
        this.scroll = scroll;
        ScrollWheelPassthrough.instalar(scroll);
    }

    protected void aplicarTemaScroll(Tema tema) {
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(tema.colorFondoTarjeta());
        scroll.getViewport().setBackground(tema.colorFondoTarjeta());
        scroll.setViewportBorder(BorderFactory.createLineBorder(tema.colorBorde(), 1));
        ScrollBarTematizado.aplicar(scroll.getVerticalScrollBar(), tema);
        ScrollBarTematizado.aplicar(scroll.getHorizontalScrollBar(), tema);
    }
}
