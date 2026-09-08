package cr.ac.una.reservas.report;

import cr.ac.una.reservas.model.EstadisticaCategoria;
import cr.ac.una.reservas.model.EstadisticaSemana;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReporteEstadisticas implements GeneradorReporte<DatosReporteEstadisticas> {

    private static final String TITULO_POR_DEFECTO = "Reporte de Estadísticas";

    @Override
    public void generar(String rutaDestino, List<DatosReporteEstadisticas> datos, Map<String, String> metadatos) {
        String titulo = obtenerMetadato(metadatos, "titulo", TITULO_POR_DEFECTO);
        String subtitulo = obtenerMetadato(metadatos, "subtitulo", null);

        DatosReporteEstadisticas datosReporte = datos.isEmpty()
                ? new DatosReporteEstadisticas(List.of(), null, List.of(), null)
                : datos.get(0);

        List<EscritorPdfBasico.Seccion> secciones = List.of(
                seccionRecursos(datosReporte),
                seccionActividades(datosReporte)
        );

        EscritorPdfBasico.generar(rutaDestino, titulo, subtitulo, secciones);
    }

    private EscritorPdfBasico.Seccion seccionRecursos(DatosReporteEstadisticas datosReporte) {
        String subtituloSeccion = "Recursos Usados por Categoría"
                + (esVacio(datosReporte.getPeriodoRecursos()) ? "" : " (" + datosReporte.getPeriodoRecursos() + ")");

        List<String> encabezados = List.of("Categoría", "Cantidad");
        List<List<String>> filas = new ArrayList<>();
        for (EstadisticaCategoria fila : datosReporte.getRecursosPorCategoria()) {
            String descripcionCategoria = fila.getCategoria() != null ? fila.getCategoria().getDescripcion() : "";
            filas.add(List.of(valorSeguro(descripcionCategoria), String.valueOf(fila.getCantidad())));
        }

        return new EscritorPdfBasico.Seccion(subtituloSeccion, new EscritorPdfBasico.Tabla(encabezados, filas));
    }

    private EscritorPdfBasico.Seccion seccionActividades(DatosReporteEstadisticas datosReporte) {
        String subtituloSeccion = "Actividades por Semana"
                + (esVacio(datosReporte.getPeriodoActividades()) ? "" : " (" + datosReporte.getPeriodoActividades() + ")");

        List<String> encabezados = List.of("Semana", "Cantidad");
        List<List<String>> filas = new ArrayList<>();
        for (EstadisticaSemana fila : datosReporte.getActividadesPorSemana()) {
            String inicioSemana = fila.getInicioSemana() == null ? "" : fila.getInicioSemana().toString();
            filas.add(List.of(valorSeguro(inicioSemana), String.valueOf(fila.getCantidad())));
        }

        return new EscritorPdfBasico.Seccion(subtituloSeccion, new EscritorPdfBasico.Tabla(encabezados, filas));
    }

    private static boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
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
