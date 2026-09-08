package cr.ac.una.reservas.presentation.tema;

import java.awt.Color;
import java.awt.Font;

public class TemaOscuro implements Tema {
    @Override
    public Color colorFondoVentana() {
        return new Color(11, 16, 26);
    }
    @Override
    public Color colorFondoTarjeta() {
        return new Color(17, 24, 39);
    }
    @Override
    public Color colorFondoCampo() {
        return new Color(10, 15, 28);
    }

    @Override
    public Color colorBorde() {
        return new Color(39, 48, 68);
    }
    @Override
    public Color colorBordeEnfocado() {
        return new Color(59, 130, 246);
    }

    @Override
    public Color colorTexto() {
        return new Color(229, 233, 242);
    }
    @Override
    public Color colorTextoSecundario() {
        return new Color(138, 148, 168);
    }

    @Override
    public Color colorPrimario() {
        return new Color(47, 111, 237);
    }
    @Override
    public Color colorPrimarioHover() {
        return new Color(74, 134, 255);
    }
    @Override
    public Color colorPeligro() {
        return new Color(138, 31, 43);
    }
    @Override
    public Color colorPeligroHover() {
        return new Color(165, 40, 53);
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
        return new Color(42, 52, 74);
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
