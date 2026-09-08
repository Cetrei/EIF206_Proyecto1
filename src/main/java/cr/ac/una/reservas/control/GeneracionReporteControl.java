package cr.ac.una.reservas.control;

import cr.ac.una.reservas.presentation.componentes.Popup;
import cr.ac.una.reservas.report.GeneradorReporte;
import cr.ac.una.reservas.service.ReporteFactory;
import cr.ac.una.reservas.util.ReservaAppException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Frame;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class GeneracionReporteControl {
    private GeneracionReporteControl() {
    }

    // Si el usuario cancela el JFileChooser, no hace nada (cancelar no es una falla).
    @SuppressWarnings("unchecked")
    public static <T> void generarYGuardar(
            Frame ventanaPropietaria,
            ReporteFactory.TipoReporte tipoReporte,
            String nombreSugerido,
            List<T> datos,
            Map<String, String> metadatos
    ) {
        File archivoDestino = elegirArchivoDestino(ventanaPropietaria, nombreSugerido);
        if (archivoDestino == null) {
            return;
        }

        try {
            GeneradorReporte<T> generador = (GeneradorReporte<T>) ReporteFactory.obtenerGenerador(tipoReporte);
            generador.generar(archivoDestino.getAbsolutePath(), datos, metadatos);
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Generar reporte",
                    "El reporte se generó correctamente en:\n" + archivoDestino.getAbsolutePath()
            );
        } catch (ReservaAppException excepcion) {
            Popup.mostrarAviso(
                    ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo generar el reporte", excepcion.getMessage()
            );
        } catch (IllegalStateException excepcion) {
            // ReporteFactory.obtenerGenerador lanza esto cuando ese TipoReporte aun no tiene un ReporteXxx implementado.
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.INFORMACION,
                    "Generar reporte",
                    "La generación de reportes en PDF todavía no está disponible para esta pantalla."
            );
        }
    }

    private static File elegirArchivoDestino(Frame ventanaPropietaria, String nombreSugerido) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte PDF");
        selector.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        selector.setSelectedFile(new File(nombreSugerido + ".pdf"));

        int resultado = selector.showSaveDialog(ventanaPropietaria);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File archivoElegido = selector.getSelectedFile();
        if (!archivoElegido.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            archivoElegido = new File(archivoElegido.getParentFile(), archivoElegido.getName() + ".pdf");
        }
        return archivoElegido;
    }
}
