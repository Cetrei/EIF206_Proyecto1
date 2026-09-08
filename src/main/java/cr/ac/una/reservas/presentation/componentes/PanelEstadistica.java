package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PanelEstadistica<T> implements CambioTemaListener {
    private JPanel panelRaiz;

    private Tarjeta tarjetaFiltrosReal;
    private Tarjeta tarjetaContenidoReal;

    private JSpinner spinnerDesdeReal;
    private JSpinner spinnerHastaReal;
    private BotonPrimario botonCargarReal;
    private TablaDatos tablaDatosReal;
    private GraficoBarras graficoBarrasReal;
    private JLabel lblEtiquetaDesde;
    private JLabel lblEtiquetaHasta;

    public enum SerieEstadistica {
        PRINCIPAL,
        SECUNDARIA
    }

    private final SerieEstadistica serie;

    private List<String> encabezadosTabla = List.of();
    private Function<T, List<Object>> filaComoTabla = fila -> List.of();
    private Function<T, GraficoBarras.EntradaGrafico> filaComoGrafico;

    public PanelEstadistica(Icono icono, String titulo, String subtitulo, SerieEstadistica serie) {
        this.serie = serie;
        GestorTema.obtenerInstancia().agregarListener(this);
        construir(icono, titulo, subtitulo);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private java.awt.Color colorBarrasActual() {
        Tema tema = GestorTema.obtenerInstancia().temaActivo();
        return serie == SerieEstadistica.SECUNDARIA ? tema.colorAcentoSecundario() : tema.colorPrimario();
    }

    public void configurar(
            List<String> encabezadosTabla,
            Function<T, List<Object>> filaComoTabla,
            Function<T, GraficoBarras.EntradaGrafico> filaComoGrafico
    ) {
        this.encabezadosTabla = encabezadosTabla;
        this.filaComoTabla = filaComoTabla;
        this.filaComoGrafico = filaComoGrafico;
        tablaDatosReal.setColumnas(encabezadosTabla);
    }

    private void construir(Icono icono, String titulo, String subtitulo) {
        panelRaiz = new JPanel(new GridBagLayout());
        panelRaiz.setOpaque(false);

        armarTarjetaFiltros(icono, titulo, subtitulo);
        armarTarjetaContenido();

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = 0;
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.weightx = 1;

        restricciones.gridy = 0;
        restricciones.weighty = 0;
        restricciones.insets = new Insets(0, 0, 16, 0);
        panelRaiz.add(tarjetaFiltrosReal.obtenerPanel(), restricciones);

        restricciones.gridy = 1;
        restricciones.weighty = 1;
        restricciones.fill = GridBagConstraints.BOTH;
        restricciones.insets = new Insets(0, 0, 0, 0);
        panelRaiz.add(tarjetaContenidoReal.obtenerPanel(), restricciones);
    }

    private void armarTarjetaFiltros(Icono icono, String titulo, String subtitulo) {
        tarjetaFiltrosReal = new Tarjeta();
        tarjetaFiltrosReal.setIcono(icono);
        tarjetaFiltrosReal.setTitulo(titulo);
        tarjetaFiltrosReal.setSubtitulo(subtitulo);

        spinnerDesdeReal = crearSpinnerFecha();
        spinnerHastaReal = crearSpinnerFecha();

        botonCargarReal = new BotonPrimario();
        botonCargarReal.setTexto("Cargar");
        botonCargarReal.setIcono(Icono.RAYO);

        JPanel contenido = tarjetaFiltrosReal.obtenerPanelContenido();
        contenido.setLayout(new GridBagLayout());
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.insets = new Insets(0, 0, 0, 8);
        restricciones.gridy = 0;

        restricciones.gridx = 0;
        restricciones.weightx = 1;
        contenido.add(envolverConEtiqueta("DESDE", spinnerDesdeReal, true), restricciones);

        restricciones.gridx = 1;
        restricciones.weightx = 1;
        contenido.add(envolverConEtiqueta("HASTA", spinnerHastaReal, false), restricciones);

        restricciones.gridx = 2;
        restricciones.weightx = 0;
        restricciones.insets = new Insets(0, 0, 0, 0);
        contenido.add(envolverConEtiqueta(" ", botonCargarReal.obtenerPanel(), false), restricciones);

        Dimension minimoSpinner = new Dimension(70, spinnerDesdeReal.getPreferredSize().height);
        spinnerDesdeReal.setMinimumSize(minimoSpinner);
        spinnerHastaReal.setMinimumSize(minimoSpinner);
    }

    private void armarTarjetaContenido() {
        tarjetaContenidoReal = new Tarjeta();

        tablaDatosReal = new TablaDatos();
        tablaDatosReal.obtenerPanel().setPreferredSize(new Dimension(0, 160));

        graficoBarrasReal = new GraficoBarras();
        graficoBarrasReal.obtenerPanel().setPreferredSize(new Dimension(0, 220));

        JPanel contenido = tarjetaContenidoReal.obtenerPanelContenido();
        contenido.setLayout(new BorderLayout(0, 16));
        contenido.add(tablaDatosReal.obtenerPanel(), BorderLayout.NORTH);
        contenido.add(graficoBarrasReal.obtenerPanel(), BorderLayout.CENTER);
    }

    private JSpinner crearSpinnerFecha() {
        SpinnerDateModel modelo = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(modelo);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        return spinner;
    }

    private JPanel envolverConEtiqueta(String etiqueta, Component control, boolean esDesde) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setBorder(new EmptyBorder(0, 2, 4, 0));
        if (esDesde) {
            lblEtiquetaDesde = lblEtiqueta;
        } else if (lblEtiquetaHasta == null) {
            lblEtiquetaHasta = lblEtiqueta;
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

    public LocalDate obtenerFechaDesde() {
        return convertir((Date) spinnerDesdeReal.getValue());
    }

    public LocalDate obtenerFechaHasta() {
        return convertir((Date) spinnerHastaReal.getValue());
    }

    private static LocalDate convertir(Date valor) {
        return valor.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // ------------------------------------------------------------------
    // Datos mostrados
    // ------------------------------------------------------------------

    private List<T> ultimasFilas = List.of();

    public void mostrarDatos(List<T> filas) {
        this.ultimasFilas = filas;
        redibujarGrafico();

        List<List<Object>> filasTabla = new ArrayList<>();
        for (T fila : filas) {
            filasTabla.add(filaComoTabla.apply(fila));
        }
        tablaDatosReal.setFilas(filasTabla);
    }

    private void redibujarGrafico() {
        java.awt.Color colorBarras = colorBarrasActual();
        List<GraficoBarras.EntradaGrafico> entradasGrafico = new ArrayList<>();
        for (T fila : ultimasFilas) {
            GraficoBarras.EntradaGrafico entrada = filaComoGrafico.apply(fila);
            entradasGrafico.add(new GraficoBarras.EntradaGrafico(entrada.getEtiqueta(), entrada.getValor(), colorBarras));
        }
        graficoBarrasReal.setDatos(entradasGrafico);
    }

    // ------------------------------------------------------------------
    // Enganches de eventos
    // ------------------------------------------------------------------

    public void alCargar(Runnable accion) {
        botonCargarReal.alHacerClick(accion);
    }

    public void alCargar(BiConsumer<LocalDate, LocalDate> accion) {
        alCargar(() -> accion.accept(obtenerFechaDesde(), obtenerFechaHasta()));
    }

    public JPanel obtenerPanel() {
        return panelRaiz;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (lblEtiquetaDesde != null) {
            lblEtiquetaDesde.setForeground(tema.colorTextoSecundario());
            lblEtiquetaDesde.setFont(tema.fuenteTexto());
        }
        if (lblEtiquetaHasta != null) {
            lblEtiquetaHasta.setForeground(tema.colorTextoSecundario());
            lblEtiquetaHasta.setFont(tema.fuenteTexto());
        }
        if (spinnerDesdeReal != null) {
            SpinnerTematizado.aplicar(spinnerDesdeReal, tema);
        }
        if (spinnerHastaReal != null) {
            SpinnerTematizado.aplicar(spinnerHastaReal, tema);
        }
        redibujarGrafico();
    }
}
