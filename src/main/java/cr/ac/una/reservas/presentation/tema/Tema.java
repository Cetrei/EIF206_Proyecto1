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

    Color colorTextoSobrePrimario();
    Color colorTextoSobrePeligro();

    Color colorAcentoSecundario();

    Color colorSuperficie();

    Font fuenteTexto();
    Font fuenteTitulo();
}
