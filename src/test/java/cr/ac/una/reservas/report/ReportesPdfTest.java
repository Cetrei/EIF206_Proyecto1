package cr.ac.una.reservas.report;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.EstadisticaCategoria;
import cr.ac.una.reservas.model.EstadisticaSemana;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportesPdfTest {

    @TempDir
    Path carpeta;

    @Test
    public void generaTodosLosTiposDeReporte() throws IOException {

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

    @Test
    public void generaReporteDeEstadisticasConGraficoDeBarras() throws IOException {

        Path archivo = carpeta.resolve("estadisticas_con_datos.pdf");

        Categoria categoriaSalas = new Categoria("CAT-SALA", "Sala de reuniones");
        Categoria categoriaProyectores = new Categoria("CAT-PROYECTOR", "Proyector");

        List<EstadisticaCategoria> recursosPorCategoria = List.of(
                new EstadisticaCategoria(categoriaSalas, 3L),
                new EstadisticaCategoria(categoriaProyectores, 5L)
        );

        List<EstadisticaSemana> actividadesPorSemana = List.of(
                new EstadisticaSemana(LocalDate.of(2026, 8, 3), 2L),
                new EstadisticaSemana(LocalDate.of(2026, 8, 10), 4L)
        );

        DatosReporteEstadisticas datos = new DatosReporteEstadisticas(
                recursosPorCategoria,
                "01/08/2026 al 15/08/2026",
                actividadesPorSemana,
                "01/08/2026 al 15/08/2026"
        );

        new ReporteEstadisticas().generar(
                archivo.toString(),
                List.of(datos),
                Map.of("subtitulo", "Prueba con datos y gráfico")
        );

        verificarPdf(archivo);

        String contenido = new String(Files.readAllBytes(archivo), StandardCharsets.ISO_8859_1);
        assertTrue(contenido.contains(" re\nf"), "El PDF no contiene el operador de rectángulo relleno del gráfico");
        assertTrue(contenido.contains("0.3 0.45 0.85 rg"), "El PDF no contiene el color de barra esperado");
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
