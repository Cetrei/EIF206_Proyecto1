package cr.ac.una.reservas.presentation.componentes;

import java.awt.Color;

/**
 * Datos que una MatrizFillStrategy entrega para una celda especifica de
 * MatrizPanel: el texto principal y secundario a mostrar (por ejemplo
 * nombre de la actividad y funcionario a cargo) y el color de fondo de
 * la celda, usado por CeldaMatriz para pintarse.
 *
 * Es un objeto de datos simple (sin logica de negocio), pensado para
 * que cada estrategia lo arme a partir de lo que le devuelva service.
 */
public class DatosCelda {

    private final String textoPrincipal;
    private final String textoSecundario;
    private final Color color;

    public DatosCelda(String textoPrincipal, String textoSecundario, Color color) {
        this.textoPrincipal = textoPrincipal;
        this.textoSecundario = textoSecundario;
        this.color = color;
    }

    public String getTextoPrincipal() {
        return textoPrincipal;
    }

    public String getTextoSecundario() {
        return textoSecundario;
    }

    public Color getColor() {
        return color;
    }
}
