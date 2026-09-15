package cr.ac.una.reservas.report;

import cr.ac.una.reservas.model.Categoria;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportesPdfTest {

    private final Path carpeta =
            Paths.get("target", "reportes-prueba");

    @Test
    public void generaTodosLosTiposDeReporte() throws IOException {

        Files.createDirectories(carpeta);

        Path funcionarios = carpeta.resolve("funcionarios.pdf");
        Path categorias = carpeta.resolve("categorias.pdf");
        Path recursos = carpeta.resolve("recursos.pdf");
        Path reservas = carpeta.resolve("reservas.pdf");
        Path calendarizacion = carpeta.resolve("calendarizacion.pdf");
        Path actividades = carpeta.resolve("actividades.pdf");
        Path estadisticas = carpeta.resolve("estadisticas.pdf");

        new ReporteFuncionarios().generar(
                funcionarios.toString(),
                List.of(),
                Map.of("subtitulo", "Prueba de funcionarios")
        );

        new ReporteCategorias().generar(
                categorias.toString(),
                List.of(),
                Map.of("subtitulo", "Prueba de categorías")
        );

        new ReporteRecursos().generar(
                recursos.toString(),
                List.of(),
                Map.of("subtitulo", "Prueba de recursos")
        );

        new ReporteReservas().generar(
                reservas.toString(),
                List.of(),
                Map.of("subtitulo", "Prueba de reservas")
        );

        new ReporteCalendarizacion().generar(
                calendarizacion.toString(),
                List.of(),
                Map.of("subtitulo", "Prueba de calendarización")
        );

        new ReporteActividades().generar(
                actividades.toString(),
                List.of(),
                Map.of("subtitulo", "Prueba de actividades")
        );

        new ReporteEstadisticas().generar(
                estadisticas.toString(),
                List.of(),
                Map.of("subtitulo", "Prueba de estadísticas")
        );

        verificarPdf(funcionarios);
        verificarPdf(categorias);
        verificarPdf(recursos);
        verificarPdf(reservas);
        verificarPdf(calendarizacion);
        verificarPdf(actividades);
        verificarPdf(estadisticas);
    }

    @Test
    public void generaReporteDeCategoriasConDatos() throws IOException {

        Files.createDirectories(carpeta);

        Path archivo =
                carpeta.resolve("categorias_con_datos.pdf");

        List<Categoria> categorias =
                List.of(
                        new Categoria(
                                "CAT-SALA",
                                "Sala de reuniones"
                        ),
                        new Categoria(
                                "CAT-PROYECTOR",
                                "Proyector"
                        )
                );

        new ReporteCategorias().generar(
                archivo.toString(),
                categorias,
                Map.of(
                        "titulo",
                        "Listado de Categorías",
                        "subtitulo",
                        "Prueba con datos"
                )
        );

        verificarPdf(archivo);
    }

    private void verificarPdf(Path archivo) throws IOException {

        assertTrue(
                Files.exists(archivo),
                "No se creó " + archivo.getFileName()
        );

        assertTrue(
                Files.size(archivo) > 0,
                "El PDF está vacío: " + archivo.getFileName()
        );

        byte[] contenido =
                Files.readAllBytes(archivo);

        assertTrue(
                contenido.length >= 5
        );

        String cabecera =
                new String(
                        contenido,
                        0,
                        5,
                        StandardCharsets.US_ASCII
                );

        assertEquals(
                "%PDF-",
                cabecera
        );
    }
}
