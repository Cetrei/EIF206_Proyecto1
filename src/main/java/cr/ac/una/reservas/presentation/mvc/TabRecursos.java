package cr.ac.una.reservas.presentation.mvc;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.mvc.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.mvc.componentes.ComboBoxTematizado;
import cr.ac.una.reservas.presentation.mvc.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.mvc.componentes.TablaDatos;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.model.RecursoModel;
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
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class TabRecursos implements CambioTemaListener, PropertyChangeListener {
    private List<Recurso> recursosMostrados = new ArrayList<>();

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

    private static final Categoria TODAS_LAS_CATEGORIAS = new Categoria(null, "-- Todas las Categorías --");

    public TabRecursos(RecursoModel modelo) {
        $$$setupUI$$$();
        modelo.addPropertyChangeListener(this);
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

    private void cargarCategorias(List<Categoria> categorias) {
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

    private void mostrarRecursos(List<Recurso> recursos) {
        recursosMostrados = recursos;
        List<List<Object>> filas = new ArrayList<>();
        for (Recurso recurso : recursos) {
            String descripcionCategoria = recurso.getCategoria() != null
                    ? recurso.getCategoria().getDescripcion()
                    : "";
            filas.add(List.of(recurso.getId(), descripcionCategoria, recurso.getDescripcion(), ""));
        }
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

    public Recurso recursoEnFila(int indiceFila) {
        if (indiceFila < 0 || indiceFila >= recursosMostrados.size()) return null;
        return recursosMostrados.get(indiceFila);
    }

    public JPanel obtenerPanel() {
        return TabRecursos;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evento) {
        if (RecursoModel.PROP_RECURSOS.equals(evento.getPropertyName())) {
            @SuppressWarnings("unchecked")
            List<Recurso> nuevaLista = (List<Recurso>) evento.getNewValue();
            mostrarRecursos(nuevaLista);
        } else if (RecursoModel.PROP_CATEGORIAS_DISPONIBLES.equals(evento.getPropertyName())) {
            @SuppressWarnings("unchecked")
            List<Categoria> nuevaLista = (List<Categoria>) evento.getNewValue();
            cargarCategorias(nuevaLista);
        } else if (RecursoModel.PROP_RECURSO_SELECCIONADO.equals(evento.getPropertyName())) {
            Recurso seleccionado = (Recurso) evento.getNewValue();
            if (seleccionado == null) {
                mostrarId("");
                comboCategoriaReal.setSelectedItem(null);
                mostrarDescripcion("");
            } else {
                mostrarId(seleccionado.getId());
                mostrarCategoriaSeleccionada(seleccionado.getCategoria());
                mostrarDescripcion(seleccionado.getDescripcion());
            }
        }
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

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        TabRecursos = new JPanel();
        TabRecursos.setLayout(new GridLayoutManager(4, 1, new Insets(20, 20, 20, 20), -1, 16));
        TabRecursos.add(TarjetaTitulo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabRecursos.add(TarjetaRecurso, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabRecursos.add(TarjetaTabla, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        TabRecursos.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return TabRecursos;
    }

}
