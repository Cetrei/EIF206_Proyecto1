package cr.ac.una.reservas.presentation.tema;

import java.awt.Color;
import java.awt.Font;

public class TemaOscuro implements Tema {
    @Override
    public Color colorFondoVentana() {
        return new Color(0x0B, 0x10, 0x1A);
    }
    @Override
    public Color colorFondoTarjeta() {
        return new Color(0x11, 0x18, 0x27);
    }
    @Override
    public Color colorFondoCampo() {
        return new Color(0x0A, 0x0F, 0x1C);
    }

    @Override
    public Color colorBorde() {
        return new Color(0x27, 0x30, 0x44);
    }
    @Override
    public Color colorBordeEnfocado() {
        return new Color(0x3B, 0x82, 0xF6);
    }

    @Override
    public Color colorTexto() {
        return new Color(0xE5, 0xE9, 0xF2);
    }
    @Override
    public Color colorTextoSecundario() {
        return new Color(0x8A, 0x94, 0xA8);
    }

    @Override
    public Color colorPrimario() {
        return new Color(0x2F, 0x6F, 0xED);
    }
    @Override
    public Color colorPrimarioHover() {
        return new Color(0x4A, 0x86, 0xFF);
    }
    @Override
    public Color colorPeligro() {
        return new Color(0x8A, 0x1F, 0x2B);
    }
    @Override
    public Color colorPeligroHover() {
        return new Color(0xA5, 0x28, 0x35);
    }

    @Override
    public Color colorSuperficie() {
        return new Color(0x2A, 0x34, 0x4A);
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
