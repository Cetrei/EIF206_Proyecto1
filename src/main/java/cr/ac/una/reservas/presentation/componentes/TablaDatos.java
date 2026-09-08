package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;

public class TablaDatos implements CambioTemaListener {
    private JTable tabla;
    private JScrollPane panelTabla;
    private DefaultTableModel modelo;
    private TableRowSorter<DefaultTableModel> ordenadorFiltro;

    public TablaDatos() {
        construirTabla();
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void construirTabla() {
        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(28);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);

        ordenadorFiltro = new TableRowSorter<>(modelo);
        tabla.setRowSorter(ordenadorFiltro);

        panelTabla = new JScrollPane(tabla);
        panelTabla.setBorder(BorderFactory.createEmptyBorder());
        // Sin esto, esta tabla atrapaba el scroll de la rueda del mouse en cualquier pestana que la use (ver ScrollWheelPassthrough).
        ScrollWheelPassthrough.instalar(panelTabla);
    }

    public void setColumnas(List<String> encabezados) {
        modelo.setColumnIdentifiers(encabezados.toArray());
    }

    public void setFilas(List<List<Object>> filas) {
        modelo.setRowCount(0);
        for (List<Object> fila : filas) {
            modelo.addRow(fila.toArray());
        }
    }

    public void filtrarPor(String texto) {
        if (texto == null || texto.isBlank()) {
            ordenadorFiltro.setRowFilter(null);
            return;
        }
        ordenadorFiltro.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
    }

    public void alHacerClickFila(IntConsumer accion) {
        tabla.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                accion.accept(tabla.convertRowIndexToModel(tabla.getSelectedRow()));
            }
        });
    }

    public JTable obtenerTabla() {
        return tabla;
    }

    public JScrollPane obtenerPanel() {
        return panelTabla;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        Color fondo = tema.colorFondoTarjeta();
        Color fondoAlterno = tema.colorFondoCampo();
        Color texto = tema.colorTexto();
        Color colorSeleccion = tema.colorPrimario();

        tabla.setBackground(fondo);
        tabla.setForeground(texto);
        tabla.setFont(tema.fuenteTexto());
        tabla.setSelectionBackground(colorSeleccion);
        tabla.setSelectionForeground(texto);
        tabla.setGridColor(tema.colorBorde());

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tablaOrigen, Object valor, boolean seleccionado, boolean conFoco, int fila, int columna) {
                Component componente = super.getTableCellRendererComponent(tablaOrigen, valor, seleccionado, conFoco, fila, columna);
                if (!seleccionado) {
                    componente.setBackground(fila % 2 == 0 ? fondo : fondoAlterno);
                    componente.setForeground(texto);
                }
                return componente;
            }
        });

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setBackground(tema.colorFondoCampo());
        encabezado.setForeground(tema.colorTextoSecundario());
        encabezado.setFont(tema.fuenteTexto());
        encabezado.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, tema.colorBorde()));
        // Renderer propio: el nativo de Metal ignora el borde por celda y recalcula el suyo, dejando lineas blancas.
        encabezado.setDefaultRenderer(new EncabezadoOrdenableRenderer(tema));

        panelTabla.getViewport().setBackground(fondo);
        panelTabla.setBackground(fondo);
        panelTabla.setViewportBorder(BorderFactory.createLineBorder(tema.colorBorde(), 1));
        ScrollBarTematizado.aplicar(panelTabla.getVerticalScrollBar(), tema);
        ScrollBarTematizado.aplicar(panelTabla.getHorizontalScrollBar(), tema);
        tabla.repaint();
    }

    private static final class EncabezadoOrdenableRenderer extends DefaultTableCellRenderer {
        private static final int TAMANO_FLECHA = 4;

        private final Color fondo;
        private final Color texto;
        private final Color colorFlecha;
        private boolean ordenAscendente;
        private boolean columnaOrdenada;

        private EncabezadoOrdenableRenderer(Tema tema) {
            this.fondo = tema.colorFondoCampo();
            this.texto = tema.colorTextoSecundario();
            this.colorFlecha = tema.colorPrimario();
            setHorizontalAlignment(SwingConstants.LEFT);
            setOpaque(true);
            setBorder(new EmptyBorder(5, 10, 5, 16));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable tablaOrigen, Object valor, boolean seleccionado, boolean conFoco, int fila, int columna
        ) {
            super.getTableCellRendererComponent(tablaOrigen, valor, seleccionado, conFoco, fila, columna);
            setBackground(fondo);
            setForeground(texto);
            setFont(tablaOrigen.getFont());

            columnaOrdenada = false;
            RowSorter<?> ordenador = tablaOrigen.getRowSorter();
            if (ordenador != null && !ordenador.getSortKeys().isEmpty()) {
                RowSorter.SortKey clave = ordenador.getSortKeys().get(0);
                int columnaModelo = tablaOrigen.convertColumnIndexToModel(columna);
                if (clave.getColumn() == columnaModelo) {
                    columnaOrdenada = true;
                    ordenAscendente = clave.getSortOrder() == SortOrder.ASCENDING;
                }
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            super.paintComponent(graficos);
            if (!columnaOrdenada) return;
            Graphics2D graficos2D = (Graphics2D) graficos.create();
            graficos2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graficos2D.setColor(colorFlecha);

            int x = getWidth() - TAMANO_FLECHA * 3;
            int yCentro = getHeight() / 2;
            Polygon flecha = new Polygon();
            if (ordenAscendente) {
                flecha.addPoint(x, yCentro + TAMANO_FLECHA / 2);
                flecha.addPoint(x + TAMANO_FLECHA * 2, yCentro + TAMANO_FLECHA / 2);
                flecha.addPoint(x + TAMANO_FLECHA, yCentro - TAMANO_FLECHA / 2);
            } else {
                flecha.addPoint(x, yCentro - TAMANO_FLECHA / 2);
                flecha.addPoint(x + TAMANO_FLECHA * 2, yCentro - TAMANO_FLECHA / 2);
                flecha.addPoint(x + TAMANO_FLECHA, yCentro + TAMANO_FLECHA / 2);
            }
            graficos2D.fillPolygon(flecha);
            graficos2D.dispose();
        }
    }
}
