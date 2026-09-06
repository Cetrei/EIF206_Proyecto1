package cr.ac.una.reservas.presentation;

import cr.ac.una.reservas.presentation.componentes.BarraSuperior;
import cr.ac.una.reservas.presentation.componentes.BotonIcono;
import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Ventana principal post-login: encabezado (logo, titulo, usuario/rol,
 * boton de cuenta) mas un JTabbedPane con una pestana por pantalla
 * principal. Cada pestana (pnlFuncionarios, pnlReservas, etc.) es un
 * JPanel vacio que su propio control llena mas adelante (ver
 * VentanaPrincipalControl); este archivo solo arma el marco visual y
 * expone lo necesario para que el control conecte el resto.
 */
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
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    /**
     * btnCuenta esta declarado custom-create="true" en el .form (igual
     * patron que btnIngresar en LoginPanel.form): en vez de un JButton
     * crudo, aqui se reemplaza por un BotonIcono real del sistema de
     * diseno, para que tenga esquinas redondeadas, hover y tema
     * aplicado automaticamente. Lo mismo aplica para BarraSuperior,
     * reemplazado por una instancia real del componente (ver paquete
     * componentes.BarraSuperior) en vez del JPanel vacio que genera el
     * binding por defecto.
     */
    private void createUIComponents() {
        botonCuentaReal = new BotonIcono();
        botonCuentaReal.setIcono(Icono.AJUSTES);
        btnCuenta = botonCuentaReal.obtenerPanel();

        barraSuperiorReal = new BarraSuperior();
        BarraSuperior = barraSuperiorReal.obtenerPanel();
    }

    /**
     * Muestra el nombre y el rol del usuario logueado en el encabezado.
     * VentanaPrincipalControl llama a esto justo despues de construir
     * la ventana, usando los datos de SesionControl.
     */
    public void mostrarUsuario(String nombre, String rolTexto) {
        lblNombreUsuario.setText(nombre);
        lblBadgeRol.setText(rolTexto);
    }

    /**
     * Registra la accion a ejecutar cuando se hace click en el boton de
     * cuenta del encabezado. El controlador decide que mostrar (el
     * popup con PanelCuenta).
     */
    public void alAbrirCuenta(Runnable accion) {
        botonCuentaReal.alHacerClick(accion);
    }

    /**
     * Registra la accion a ejecutar al presionar la X de BarraSuperior
     * en el borde superior de la ventana principal. A diferencia de la
     * X de PanelCuenta (que solo cierra un JDialog secundario), esta es
     * la ventana raiz de toda la aplicacion: VentanaPrincipalControl
     * decide que significa cerrarla (tipicamente, pedir confirmacion y
     * salir de la aplicacion), esta vista solo expone el punto de
     * enganche.
     */
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

    /**
     * Oculta las pestanas que solo puede usar un administrador
     * (Funcionarios, Categorias, Recursos), para el caso de un usuario
     * tipo funcionario. VentanaPrincipalControl decide cuando llamar
     * esto segun SesionControl.esAdministrador().
     */
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

        IconoAplicador.aplicar(iconLogo, Icono.CALENDARIO, 20f, tema.colorPrimario());

        lblTituloApp.setForeground(tema.colorTexto());
        lblTituloApp.setFont(tema.fuenteTitulo().deriveFont(15f));
        // Sin esto, cada JLabel reserva el alto de linea completo que
        // le da su FontMetrics (ascent+descent+leading), que puede ser
        // bastante mayor que el texto visible; con dos labels apilados
        // en filas separadas (titulo/subtitulo, nombre/badge) eso se
        // traduce en un espacio vertical entre ellos que vgap="0" en el
        // .form no alcanza a corregir, porque el vgap separa CELDAS,
        // no compensa el alto que cada celda reclama por su contenido.
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
        // El borde propio del Look and Feel (Metal) alrededor del area
        // de contenido del JTabbedPane no respeta el tema, asi que se
        // reemplaza por uno del color de borde del tema (o se quita del
        // todo, ver EmptyBorder mas abajo si se prefiere sin linea).
        tbpVentanas.setBorder(new javax.swing.border.LineBorder(tema.colorBorde(), 1));

        VentanaPrincipal.repaint();
    }

    /**
     * Fija el alto preferido de un JLabel al valor pedido (en vez de
     * dejar que el propio FontMetrics de su fuente decida), sin tocar
     * el ancho (se calcula igual, a partir del texto). Ver comentario
     * en aplicarTema para el porque: es lo que realmente controla la
     * separacion vertical entre titulo/subtitulo y nombre/badge, no el
     * vgap del grid.
     */
    private static void ajustarAltoLinea(JLabel label, int alto) {
        Dimension preferido = label.getPreferredSize();
        label.setPreferredSize(new Dimension(preferido.width, alto));
    }
}
