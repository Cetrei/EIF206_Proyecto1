package cr.ac.una.reservas.presentation;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.presentation.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.componentes.SpinnerTematizado;
import cr.ac.una.reservas.presentation.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.componentes.TablaDatos;
import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.IntConsumer;

public class TabReservas implements CambioTemaListener {
    private JPanel TabReservas;
    private JPanel TarjetaTitulo;
    private JPanel TarjetaFormulario;
    private JPanel TarjetaTabla;

    private Tarjeta tarjetaTituloReal;
    private Tarjeta tarjetaFormularioReal;
    private Tarjeta tarjetaTablaReal;

    private BotonSecundario botonReporteReal;

    // Bloque de IA.
    private JTextArea txtFraseIaReal;
    private BotonPrimario botonExtraerIaReal;

    // Detalles de la reserva.
    private CampoTexto campoActividadReal;
    private JSpinner spinnerFechaReal;
    private JSpinner spinnerHoraInicioReal;
    private JSpinner spinnerHoraFinReal;
    private JList<Categoria> listaCategoriasReal;

    private BotonPrimario botonSolicitarReal;
    private BotonIcono botonLimpiarReal;

    // Tabla de reservas propias.
    private TablaDatos tablaReal;
    private BotonSecundario botonCancelarReal;

    private JLabel lblEtiquetaFecha;
    private JLabel lblEtiquetaHoraInicio;
    private JLabel lblEtiquetaHoraFin;
    private JLabel lblEtiquetaCategorias;

    public TabReservas() {
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        armarTarjetaTitulo();
        armarTarjetaFormulario();
        armarTarjetaTabla();
    }

    private void armarTarjetaTitulo() {
        tarjetaTituloReal = new Tarjeta();
        tarjetaTituloReal.setIcono(IconoSemantico.LOGO_APP.icono());
        tarjetaTituloReal.setTitulo("Gestión de Reservas");
        tarjetaTituloReal.setSubtitulo("Solicite recursos manualmente o mediante Asistente IA");

        botonReporteReal = new BotonSecundario();
        botonReporteReal.setVariante(BotonSecundario.Variante.PELIGRO);
        botonReporteReal.setTexto("Generar Reporte PDF");
        botonReporteReal.setIcono(IconoSemantico.REPORTE_PDF.icono());
        tarjetaTituloReal.setAccion(botonReporteReal.obtenerPanel());

        TarjetaTitulo = tarjetaTituloReal.obtenerPanel();
    }

    private void armarTarjetaFormulario() {
        tarjetaFormularioReal = new Tarjeta();

        JPanel contenido = tarjetaFormularioReal.obtenerPanelContenido();
        contenido.setLayout(new BorderLayout(0, 16));
        contenido.add(armarBloqueIa(), BorderLayout.NORTH);
        contenido.add(armarBloqueDetalles(), BorderLayout.CENTER);

        TarjetaFormulario = tarjetaFormularioReal.obtenerPanel();
    }

    private JPanel armarBloqueIa() {
        JPanel bloque = new JPanel(new BorderLayout(0, 10));
        bloque.setOpaque(false);
        bloque.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel lblTituloIa = new JLabel("LLENADO AUTOMÁTICO CON IA");
        lblTituloIa.setBorder(new EmptyBorder(0, 2, 0, 0));

        txtFraseIaReal = new JTextArea(3, 0);
        txtFraseIaReal.setLineWrap(true);
        txtFraseIaReal.setWrapStyleWord(true);
        txtFraseIaReal.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scrollFrase = new JScrollPane(txtFraseIaReal);

        Color colorBorde = GestorTema.obtenerInstancia().temaActivo().colorBorde();
        scrollFrase.setBorder(BorderFactory.createLineBorder(colorBorde, 1, true));

        botonExtraerIaReal = new BotonPrimario();
        botonExtraerIaReal.setTexto("Extraer Datos con IA");
        botonExtraerIaReal.setIcono(IconoSemantico.RAYO.icono());

        JPanel filaBoton = new JPanel(new BorderLayout());
        filaBoton.setOpaque(false);
        filaBoton.add(botonExtraerIaReal.obtenerPanel(), BorderLayout.CENTER);

        bloque.add(lblTituloIa, BorderLayout.NORTH);
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(scrollFrase, BorderLayout.CENTER);
        centro.add(filaBoton, BorderLayout.SOUTH);
        bloque.add(centro, BorderLayout.CENTER);

        return bloque;
    }

