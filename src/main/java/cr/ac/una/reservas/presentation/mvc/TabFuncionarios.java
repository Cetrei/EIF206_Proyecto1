package cr.ac.una.reservas.presentation.mvc;

import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.mvc.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.mvc.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.mvc.componentes.TablaDatos;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.model.FuncionarioModel;
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

public class TabFuncionarios implements CambioTemaListener, PropertyChangeListener {
    private List<Funcionario> funcionariosMostrados = new ArrayList<>();

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

    public TabFuncionarios(FuncionarioModel modelo) {
        $$$setupUI$$$();
        modelo.addPropertyChangeListener(this);
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

    private void mostrarFuncionarios(List<Funcionario> funcionarios) {
        funcionariosMostrados = funcionarios;
        List<List<Object>> filas = new ArrayList<>();
        for (Funcionario funcionario : funcionarios) {
            filas.add(List.of(
                    funcionario.getId(),
                    funcionario.getNombre(),
                    funcionario.getTelefono() == null ? "" : funcionario.getTelefono(),
                    ""
            ));
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

    public Funcionario funcionarioEnFila(int indiceFila) {
        if (indiceFila < 0 || indiceFila >= funcionariosMostrados.size()) return null;
        return funcionariosMostrados.get(indiceFila);
    }

    public JPanel obtenerPanel() {
        return TabFuncionarios;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evento) {
        if (FuncionarioModel.PROP_FUNCIONARIOS.equals(evento.getPropertyName())) {
            @SuppressWarnings("unchecked")
            List<Funcionario> nuevaLista = (List<Funcionario>) evento.getNewValue();
            mostrarFuncionarios(nuevaLista);
        } else if (FuncionarioModel.PROP_FUNCIONARIO_SELECCIONADO.equals(evento.getPropertyName())) {
            Funcionario seleccionado = (Funcionario) evento.getNewValue();
            if (seleccionado == null) {
                mostrarId("");
                mostrarNombre("");
                mostrarTelefono("");
            } else {
                mostrarId(seleccionado.getId());
                mostrarNombre(seleccionado.getNombre());
                mostrarTelefono(seleccionado.getTelefono());
            }
        }
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

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        TabFuncionarios = new JPanel();
        TabFuncionarios.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(20, 20, 20, 20), -1, 16));
        TabFuncionarios.add(TarjetaTitulo, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabFuncionarios.add(TarjetaFuncionario, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabFuncionarios.add(TarjetaTabla, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        TabFuncionarios.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return TabFuncionarios;
    }
}
