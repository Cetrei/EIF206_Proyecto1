package cr.ac.una.reservas.report;

import cr.ac.una.reservas.model.Reserva;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReporteReservas implements GeneradorReporte<Reserva> {

    private static final String TITULO_POR_DEFECTO = "Listado de Reservas";

    @Override
    public void generar(String rutaDestino, List<Reserva> datos, Map<String, String> metadatos) {
        String titulo = obtenerMetadato(metadatos, "titulo", TITULO_POR_DEFECTO);
        String subtitulo = obtenerMetadato(metadatos, "subtitulo", null);

        List<String> encabezados = List.of(
                "ID", "Actividad", "Fecha", "Horario", "Recursos Asignados", "Estado"
        );
        List<List<String>> filas = new ArrayList<>();
        for (Reserva reserva : datos) {
            filas.add(List.of(
                    valorSeguro(reserva.getId()),
                    valorSeguro(reserva.getActividad()),
                    valorSeguro(reserva.getFecha() == null ? "" : reserva.getFecha().toString()),
                    horario(reserva),
                    String.join(", ", reserva.getIdsRecursosAsignados()),
                    valorSeguro(reserva.getEstado() == null ? "" : reserva.getEstado().toString())
            ));
        }

        EscritorPdfBasico.generar(rutaDestino, titulo, subtitulo, new EscritorPdfBasico.Tabla(encabezados, filas));
    }

    private static String horario(Reserva reserva) {
        if (reserva.getHoraInicio() == null || reserva.getHoraFin() == null) {
            return "";
        }
        return reserva.getHoraInicio() + " - " + reserva.getHoraFin();
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