    private JPanel armarBloqueDetalles() {
        JPanel bloque = new JPanel(new GridBagLayout());
        bloque.setOpaque(false);
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.weightx = 1;
        restricciones.insets = new Insets(0, 0, 16, 0);
        restricciones.gridx = 0;
        restricciones.gridwidth = 3;

        campoActividadReal = new CampoTexto();
        campoActividadReal.setEtiqueta("NOMBRE DE LA ACTIVIDAD");
        campoActividadReal.setPlaceholder("Ej: Sesión de Junta Directiva");

        restricciones.gridy = 0;
        bloque.add(campoActividadReal.obtenerPanel(), restricciones);

        spinnerFechaReal = crearSpinnerFecha();
        spinnerHoraInicioReal = crearSpinnerHora();
        spinnerHoraFinReal = crearSpinnerHora();

        restricciones.gridy = 1;
        restricciones.gridwidth = 1;
        restricciones.insets = new Insets(0, 0, 16, 16);
        restricciones.gridx = 0;
        bloque.add(envolverConEtiqueta("FECHA", spinnerFechaReal, 0), restricciones);

        restricciones.gridx = 1;
        bloque.add(envolverConEtiqueta("HORA INICIO", spinnerHoraInicioReal, 1), restricciones);

        restricciones.gridx = 2;
        restricciones.insets = new Insets(0, 0, 16, 0);
        bloque.add(envolverConEtiqueta("HORA FIN", spinnerHoraFinReal, 2), restricciones);

        listaCategoriasReal = crearListaCategorias();
        JScrollPane scrollCategorias = new JScrollPane(listaCategoriasReal);
        scrollCategorias.setPreferredSize(new Dimension(0, 110));

        restricciones.gridx = 0;
        restricciones.gridy = 2;
        restricciones.gridwidth = 3;
        restricciones.insets = new Insets(0, 0, 4, 0);
        bloque.add(envolverConEtiqueta("CATEGORÍAS REQUERIDAS (MÚLTIPLE)", scrollCategorias, 3), restricciones);

        JLabel lblAyudaMultiple = new JLabel("* Mantenga Ctrl para seleccionar varias opciones");
        lblAyudaMultiple.setFont(GestorTema.obtenerInstancia().temaActivo().fuenteTexto().deriveFont(10.5f));
        lblAyudaMultiple.setForeground(GestorTema.obtenerInstancia().temaActivo().colorTextoSecundario());
        restricciones.gridy = 3;
        restricciones.insets = new Insets(0, 0, 16, 0);
        bloque.add(lblAyudaMultiple, restricciones);

        botonSolicitarReal = new BotonPrimario();
        botonSolicitarReal.setTexto("Solicitar Reserva");
        botonSolicitarReal.setIcono(IconoSemantico.CONFIRMAR.icono());

        botonLimpiarReal = new BotonIcono();
        botonLimpiarReal.setIcono(IconoSemantico.LIMPIAR.icono());

        JPanel filaBotones = new JPanel(new BorderLayout(8, 0));
        filaBotones.setOpaque(false);
        filaBotones.add(botonSolicitarReal.obtenerPanel(), BorderLayout.CENTER);
        filaBotones.add(botonLimpiarReal.obtenerPanel(), BorderLayout.EAST);

        restricciones.gridy = 4;
        restricciones.insets = new Insets(0, 0, 0, 0);
        bloque.add(filaBotones, restricciones);

        return bloque;
    }

