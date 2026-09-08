package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MatrizPanel implements CambioTemaListener {
    private static final LocalTime HORA_INICIO = LocalTime.of(6, 0);
    private static final LocalTime HORA_FIN = LocalTime.of(21, 0);

    public static LocalTime obtenerHoraDeFila(int fila) {
        List<LocalTime> horas = generarHoras();
        if (fila < 0 || fila >= horas.size()) return null;
        return horas.get(fila);
    }

    private JPanel MatrizPanel;
    private JScrollPane scrollMatriz;
    private JPanel pnlGrilla;

    private MatrizFillStrategy estrategia;
    private final List<CeldaMatriz> celdasActivas = new ArrayList<>();
    private final List<JLabel> etiquetasEncabezado = new ArrayList<>();
    private final List<JLabel> etiquetasHora = new ArrayList<>();
    private final List<JLabel> etiquetasCeldaVacia = new ArrayList<>();
    private Tema temaActual;

    public MatrizPanel() {
        GestorTema.obtenerInstancia().agregarListener(this);
        temaActual = GestorTema.obtenerInstancia().temaActivo();
        scrollMatriz.setPreferredSize(new Dimension(0, 320));
        ScrollWheelPassthrough.instalar(scrollMatriz);
        scrollMatriz.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evento) {
                pnlGrilla.revalidate();
            }
        });
        aplicarTema(temaActual);
    }

    private void createUIComponents() {
        pnlGrilla = new JPanel(new GridBagLayout()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension preferido = super.getPreferredSize();
                if (getParent() instanceof javax.swing.JViewport) {
                    int anchoViewport = getParent().getWidth();
                    if (anchoViewport > 0) {
                        return new Dimension(anchoViewport, preferido.height);
                    }
                }
                return preferido;
            }
        };
        pnlGrilla.setOpaque(false);
    }

    public void setEstrategia(MatrizFillStrategy estrategia) {
        this.estrategia = estrategia;
        redibujar();
    }

    public void redibujar() {
        pnlGrilla.removeAll();
        celdasActivas.clear();
        etiquetasEncabezado.clear();
        etiquetasHora.clear();
        etiquetasCeldaVacia.clear();
        if (estrategia == null) {
            pnlGrilla.revalidate();
            pnlGrilla.repaint();
            return;
        }

        List<String> columnas = estrategia.obtenerColumnas();
        if (columnas.isEmpty()) {
            mostrarMensajeSinColumnas();
            pnlGrilla.revalidate();
            pnlGrilla.repaint();
            return;
        }
        List<LocalTime> horas = generarHoras();

        agregarCabeceras(columnas);
        for (int fila = 0; fila < horas.size(); fila++) {
            agregarCabeceraHora(horas.get(fila), fila);
            for (int columna = 0; columna < columnas.size(); columna++) {
                agregarCelda(fila, columna);
            }
        }

        aplicarTemaAEtiquetas(temaActual);
        pnlGrilla.revalidate();
        pnlGrilla.repaint();
    }

    private void mostrarMensajeSinColumnas() {
        JLabel mensaje = new JLabel(
                "Seleccione los filtros y presione \"Cargar\" para ver la disponibilidad.",
                SwingConstants.CENTER
        );
        mensaje.setForeground(temaActual.colorTextoSecundario());
        mensaje.setFont(temaActual.fuenteTexto());
        mensaje.setBorder(new EmptyBorder(24, 16, 24, 16));
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = 0;
        restricciones.gridy = 0;
        restricciones.weightx = 1;
        restricciones.weighty = 1;
        restricciones.fill = GridBagConstraints.BOTH;
        pnlGrilla.add(mensaje, restricciones);
    }

    private void agregarCabeceras(List<String> columnas) {
        JLabel esquina = new JLabel("Hora", SwingConstants.CENTER);
        etiquetasEncabezado.add(esquina);
        pnlGrilla.add(esquina, restriccionColumnaHora(0));
        for (int columna = 0; columna < columnas.size(); columna++) {
            JLabel etiqueta = new JLabel(columnas.get(columna), SwingConstants.CENTER);
            etiquetasEncabezado.add(etiqueta);
            pnlGrilla.add(etiqueta, restriccionColumnaDia(0, columna + 1));
        }
    }

    private void agregarCabeceraHora(LocalTime hora, int fila) {
        JLabel etiqueta = new JLabel(hora.toString(), SwingConstants.CENTER);
        etiquetasHora.add(etiqueta);
        pnlGrilla.add(etiqueta, restriccionColumnaHora(fila + 1));
    }

    private void agregarCelda(int fila, int columna) {
        DatosCelda datos = estrategia.obtenerCelda(fila, columna);
        GridBagConstraints restricciones = restriccionColumnaDia(fila + 1, columna + 1);
        if (datos == null) {
            JLabel etiquetaVacia = new JLabel("-", SwingConstants.CENTER);
            etiquetasCeldaVacia.add(etiquetaVacia);
            pnlGrilla.add(etiquetaVacia, restricciones);
            return;
        }
        restricciones.insets = new Insets(3, 3, 3, 3);
        CeldaMatriz celda = new CeldaMatriz();
        celda.setDatos(datos);
        int filaFinal = fila;
        int columnaFinal = columna;
        celda.obtenerPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                estrategia.alHacerClickCelda(filaFinal, columnaFinal, datos);
            }
        });
        celdasActivas.add(celda);
        pnlGrilla.add(celda.obtenerPanel(), restricciones);
    }

    private static GridBagConstraints restriccionColumnaHora(int fila) {
        GridBagConstraints restricciones = restriccionBase(fila, 0);
        restricciones.weightx = 0;
        return restricciones;
    }

    private static GridBagConstraints restriccionColumnaDia(int fila, int columna) {
        GridBagConstraints restricciones = restriccionBase(fila, columna);
        restricciones.weightx = 1;
        return restricciones;
    }

    private static GridBagConstraints restriccionBase(int fila, int columna) {
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = columna;
        restricciones.gridy = fila;
        restricciones.insets = new Insets(0, 0, 0, 0);
        restricciones.fill = GridBagConstraints.BOTH;
        return restricciones;
    }

    private static List<LocalTime> generarHoras() {
        List<LocalTime> horas = new ArrayList<>();
        LocalTime actual = HORA_INICIO;
        while (!actual.isAfter(HORA_FIN)) {
            horas.add(actual);
            actual = actual.plusHours(1);
        }
        return horas;
    }

    public JPanel obtenerPanel() {
        return MatrizPanel;
    }

    @Override
    public void onCambioTema(Tema tema) {
        temaActual = tema;
        aplicarTema(tema);
        aplicarTemaAEtiquetas(tema);
    }

    private void aplicarTema(Tema tema) {
        pnlGrilla.setBackground(tema.colorFondoTarjeta());
        MatrizPanel.setBackground(tema.colorFondoTarjeta());
        scrollMatriz.getViewport().setBackground(tema.colorFondoTarjeta());
        scrollMatriz.setBackground(tema.colorFondoTarjeta());
        scrollMatriz.setViewportBorder(BorderFactory.createLineBorder(tema.colorBorde(), 1));
        scrollMatriz.setBorder(BorderFactory.createEmptyBorder());
        ScrollBarTematizado.aplicar(scrollMatriz.getVerticalScrollBar(), tema);
        ScrollBarTematizado.aplicar(scrollMatriz.getHorizontalScrollBar(), tema);
        pnlGrilla.repaint();
    }

    private void aplicarTemaAEtiquetas(Tema tema) {
        MatteBorder bordeEncabezado = BorderFactory.createMatteBorder(0, 0, 2, 1, tema.colorBorde());
        for (int i = 0; i < etiquetasEncabezado.size(); i++) {
            JLabel etiqueta = etiquetasEncabezado.get(i);
            etiqueta.setOpaque(true);
            etiqueta.setBackground(tema.colorFondoCampo());
            etiqueta.setForeground(tema.colorTextoSecundario());
            etiqueta.setFont(tema.fuenteTexto().deriveFont(Font.BOLD));
            etiqueta.setBorder(new CompoundBorder(bordeEncabezado, new EmptyBorder(8, 6, 8, 6)));
            if (i > 0) {
                etiqueta.setMinimumSize(new Dimension(0, etiqueta.getPreferredSize().height));
            }
        }

        MatteBorder bordeHora = BorderFactory.createMatteBorder(0, 0, 1, 1, tema.colorBorde());
        for (int i = 0; i < etiquetasHora.size(); i++) {
            JLabel etiqueta = etiquetasHora.get(i);
            etiqueta.setOpaque(true);
            etiqueta.setBackground(tema.colorFondoCampo());
            etiqueta.setForeground(tema.colorTextoSecundario());
            etiqueta.setFont(tema.fuenteTexto());
            etiqueta.setBorder(new CompoundBorder(bordeHora, new EmptyBorder(6, 10, 6, 10)));
        }

        List<String> columnas = estrategia == null ? List.of() : estrategia.obtenerColumnas();
        int columnasCantidad = Math.max(columnas.size(), 1);
        MatteBorder bordeCelda = BorderFactory.createMatteBorder(0, 0, 1, 1, tema.colorBorde());
        for (int i = 0; i < etiquetasCeldaVacia.size(); i++) {
            JLabel etiqueta = etiquetasCeldaVacia.get(i);
            int fila = i / columnasCantidad;
            etiqueta.setOpaque(true);
            etiqueta.setBackground(fila % 2 == 0 ? tema.colorFondoTarjeta() : tema.colorFondoCampo());
            etiqueta.setForeground(tema.colorTextoSecundario());
            etiqueta.setFont(tema.fuenteTexto());
            etiqueta.setBorder(new CompoundBorder(bordeCelda, new EmptyBorder(6, 6, 6, 6)));
            etiqueta.setMinimumSize(new Dimension(0, etiqueta.getPreferredSize().height));
        }
    }
}
