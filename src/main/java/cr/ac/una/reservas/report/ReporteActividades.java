package cr.ac.una.reservas.report;

import cr.ac.una.reservas.model.Reserva;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReporteActividades implements GeneradorReporte<Reserva> {

    private static final String TITULO_POR_DEFECTO = "Programación Semanal de Actividades";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT);

    @Override
    public void generar(String rutaDestino, List<Reserva> datos, Map<String, String> metadatos) {
        String titulo = obtenerMetadato(metadatos, "titulo", TITULO_POR_DEFECTO);
        String subtitulo = obtenerMetadato(metadatos, "subtitulo", null);

        List<String> encabezados = List.of("Fecha", "Horario", "Actividad", "Funcionario", "Estado");
        List<List<String>> filas = new ArrayList<>();
        for (Reserva reserva : datos) {
            String fecha = reserva.getFecha() != null ? reserva.getFecha().format(FORMATO_FECHA) : "";
            String horario = valorSeguro(String.valueOf(reserva.getHoraInicio()))
                    + " - " + valorSeguro(String.valueOf(reserva.getHoraFin()));
            String estado = reserva.getEstado() != null ? reserva.getEstado().toString() : "";
            filas.add(List.of(
                    fecha,
                    horario,
                    valorSeguro(reserva.getActividad()),
                    valorSeguro(reserva.getIdFuncionario()),
                    estado
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
        return valor == null || "null".equals(valor) ? "" : valor;
    }
}
