package cr.ac.una.reservas.presentation.tema;

import java.awt.Color;
import java.awt.Font;

public interface Tema {
    Color colorFondoVentana();
    Color colorFondoTarjeta();
    Color colorFondoCampo();
    Color colorBorde();
    Color colorBordeEnfocado();
    Color colorTexto();
    Color colorTextoSecundario();
    Color colorPrimario();
    Color colorPrimarioHover();
    Color colorPeligro();
    Color colorPeligroHover();

    /**
     * Color de superficies "elevadas" pequenas que necesitan
     * distinguirse tanto del fondo de ventana como del fondo de
     * tarjeta/campo: el thumb (la parte arrastrable) de una
     * JScrollBar, por ejemplo. Sin esto, Swing usa el gris/blanco por
     * defecto del Look and Feel para esas piezas, que no tiene
     * relacion con el tema oscuro/claro de la app.
     */
    Color colorSuperficie();

    Font fuenteTexto();
    Font fuenteTitulo();
}
