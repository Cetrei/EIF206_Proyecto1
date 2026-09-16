package cr.ac.una.reservas.presentation.mvc;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cr.ac.una.reservas.presentation.mvc.componentes.BarraSuperior;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.mvc.iconos.Icono;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.model.VentanaPrincipalModel;
import cr.ac.una.reservas.presentation.mvc.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.presentation.mvc.tema.Tema;
import cr.ac.una.reservas.presentation.mvc.tema.TemaClaro;
import cr.ac.una.reservas.presentation.mvc.tema.TemaOscuro;

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
    private JPanel btnTema;
    private JLabel lblNombreUsuario;
    private JLabel lblBadgeRol;
    private JPanel pnlFuncionarios;
    private JPanel pnlCategorias;
    private JPanel pnlRecursos;
    private JPanel pnlCalendarizacion;
    private JPanel pnlActividades;
    private JPanel pnlEstadisticas;
    private JPanel pnlReservas;
    private JPanel pnlConfiguracionIa;
    private JPanel BarraSuperior;

    private BotonIcono botonCuentaReal;
    private BotonIcono botonTemaReal;
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

        botonTemaReal = new BotonIcono();
        botonTemaReal.setIcono(iconoParaCambiarA(GestorTema.obtenerInstancia().temaActivo()));
        botonTemaReal.setTamano(28, 28);
        botonTemaReal.alHacerClick(this::alternarTema);
        btnTema = botonTemaReal.obtenerPanel();

        barraSuperiorReal = new BarraSuperior();
        BarraSuperior = barraSuperiorReal.obtenerPanel();
    }

    private void alternarTema() {
        Tema temaActivo = GestorTema.obtenerInstancia().temaActivo();
        Tema nuevoTema = temaActivo instanceof TemaOscuro ? new TemaClaro() : new TemaOscuro();
        GestorTema.obtenerInstancia().establecerTema(nuevoTema);
    }

    private Icono iconoParaCambiarA(Tema temaActivo) {
        return temaActivo instanceof TemaOscuro
                ? IconoSemantico.TEMA_CLARO.icono()
                : IconoSemantico.TEMA_OSCURO.icono();
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

    public JPanel obtenerPanelConfiguracionIa() {
        return pnlConfiguracionIa;
    }

    public void mostrarSoloPestanasDeFuncionario() {
        quitarPestana(pnlFuncionarios);
        quitarPestana(pnlCategorias);
        quitarPestana(pnlRecursos);
        quitarPestana(pnlConfiguracionIa);
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
        tbpVentanas.setBorder(new LineBorder(tema.colorBorde(), 1));

        botonTemaReal.setIcono(iconoParaCambiarA(tema));

        VentanaPrincipal.repaint();
    }

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
        VentanaPrincipal.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas = new JTabbedPane();
        VentanaPrincipal.add(tbpVentanas, new GridConstraints(2, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        pnlCalendarizacion = new JPanel();
        pnlCalendarizacion.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Calendarizacion", pnlCalendarizacion);
        pnlFuncionarios = new JPanel();
        pnlFuncionarios.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Funcionarios", pnlFuncionarios);
        pnlCategorias = new JPanel();
        pnlCategorias.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Categorias", pnlCategorias);
        pnlRecursos = new JPanel();
        pnlRecursos.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Recursos", pnlRecursos);
        pnlActividades = new JPanel();
        pnlActividades.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Actividades", pnlActividades);
        pnlEstadisticas = new JPanel();
        pnlEstadisticas.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Estadisticas", pnlEstadisticas);
        pnlReservas = new JPanel();
        pnlReservas.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Reservas", pnlReservas);
        pnlConfiguracionIa = new JPanel();
        pnlConfiguracionIa.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tbpVentanas.addTab("Configuracion IA", pnlConfiguracionIa);
        pnlEncabezado = new JPanel();
        pnlEncabezado.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, 0));
        VentanaPrincipal.add(pnlEncabezado, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        iconLogo = new JLabel();
        iconLogo.setText("Icono");
        pnlEncabezado.add(iconLogo, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        pnlEncabezado.add(spacer1, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        lblTituloApp = new JLabel();
        lblTituloApp.setText("SISTEMA DE RESERVA DE RECURSOS");
        pnlEncabezado.add(lblTituloApp, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblSubtituloApp = new JLabel();
        lblSubtituloApp.setText("Escuela de Informática - UNA");
        pnlEncabezado.add(lblSubtituloApp, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblNombreUsuario = new JLabel();
        lblNombreUsuario.setText("Nombre De Usuario");
        pnlEncabezado.add(lblNombreUsuario, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_SOUTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblBadgeRol = new JLabel();
        lblBadgeRol.setText("ADMINISTRADOR");
        pnlEncabezado.add(lblBadgeRol, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlEncabezado.add(btnCuenta, new GridConstraints(0, 4, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pnlEncabezado.add(btnTema, new GridConstraints(0, 5, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        VentanaPrincipal.add(BarraSuperior, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return VentanaPrincipal;
    }

}
