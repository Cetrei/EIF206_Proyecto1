package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class GraficoBarras implements CambioTemaListener {
    public static class EntradaGrafico {
        private final String etiqueta;
        private final double valor;
        private final Color color;

        public EntradaGrafico(String etiqueta, double valor, Color color) {
            this.etiqueta = etiqueta;
            this.valor = valor;
            this.color = color;
        }

        public String getEtiqueta() {
            return etiqueta;
        }
        public double getValor() {
            return valor;
        }
        public Color getColor() {
            return color;
        }
    }

    private static final int MARGEN_IZQUIERDO = 44;
    private static final int MARGEN_INFERIOR = 28;
    private static final int MARGEN_SUPERIOR = 16;
    private static final int MARGEN_DERECHO = 16;
    private static final int CANTIDAD_LINEAS_GRID = 4;

    private JPanel panelGrafico;

    private List<EntradaGrafico> entradas = new ArrayList<>();
    private Tema temaActual;
    private int indiceHover = -1;

    public GraficoBarras() {
        GestorTema.obtenerInstancia().agregarListener(this);
        temaActual = GestorTema.obtenerInstancia().temaActivo();
        construirPanel();
    }

    private void construirPanel() {
        panelGrafico = new JPanel() {
            @Override
            protected void paintComponent(Graphics graficos) {
                super.paintComponent(graficos);
                dibujar((Graphics2D) graficos.create());
            }
        };
        panelGrafico.setOpaque(false);
        panelGrafico.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evento) {
                actualizarHover(evento.getX(), evento.getY());
            }
        });
        panelGrafico.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent evento) {
                indiceHover = -1;
                panelGrafico.repaint();
            }
        });
    }

    public void setDatos(List<EntradaGrafico> nuevasEntradas) {
        this.entradas = nuevasEntradas == null ? new ArrayList<>() : nuevasEntradas;
        indiceHover = -1;
        panelGrafico.repaint();
    }

    public JPanel obtenerPanel() {
        return panelGrafico;
    }

    private void actualizarHover(int x, int y) {
        int nuevoIndice = indiceBarraEn(x, y);
        if (nuevoIndice != indiceHover) {
            indiceHover = nuevoIndice;
            panelGrafico.repaint();
        }
    }

    private int indiceBarraEn(int x, int y) {
        if (entradas.isEmpty()) return -1;
        Rectangle areaGrafico = areaGrafico();
        if (!areaGrafico.contains(x, y)) return -1;
        double anchoBarra = areaGrafico.width / (double) entradas.size();
        int indice = (int) ((x - areaGrafico.x) / anchoBarra);
        return indice >= 0 && indice < entradas.size() ? indice : -1;
    }

    private Rectangle areaGrafico() {
        int ancho = panelGrafico.getWidth() - MARGEN_IZQUIERDO - MARGEN_DERECHO;
        int alto = panelGrafico.getHeight() - MARGEN_SUPERIOR - MARGEN_INFERIOR;
        return new Rectangle(MARGEN_IZQUIERDO, MARGEN_SUPERIOR, Math.max(ancho, 0), Math.max(alto, 0));
    }

    private void dibujar(Graphics2D graficos2D) {
        graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle areaGrafico = areaGrafico();

        double valorMaximo = valorMaximo();
        dibujarGrid(graficos2D, areaGrafico, valorMaximo);
        dibujarBarras(graficos2D, areaGrafico, valorMaximo);
        dibujarEtiquetasEje(graficos2D, areaGrafico);

        graficos2D.dispose();
    }

    private double valorMaximo() {
        double maximo = 0;
        for (EntradaGrafico entrada : entradas) {
            maximo = Math.max(maximo, entrada.getValor());
        }
        return maximo <= 0 ? 1 : maximo;
    }

    private void dibujarGrid(Graphics2D graficos2D, Rectangle areaGrafico, double valorMaximo) {
        graficos2D.setColor(temaActual.colorBorde());
        graficos2D.setFont(temaActual.fuenteTexto().deriveFont(11f));
        FontMetrics metricas = graficos2D.getFontMetrics();

        for (int linea = 0; linea <= CANTIDAD_LINEAS_GRID; linea++) {
            double proporcion = linea / (double) CANTIDAD_LINEAS_GRID;
            int y = areaGrafico.y + areaGrafico.height - (int) (proporcion * areaGrafico.height);
            graficos2D.drawLine(areaGrafico.x, y, areaGrafico.x + areaGrafico.width, y);

            String etiquetaValor = formatearValor(proporcion * valorMaximo);
            int anchoTexto = metricas.stringWidth(etiquetaValor);
            graficos2D.setColor(temaActual.colorTextoSecundario());
            graficos2D.drawString(etiquetaValor, areaGrafico.x - anchoTexto - 6, y + metricas.getAscent() / 2 - 2);
            graficos2D.setColor(temaActual.colorBorde());
        }
    }

    private void dibujarBarras(Graphics2D graficos2D, Rectangle areaGrafico, double valorMaximo) {
        if (entradas.isEmpty()) return;
        double anchoBarra = areaGrafico.width / (double) entradas.size();
        double factorRelleno = 0.6;

        for (int indice = 0; indice < entradas.size(); indice++) {
            EntradaGrafico entrada = entradas.get(indice);
            Color colorBarra = entrada.getColor() != null ? entrada.getColor() : temaActual.colorPrimario();
            if (indice == indiceHover) {
                colorBarra = colorBarra.brighter();
            }

            double alturaBarra = (entrada.getValor() / valorMaximo) * areaGrafico.height;
            int anchoDibujado = (int) (anchoBarra * factorRelleno);
            int x = (int) (areaGrafico.x + indice * anchoBarra + (anchoBarra - anchoDibujado) / 2.0);
            int y = areaGrafico.y + areaGrafico.height - (int) alturaBarra;

            graficos2D.setColor(colorBarra);
            graficos2D.fillRoundRect(x, y, anchoDibujado, (int) alturaBarra, 6, 6);

            if (indice == indiceHover) {
                dibujarValorSobreBarra(graficos2D, entrada, x, y, anchoDibujado);
            }
        }
    }

    private void dibujarValorSobreBarra(Graphics2D graficos2D, EntradaGrafico entrada, int x, int y, int ancho) {
        graficos2D.setFont(temaActual.fuenteTexto().deriveFont(Font.BOLD, 11f));
        String texto = formatearValor(entrada.getValor());
        FontMetrics metricas = graficos2D.getFontMetrics();
        int anchoTexto = metricas.stringWidth(texto);
        graficos2D.setColor(temaActual.colorTexto());
        graficos2D.drawString(texto, x + ancho / 2 - anchoTexto / 2, y - 6);
    }

    private void dibujarEtiquetasEje(Graphics2D graficos2D, Rectangle areaGrafico) {
        if (entradas.isEmpty()) return;
        graficos2D.setFont(temaActual.fuenteTexto().deriveFont(11f));
        graficos2D.setColor(temaActual.colorTextoSecundario());
        FontMetrics metricas = graficos2D.getFontMetrics();

        double anchoBarra = areaGrafico.width / (double) entradas.size();
        for (int indice = 0; indice < entradas.size(); indice++) {
            String etiqueta = entradas.get(indice).getEtiqueta();
            int anchoTexto = metricas.stringWidth(etiqueta);
            int x = (int) (areaGrafico.x + indice * anchoBarra + anchoBarra / 2 - anchoTexto / 2.0);
            int y = areaGrafico.y + areaGrafico.height + metricas.getAscent() + 6;
            graficos2D.drawString(etiqueta, x, y);
        }
    }

    private static String formatearValor(double valor) {
        if (valor == Math.floor(valor)) {
            return String.valueOf((long) valor);
        }
        return String.format("%.1f", valor);
    }

    @Override
    public void onCambioTema(Tema tema) {
        this.temaActual = tema;
        SwingUtilities.invokeLater(panelGrafico::repaint);
    }
}
