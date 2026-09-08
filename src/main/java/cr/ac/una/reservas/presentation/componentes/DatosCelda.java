package cr.ac.una.reservas.presentation.componentes;

import java.awt.Color;
import java.util.List;

public class DatosCelda {

    private final String textoPrincipal;
    private final String textoSecundario;
    private final Color color;

    public DatosCelda(String textoPrincipal, String textoSecundario, Color color) {
        this.textoPrincipal = textoPrincipal;
        this.textoSecundario = textoSecundario;
        this.color = color;
    }

    /**
     * Construye una celda con varias entradas apiladas
     * Como varias actividades programadas en la misma franja horaria del mismo dia
     */
    public static DatosCelda apilada(List<String[]> lineas, Color color) {
        StringBuilder principal = new StringBuilder("<html>");
        StringBuilder secundario = new StringBuilder("<html>");
        for (int i = 0; i < lineas.size(); i++) {
            if (i > 0) {
                principal.append("<br>");
                secundario.append("<br>");
            }
            String[] linea = lineas.get(i);
            principal.append(escaparHtml(linea[0]));
            secundario.append(linea.length > 1 && linea[1] != null ? escaparHtml(linea[1]) : "");
        }
        principal.append("</html>");
        secundario.append("</html>");
        return new DatosCelda(principal.toString(), secundario.toString(), color);
    }

    private static String escaparHtml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
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
