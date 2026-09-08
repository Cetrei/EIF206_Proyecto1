package cr.ac.una.reservas.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReporteCalendarizacion implements GeneradorReporte<FilaCalendarizacion> {

    private static final String TITULO_POR_DEFECTO = "Calendarización de Recursos";

    @Override
    public void generar(String rutaDestino, List<FilaCalendarizacion> datos, Map<String, String> metadatos) {
        String titulo = obtenerMetadato(metadatos, "titulo", TITULO_POR_DEFECTO);
        String subtitulo = obtenerMetadato(metadatos, "subtitulo", null);

        List<String> encabezados = List.of("Recurso", "Horario", "Actividad");
        List<List<String>> filas = new ArrayList<>();
        for (FilaCalendarizacion fila : datos) {
            filas.add(List.of(
                    valorSeguro(fila.getRecurso()),
                    valorSeguro(fila.getHorario()),
                    valorSeguro(fila.getActividad())
            ));
        }

        EscritorPdfBasico.generar(rutaDestino, titulo, subtitulo, new EscritorPdfBasico.Tabla(encabezados, filas));
    }

    private static String obtenerMetadato(Map<String, String> metadatos, String clave, String porDefecto) {
        if (metadatos == null || !metadatos.containsKey(clave)) {
            return porDefecto;
        }
        return metadatos.get(clave);
    }

    private static String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }
}
