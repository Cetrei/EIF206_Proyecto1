package cr.ac.una.reservas.ai;

import cr.ac.una.reservas.model.Categoria;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiExtractorReservaTest {

    private final GeminiExtractorReserva extractor = new GeminiExtractorReserva("clave-de-prueba", "modelo-de-prueba");

    @Test
    void retornaDatosVaciosSiLaFraseEstaVaciaSinHacerNingunaLlamadaHttp() {
        DatosReservaExtraidos resultado = extractor.extraer("", List.of());

        assertNull(resultado.getActividad());
        assertNull(resultado.getFecha());
        assertTrue(resultado.getIdsCategoriasIdentificadas().isEmpty());
    }

    @Test
    void retornaDatosVaciosSiLaFraseEsNulaSinHacerNingunaLlamadaHttp() {
        DatosReservaExtraidos resultado = extractor.extraer(null, List.of());

        assertNull(resultado.getActividad());
    }

    @Test
    void rechazaSinIntentarLaLlamadaHttpSiNoHayApiKeyConfigurada() {
        GeminiExtractorReserva extractorSinClave = new GeminiExtractorReserva(null, "modelo-de-prueba");

        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> extractorSinClave.extraer("Reunion manana de 8 a 10", List.of())
        );

        assertTrue(excepcion.getMessage().contains("GEMINI_API_KEY"));
    }

    @Test
    void rechazaSinIntentarLaLlamadaHttpSiLaApiKeyEstaEnBlanco() {
        GeminiExtractorReserva extractorConClaveEnBlanco = new GeminiExtractorReserva("   ", "modelo-de-prueba");

        assertThrows(
                IllegalStateException.class,
                () -> extractorConClaveEnBlanco.extraer("Reunion manana de 8 a 10", List.of())
        );
    }

    @Test
    void usaModeloPorDefectoSiSeConstruyeConModeloNulo() {
        GeminiExtractorReserva extractorConModeloNulo = new GeminiExtractorReserva("clave", null);

        assertEquals("gemini-3-flash-preview", leerCampoModelo(extractorConModeloNulo));
    }

    @Test
    void usaModeloPorDefectoSiSeConstruyeConModeloEnBlanco() {
        GeminiExtractorReserva extractorConModeloEnBlanco = new GeminiExtractorReserva("clave", "   ");

        assertEquals("gemini-3-flash-preview", leerCampoModelo(extractorConModeloEnBlanco));
    }

    @Test
    void respetaElModeloExplicitoSiSeIndicaUno() {
        assertEquals("modelo-de-prueba", leerCampoModelo(extractor));
    }

    @Test
    void ofreceLaCadenaCompletaDeModelosGratuitosSiNoSeFijaUnoExplicito() {
        GeminiExtractorReserva extractorConModeloNulo = new GeminiExtractorReserva("clave", null);

        Object[] candidatos = invocarPrivado(
                extractorConModeloNulo, "obtenerCandidatosDeModelo", new Class[]{}
        );

        assertEquals(2, candidatos.length);
    }

    @Test
    void ofreceUnUnicoCandidatoSiSeFijaUnModeloExplicito() {
        Object[] candidatos = invocarPrivado(
                extractor, "obtenerCandidatosDeModelo", new Class[]{}
        );

        assertEquals(1, candidatos.length);
    }

    @Test
    void extraeElTextoDeUnaRespuestaEnvueltaComoLaDeGemini() {
        String respuestaCompleta =
                "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\""
                        + "{\\\"actividad\\\":\\\"Reunion\\\"}"
                        + "\"}]}}]}";

        String texto = invocarPrivado(extractor, "extraerTextoRespuesta", new Class[]{String.class}, respuestaCompleta);

        assertEquals("{\"actividad\":\"Reunion\"}", texto);
    }

    @Test
    void lanzaExcepcionSiLaRespuestaNoTraeCampoText() {
        String respuestaSinTexto = "{\"candidates\":[]}";

        assertThrows(
                RuntimeException.class,
                () -> invocarPrivado(extractor, "extraerTextoRespuesta", new Class[]{String.class}, respuestaSinTexto)
        );
    }

    @Test
    void descartaLasPartesDePensamientoYUsaElTextoDeRespuestaReal() {
        String respuestaConThinking =
                "{\"candidates\":[{\"content\":{\"parts\":["
                        + "{\"text\":\"Analizando la frase del usuario...\",\"thought\":true},"
                        + "{\"text\":\"Ahora construyo el JSON de respuesta\",\"thought\":true},"
                        + "{\"text\":\""
                        + "{\\\"actividad\\\":\\\"Sesion de junta directiva\\\"}"
                        + "\"}"
                        + "]}}]}";

        String texto = invocarPrivado(
                extractor, "extraerTextoRespuesta", new Class[]{String.class}, respuestaConThinking
        );

        assertEquals("{\"actividad\":\"Sesion de junta directiva\"}", texto);
    }

    @Test
    void eligeElBloqueQueParecenJsonDeReservaCuandoHayVariosTextosSinMarcarPensamiento() {
        String respuestaConTextoSuelto =
                "{\"candidates\":[{\"content\":{\"parts\":["
                        + "{\"text\":\"Claro, aqui esta el resultado solicitado:\"},"
                        + "{\"text\":\""
                        + "{\\\"actividad\\\":\\\"Sesion\\\",\\\"categorias\\\":[]}"
                        + "\"}"
                        + "]}}]}";

        String texto = invocarPrivado(
                extractor, "extraerTextoRespuesta", new Class[]{String.class}, respuestaConTextoSuelto
        );

        assertEquals("{\"actividad\":\"Sesion\",\"categorias\":[]}", texto);
    }

    @Test
    void resumeElMensajeDeErrorEstandarDeLaApiDeGemini() {
        String cuerpoError =
                "{\"error\":{\"code\":400,\"message\":\"API key not valid. Please pass a valid API key.\","
                        + "\"status\":\"INVALID_ARGUMENT\"}}";

        String resumen = invocarPrivado(
                extractor, "resumirCuerpoError", new Class[]{String.class}, cuerpoError
        );

        assertEquals("API key not valid. Please pass a valid API key.", resumen);
    }

    @Test
    void resumeElCuerpoCrudoSiNoTraeElCampoMessageEstandar() {
        String cuerpoError = "<html>502 Bad Gateway</html>";

        String resumen = invocarPrivado(
                extractor, "resumirCuerpoError", new Class[]{String.class}, cuerpoError
        );

        assertEquals("<html>502 Bad Gateway</html>", resumen);
    }

    @Test
    void indicaFaltaDeDetalleSiElCuerpoDeErrorEstaVacio() {
        String resumen = invocarPrivado(
                extractor, "resumirCuerpoError", new Class[]{String.class}, ""
        );

        assertTrue(resumen.toLowerCase().contains("sin detalle"));
    }

    @Test
    void convierteUnaRespuestaCompletaConTodosLosCampos() {
        List<Categoria> categorias = List.of(
                new Categoria("CAT-SALA", "Sala de reuniones"),
                new Categoria("CAT-PROYECTOR", "Proyector")
        );

        String json = "{"
                + "\"actividad\":\"Sesion de junta\","
                + "\"fecha\":\"2026-09-15\","
                + "\"horaInicio\":\"08:00\","
                + "\"horaFin\":\"10:00\","
                + "\"categorias\":[\"CAT-SALA\",\"CAT-PROYECTOR\"]"
                + "}";

        DatosReservaExtraidos resultado = invocarConvertirRespuesta(json, categorias);

        assertEquals("Sesion de junta", resultado.getActividad());
        assertEquals(LocalDate.of(2026, 9, 15), resultado.getFecha());
        assertEquals(LocalTime.of(8, 0), resultado.getHoraInicio());
        assertEquals(LocalTime.of(10, 0), resultado.getHoraFin());
        assertTrue(resultado.getIdsCategoriasIdentificadas().contains("CAT-SALA"));
        assertTrue(resultado.getIdsCategoriasIdentificadas().contains("CAT-PROYECTOR"));
    }

    @Test
    void convierteCamposNulosDelJsonComoNulosEnElResultado() {
        String json = "{"
                + "\"actividad\":null,"
                + "\"fecha\":null,"
                + "\"horaInicio\":null,"
                + "\"horaFin\":null,"
                + "\"categorias\":[]"
                + "}";

        DatosReservaExtraidos resultado = invocarConvertirRespuesta(json, List.of());

        assertNull(resultado.getActividad());
        assertNull(resultado.getFecha());
        assertNull(resultado.getHoraInicio());
        assertNull(resultado.getHoraFin());
        assertTrue(resultado.getIdsCategoriasIdentificadas().isEmpty());
    }

    @Test
    void ignoraFechaConFormatoInvalidoYLaDejaNula() {
        String json = "{\"actividad\":\"X\",\"fecha\":\"no-es-una-fecha\",\"horaInicio\":null,\"horaFin\":null,\"categorias\":[]}";

        DatosReservaExtraidos resultado = invocarConvertirRespuesta(json, List.of());

        assertNull(resultado.getFecha());
    }

    @Test
    void ignoraHoraConFormatoInvalidoYLaDejaNula() {
        String json = "{\"actividad\":\"X\",\"fecha\":null,\"horaInicio\":\"no-es-una-hora\",\"horaFin\":null,\"categorias\":[]}";

        DatosReservaExtraidos resultado = invocarConvertirRespuesta(json, List.of());

        assertNull(resultado.getHoraInicio());
    }

    @Test
    void resuelveCategoriasPorDescripcionCuandoElModeloDevuelveElTextoEnVezDelId() {
        List<Categoria> categorias = List.of(new Categoria("CAT-SALA", "Sala de reuniones"));

        String json = "{\"actividad\":null,\"fecha\":null,\"horaInicio\":null,\"horaFin\":null,"
                + "\"categorias\":[\"Sala de reuniones\"]}";

        DatosReservaExtraidos resultado = invocarConvertirRespuesta(json, categorias);

        assertTrue(resultado.getIdsCategoriasIdentificadas().contains("CAT-SALA"));
    }

    @Test
    void ignoraCategoriasQueNoCoincidenConNingunaDisponible() {
        List<Categoria> categorias = List.of(new Categoria("CAT-SALA", "Sala de reuniones"));

        String json = "{\"actividad\":null,\"fecha\":null,\"horaInicio\":null,\"horaFin\":null,"
                + "\"categorias\":[\"Categoria inexistente\"]}";

        DatosReservaExtraidos resultado = invocarConvertirRespuesta(json, categorias);

        assertTrue(resultado.getIdsCategoriasIdentificadas().isEmpty());
    }

    @Test
    void noDuplicaCategoriasCuandoElModeloRepiteLaMismaEnLaLista() {
        List<Categoria> categorias = List.of(new Categoria("CAT-SALA", "Sala de reuniones"));

        String json = "{\"actividad\":null,\"fecha\":null,\"horaInicio\":null,\"horaFin\":null,"
                + "\"categorias\":[\"CAT-SALA\",\"Sala de reuniones\"]}";

        DatosReservaExtraidos resultado = invocarConvertirRespuesta(json, categorias);

        assertEquals(1, resultado.getIdsCategoriasIdentificadas().size());
    }

    @Test
    void usaClaveAlternativaIdsCategoriasIdentificadasSiCategoriasVieneVacio() {
        List<Categoria> categorias = List.of(new Categoria("CAT-SALA", "Sala de reuniones"));

        String json = "{\"actividad\":null,\"fecha\":null,\"horaInicio\":null,\"horaFin\":null,"
                + "\"categorias\":[],\"idsCategoriasIdentificadas\":[\"CAT-SALA\"]}";

        DatosReservaExtraidos resultado = invocarConvertirRespuesta(json, categorias);

        assertTrue(resultado.getIdsCategoriasIdentificadas().contains("CAT-SALA"));
    }

    @Test
    void escapaComillasYSaltosDeLineaAlConstruirElCuerpoDeLaSolicitud() {
        String escapado = invocarPrivado(
                extractor, "escaparJson", new Class[]{String.class}, "Reunion \"importante\"\ncon salto"
        );

        assertEquals("Reunion \\\"importante\\\"\\ncon salto", escapado);
    }

    @Test
    void desescapaSecuenciasUnicodeDeLaRespuestaDeGemini() {
        String desescapado = invocarPrivado(
                extractor, "desescaparJson", new Class[]{String.class}, "Sala \\u00e1"
        );

        assertEquals("Sala á", desescapado);
    }

    @Test
    void desescapaBarrasInvertidasYComillasDeLaRespuestaDeGemini() {
        String desescapado = invocarPrivado(
                extractor, "desescaparJson", new Class[]{String.class}, "Ruta \\\\ con \\\"comillas\\\""
        );

        assertEquals("Ruta \\ con \"comillas\"", desescapado);
    }

    private DatosReservaExtraidos invocarConvertirRespuesta(String json, List<Categoria> categorias) {
        return invocarPrivado(
                extractor,
                "convertirRespuesta",
                new Class[]{String.class, List.class},
                json,
                categorias
        );
    }

    private String leerCampoModelo(GeminiExtractorReserva instancia) {
        try {
            java.lang.reflect.Field campo = GeminiExtractorReserva.class.getDeclaredField("modelo");
            campo.setAccessible(true);
            return (String) campo.get(instancia);
        } catch (ReflectiveOperationException excepcion) {
            throw new RuntimeException(excepcion);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T invocarPrivado(Object objetivo, String nombreMetodo, Class<?>[] tipos, Object... argumentos) {
        try {
            Method metodo = GeminiExtractorReserva.class.getDeclaredMethod(nombreMetodo, tipos);
            metodo.setAccessible(true);
            return (T) metodo.invoke(objetivo, argumentos);
        } catch (java.lang.reflect.InvocationTargetException excepcion) {
            Throwable causa = excepcion.getCause();
            if (causa instanceof RuntimeException) {
                throw (RuntimeException) causa;
            }
            throw new RuntimeException(causa);
        } catch (ReflectiveOperationException excepcion) {
            throw new RuntimeException(excepcion);
        }
    }
}
