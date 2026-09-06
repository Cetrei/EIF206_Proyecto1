package cr.ac.una.reservas;

import cr.ac.una.reservas.control.LoginControl;
import cr.ac.una.reservas.presentation.LoginPanel;
import cr.ac.una.reservas.presentation.tema.GestorTema;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {
        // Debe llamarse antes de construir cualquier componente Swing:
        // sin esto, el JTabbedPane (y otros detalles de "cromo" que
        // ningun componente propio del sistema de diseno pinta a mano
        // todavia) usan el Look and Feel Metal por defecto, con bordes
        // grises sin relacion con el tema oscuro/claro de la app.
        GestorTema.aplicarUIManager(GestorTema.obtenerInstancia().temaActivo());

        JFrame ventana = new JFrame("Sistema de Reservas - EIF206 Proyecto 1");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.getContentPane().setBackground(GestorTema.obtenerInstancia().temaActivo().colorFondoVentana());

        LoginPanel loginPanel = new LoginPanel();
        new LoginControl(loginPanel, ventana);

        ventana.setContentPane(loginPanel.obtenerPanel());
        ventana.pack();
        ventana.setMinimumSize(ventana.getSize());
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
