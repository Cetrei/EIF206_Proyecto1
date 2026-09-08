package cr.ac.una.reservas.presentation;

import cr.ac.una.reservas.presentation.componentes.BarraSuperior;
import cr.ac.una.reservas.presentation.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Dimension;
import java.awt.Font;

public class VentanaPrincipal implements CambioTemaListener {
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

    public VentanaPrincipal() {
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

    public void mostrarUsuario(String nombre, String rolTexto) {
        lblNombreUsuario.setText(nombre);
        lblBadgeRol.setText(rolTexto);
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
}
