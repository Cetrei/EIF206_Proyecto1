package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.List;

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
        tabla.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tabla.setFillsViewportHeight(true);

        ordenadorFiltro = new TableRowSorter<>(modelo);
        tabla.setRowSorter(ordenadorFiltro);

        panelTabla = new JScrollPane(tabla);
        panelTabla.setBorder(javax.swing.BorderFactory.createEmptyBorder());
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
        ordenadorFiltro.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
    }

    public void alHacerClickFila(java.util.function.IntConsumer accion) {
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

        tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable tablaOrigen, Object valor, boolean seleccionado, boolean conFoco, int fila, int columna) {
                java.awt.Component componente = super.getTableCellRendererComponent(tablaOrigen, valor, seleccionado, conFoco, fila, columna);
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
        encabezado.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, tema.colorBorde()));
        // Un renderer propio en vez del nativo de Metal: mutar el
        // renderer nativo (ver historial) deja lineas divisorias
        // blancas entre columnas porque Metal recalcula su propio
        // borde por celda en cada repintado, ignorando el que se le
        // ponga desde afuera; y reemplazarlo por un
        // DefaultTableCellRenderer generico pierde la flecha de orden,
        // que Metal solo agrega sobre SU renderer interno. La unica
        // forma de tener control total de colores/bordes sin perder el
        // indicador de orden es dibujar la flecha a mano aqui.
        encabezado.setDefaultRenderer(new EncabezadoOrdenableRenderer(tema));

        panelTabla.getViewport().setBackground(fondo);
        panelTabla.setBackground(fondo);
        // El viewportBorder por defecto de JScrollPane (biselado gris
        // claro de Metal) es lo que se veia como "borde blanco" de la
        // tabla en la captura; se reemplaza por una linea del color de
        // borde del tema.
        panelTabla.setViewportBorder(javax.swing.BorderFactory.createLineBorder(tema.colorBorde(), 1));
        // Igual que el fondo del viewport: el JScrollBar recien creado
        // puede quedar con el blanco por defecto de Metal en la zona
        // sin thumb (y en los botones de flecha) si no se colorea de
        // forma explicita, sin depender solo de las claves globales de
        // UIManager. Ademas los botones de flecha de MetalScrollBarUI
        // ignoran ScrollBar.thumb/track por completo (toman su color
        // de claves "control*" compartidas por todo Metal), asi que se
        // instala un ScrollBarUI propio en vez de solo colorear.
        ScrollBarTematizado.aplicar(panelTabla.getVerticalScrollBar(), tema);
        ScrollBarTematizado.aplicar(panelTabla.getHorizontalScrollBar(), tema);
        tabla.repaint();
    }

    /**
     * Renderer del encabezado de columnas: fondo/texto/borde del tema,
     * mas una flecha de orden dibujada a mano (triangulo relleno) segun
     * table.getRowSorter().getSortKeys(), sin depender del renderer
     * nativo de Metal (ver comentario en aplicarTema para el porque).
     */
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
            javax.swing.RowSorter<?> ordenador = tablaOrigen.getRowSorter();
            if (ordenador != null && !ordenador.getSortKeys().isEmpty()) {
                javax.swing.RowSorter.SortKey clave = ordenador.getSortKeys().get(0);
                int columnaModelo = tablaOrigen.convertColumnIndexToModel(columna);
                if (clave.getColumn() == columnaModelo) {
                    columnaOrdenada = true;
                    ordenAscendente = clave.getSortOrder() == javax.swing.SortOrder.ASCENDING;
                }
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            super.paintComponent(graficos);
            if (!columnaOrdenada) {
                return;
            }
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
