package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Barra delgada para el borde superior de una ventana o dialogo propio
 * (VentanaPrincipal, PanelCuenta/"Mi Perfil", "Cambiar contraseña",
 * etc), pensada para no competir visualmente con el resto de la
 * interfaz: fondo transparente (hereda el color de quien la contenga)
 * y, por ahora, un unico boton de cerrar alineado a la derecha.
 * <p>
 * Se deja como su propio .form (en vez de resolver esto con un boton
 * suelto en cada pantalla) para poder agregarle mas botones mas
 * adelante (por ejemplo minimizar, o un atajo de ayuda) sin tener que
 * tocar cada pantalla que la use.
 * <p>
 * btnSalir esta declarado custom-create="true" en el .form (igual
 * patron que btnCuenta en VentanaPrincipal.form) para reemplazarlo por
 * un BotonIcono real, con esquinas redondeadas, hover y tema aplicado
 * automaticamente en vez de un JButton crudo.
 */
public class BarraSuperior implements CambioTemaListener {

    private static final int ALTO_BARRA = 28;

    private JPanel BarraSuperior;
    private JPanel btnSalir;

    private BotonIcono botonSalirReal;
    private JPanel envoltorio;

    public BarraSuperior() {
        BarraSuperior.setOpaque(false);
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        botonSalirReal = new BotonIcono();
        botonSalirReal.setIcono(Icono.CERRAR);
        // 22x22 en vez de los 40x40 por defecto: BarraSuperior es una
        // franja delgada de borde superior, no un boton de encabezado
        // como btnCuenta en VentanaPrincipal, asi que no necesita el
        // mismo tamano de golpe (era la causa de que la barra completa
        // se viera desproporcionadamente alta).
        botonSalirReal.setTamano(22, 22);
        btnSalir = botonSalirReal.obtenerPanel();
    }

    /**
     * Registra la accion a ejecutar al presionar el boton de cerrar.
     * Quien contenga la barra decide que significa "cerrar": disponer
     * un JDialog (ver PanelCuenta) o pedir confirmacion antes de salir
     * de la aplicacion (ver VentanaPrincipal), esta clase no lo asume.
     */
    public void alCerrar(Runnable accion) {
        botonSalirReal.alHacerClick(accion);
    }

    /**
     * Devuelve BarraSuperior envuelta en un panel propio con
     * BorderLayout y alto fijo, en vez de exponer BarraSuperior
     * directamente. Se necesita este envoltorio porque BarraSuperior
     * es un panel armado por su propio GridLayoutManager (definido en
     * BarraSuperior.form): ese layout manager recalcula su
     * getPreferredSize()/getMaximumSize() a partir de sus hijos en
     * cada layout pass, ignorando cualquier setPreferredSize/
     * setMaximumSize puesto desde afuera sobre ESE panel. Un
     * BorderLayout normal, en cambio, si respeta el preferred size
     * fijo del envoltorio, que es donde realmente se controla el alto
     * final de la franja de borde superior.
     */
    public JPanel obtenerPanel() {
        if (envoltorio == null) {
            envoltorio = new JPanel(new BorderLayout());
            envoltorio.setOpaque(false);
            envoltorio.setPreferredSize(new Dimension(10, ALTO_BARRA));
            envoltorio.setMinimumSize(new Dimension(10, ALTO_BARRA));
            envoltorio.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTO_BARRA));
            envoltorio.add(BarraSuperior, BorderLayout.CENTER);
        }
        return envoltorio;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        BarraSuperior.setOpaque(false);
        BarraSuperior.repaint();
    }
}
