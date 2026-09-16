package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.presentation.mvc.componentes.Popup;

import java.awt.Frame;
import java.awt.Window;

public final class ManejadorExcepcionesGlobal {
    private ManejadorExcepcionesGlobal() {
    }

    public static void instalar() {
        Thread.setDefaultUncaughtExceptionHandler(ManejadorExcepcionesGlobal::manejar);
    }

    private static void manejar(Thread hilo, Throwable excepcion) {
        excepcion.printStackTrace();

        Frame ventanaActiva = obtenerVentanaActiva();
        if (ventanaActiva == null) {
            return;
        }

        Popup.mostrarAviso(
                ventanaActiva,
                Popup.Tipo.ERROR,
                "Ocurrió un error inesperado",
                "Se produjo un error inesperado y la acción no se pudo completar. "
                        + "Puede intentar de nuevo; si el problema persiste, reinicie la aplicación."
        );
    }

    private static Frame obtenerVentanaActiva() {
        for (Window ventana : Window.getWindows()) {
            if (ventana instanceof Frame && ventana.isVisible() && ventana.isActive()) {
                return (Frame) ventana;
            }
        }
        for (Window ventana : Window.getWindows()) {
            if (ventana instanceof Frame && ventana.isVisible()) {
                return (Frame) ventana;
            }
        }
        return null;
    }
}