package cr.ac.una.reservas.presentation.mvc;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.mvc.componentes.MatrizPanel;
import cr.ac.una.reservas.presentation.mvc.componentes.MatrizFillStrategy;
import cr.ac.una.reservas.presentation.mvc.componentes.SpinnerTematizado;
import cr.ac.una.reservas.presentation.mvc.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.model.ActividadModel;
import cr.ac.una.reservas.presentation.mvc.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.presentation.mvc.tema.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class TabActividades implements CambioTemaListener, PropertyChangeListener {

    private JPanel TabActividades;
    private JPanel TarjetaTitulo;
    private JPanel TarjetaFiltros;
    private JPanel TarjetaMatriz;

    private Tarjeta tarjetaTituloReal;
    private Tarjeta tarjetaFiltrosReal;
    private Tarjeta tarjetaMatrizReal;

    private JSpinner spinnerFechaReal;
    private BotonPrimario botonCargarSemanaReal;
    private BotonSecundario botonReporteReal;
    private MatrizPanel matrizPanelReal;
    private JLabel lblEtiquetaFecha;

    public TabActividades(ActividadModel modelo) {
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
        tarjetaTituloReal.setTitulo("Programación Semanal de Actividades");
        tarjetaTituloReal.setSubtitulo("Vista general de compromisos de la organización por semana");

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

        botonCargarSemanaReal = new BotonPrimario();
        botonCargarSemanaReal.setTexto("Cargar Semana");
        botonCargarSemanaReal.setIcono(IconoSemantico.RAYO.icono());

        JPanel contenido = tarjetaFiltrosReal.obtenerPanelContenido();
        contenido.setLayout(new GridBagLayout());
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.insets = new Insets(0, 0, 0, 16);
        restricciones.gridy = 0;

        restricciones.gridx = 0;
        restricciones.weightx = 1;
        contenido.add(envolverConEtiqueta("FECHA DE REFERENCIA (SEMANA)", spinnerFechaReal), restricciones);

        restricciones.gridx = 1;
        restricciones.weightx = 0;
        restricciones.insets = new Insets(0, 0, 0, 0);
        contenido.add(envolverConEtiqueta(" ", botonCargarSemanaReal.obtenerPanel()), restricciones);

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

    private JSpinner crearSpinnerFecha() {
        SpinnerDateModel modelo = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(modelo);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        return spinner;
    }

    private JPanel envolverConEtiqueta(String etiqueta, Component control) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setBorder(new EmptyBorder(0, 2, 4, 0));
        if (lblEtiquetaFecha == null) {
            lblEtiquetaFecha = lblEtiqueta;
        }

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(lblEtiqueta, BorderLayout.NORTH);
        envoltorio.add(control, BorderLayout.CENTER);
        return envoltorio;
    }

    public LocalDate obtenerFechaReferencia() {
        Date valor = (Date) spinnerFechaReal.getValue();
        return valor.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void mostrarMatriz(MatrizFillStrategy estrategia) {
        matrizPanelReal.setEstrategia(estrategia);
    }


    public void alCargarSemana(Runnable accion) {
        botonCargarSemanaReal.alHacerClick(accion);
    }

    public void alGenerarReporte(Runnable accion) {
        botonReporteReal.alHacerClick(accion);
    }

    public JPanel obtenerPanel() {
        return TabActividades;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evento) {
        if (ActividadModel.PROP_MATRIZ.equals(evento.getPropertyName())) {
            mostrarMatriz((MatrizFillStrategy) evento.getNewValue());
        }
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (TabActividades == null) return;

        TabActividades.setOpaque(true);
        TabActividades.setBackground(tema.colorFondoVentana());
        TabActividades.setBorder(new EmptyBorder(0, 0, 0, 0));

        if (lblEtiquetaFecha != null) {
            lblEtiquetaFecha.setForeground(tema.colorTextoSecundario());
            lblEtiquetaFecha.setFont(tema.fuenteTexto());
        }
        if (spinnerFechaReal != null) {
            SpinnerTematizado.aplicar(spinnerFechaReal, tema);
        }

        TabActividades.repaint();
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
        TabActividades = new JPanel();
        TabActividades.setLayout(new GridLayoutManager(4, 1, new Insets(20, 20, 20, 20), -1, 16));
        TabActividades.add(TarjetaTitulo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabActividades.add(TarjetaFiltros, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabActividades.add(TarjetaMatriz, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        TabActividades.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return TabActividades;
    }

}
