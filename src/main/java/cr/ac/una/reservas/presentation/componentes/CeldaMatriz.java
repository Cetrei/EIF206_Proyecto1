package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class CeldaMatriz implements CambioTemaListener {
    private static final int RADIO_BORDE = 8;

    private JPanel CeldaMatriz;
    private JLabel lblTextoPrincipal;
    private JLabel lblTextoSecundario;

    private Color colorFondo;

    public CeldaMatriz() {
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        CeldaMatriz = new JPanel() {
            @Override
            protected void paintComponent(Graphics graficos) {
                Graphics2D graficos2D = (Graphics2D) graficos.create();
                graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graficos2D.setColor(colorFondo);
                graficos2D.fillRoundRect(0, 0, getWidth(), getHeight(), RADIO_BORDE, RADIO_BORDE);
                graficos2D.dispose();
                super.paintComponent(graficos);
            }
        };
        CeldaMatriz.setOpaque(false);
    }

    public void setDatos(DatosCelda datos) {
        lblTextoPrincipal.setText(datos.getTextoPrincipal());
        lblTextoSecundario.setText(datos.getTextoSecundario() == null ? "" : datos.getTextoSecundario());
        colorFondo = datos.getColor();
        CeldaMatriz.repaint();
    }

    public JPanel obtenerPanel() {
        return CeldaMatriz;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (colorFondo == null) {
            colorFondo = tema.colorPrimario();
        }
        Font fuenteBase = tema.fuenteTexto();
        lblTextoPrincipal.setForeground(tema.colorTexto());
        lblTextoPrincipal.setFont(fuenteBase.deriveFont(Font.BOLD));
        lblTextoSecundario.setForeground(tema.colorTexto());
        lblTextoSecundario.setFont(fuenteBase.deriveFont(fuenteBase.getSize2D() - 1f));
        CeldaMatriz.repaint();
    }
}