    private void armarTarjetaTabla() {
        tarjetaTablaReal = new Tarjeta();
        tarjetaTituloConAccionTabla();

        tablaReal = new TablaDatos();
        tablaReal.setColumnas(List.of("ID", "ACTIVIDAD", "FECHA", "HORARIO", "RECURSOS ASIGNADOS", "ESTADO"));
        tablaReal.obtenerTabla().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel contenido = tarjetaTablaReal.obtenerPanelContenido();
        contenido.setLayout(new BorderLayout());
        contenido.add(tablaReal.obtenerPanel(), BorderLayout.CENTER);

        TarjetaTabla = tarjetaTablaReal.obtenerPanel();
    }

    private void tarjetaTituloConAccionTabla() {
        tarjetaTablaReal.setTitulo("Mis Reservas Registradas");

        botonCancelarReal = new BotonSecundario();
        botonCancelarReal.setVariante(BotonSecundario.Variante.PELIGRO);
        botonCancelarReal.setTexto("Cancelar Seleccionada");
        botonCancelarReal.setIcono(IconoSemantico.BORRAR.icono());
        tarjetaTablaReal.setAccion(botonCancelarReal.obtenerPanel());
    }

    private JSpinner crearSpinnerFecha() {
        SpinnerDateModel modelo = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(modelo);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        return spinner;
    }

