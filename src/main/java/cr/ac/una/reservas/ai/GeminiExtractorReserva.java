package cr.ac.una.reservas.ai;

import cr.ac.una.reservas.model.Categoria;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiExtractorReserva implements ExtractorReserva {

    private static final String URL_BASE =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    private static final String MODELO_POR_DEFECTO =
            "gemini-3.8-flash";

    private final HttpClient cliente;
    private final String apiKey;
    private final String modelo;

    public GeminiExtractorReserva() {
        this(
                System.getenv("GEMINI_API_KEY"),
                obtenerModeloConfigurado()
        );
    }

    public GeminiExtractorReserva(String apiKey, String modelo) {
        this.apiKey = apiKey;

        this.modelo =
                modelo == null || modelo.trim().isEmpty()
                        ? MODELO_POR_DEFECTO
                        : modelo.trim();

        this.cliente =
                HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();
    }

    @Override
    public DatosReservaExtraidos extraer(
            String frase,
            List<Categoria> categoriasDisponibles) {

        if (frase == null || frase.trim().isEmpty()) {
            return new DatosReservaExtraidos();
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException(
                    "No se configuro la variable de entorno GEMINI_API_KEY."
            );
        }

        String prompt =
                construirPrompt(frase, categoriasDisponibles);

        String cuerpo =
                construirCuerpo(prompt);

        HttpRequest solicitud =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        URL_BASE
                                                + modelo
                                                + ":generateContent"))
                        .timeout(Duration.ofSeconds(25))
                        .header(
                                "Content-Type",
                                "application/json")
                        .header(
                                "x-goog-api-key",
                                apiKey)
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(
                                                cuerpo,
                                                StandardCharsets.UTF_8))
                        .build();

        try {
            HttpResponse<String> respuesta =
                    cliente.send(
                            solicitud,
                            HttpResponse.BodyHandlers
                                    .ofString(StandardCharsets.UTF_8)
                    );

            if (respuesta.statusCode() < 200
                    || respuesta.statusCode() >= 300) {

                throw new IllegalStateException(
                        "Gemini respondio con codigo HTTP "
                                + respuesta.statusCode()
                                + "."
                );
            }

            String jsonDelModelo =
                    extraerTextoRespuesta(respuesta.body());

            return convertirRespuesta(
                    jsonDelModelo,
                    categoriasDisponibles
            );

        } catch (InterruptedException excepcion) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "La solicitud a Gemini fue interrumpida.",
                    excepcion
            );

        } catch (Exception excepcion) {

            if (excepcion instanceof IllegalStateException) {
                throw (IllegalStateException) excepcion;
            }

            throw new IllegalStateException(
                    "No se pudo obtener una respuesta valida de Gemini.",
                    excepcion
            );
        }
    }

    private String construirPrompt(
            String frase,
            List<Categoria> categoriasDisponibles) {

        StringBuilder categorias =
                new StringBuilder();

        if (categoriasDisponibles != null) {

            for (Categoria categoria : categoriasDisponibles) {

                if (categoria == null) {
                    continue;
                }

                categorias
                        .append("- id=")
                        .append(categoria.getId())
                        .append(", descripcion=")
                        .append(categoria.getDescripcion())
                        .append("\n");
            }
        }

        return "Extrae los datos de una solicitud de reserva.\n"
                + "Fecha actual: "
                + LocalDate.now()
                + ".\n"
                + "Devuelve UNICAMENTE un objeto JSON, sin markdown ni explicaciones.\n"
                + "Usa exactamente estas claves:\n"
                + "actividad, fecha, horaInicio, horaFin, categorias.\n"
                + "fecha debe tener formato yyyy-MM-dd.\n"
                + "horaInicio y horaFin deben tener formato HH:mm.\n"
                + "categorias debe ser un arreglo que contenga UNICAMENTE ids de la lista disponible.\n"
                + "Si no puedes identificar un dato, usa null. "
                + "Si no identificas categorias, usa [].\n"
                + "Categorias disponibles:\n"
                + categorias
                + "Frase del usuario:\n"
                + frase;
    }

    private String construirCuerpo(String prompt) {
        return "{"
                + "\"contents\":[{\"parts\":[{\"text\":\""
                + escaparJson(prompt)
                + "\"}]}],"
                + "\"generationConfig\":{\"temperature\":0.1}"
                + "}";
    }

    private String extraerTextoRespuesta(String respuestaJson) {

        Pattern patron = Pattern.compile(
                "\\\"text\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"\\\\])*)\\\""
        );

        Matcher matcher =
                patron.matcher(respuestaJson);

        if (!matcher.find()) {
            throw new IllegalStateException(
                    "Gemini no devolvio contenido de texto."
            );
        }

        return desescaparJson(
                matcher.group(1)
        ).trim();
    }

    private DatosReservaExtraidos convertirRespuesta(
            String json,
            List<Categoria> categoriasDisponibles) {

        DatosReservaExtraidos datos =
                new DatosReservaExtraidos();

        datos.setActividad(
                leerCampoTexto(json, "actividad")
        );

        datos.setFecha(
                parsearFecha(
                        leerCampoTexto(json, "fecha"))
        );

        datos.setHoraInicio(
                parsearHora(
                        leerCampoTexto(json, "horaInicio"))
        );

        datos.setHoraFin(
                parsearHora(
                        leerCampoTexto(json, "horaFin"))
        );

        List<String> categorias =
                leerArregloTexto(
                        json,
                        "categorias");

        if (categorias.isEmpty()) {
            categorias =
                    leerArregloTexto(
                            json,
                            "idsCategoriasIdentificadas");
        }

        datos.setIdsCategoriasIdentificadas(
                resolverCategorias(
                        categorias,
                        categoriasDisponibles)
        );

        return datos;
    }

    private String leerCampoTexto(
            String json,
            String campo) {

        Pattern patron = Pattern.compile(
                "\\\""
                        + Pattern.quote(campo)
                        + "\\\"\\s*:\\s*"
                        + "(null|\\\"((?:\\\\.|[^\\\"\\\\])*)\\\")",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher =
                patron.matcher(json);

        if (!matcher.find()
                || "null".equalsIgnoreCase(
                matcher.group(1))) {

            return null;
        }

        return desescaparJson(
                matcher.group(2)
        );
    }

    private List<String> leerArregloTexto(
            String json,
            String campo) {

        List<String> valores =
                new ArrayList<>();

        Pattern patronArreglo =
                Pattern.compile(
                        "\\\""
                                + Pattern.quote(campo)
                                + "\\\"\\s*:\\s*\\[(.*?)]",
                        Pattern.CASE_INSENSITIVE
                                | Pattern.DOTALL
                );

        Matcher arreglo =
                patronArreglo.matcher(json);

        if (!arreglo.find()) {
            return valores;
        }

        Pattern patronValor =
                Pattern.compile(
                        "\\\"((?:\\\\.|[^\\\"\\\\])*)\\\""
                );

        Matcher valor =
                patronValor.matcher(
                        arreglo.group(1));

        while (valor.find()) {
            valores.add(
                    desescaparJson(
                            valor.group(1))
            );
        }

        return valores;
    }

    private List<String> resolverCategorias(
            List<String> valores,
            List<Categoria> categoriasDisponibles) {

        List<String> ids =
                new ArrayList<>();

        Set<String> agregados =
                new HashSet<>();

        if (categoriasDisponibles == null) {
            return ids;
        }

        for (String valor : valores) {

            if (valor == null) {
                continue;
            }

            for (Categoria categoria
                    : categoriasDisponibles) {

                if (categoria == null
                        || categoria.getId() == null) {

                    continue;
                }

                boolean coincideId =
                        categoria.getId()
                                .equalsIgnoreCase(
                                        valor.trim());

                boolean coincideDescripcion =
                        categoria.getDescripcion() != null
                                && categoria
                                .getDescripcion()
                                .equalsIgnoreCase(
                                        valor.trim());

                if ((coincideId || coincideDescripcion)
                        && agregados.add(
                        categoria.getId())) {

                    ids.add(categoria.getId());
                }
            }
        }

        return ids;
    }

    private LocalDate parsearFecha(String texto) {

        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException excepcion) {
            return null;
        }
    }

    private LocalTime parsearHora(String texto) {

        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalTime.parse(texto.trim());
        } catch (DateTimeParseException excepcion) {
            return null;
        }
    }

    private static String obtenerModeloConfigurado() {

        String configurado =
                System.getenv("GEMINI_MODEL");

        return configurado == null
                || configurado.trim().isEmpty()
                ? MODELO_POR_DEFECTO
                : configurado.trim();
    }

    private String escaparJson(String texto) {

        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String desescaparJson(String texto) {

        StringBuilder resultado =
                new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {

            char actual =
                    texto.charAt(i);

            if (actual != '\\'
                    || i + 1 >= texto.length()) {

                resultado.append(actual);
                continue;
            }

            char siguiente =
                    texto.charAt(++i);

            switch (siguiente) {

                case '"':
                    resultado.append('"');
                    break;

                case '\\':
                    resultado.append('\\');
                    break;

                case '/':
                    resultado.append('/');
                    break;

                case 'b':
                    resultado.append('\b');
                    break;

                case 'f':
                    resultado.append('\f');
                    break;

                case 'n':
                    resultado.append('\n');
                    break;

                case 'r':
                    resultado.append('\r');
                    break;

                case 't':
                    resultado.append('\t');
                    break;

                case 'u':

                    if (i + 4 >= texto.length()) {
                        throw new IllegalStateException(
                                "Respuesta JSON invalida."
                        );
                    }

                    String hexadecimal =
                            texto.substring(
                                    i + 1,
                                    i + 5);

                    resultado.append(
                            (char) Integer.parseInt(
                                    hexadecimal,
                                    16));

                    i += 4;
                    break;

                default:
                    resultado.append(siguiente);
                    break;
            }
        }

        return resultado.toString();
    }
}