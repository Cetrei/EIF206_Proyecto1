package cr.ac.una.reservas.presentation.tema;

import java.awt.Color;
import java.awt.Font;

public class TemaClaro implements Tema {
    @Override
    public Color colorFondoVentana() {
        return new Color(0xF1, 0xF3, 0xF7);
    }
    @Override
    public Color colorFondoTarjeta() {
        return Color.WHITE;
    }
    @Override
    public Color colorFondoCampo() {
        return new Color(0xF7, 0xF8, 0xFA);
    }

    @Override
    public Color colorBorde() {
        return new Color(0xD8, 0xDC, 0xE3);
    }
    @Override
    public Color colorBordeEnfocado() {
        return new Color(0x2F, 0x6F, 0xED);
    }

    @Override
    public Color colorTexto() {
        return new Color(0x1A, 0x1F, 0x2B);
    }
    @Override
    public Color colorTextoSecundario() {
        return new Color(0x64, 0x6E, 0x82);
    }

    @Override
    public Color colorPrimario() {
        return new Color(0x2F, 0x6F, 0xED);
    }
    @Override
    public Color colorPrimarioHover() {
        return new Color(0x1E, 0x5B, 0xD6);
    }
    @Override
    public Color colorPeligro() {
        return new Color(0xC0, 0x2B, 0x38);
    }
    @Override
    public Color colorPeligroHover() {
        return new Color(0xA5, 0x22, 0x2E);
    }

    @Override
    public Color colorSuperficie() {
        return new Color(0xDD, 0xE1, 0xE8);
    }

    @Override
    public Font fuenteTexto() {
        return new Font("Segoe UI", Font.PLAIN, 12);
    }
    @Override
    public Font fuenteTitulo() {
        return new Font("Segoe UI", Font.BOLD, 18);
    }
}
