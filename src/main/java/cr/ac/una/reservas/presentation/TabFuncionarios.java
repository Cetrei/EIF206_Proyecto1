package cr.ac.una.reservas.presentation;

import cr.ac.una.reservas.presentation.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.componentes.TablaDatos;
import cr.ac.una.reservas.presentation.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.function.IntConsumer;

public class TabFuncionarios implements CambioTemaListener {
    private JPanel TabFuncionarios;
    private JPanel TarjetaTitulo;
    private JPanel TarjetaFuncionario;
    private JPanel TarjetaTabla;

    private Tarjeta tarjetaTituloReal;
    private Tarjeta tarjetaFuncionarioReal;
    private Tarjeta tarjetaTablaReal;

    private CampoTexto campoIdReal;
    private CampoTexto campoNombreReal;
    private CampoTexto campoTelefonoReal;
    private CampoTexto campoBuscarReal;
    private TablaDatos tablaReal;

    private BotonPrimario botonGuardarReal;
    private BotonIcono botonBorrarReal;
    private BotonIcono botonLimpiarReal;
    private BotonSecundario botonReporteReal;

    public TabFuncionarios() {
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        armarTarjetaTitulo();
        armarTarjetaFuncionario();
        armarTarjetaTabla();
    }

    private void armarTarjetaTitulo() {
        tarjetaTituloReal = new Tarjeta();
        tarjetaTituloReal.setIcono(IconoSemantico.USUARIO.icono());
        tarjetaTituloReal.setTitulo("Gestión de Funcionarios");
        tarjetaTituloReal.setSubtitulo("Administre el registro de personal con privilegios de usuario");

        botonReporteReal = new BotonSecundario();
        botonReporteReal.setVariante(BotonSecundario.Variante.PELIGRO);
        botonReporteReal.setTexto("Generar Reporte PDF");
        botonReporteReal.setIcono(IconoSemantico.REPORTE_PDF.icono());
        tarjetaTituloReal.setAccion(botonReporteReal.obtenerPanel());

        TarjetaTitulo = tarjetaTituloReal.obtenerPanel();
    }

    private void armarTarjetaFuncionario() {
        tarjetaFuncionarioReal = new Tarjeta();
        tarjetaFuncionarioReal.setTitulo("Datos del Funcionario");
        tarjetaFuncionarioReal.setSubtitulo("Clave inicial = ID");

        campoIdReal = new CampoTexto();
        campoIdReal.setEtiqueta("IDENTIFICACIÓN (ID)");
        campoIdReal.setPlaceholder("Ej: 304560789");

        campoNombreReal = new CampoTexto();
        campoNombreReal.setEtiqueta("NOMBRE COMPLETO");
        campoNombreReal.setPlaceholder("Ej: María Pérez Delgado");

        campoTelefonoReal = new CampoTexto();
        campoTelefonoReal.setEtiqueta("TELÉFONO");
        campoTelefonoReal.setPlaceholder("Ej: 8888-2222");

        botonGuardarReal = new BotonPrimario();
        botonGuardarReal.setTexto("Guardar");
        botonGuardarReal.setIcono(IconoSemantico.GUARDAR.icono());

        botonBorrarReal = new BotonIcono();
        botonBorrarReal.setVariante(BotonIcono.Variante.PELIGRO);
        botonBorrarReal.setIcono(IconoSemantico.BORRAR.icono());

        botonLimpiarReal = new BotonIcono();
        botonLimpiarReal.setIcono(IconoSemantico.LIMPIAR.icono());

        JPanel contenido = tarjetaFuncionarioReal.obtenerPanelContenido();
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
        restricciones.insets = new Insets(0, 0, 10, 0);
        contenido.add(campoNombreReal.obtenerPanel(), restricciones);

        restricciones.gridy = 2;
        restricciones.insets = new Insets(0, 0, 16, 0);
        contenido.add(campoTelefonoReal.obtenerPanel(), restricciones);

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

        TarjetaFuncionario = tarjetaFuncionarioReal.obtenerPanel();
    }

    private void armarTarjetaTabla() {
        tarjetaTablaReal = new Tarjeta();

        campoBuscarReal = new CampoTexto();
        campoBuscarReal.setPlaceholder("Buscar por ID o Nombre...");
        campoBuscarReal.setIcono(IconoSemantico.BUSCAR.icono());

        tablaReal = new TablaDatos();
        tablaReal.setColumnas(List.of("ID", "NOMBRE", "TELÉFONO", "ACCIONES"));

        JPanel contenido = tarjetaTablaReal.obtenerPanelContenido();
        contenido.setLayout(new BorderLayout(0, 12));
        contenido.add(campoBuscarReal.obtenerPanel(), BorderLayout.NORTH);
        contenido.add(tablaReal.obtenerPanel(), BorderLayout.CENTER);

        TarjetaTabla = tarjetaTablaReal.obtenerPanel();
    }

    // ------------------------------------------------------------------
    // Datos del formulario (Datos del Funcionario)
    // ------------------------------------------------------------------

    public String obtenerId() {
        return campoIdReal.obtenerTexto();
    }

    public void mostrarId(String id) {
        campoIdReal.mostrarValor(id);
    }

    public String obtenerNombre() {
        return campoNombreReal.obtenerTexto();
    }

    public void mostrarNombre(String nombre) {
        campoNombreReal.mostrarValor(nombre);
    }

    public String obtenerTelefono() {
        return campoTelefonoReal.obtenerTexto();
    }

    public void mostrarTelefono(String telefono) {
        campoTelefonoReal.mostrarValor(telefono);
    }

    public void limpiarFormulario() {
        mostrarId("");
        mostrarNombre("");
        mostrarTelefono("");
    }

    // ------------------------------------------------------------------
    // Tabla de funcionarios
    // ------------------------------------------------------------------

    public void mostrarFuncionarios(List<List<Object>> filas) {
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
        campoIdReal.alConfirmar(accion);
        campoNombreReal.alConfirmar(accion);
        campoTelefonoReal.alConfirmar(accion);
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

    public JPanel obtenerPanel() {
        return TabFuncionarios;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (TabFuncionarios == null) return;

        TabFuncionarios.setOpaque(true);
        TabFuncionarios.setBackground(tema.colorFondoVentana());
        TabFuncionarios.setBorder(new EmptyBorder(0, 0, 0, 0));
        TabFuncionarios.repaint();
    }
}
