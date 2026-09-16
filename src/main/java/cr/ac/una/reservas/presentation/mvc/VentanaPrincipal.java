package cr.ac.una.reservas.presentation.mvc;

import cr.ac.una.reservas.presentation.mvc.componentes.BarraSuperior;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.model.VentanaPrincipalModel;
import cr.ac.una.reservas.presentation.mvc.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.presentation.mvc.tema.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class VentanaPrincipal implements CambioTemaListener, PropertyChangeListener {
    private JPanel VentanaPrincipal;
    private JTabbedPane tbpVentanas;
    private JPanel pnlEncabezado;
    private JLabel iconLogo;
    private JLabel lblTituloApp;
    private JLabel lblSubtituloApp;
    private JPanel btnCuenta;
    private JLabel lblNombreUsuario;
    private JLabel lblBadgeRol;
    private JPanel pnlFuncionarios;
    private JPanel pnlCategorias;
    private JPanel pnlRecursos;
    private JPanel pnlCalendarizacion;
    private JPanel pnlActividades;
    private JPanel pnlEstadisticas;
    private JPanel pnlReservas;
    private JPanel BarraSuperior;

    private BotonIcono botonCuentaReal;
    private BarraSuperior barraSuperiorReal;

    public VentanaPrincipal(VentanaPrincipalModel modelo) {
        $$$setupUI$$$();
        modelo.addPropertyChangeListener(this);
        GestorTema.obtenerInstancia().agregarListener(this);
        lblNombreUsuario.setHorizontalAlignment(SwingConstants.RIGHT);
        lblBadgeRol.setHorizontalAlignment(SwingConstants.RIGHT);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        botonCuentaReal = new BotonIcono();
        botonCuentaReal.setIcono(IconoSemantico.AJUSTES_CUENTA.icono());
        botonCuentaReal.setTamano(28, 28);
        btnCuenta = botonCuentaReal.obtenerPanel();

        barraSuperiorReal = new BarraSuperior();
        BarraSuperior = barraSuperiorReal.obtenerPanel();
    }

    private void mostrarUsuario(String nombre, String rolTexto) {
        lblNombreUsuario.setText(nombre);
        lblBadgeRol.setText(rolTexto);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evento) {
        if (VentanaPrincipalModel.PROP_USUARIO.equals(evento.getPropertyName())) {
            VentanaPrincipalModel modelo = (VentanaPrincipalModel) evento.getSource();
            mostrarUsuario(modelo.getNombreUsuario(), modelo.getRolTexto());
        }
    }

    public void alAbrirCuenta(Runnable accion) {
        botonCuentaReal.alHacerClick(accion);
    }

    public void alCerrar(Runnable accion) {
        barraSuperiorReal.alCerrar(accion);
    }

    public JPanel obtenerPanelFuncionarios() {
        return pnlFuncionarios;
    }

    public JPanel obtenerPanelCategorias() {
        return pnlCategorias;
    }

    public JPanel obtenerPanelRecursos() {
        return pnlRecursos;
    }

    public JPanel obtenerPanelCalendarizacion() {
        return pnlCalendarizacion;
    }

    public JPanel obtenerPanelActividades() {
        return pnlActividades;
    }

    public JPanel obtenerPanelEstadisticas() {
        return pnlEstadisticas;
    }

    public JPanel obtenerPanelReservas() {
        return pnlReservas;
    }

    public void mostrarSoloPestanasDeFuncionario() {
        quitarPestana(pnlFuncionarios);
        quitarPestana(pnlCategorias);
        quitarPestana(pnlRecursos);
    }

    public void mostrarSoloPestanasDeAdministrador() {
        quitarPestana(pnlReservas);
    }

    private void quitarPestana(JPanel panelPestana) {
        int indice = tbpVentanas.indexOfComponent(panelPestana);
        if (indice >= 0) {
            tbpVentanas.removeTabAt(indice);
        }
    }

    public JPanel obtenerPanel() {
        return VentanaPrincipal;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        VentanaPrincipal.setBackground(tema.colorFondoVentana());
        pnlEncabezado.setBackground(tema.colorFondoTarjeta());
        pnlEncabezado.setOpaque(true);
        pnlEncabezado.setBorder(new EmptyBorder(10, 18, 10, 18));

        IconoAplicador.aplicar(iconLogo, IconoSemantico.LOGO_APP.icono(), 20f, tema.colorPrimario());
        ajustarAltoLinea(iconLogo, 31);

        lblTituloApp.setForeground(tema.colorTexto());
        lblTituloApp.setFont(tema.fuenteTitulo().deriveFont(15f));
        ajustarAltoLinea(lblTituloApp, 17);

        lblSubtituloApp.setForeground(tema.colorTextoSecundario());
        lblSubtituloApp.setFont(tema.fuenteTexto().deriveFont(11f));
        ajustarAltoLinea(lblSubtituloApp, 14);

        lblNombreUsuario.setForeground(tema.colorTexto());
        lblNombreUsuario.setFont(tema.fuenteTexto().deriveFont(Font.BOLD, 13f));
        ajustarAltoLinea(lblNombreUsuario, 16);

        lblBadgeRol.setForeground(tema.colorPrimario());
        lblBadgeRol.setFont(tema.fuenteTexto().deriveFont(Font.BOLD, 10f));
        ajustarAltoLinea(lblBadgeRol, 13);

        tbpVentanas.setBackground(tema.colorFondoVentana());
        tbpVentanas.setForeground(tema.colorTexto());
        tbpVentanas.setFont(tema.fuenteTexto());
        // El borde de Metal alrededor del contenido del JTabbedPane no respeta el tema; se reemplaza por uno del color de borde del tema.
        tbpVentanas.setBorder(new LineBorder(tema.colorBorde(), 1));

        VentanaPrincipal.repaint();
    }

    // Fija el alto preferido/min/max de un JLabel
    private static void ajustarAltoLinea(JLabel label, int alto) {
        Dimension preferido = label.getPreferredSize();
        Dimension fijado = new Dimension(preferido.width, alto);
        label.setPreferredSize(fijado);
        label.setMinimumSize(fijado);
        label.setMaximumSize(fijado);
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
        VentanaPrincipal = new JPanel();
        VentanaPrincipal.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas = new JTabbedPane();
        VentanaPrincipal.add(tbpVentanas, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        pnlCalendarizacion = new JPanel();
        pnlCalendarizacion.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Calendarizacion", pnlCalendarizacion);
        pnlFuncionarios = new JPanel();
        pnlFuncionarios.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Funcionarios", pnlFuncionarios);
        pnlCategorias = new JPanel();
        pnlCategorias.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Categorias", pnlCategorias);
        pnlRecursos = new JPanel();
        pnlRecursos.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Recursos", pnlRecursos);
        pnlActividades = new JPanel();
        pnlActividades.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Actividades", pnlActividades);
        pnlEstadisticas = new JPanel();
        pnlEstadisticas.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Estadisticas", pnlEstadisticas);
        pnlReservas = new JPanel();
        pnlReservas.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Reservas", pnlReservas);
        pnlEncabezado = new JPanel();
        pnlEncabezado.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, 0));
        VentanaPrincipal.add(pnlEncabezado, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        iconLogo = new JLabel();
        iconLogo.setText("Icono");
        pnlEncabezado.add(iconLogo, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        pnlEncabezado.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        lblTituloApp = new JLabel();
        lblTituloApp.setText("SISTEMA DE RESERVA DE RECURSOS");
        pnlEncabezado.add(lblTituloApp, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHEAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblSubtituloApp = new JLabel();
        lblSubtituloApp.setText("Escuela de Informática - UNA");
        pnlEncabezado.add(lblSubtituloApp, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblNombreUsuario = new JLabel();
        lblNombreUsuario.setText("Nombre De Usuario");
        pnlEncabezado.add(lblNombreUsuario, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHEAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblBadgeRol = new JLabel();
        lblBadgeRol.setText("ADMINISTRADOR");
        pnlEncabezado.add(lblBadgeRol, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHEAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlEncabezado.add(btnCuenta, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        VentanaPrincipal.add(BarraSuperior, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return VentanaPrincipal;
    }
}
