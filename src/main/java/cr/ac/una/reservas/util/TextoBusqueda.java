package cr.ac.una.reservas.util;

import java.text.Normalizer;
import java.util.Locale;

public final class TextoBusqueda {
    private TextoBusqueda() {
    }

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinAcentos.toLowerCase(Locale.ROOT).trim();
    }

    public static boolean contiene(String textoFuente, String textoBuscado) {
        return normalizar(textoFuente).contains(normalizar(textoBuscado));
    }
}
