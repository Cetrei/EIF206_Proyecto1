package cr.ac.una.reservas.presentation.tema;

import java.awt.Color;
import java.awt.Font;

public class TemaClaro implements Tema {
    @Override
    public Color colorFondoVentana() {
        return new Color(241, 243, 247);
    }
    @Override
    public Color colorFondoTarjeta() {
        return new Color(255, 255, 255);
    }
    @Override
    public Color colorFondoCampo() {
        return new Color(247, 248, 250);
    }

    @Override
    public Color colorBorde() {
        return new Color(216, 220, 227);
    }
    @Override
    public Color colorBordeEnfocado() {
        return new Color(47, 111, 237);
    }

    @Override
    public Color colorTexto() {
        return new Color(26, 31, 43);
    }
    @Override
    public Color colorTextoSecundario() {
        return new Color(100, 110, 130);
    }

    @Override
    public Color colorPrimario() {
        return new Color(47, 111, 237);
    }
    @Override
    public Color colorPrimarioHover() {
        return new Color(30, 91, 214);
    }
    @Override
    public Color colorPeligro() {
        return new Color(192, 43, 56);
    }
    @Override
    public Color colorPeligroHover() {
        return new Color(165, 34, 46);
    }

    @Override
    public Color colorTextoSobrePrimario() {
        return new Color(255, 255, 255);
    }
    @Override
    public Color colorTextoSobrePeligro() {
        return new Color(252, 228, 230);
    }

    @Override
    public Color colorAcentoSecundario() {
        return new Color(224, 27, 132);
    }

    @Override
    public Color colorSuperficie() {
        return new Color(221, 225, 232);
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
