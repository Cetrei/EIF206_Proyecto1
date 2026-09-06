package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MatrizPanel implements CambioTemaListener {
    private static final LocalTime HORA_INICIO = LocalTime.of(6, 0);
    private static final LocalTime HORA_FIN = LocalTime.of(21, 0);

    private JPanel MatrizPanel;
    private JScrollPane scrollMatriz;
    private JPanel pnlGrilla;

    private MatrizFillStrategy estrategia;
    private final List<CeldaMatriz> celdasActivas = new ArrayList<>();

    public MatrizPanel() {
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        pnlGrilla = new JPanel(new GridBagLayout());
        pnlGrilla.setOpaque(false);
    }

    public void setEstrategia(MatrizFillStrategy estrategia) {
        this.estrategia = estrategia;
        redibujar();
    }

    public void redibujar() {
        pnlGrilla.removeAll();
        celdasActivas.clear();
        if (estrategia == null) {
            pnlGrilla.revalidate();
            pnlGrilla.repaint();
            return;
        }

        List<String> columnas = estrategia.obtenerColumnas();
        List<LocalTime> horas = generarHoras();

        agregarCabeceras(columnas);
        for (int fila = 0; fila < horas.size(); fila++) {
            agregarCabeceraHora(horas.get(fila), fila);
            for (int columna = 0; columna < columnas.size(); columna++) {
                agregarCelda(fila, columna);
            }
        }

        pnlGrilla.revalidate();
        pnlGrilla.repaint();
    }

    private void agregarCabeceras(List<String> columnas) {
        pnlGrilla.add(new JLabel(""), restriccionBase(0, 0));
        for (int columna = 0; columna < columnas.size(); columna++) {
            JLabel etiqueta = new JLabel(columnas.get(columna), SwingConstants.CENTER);
            pnlGrilla.add(etiqueta, restriccionBase(0, columna + 1));
        }
    }

    private void agregarCabeceraHora(LocalTime hora, int fila) {
        JLabel etiqueta = new JLabel(hora.toString());
        pnlGrilla.add(etiqueta, restriccionBase(fila + 1, 0));
    }

    private void agregarCelda(int fila, int columna) {
        DatosCelda datos = estrategia.obtenerCelda(fila, columna);
        GridBagConstraints restricciones = restriccionBase(fila + 1, columna + 1);
        if (datos == null) {
            pnlGrilla.add(new JLabel("-", SwingConstants.CENTER), restricciones);
            return;
        }
        CeldaMatriz celda = new CeldaMatriz();
        celda.setDatos(datos);
        int filaFinal = fila;
        int columnaFinal = columna;
        celda.obtenerPanel().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evento) {
                estrategia.alHacerClickCelda(filaFinal, columnaFinal, datos);
            }
        });
        celdasActivas.add(celda);
        pnlGrilla.add(celda.obtenerPanel(), restricciones);
    }

    private static GridBagConstraints restriccionBase(int fila, int columna) {
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = columna;
        restricciones.gridy = fila;
        restricciones.insets = new Insets(2, 2, 2, 2);
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
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        pnlGrilla.setBackground(tema.colorFondoTarjeta());
        MatrizPanel.setBackground(tema.colorFondoTarjeta());
        scrollMatriz.getViewport().setBackground(tema.colorFondoTarjeta());
        pnlGrilla.repaint();
    }
}
