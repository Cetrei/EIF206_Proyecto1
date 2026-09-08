package cr.ac.una.reservas.report;

import cr.ac.una.reservas.model.Funcionario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReporteFuncionarios implements GeneradorReporte<Funcionario> {

    private static final String TITULO_POR_DEFECTO = "Listado de Funcionarios";

    @Override
    public void generar(String rutaDestino, List<Funcionario> datos, Map<String, String> metadatos) {
        String titulo = obtenerMetadato(metadatos, "titulo", TITULO_POR_DEFECTO);
        String subtitulo = obtenerMetadato(metadatos, "subtitulo", null);

        List<String> encabezados = List.of("ID", "Nombre", "Teléfono");
        List<List<String>> filas = new ArrayList<>();
        for (Funcionario funcionario : datos) {
            filas.add(List.of(
                    valorSeguro(funcionario.getId()),
                    valorSeguro(funcionario.getNombre()),
                    valorSeguro(funcionario.getTelefono())
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
