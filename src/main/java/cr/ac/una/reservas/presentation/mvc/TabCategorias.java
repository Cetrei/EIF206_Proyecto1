package cr.ac.una.reservas.presentation.mvc;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.mvc.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.mvc.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.mvc.componentes.TablaDatos;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.model.CategoriaModel;
import cr.ac.una.reservas.presentation.mvc.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.presentation.mvc.tema.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class TabCategorias implements CambioTemaListener, PropertyChangeListener {
    private List<Categoria> categoriasMostradas = new ArrayList<>();

    private JPanel TabCategorias;
    private JPanel TarjetaTitulo;
    private JPanel TarjetaCategoria;
    private JPanel TarjetaTabla;

    private Tarjeta tarjetaTituloReal;
    private Tarjeta tarjetaCategoriaReal;
    private Tarjeta tarjetaTablaReal;

    private CampoTexto campoIdReal;
    private CampoTexto campoDescripcionReal;
    private CampoTexto campoBuscarReal;
    private TablaDatos tablaReal;

    private BotonPrimario botonGuardarReal;
    private BotonIcono botonBorrarReal;
    private BotonIcono botonLimpiarReal;
    private BotonSecundario botonReporteReal;

    public TabCategorias(CategoriaModel modelo) {
        $$$setupUI$$$();
        modelo.addPropertyChangeListener(this);
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        armarTarjetaTitulo();
        armarTarjetaCategoria();
        armarTarjetaTabla();
    }

    private void armarTarjetaTitulo() {
        tarjetaTituloReal = new Tarjeta();
        tarjetaTituloReal.setIcono(IconoSemantico.ETIQUETA.icono());
        tarjetaTituloReal.setTitulo("Categorías de Recursos");
        tarjetaTituloReal.setSubtitulo("Gestión de clasificaciones de insumos y espacios");

        botonReporteReal = new BotonSecundario();
        botonReporteReal.setVariante(BotonSecundario.Variante.PELIGRO);
        botonReporteReal.setTexto("Generar Reporte PDF");
        botonReporteReal.setIcono(IconoSemantico.REPORTE_PDF.icono());
        tarjetaTituloReal.setAccion(botonReporteReal.obtenerPanel());

        TarjetaTitulo = tarjetaTituloReal.obtenerPanel();
    }

    private void armarTarjetaCategoria() {
        tarjetaCategoriaReal = new Tarjeta();
        tarjetaCategoriaReal.setTitulo("Datos de la Categoría");

        campoIdReal = new CampoTexto();
        campoIdReal.setEtiqueta("ID CATEGORÍA");
        campoIdReal.setPlaceholder("Se autogenera al guardar");
        campoIdReal.setSoloLectura(true);

        campoDescripcionReal = new CampoTexto();
        campoDescripcionReal.setEtiqueta("DESCRIPCIÓN");
        campoDescripcionReal.setPlaceholder("Ej: Proyector HDMI 4K");

        botonGuardarReal = new BotonPrimario();
        botonGuardarReal.setTexto("Guardar");
        botonGuardarReal.setIcono(IconoSemantico.GUARDAR.icono());

        botonBorrarReal = new BotonIcono();
        botonBorrarReal.setVariante(BotonIcono.Variante.PELIGRO);
        botonBorrarReal.setIcono(IconoSemantico.BORRAR.icono());

        botonLimpiarReal = new BotonIcono();
        botonLimpiarReal.setIcono(IconoSemantico.LIMPIAR.icono());

        JPanel contenido = tarjetaCategoriaReal.obtenerPanelContenido();
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
        contenido.add(campoDescripcionReal.obtenerPanel(), restricciones);

        JPanel filaBotones = new JPanel(new BorderLayout(8, 0));
        filaBotones.setOpaque(false);
        filaBotones.add(botonGuardarReal.obtenerPanel(), BorderLayout.CENTER);

        JPanel botonesSecundarios = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botonesSecundarios.setOpaque(false);
        botonesSecundarios.add(botonBorrarReal.obtenerPanel());
        botonesSecundarios.add(botonLimpiarReal.obtenerPanel());
        filaBotones.add(botonesSecundarios, BorderLayout.EAST);

        restricciones.gridy = 2;
        restricciones.gridwidth = 3;
        restricciones.insets = new Insets(0, 0, 0, 0);
        contenido.add(filaBotones, restricciones);

        TarjetaCategoria = tarjetaCategoriaReal.obtenerPanel();
    }

    private void armarTarjetaTabla() {
        tarjetaTablaReal = new Tarjeta();

        campoBuscarReal = new CampoTexto();
        campoBuscarReal.setPlaceholder("Buscar categorías por descripción...");
        campoBuscarReal.setIcono(IconoSemantico.BUSCAR.icono());

        tablaReal = new TablaDatos();
        tablaReal.setColumnas(List.of("ID", "DESCRIPCIÓN", "ACCIONES"));

        JPanel contenido = tarjetaTablaReal.obtenerPanelContenido();
        contenido.setLayout(new BorderLayout(0, 12));
        contenido.add(campoBuscarReal.obtenerPanel(), BorderLayout.NORTH);
        contenido.add(tablaReal.obtenerPanel(), BorderLayout.CENTER);

        TarjetaTabla = tarjetaTablaReal.obtenerPanel();
    }

    // ------------------------------------------------------------------
    // Datos del formulario (Datos de la Categoria)
    // ------------------------------------------------------------------

    public String obtenerId() {
        return campoIdReal.obtenerTexto();
    }

    public void mostrarId(String id) {
        campoIdReal.mostrarValor(id);
    }

    public String obtenerDescripcion() {
        return campoDescripcionReal.obtenerTexto();
    }

    public void mostrarDescripcion(String descripcion) {
        campoDescripcionReal.mostrarValor(descripcion);
    }

    public void limpiarFormulario() {
        mostrarId("");
        mostrarDescripcion("");
    }

    // ------------------------------------------------------------------
    // Tabla de categorias
    // ------------------------------------------------------------------

    // La columna ACCIONES va vacia: TablaDatos no soporta botones embebidos, la fila completa es clickeable via alSeleccionarFila.
    private void mostrarCategorias(List<Categoria> categorias) {
        categoriasMostradas = categorias;
        List<List<Object>> filas = new ArrayList<>();
        for (Categoria categoria : categorias) {
            filas.add(List.of(categoria.getId(), categoria.getDescripcion(), ""));
        }
        tablaReal.setFilas(filas);
    }

    public String obtenerTextoBusqueda() {
        return campoBuscarReal.obtenerTexto();
    }

    // ------------------------------------------------------------------
    // Enganches de eventos
    // ------------------------------------------------------------------

    public void alGuardar(Runnable accion) {
        botonGuardarReal.alHacerClick(accion);
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

    public void alBuscar(Runnable accion) {
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

    public Categoria categoriaEnFila(int indiceFila) {
        if (indiceFila < 0 || indiceFila >= categoriasMostradas.size()) return null;
        return categoriasMostradas.get(indiceFila);
    }

    public JPanel obtenerPanel() {
        return TabCategorias;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evento) {
        if (CategoriaModel.PROP_CATEGORIAS.equals(evento.getPropertyName())) {
            @SuppressWarnings("unchecked")
            List<Categoria> nuevaLista = (List<Categoria>) evento.getNewValue();
            mostrarCategorias(nuevaLista);
        } else if (CategoriaModel.PROP_CATEGORIA_SELECCIONADA.equals(evento.getPropertyName())) {
            Categoria seleccionada = (Categoria) evento.getNewValue();
            if (seleccionada == null) {
                mostrarId("");
                mostrarDescripcion("");
            } else {
                mostrarId(seleccionada.getId());
                mostrarDescripcion(seleccionada.getDescripcion());
            }
        }
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (TabCategorias == null) return;

        TabCategorias.setOpaque(true);
        TabCategorias.setBackground(tema.colorFondoVentana());
        TabCategorias.setBorder(new EmptyBorder(0, 0, 0, 0));
        TabCategorias.repaint();
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
        TabCategorias = new JPanel();
        TabCategorias.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(20, 20, 20, 20), -1, 16));
        TabCategorias.add(TarjetaTitulo, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabCategorias.add(TarjetaCategoria, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabCategorias.add(TarjetaTabla, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        TabCategorias.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return TabCategorias;
    }
}