    private JSpinner crearSpinnerHora() {
        SpinnerDateModel modelo = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        JSpinner spinner = new JSpinner(modelo);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "hh:mm a"));
        return spinner;
    }

    private JList<Categoria> crearListaCategorias() {
        JList<Categoria> lista = new JList<>();
        lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> listaOrigen, Object valor, int indice, boolean seleccionado, boolean conFoco
            ) {
                Component componente = super.getListCellRendererComponent(listaOrigen, valor, indice, seleccionado, conFoco);
                if (valor instanceof Categoria) {
                    setText(((Categoria) valor).getDescripcion());
                }
                return componente;
            }
        });
        return lista;
    }

    private JPanel envolverConEtiqueta(String etiqueta, Component control, int indiceEtiqueta) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setBorder(new EmptyBorder(0, 2, 4, 0));
        switch (indiceEtiqueta) {
            case 0: lblEtiquetaFecha = lblEtiqueta; break;
            case 1: lblEtiquetaHoraInicio = lblEtiqueta; break;
            case 2: lblEtiquetaHoraFin = lblEtiqueta; break;
            default: lblEtiquetaCategorias = lblEtiqueta; break;
        }

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(lblEtiqueta, BorderLayout.NORTH);
        envoltorio.add(control, BorderLayout.CENTER);
        return envoltorio;
    }

    // ------------------------------------------------------------------
    // Bloque de IA
    // ------------------------------------------------------------------

    public String obtenerFraseIa() {
        return txtFraseIaReal.getText();
    }

    public void alExtraerConIA(Runnable accion) {
        botonExtraerIaReal.alHacerClick(accion);
    }

    // ------------------------------------------------------------------
    // Datos del formulario (Detalles de la reserva)
    // ------------------------------------------------------------------

    public String obtenerActividad() {
        return campoActividadReal.obtenerTexto();
    }

    public void mostrarActividad(String actividad) {
        campoActividadReal.mostrarValor(actividad);
    }

    public LocalDate obtenerFecha() {
        Object valor = spinnerFechaReal.getValue();
        return valor instanceof Date ? convertirFecha((Date) valor) : null;
    }

    public void mostrarFecha(LocalDate fecha) {
        if (fecha == null) return;

        spinnerFechaReal.setValue(Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public LocalTime obtenerHoraInicio() {
        return convertirHora((Date) spinnerHoraInicioReal.getValue());
    }

    public void mostrarHoraInicio(LocalTime hora) {
        if (hora == null) return;

        spinnerHoraInicioReal.setValue(convertirAFecha(hora));
    }

    public LocalTime obtenerHoraFin() {
        return convertirHora((Date) spinnerHoraFinReal.getValue());
    }

    public void mostrarHoraFin(LocalTime hora) {
        if (hora == null) return;

        spinnerHoraFinReal.setValue(convertirAFecha(hora));
    }

    private static LocalDate convertirFecha(Date valor) {
        return valor.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static LocalTime convertirHora(Date valor) {
        return valor.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
    }

    private static Date convertirAFecha(LocalTime hora) {
        return Date.from(hora.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
    }

    public List<Categoria> obtenerCategoriasSeleccionadas() {
        return listaCategoriasReal.getSelectedValuesList();
    }

    public void mostrarCategoriasSeleccionadas(List<String> idsCategorias) {
        if (idsCategorias == null) return;
        ListModel<Categoria> modelo = listaCategoriasReal.getModel();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < modelo.getSize(); i++) {
            if (idsCategorias.contains(modelo.getElementAt(i).getId())) {
                indices.add(i);
            }
        }
        int[] indicesArray = indices.stream().mapToInt(Integer::intValue).toArray();
        listaCategoriasReal.setSelectedIndices(indicesArray);
    }

    public void cargarCategorias(List<Categoria> categorias) {
        listaCategoriasReal.setListData(categorias.toArray(new Categoria[0]));
    }

    public void limpiarFormulario() {
        txtFraseIaReal.setText("");
        mostrarActividad("");
        spinnerFechaReal.setValue(new Date());
        spinnerHoraInicioReal.setValue(new Date());
        spinnerHoraFinReal.setValue(new Date());
        listaCategoriasReal.clearSelection();
    }

    // ------------------------------------------------------------------
    // Tabla de reservas propias
    // ------------------------------------------------------------------

    public void mostrarReservas(List<List<Object>> filas) {
        tablaReal.setFilas(filas);
    }

    public void alSeleccionarFila(IntConsumer accion) {
        tablaReal.alHacerClickFila(accion);
    }

    // ------------------------------------------------------------------
    // Enganches de eventos
    // ------------------------------------------------------------------

    public void alSolicitarReserva(Runnable accion) {
        botonSolicitarReal.alHacerClick(accion);
        campoActividadReal.alConfirmar(accion);
    }

    public void alLimpiar(Runnable accion) {
        botonLimpiarReal.alHacerClick(accion);
    }

    public void alCancelarSeleccionada(Runnable accion) {
        botonCancelarReal.alHacerClick(accion);
    }

    public void alGenerarReporte(Runnable accion) {
        botonReporteReal.alHacerClick(accion);
    }

    public JPanel obtenerPanel() {
        return TabReservas;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (TabReservas == null) return;

        TabReservas.setOpaque(true);
        TabReservas.setBackground(tema.colorFondoVentana());
        TabReservas.setBorder(new EmptyBorder(0, 0, 0, 0));

        if (txtFraseIaReal != null) {
            txtFraseIaReal.setBackground(tema.colorFondoCampo());
            txtFraseIaReal.setForeground(tema.colorTexto());
            txtFraseIaReal.setCaretColor(tema.colorTexto());
            txtFraseIaReal.setFont(tema.fuenteTexto());
        }
        if (listaCategoriasReal != null) {
            listaCategoriasReal.setBackground(tema.colorFondoCampo());
            listaCategoriasReal.setForeground(tema.colorTexto());
            listaCategoriasReal.setSelectionBackground(tema.colorPrimario());
            listaCategoriasReal.setSelectionForeground(tema.colorTexto());
            listaCategoriasReal.setFont(tema.fuenteTexto());
        }
        aplicarTemaEtiqueta(lblEtiquetaFecha, tema);
        aplicarTemaEtiqueta(lblEtiquetaHoraInicio, tema);
        aplicarTemaEtiqueta(lblEtiquetaHoraFin, tema);
        aplicarTemaEtiqueta(lblEtiquetaCategorias, tema);

        if (spinnerFechaReal != null) {
            SpinnerTematizado.aplicar(spinnerFechaReal, tema);
        }
        if (spinnerHoraInicioReal != null) {
            SpinnerTematizado.aplicar(spinnerHoraInicioReal, tema);
        }
        if (spinnerHoraFinReal != null) {
            SpinnerTematizado.aplicar(spinnerHoraFinReal, tema);
        }

        TabReservas.repaint();
    }

    private static void aplicarTemaEtiqueta(JLabel etiqueta, Tema tema) {
        if (etiqueta == null) return;

        etiqueta.setForeground(tema.colorTextoSecundario());
        etiqueta.setFont(tema.fuenteTexto());
    }
}
