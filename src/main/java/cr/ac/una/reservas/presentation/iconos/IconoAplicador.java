package cr.ac.una.reservas.presentation.iconos;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import java.awt.Color;

public final class IconoAplicador {
    private IconoAplicador() {
    }

    public static void aplicar(JLabel label, Icono icono, float tamano, Color color) {
        label.setFont(FuenteIconos.obtenerFuente(tamano));
        label.setText(FuenteIconos.obtenerTexto(icono));
        label.setForeground(color);
    }

    public static void aplicar(AbstractButton boton, Icono icono, float tamano, Color color) {
        boton.setFont(FuenteIconos.obtenerFuente(tamano));
        boton.setText(FuenteIconos.obtenerTexto(icono));
        boton.setForeground(color);
    }
}
