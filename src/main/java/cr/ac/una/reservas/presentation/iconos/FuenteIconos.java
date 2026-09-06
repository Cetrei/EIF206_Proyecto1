package cr.ac.una.reservas.presentation.iconos;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public final class FuenteIconos {
    private static final String RUTA_TTF = "/fonts/fa-solid-900.ttf";
    private static Font fuenteBase;

    private FuenteIconos() {
    }

    private static Font cargarFuenteBase() {
        if (fuenteBase != null) {
            return fuenteBase;
        }
        try (InputStream entrada = FuenteIconos.class.getResourceAsStream(RUTA_TTF)) {
            if (entrada == null) {
                throw new IllegalStateException("No se encontro el archivo de fuente en " + RUTA_TTF);
            }
            fuenteBase = Font.createFont(Font.TRUETYPE_FONT, entrada);
            return fuenteBase;
        } catch (FontFormatException | IOException e) {
            throw new IllegalStateException("No se pudo cargar la fuente de iconos", e);
        }
    }

    public static Font obtenerFuente(float tamano) {
        return cargarFuenteBase().deriveFont(tamano);
    }
    public static String obtenerTexto(Icono icono) {
        return String.valueOf(icono.getCodigo());
    }
}
