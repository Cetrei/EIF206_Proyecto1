package cr.ac.una.reservas.presentation;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.presentation.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.componentes.ComboBoxTematizado;
import cr.ac.una.reservas.presentation.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.componentes.TablaDatos;
import cr.ac.una.reservas.presentation.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class TabRecursos implements CambioTemaListener {
    private JPanel TabRecursos;
    private JPanel TarjetaTitulo;
    private JPanel TarjetaRecurso;
    private JPanel TarjetaTabla;

    private Tarjeta tarjetaTituloReal;
    private Tarjeta tarjetaRecursoReal;
    private Tarjeta tarjetaTablaReal;

    private CampoTexto campoIdReal;
    private JComboBox<Categoria> comboCategoriaReal;
    private CampoTexto campoDescripcionReal;
    private JComboBox<Categoria> comboFiltroCategoriaReal;
    private CampoTexto campoBuscarReal;
    private TablaDatos tablaReal;

    private BotonPrimario botonGuardarReal;
    private BotonIcono botonBorrarReal;
    private BotonIcono botonLimpiarReal;
    private BotonSecundario botonReporteReal;

    // Representa "-- Todas las Categorías --" en comboFiltroCategoriaReal
    private static final Categoria TODAS_LAS_CATEGORIAS = new Categoria(null, "-- Todas las Categorías --");

    public TabRecursos() {
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        armarTarjetaTitulo();
        armarTarjetaRecurso();
        armarTarjetaTabla();
    }

    private void armarTarjetaTitulo() {
        tarjetaTituloReal = new Tarjeta();
        tarjetaTituloReal.setIcono(IconoSemantico.CAJA.icono());
        tarjetaTituloReal.setTitulo("Inventario de Recursos");
        tarjetaTituloReal.setSubtitulo("Asignación de unidades físicas pertenecientes a categorías");

        botonReporteReal = new BotonSecundario();
        botonReporteReal.setVariante(BotonSecundario.Variante.PELIGRO);
        botonReporteReal.setTexto("Generar Reporte PDF");
        botonReporteReal.setIcono(IconoSemantico.REPORTE_PDF.icono());
        tarjetaTituloReal.setAccion(botonReporteReal.obtenerPanel());

        TarjetaTitulo = tarjetaTituloReal.obtenerPanel();
    }

    private void armarTarjetaRecurso() {
        tarjetaRecursoReal = new Tarjeta();
        tarjetaRecursoReal.setTitulo("Detalles del Recurso");

        campoIdReal = new CampoTexto();
        campoIdReal.setEtiqueta("ID / Nº ACTIVO");
        campoIdReal.setPlaceholder("Ej: 238715");

        comboCategoriaReal = crearComboCategorias();

        campoDescripcionReal = new CampoTexto();
        campoDescripcionReal.setEtiqueta("DESCRIPCIÓN DEL RECURSO");
        campoDescripcionReal.setPlaceholder("Ej: Laptop #238715 Core i7");

        botonGuardarReal = new BotonPrimario();
        botonGuardarReal.setTexto("Guardar");
        botonGuardarReal.setIcono(IconoSemantico.GUARDAR.icono());

        botonBorrarReal = new BotonIcono();
        botonBorrarReal.setVariante(BotonIcono.Variante.PELIGRO);
        botonBorrarReal.setIcono(IconoSemantico.BORRAR.icono());

        botonLimpiarReal = new BotonIcono();
        botonLimpiarReal.setIcono(IconoSemantico.LIMPIAR.icono());

        JPanel contenido = tarjetaRecursoReal.obtenerPanelContenido();
        contenido.setLayout(new GridBagLayout());
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.weightx = 1;
        restricciones.gridx = 0;

        restricciones.gridy = 0;
        restricciones.gridwidth = 3;
        restricciones.insets = new Insets(0, 0, 10, 0);
        contenido.add(campoIdReal.obtenerPanel(), restricciones);

        restricciones.gridy = 1;
        restricciones.insets = new Insets(0, 0, 16, 0);
        contenido.add(envolverConEtiqueta("CATEGORÍA", comboCategoriaReal), restricciones);

        restricciones.gridy = 2;
        restricciones.insets = new Insets(0, 0, 16, 0);
        contenido.add(campoDescripcionReal.obtenerPanel(), restricciones);

        JPanel filaBotones = new JPanel(new BorderLayout(8, 0));
        filaBotones.setOpaque(false);
        filaBotones.add(botonGuardarReal.obtenerPanel(), BorderLayout.CENTER);

        JPanel botonesSecundarios = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botonesSecundarios.setOpaque(false);
        botonesSecundarios.add(botonBorrarReal.obtenerPanel());
        botonesSecundarios.add(botonLimpiarReal.obtenerPanel());
        filaBotones.add(botonesSecundarios, BorderLayout.EAST);

        restricciones.gridy = 3;
        restricciones.gridwidth = 3;
        restricciones.insets = new Insets(0, 0, 0, 0);
        contenido.add(filaBotones, restricciones);

        TarjetaRecurso = tarjetaRecursoReal.obtenerPanel();
    }

    private void armarTarjetaTabla() {
        tarjetaTablaReal = new Tarjeta();

        comboFiltroCategoriaReal = crearComboCategorias();

        campoBuscarReal = new CampoTexto();
        campoBuscarReal.setPlaceholder("Buscar recursos por descripción...");
        campoBuscarReal.setIcono(IconoSemantico.BUSCAR.icono());

        tablaReal = new TablaDatos();
        tablaReal.setColumnas(List.of("ACTIVO / ID", "CATEGORÍA", "DESCRIPCIÓN", "ACCIONES"));

        JPanel filaFiltros = new JPanel(new BorderLayout(12, 0));
        filaFiltros.setOpaque(false);
        filaFiltros.add(comboFiltroCategoriaReal, BorderLayout.WEST);
        filaFiltros.add(campoBuscarReal.obtenerPanel(), BorderLayout.CENTER);

        JPanel contenido = tarjetaTablaReal.obtenerPanelContenido();
        contenido.setLayout(new BorderLayout(0, 12));
        contenido.add(filaFiltros, BorderLayout.NORTH);
        contenido.add(tablaReal.obtenerPanel(), BorderLayout.CENTER);

        TarjetaTabla = tarjetaTablaReal.obtenerPanel();
    }

    // El renderer muestra la descripcion, no Categoria.toString() (pensado para debug/logs).
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

    private JComponent envolverConEtiqueta(String etiqueta, JComponent control) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(GestorTema.obtenerInstancia().temaActivo().fuenteTexto());
        lblEtiqueta.setForeground(GestorTema.obtenerInstancia().temaActivo().colorTextoSecundario());
        lblEtiqueta.setBorder(new EmptyBorder(0, 2, 4, 0));

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(lblEtiqueta, BorderLayout.NORTH);
        envoltorio.add(control, BorderLayout.CENTER);

        control.putClientProperty("etiquetaAsociada", lblEtiqueta);
        return envoltorio;
    }

    // ------------------------------------------------------------------
    // Datos del formulario (Detalles del Recurso)
    // ------------------------------------------------------------------

    public String obtenerId() {
        return campoIdReal.obtenerTexto();
    }

    public void mostrarId(String id) {
        campoIdReal.mostrarValor(id);
    }

    public Categoria obtenerCategoriaSeleccionada() {
        Object seleccion = comboCategoriaReal.getSelectedItem();
        return seleccion instanceof Categoria ? (Categoria) seleccion : null;
    }

    public void mostrarCategoriaSeleccionada(Categoria categoria) {
        comboCategoriaReal.setSelectedItem(categoria);
    }

    public String obtenerDescripcion() {
        return campoDescripcionReal.obtenerTexto();
    }

    public void mostrarDescripcion(String descripcion) {
        campoDescripcionReal.mostrarValor(descripcion);
    }

    // Actualiza ambos combos (formulario y filtro); el de filtro agrega ademas "-- Todas las Categorias --".
    public void cargarCategorias(List<Categoria> categorias) {
        Categoria seleccionActualDetalle = obtenerCategoriaSeleccionada();
        comboCategoriaReal.setModel(new DefaultComboBoxModel<>(categorias.toArray(new Categoria[0])));
        if (categorias.contains(seleccionActualDetalle)) {
            comboCategoriaReal.setSelectedItem(seleccionActualDetalle);
        }

        Categoria seleccionActualFiltro = obtenerCategoriaFiltro();
        List<Categoria> opcionesFiltro = new ArrayList<>();
        opcionesFiltro.add(TODAS_LAS_CATEGORIAS);
        opcionesFiltro.addAll(categorias);
        comboFiltroCategoriaReal.setModel(new DefaultComboBoxModel<>(opcionesFiltro.toArray(new Categoria[0])));
        boolean existeFiltro = seleccionActualFiltro != null && opcionesFiltro.contains(seleccionActualFiltro);
        comboFiltroCategoriaReal.setSelectedItem(existeFiltro ? seleccionActualFiltro : TODAS_LAS_CATEGORIAS);
    }

    public void limpiarFormulario() {
        mostrarId("");
        comboCategoriaReal.setSelectedItem(null);
        mostrarDescripcion("");
    }

    // ------------------------------------------------------------------
    // Tabla de recursos
    // ------------------------------------------------------------------

    public void mostrarRecursos(List<List<Object>> filas) {
        tablaReal.setFilas(filas);
    }

    public Categoria obtenerCategoriaFiltro() {
        Object seleccion = comboFiltroCategoriaReal.getSelectedItem();
        if (seleccion == null || seleccion == TODAS_LAS_CATEGORIAS) {
            return null;
        }
        return (Categoria) seleccion;
    }

    public String obtenerTextoBusqueda() {
        return campoBuscarReal.obtenerTexto();
    }

    // ------------------------------------------------------------------
    // Enganches de eventos
    // ------------------------------------------------------------------

    public void alGuardar(Runnable accion) {
        botonGuardarReal.alHacerClick(accion);
        campoIdReal.alConfirmar(accion);
        campoDescripcionReal.alConfirmar(accion);
    }

    public void alBorrar(Runnable accion) {
        botonBorrarReal.alHacerClick(accion);
    }

    public void alLimpiar(Runnable accion) {
        botonLimpiarReal.alHacerClick(accion);
    }

    public void alGenerarReporte(Runnable accion) {
        botonReporteReal.alHacerClick(accion);
    }

    public void alFiltrar(Runnable accion) {
        comboFiltroCategoriaReal.addActionListener(evento -> accion.run());
        campoBuscarReal.obtenerCampoTexto().getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        accion.run();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        accion.run();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        accion.run();
                    }
                }
        );
    }

    public void alSeleccionarFila(IntConsumer accion) {
        tablaReal.alHacerClickFila(accion);
    }

    public JPanel obtenerPanel() {
        return TabRecursos;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (TabRecursos == null) return;

        TabRecursos.setOpaque(true);
        TabRecursos.setBackground(tema.colorFondoVentana());
        TabRecursos.setBorder(new EmptyBorder(0, 0, 0, 0));

        aplicarTemaCombo(comboCategoriaReal, tema);
        aplicarTemaCombo(comboFiltroCategoriaReal, tema);

        TabRecursos.repaint();
    }

    private void aplicarTemaCombo(JComboBox<Categoria> combo, Tema tema) {
        if (combo == null) return;

        ComboBoxTematizado.aplicar(combo, tema);

        Object etiquetaAsociada = combo.getClientProperty("etiquetaAsociada");
        if (etiquetaAsociada instanceof JLabel) {
            ((JLabel) etiquetaAsociada).setForeground(tema.colorTextoSecundario());
            ((JLabel) etiquetaAsociada).setFont(tema.fuenteTexto());
        }
    }
}
