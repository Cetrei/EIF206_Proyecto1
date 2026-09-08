package cr.ac.una.reservas.report;

import cr.ac.una.reservas.model.Categoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReporteCategorias implements GeneradorReporte<Categoria> {

    private static final String TITULO_POR_DEFECTO = "Listado de Categorías";

    @Override
    public void generar(String rutaDestino, List<Categoria> datos, Map<String, String> metadatos) {
        String titulo = obtenerMetadato(metadatos, "titulo", TITULO_POR_DEFECTO);
        String subtitulo = obtenerMetadato(metadatos, "subtitulo", null);

        List<String> encabezados = List.of("ID", "Descripción");
        List<List<String>> filas = new ArrayList<>();
        for (Categoria categoria : datos) {
            filas.add(List.of(
                    valorSeguro(categoria.getId()),
                    valorSeguro(categoria.getDescripcion())
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
