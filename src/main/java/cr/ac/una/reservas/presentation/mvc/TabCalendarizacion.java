package cr.ac.una.reservas.presentation.mvc;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.mvc.componentes.ComboBoxTematizado;
import cr.ac.una.reservas.presentation.mvc.componentes.MatrizPanel;
import cr.ac.una.reservas.presentation.mvc.componentes.MatrizFillStrategy;
import cr.ac.una.reservas.presentation.mvc.componentes.SpinnerTematizado;
import cr.ac.una.reservas.presentation.mvc.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.model.CalendarizacionModel;
import cr.ac.una.reservas.presentation.mvc.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.presentation.mvc.tema.Tema;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TabCalendarizacion implements CambioTemaListener, PropertyChangeListener {
    private JPanel TabCalendarizacion;
    private JPanel TarjetaTitulo;
    private JPanel TarjetaFiltros;
    private JPanel TarjetaMatriz;

    private Tarjeta tarjetaTituloReal;
    private Tarjeta tarjetaFiltrosReal;
    private Tarjeta tarjetaMatrizReal;

    private JSpinner spinnerFechaReal;
    private JComboBox<Categoria> comboCategoriaReal;
    private BotonPrimario botonCargarMatrizReal;
    private BotonSecundario botonReporteReal;
    private MatrizPanel matrizPanelReal;
    private JLabel lblEtiquetaFecha;
    private JLabel lblEtiquetaCategoria;

    public TabCalendarizacion(CalendarizacionModel modelo) {
        $$$setupUI$$$();
        modelo.addPropertyChangeListener(this);
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        armarTarjetaTitulo();
        armarTarjetaFiltros();
        armarTarjetaMatriz();
    }

    private void armarTarjetaTitulo() {
        tarjetaTituloReal = new Tarjeta();
        tarjetaTituloReal.setIcono(IconoSemantico.LOGO_APP.icono());
        tarjetaTituloReal.setTitulo("Calendarización por Recurso");
        tarjetaTituloReal.setSubtitulo("Consulta de disponibilidad por fecha y categoría específica");

        botonReporteReal = new BotonSecundario();
        botonReporteReal.setVariante(BotonSecundario.Variante.PELIGRO);
        botonReporteReal.setTexto("Generar Reporte PDF");
        botonReporteReal.setIcono(IconoSemantico.REPORTE_PDF.icono());
        tarjetaTituloReal.setAccion(botonReporteReal.obtenerPanel());

        TarjetaTitulo = tarjetaTituloReal.obtenerPanel();
    }

    private void armarTarjetaFiltros() {
        tarjetaFiltrosReal = new Tarjeta();

        spinnerFechaReal = crearSpinnerFecha();
        comboCategoriaReal = crearComboCategorias();

        botonCargarMatrizReal = new BotonPrimario();
        botonCargarMatrizReal.setTexto("Cargar Matriz");
        botonCargarMatrizReal.setIcono(IconoSemantico.RAYO.icono());

        JPanel contenido = tarjetaFiltrosReal.obtenerPanelContenido();
        contenido.setLayout(new GridBagLayout());
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.insets = new Insets(0, 0, 0, 16);
        restricciones.gridy = 0;

        restricciones.gridx = 0;
        restricciones.weightx = 1;
        contenido.add(envolverConEtiqueta("FECHA DE CONSULTA", spinnerFechaReal, true), restricciones);

        restricciones.gridx = 1;
        restricciones.weightx = 1;
        contenido.add(envolverConEtiqueta("CATEGORÍA", comboCategoriaReal, false), restricciones);

        restricciones.gridx = 2;
        restricciones.weightx = 0;
        restricciones.insets = new Insets(0, 0, 0, 0);
        contenido.add(envolverConEtiqueta(" ", botonCargarMatrizReal.obtenerPanel(), false), restricciones);

        TarjetaFiltros = tarjetaFiltrosReal.obtenerPanel();
    }

    private void armarTarjetaMatriz() {
        tarjetaMatrizReal = new Tarjeta();

        matrizPanelReal = new MatrizPanel();

        JPanel contenido = tarjetaMatrizReal.obtenerPanelContenido();
        contenido.setLayout(new BorderLayout());
        contenido.add(matrizPanelReal.obtenerPanel(), BorderLayout.CENTER);

        TarjetaMatriz = tarjetaMatrizReal.obtenerPanel();
    }

    // Hoy como valor inicial: CalendarizacionControl carga la matriz de hoy apenas se abre la pestana.
    private JSpinner crearSpinnerFecha() {
        SpinnerDateModel modelo = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(modelo);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        return spinner;
    }

    // El renderer muestra la descripcion, no Categoria.toString(). Sin "Todas": la matriz necesita una categoria concreta.
    private JComboBox<Categoria> crearComboCategorias() {
        JComboBox<Categoria> combo = new JComboBox<>();
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> lista, Object valor, int indice, boolean seleccionado, boolean conFoco
            ) {
                Component componente = super.getListCellRendererComponent(lista, valor, indice, seleccionado, conFoco);
                if (valor instanceof Categoria) {
                    setText(((Categoria) valor).getDescripcion());
                }
                return componente;
            }
        });
        return combo;
    }

    // esFecha guarda el label en lblEtiquetaFecha en vez de la client property que usa el combo, para tematizar sin recorrer subcomponentes.
    private JComponent envolverConEtiqueta(String etiqueta, JComponent control, boolean esFecha) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setBorder(new EmptyBorder(0, 2, 4, 0));
        if (esFecha) {
            lblEtiquetaFecha = lblEtiqueta;
        } else if (lblEtiquetaCategoria == null) {
            lblEtiquetaCategoria = lblEtiqueta;
        }

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(lblEtiqueta, BorderLayout.NORTH);
        envoltorio.add(control, BorderLayout.CENTER);
        return envoltorio;
    }

    // ------------------------------------------------------------------
    // Datos de los filtros
    // ------------------------------------------------------------------

    // Convierte de java.util.Date (lo que maneja JSpinner) a LocalDate (lo que usa el resto del sistema).
    public LocalDate obtenerFecha() {
        Date valor = (Date) spinnerFechaReal.getValue();
        return valor.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public Categoria obtenerCategoriaSeleccionada() {
        Object seleccion = comboCategoriaReal.getSelectedItem();
        return seleccion instanceof Categoria ? (Categoria) seleccion : null;
    }

    private void cargarCategorias(List<Categoria> categorias) {
        Categoria seleccionActual = obtenerCategoriaSeleccionada();
        comboCategoriaReal.setModel(new DefaultComboBoxModel<>(categorias.toArray(new Categoria[0])));
        if (categorias.contains(seleccionActual)) {
            comboCategoriaReal.setSelectedItem(seleccionActual);
        }
    }

    // ------------------------------------------------------------------
    // Matriz
    // ------------------------------------------------------------------

    private void mostrarMatriz(MatrizFillStrategy estrategia) {
        matrizPanelReal.setEstrategia(estrategia);
    }

    // ------------------------------------------------------------------
    // Enganches de eventos
    // ------------------------------------------------------------------

    public void alCargarMatriz(Runnable accion) {
        botonCargarMatrizReal.alHacerClick(accion);
    }

    public void alGenerarReporte(Runnable accion) {
        botonReporteReal.alHacerClick(accion);
    }

    public JPanel obtenerPanel() {
        return TabCalendarizacion;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evento) {
        if (CalendarizacionModel.PROP_CATEGORIAS_DISPONIBLES.equals(evento.getPropertyName())) {
            @SuppressWarnings("unchecked")
            List<Categoria> nuevaLista = (List<Categoria>) evento.getNewValue();
            cargarCategorias(nuevaLista);
        } else if (CalendarizacionModel.PROP_MATRIZ.equals(evento.getPropertyName())) {
            mostrarMatriz((MatrizFillStrategy) evento.getNewValue());
        }
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (TabCalendarizacion == null) return;

        TabCalendarizacion.setOpaque(true);
        TabCalendarizacion.setBackground(tema.colorFondoVentana());
        TabCalendarizacion.setBorder(new EmptyBorder(0, 0, 0, 0));

        if (lblEtiquetaFecha != null) {
            lblEtiquetaFecha.setForeground(tema.colorTextoSecundario());
            lblEtiquetaFecha.setFont(tema.fuenteTexto());
        }
        if (lblEtiquetaCategoria != null) {
            lblEtiquetaCategoria.setForeground(tema.colorTextoSecundario());
            lblEtiquetaCategoria.setFont(tema.fuenteTexto());
        }
        if (spinnerFechaReal != null) {
            SpinnerTematizado.aplicar(spinnerFechaReal, tema);
        }
        ComboBoxTematizado.aplicar(comboCategoriaReal, tema);

        TabCalendarizacion.repaint();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        TabCalendarizacion = new JPanel();
        TabCalendarizacion.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(20, 20, 20, 20), -1, 16));
        TabCalendarizacion.add(TarjetaTitulo, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabCalendarizacion.add(TarjetaFiltros, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabCalendarizacion.add(TarjetaMatriz, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        TabCalendarizacion.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return TabCalendarizacion;
    }
}
