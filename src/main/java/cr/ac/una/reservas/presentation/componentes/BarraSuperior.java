package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

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
        botonSalirReal.setIcono(IconoSemantico.CERRAR_VENTANA.icono());
        botonSalirReal.setTamano(22, 22);
        btnSalir = botonSalirReal.obtenerPanel();
    }

    public void alCerrar(Runnable accion) {
        botonSalirReal.alHacerClick(accion);
    }

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
