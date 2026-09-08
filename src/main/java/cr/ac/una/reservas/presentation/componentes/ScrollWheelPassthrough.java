package cr.ac.una.reservas.presentation.componentes;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.Container;
import java.awt.event.MouseWheelEvent;

public final class ScrollWheelPassthrough {
    private ScrollWheelPassthrough() {
    }

    public static void instalar(JScrollPane scrollAnidado) {
        scrollAnidado.addMouseWheelListener(evento -> {
            JScrollBar barra = scrollAnidado.getVerticalScrollBar();
            boolean puedeSubir = evento.getWheelRotation() < 0 && barra.getValue() > barra.getMinimum();
            boolean puedeBajar = evento.getWheelRotation() > 0
                    && barra.getValue() < barra.getMaximum() - barra.getVisibleAmount();
            if (puedeSubir || puedeBajar) {
                // Todavia hay recorrido en la direccion que se esta girando
                return;
            }
            Container padre = scrollAnidado.getParent();
            while (padre != null && !(padre instanceof JScrollPane)) {
                padre = padre.getParent();
            }
            if (padre != null) {
                MouseWheelEvent reenviado = new MouseWheelEvent(
                        padre, evento.getID(), evento.getWhen(), evento.getModifiers(),
                        1, 1, evento.getClickCount(), false,
                        evento.getScrollType(), evento.getScrollAmount(), evento.getWheelRotation()
                );
                padre.dispatchEvent(reenviado);
            }
        });
    }
}
