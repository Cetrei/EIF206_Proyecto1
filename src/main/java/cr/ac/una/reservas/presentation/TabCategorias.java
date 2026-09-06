package cr.ac.una.reservas.presentation;

import cr.ac.una.reservas.presentation.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.componentes.TablaDatos;
import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Vista de la pestana "Categorias de Recursos" (funcionalidad 4 del
 * enunciado, ver docs/06_control_presentation.md). Sigue el mismo
 * patron que PanelCuenta: los tres paneles que trae el .form
 * (TarjetaTitulo, TarjetaCategoria, TarjetaTabla) estan declarados
 * custom-create="true" y aqui, en createUIComponents(), se reemplazan
 * por instancias reales de los componentes del sistema de diseno
 * (Tarjeta, CampoTexto, BotonPrimario/Secundario/Icono, TablaDatos).
 * <p>
 * Esta clase no conoce CategoriaService ni ninguna otra clase de
 * service: solo expone datos (obtenerId/obtenerDescripcion,
 * obtenerFilaSeleccionada) y enganches de eventos (alGuardar,
 * alBorrar, alLimpiar, alBuscar, alSeleccionarFila, alGenerarReporte),
 * igual que LoginPanel expone alIngresar/alCambiarClave. Toda la logica
 * de negocio vive en CategoriaControl.
 */
public class TabCategorias implements CambioTemaListener {

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

    public TabCategorias() {
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    /**
     * TarjetaTitulo/TarjetaCategoria/TarjetaTabla se declaran
     * custom-create="true" en el .form (paneles vacios), asi que aqui
     * se reemplazan por instancias reales de Tarjeta y se arma su
     * contenido a mano, igual patron que PanelCuenta.createUIComponents().
     */
    private void createUIComponents() {
        armarTarjetaTitulo();
        armarTarjetaCategoria();
        armarTarjetaTabla();
    }

    /**
     * El "componente especial TarjetaTitulo" que cada pestana necesita
     * (icono + titulo + descripcion + boton de generar reporte) no es
     * una clase nueva: es la Tarjeta generica de componentes, usando el
     * encabezado que ya soporta (setIcono/setTitulo/setSubtitulo/
     * setAccion). Cada pestana solo decide su propio icono, titulo,
     * subtitulo y que reporte genera el boton (ver CategoriaControl).
     */
    private void armarTarjetaTitulo() {
        tarjetaTituloReal = new Tarjeta();
        tarjetaTituloReal.setIcono(Icono.ETIQUETA);
        tarjetaTituloReal.setTitulo("Categorías de Recursos");
        tarjetaTituloReal.setSubtitulo("Gestión de clasificaciones de insumos y espacios");

        botonReporteReal = new BotonSecundario();
        botonReporteReal.setVariante(BotonSecundario.Variante.PELIGRO);
        botonReporteReal.setTexto("Generar Reporte PDF");
        botonReporteReal.setIcono(Icono.PDF);
        tarjetaTituloReal.setAccion(botonReporteReal.obtenerPanel());

        TarjetaTitulo = tarjetaTituloReal.obtenerPanel();
    }

    /**
     * Formulario de "Datos de la Categoria": ID (autogenerado, solo
     * lectura) + Descripcion + Guardar/Borrar/Limpiar, igual
     * distribucion que la captura de referencia adjunta.
     */
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
        botonGuardarReal.setIcono(Icono.GUARDAR);

        botonBorrarReal = new BotonIcono();
        botonBorrarReal.setVariante(BotonIcono.Variante.PELIGRO);
        botonBorrarReal.setIcono(Icono.BASURA);

        botonLimpiarReal = new BotonIcono();
        botonLimpiarReal.setIcono(Icono.BORRADOR);

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

        // Fila de botones: Guardar ocupa el espacio disponible, Borrar
        // y Limpiar quedan como iconos compactos a la derecha (igual que
        // en la captura de referencia).
        JPanel filaBotones = new JPanel(new BorderLayout(8, 0));
        filaBotones.setOpaque(false);
        filaBotones.add(botonGuardarReal.obtenerPanel(), BorderLayout.CENTER);

        JPanel botonesSecundarios = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
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

    /**
     * Buscador + listado de categorias existentes, con columnas Id,
     * Descripcion y Acciones (icono de editar), igual que la captura de
     * referencia.
     */
    private void armarTarjetaTabla() {
        tarjetaTablaReal = new Tarjeta();

        campoBuscarReal = new CampoTexto();
        campoBuscarReal.setPlaceholder("Buscar categorías por descripción...");
        campoBuscarReal.setIcono(Icono.BUSCAR);

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

    /**
     * Limpia el formulario para el estado "nueva categoria": ID vacio
     * (se autogenera en CategoriaService al guardar) y descripcion
     * vacia. CategoriaControl llama a esto al presionar Limpiar o justo
     * despues de guardar con exito.
     */
    public void limpiarFormulario() {
        mostrarId("");
        mostrarDescripcion("");
    }

    // ------------------------------------------------------------------
    // Tabla de categorias
    // ------------------------------------------------------------------

    /**
     * Reemplaza las filas de la tabla con el listado recibido.
     * CategoriaControl arma cada fila como [id, descripcion, ""] (la
     * tercera columna, ACCIONES, se muestra vacia porque TablaDatos no
     * soporta botones embebidos por celda; la fila completa es
     * clickeable via alSeleccionarFila, igual de funcional que el
     * icono de editar de la captura de referencia).
     */
    public void mostrarCategorias(List<List<Object>> filas) {
        tablaReal.setFilas(filas);
    }

    public String obtenerTextoBusqueda() {
        return campoBuscarReal.obtenerTexto();
    }

    // ------------------------------------------------------------------
    // Enganches de eventos: el control decide que hacer, esta vista solo
    // expone el punto de enganche (mismo patron que LoginPanel/PanelCuenta).
    // ------------------------------------------------------------------

    public void alGuardar(Runnable accion) {
        botonGuardarReal.alHacerClick(accion);
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

    /**
     * CategoriaControl decide si busca en cada tecla o con un pequeno
     * debounce; esta vista solo notifica cambios en el texto del campo
     * de busqueda via un DocumentListener simple.
     */
    public void alBuscar(Runnable accion) {
        campoBuscarReal.obtenerCampoTexto().getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    @Override
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        accion.run();
                    }

                    @Override
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        accion.run();
                    }

                    @Override
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        accion.run();
                    }
                }
        );
    }

    /**
     * Notifica que fila del listado se selecciono (por indice del
     * modelo, ya resuelto contra el orden/filtro de TablaDatos), para
     * que CategoriaControl cargue esa categoria en el formulario de
     * arriba, igual que hace click en el icono de editar en la captura
     * de referencia.
     */
    public void alSeleccionarFila(IntConsumer accion) {
        tablaReal.alHacerClickFila(accion);
    }

    public JPanel obtenerPanel() {
        return TabCategorias;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (TabCategorias == null) {
            return;
        }
        TabCategorias.setOpaque(true);
        TabCategorias.setBackground(tema.colorFondoVentana());
        TabCategorias.setBorder(new EmptyBorder(0, 0, 0, 0));
        TabCategorias.repaint();
    }
}
