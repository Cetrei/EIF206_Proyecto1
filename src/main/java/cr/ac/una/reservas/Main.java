package cr.ac.una.reservas;

import cr.ac.una.reservas.presentation.controller.LoginControl;
import cr.ac.una.reservas.presentation.mvc.LoginPanel;
import cr.ac.una.reservas.presentation.model.LoginModel;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciar);
    }

    private static void iniciar() {
        GestorTema.aplicarUIManager(GestorTema.obtenerInstancia().temaActivo());

        JFrame ventana = new JFrame("Sistema de Reservas - EIF206 Proyecto 1");
        ventana.setUndecorated(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.getContentPane().setBackground(GestorTema.obtenerInstancia().temaActivo().colorFondoVentana());

        LoginModel loginModelo = new LoginModel();
        LoginPanel loginPanel = new LoginPanel(loginModelo);
        new LoginControl(loginModelo, loginPanel, ventana);

        ventana.setContentPane(loginPanel.obtenerPanel());
        ventana.pack();
        ventana.setMinimumSize(ventana.getSize());
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
