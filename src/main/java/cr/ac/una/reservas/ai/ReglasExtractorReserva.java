package cr.ac.una.reservas.ai;

import cr.ac.una.reservas.model.Categoria;

import java.text.Normalizer;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReglasExtractorReserva implements ExtractorReserva {

    private static final Pattern FECHA_DMY =
            Pattern.compile("\\b(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})\\b");

    private static final Pattern FECHA_ISO =
            Pattern.compile("\\b(\\d{4})-(\\d{1,2})-(\\d{1,2})\\b");

    private static final Pattern RANGO_HORAS = Pattern.compile(
            "(?i)\\b(?:de\\s+)?(\\d{1,2})(?::(\\d{2}))?\\s*(a\\.?\\s*m\\.?|p\\.?\\s*m\\.?)?"
                    + "\\s*(?:a|hasta|-)\\s*"
                    + "(\\d{1,2})(?::(\\d{2}))?\\s*(a\\.?\\s*m\\.?|p\\.?\\s*m\\.?)?\\b"
    );

    private static final Pattern ACTIVIDAD_EXPLICITA = Pattern.compile(
            "(?i)\\b(?:actividad|evento)\\s*[:=-]?\\s*(.+?)"
                    + "(?=\\s+(?:el\\s+\\d{1,2}[/-]|el\\s+\\d{4}-|hoy\\b|mañana\\b|manana\\b|"
                    + "de\\s+\\d{1,2}(?::\\d{2})?|con\\s+|usando\\s+)|$)"
    );

    private static final Pattern ACTIVIDAD_PARA = Pattern.compile(
            "(?i)\\bpara\\s+(?:una?|el|la)?\\s*(.+?)"
                    + "(?=\\s+(?:el\\s+\\d{1,2}[/-]|el\\s+\\d{4}-|hoy\\b|mañana\\b|manana\\b|"
                    + "de\\s+\\d{1,2}(?::\\d{2})?|con\\s+|usando\\s+)|$)"
    );

    @Override
    public DatosReservaExtraidos extraer(
            String frase,
            List<Categoria> categoriasDisponibles) {

        DatosReservaExtraidos datos = new DatosReservaExtraidos();

        if (frase == null || frase.trim().isEmpty()) {
            return datos;
        }

        datos.setActividad(extraerActividad(frase));
        datos.setFecha(extraerFecha(frase));

        LocalTime[] horas = extraerHoras(frase);

        datos.setHoraInicio(horas[0]);
        datos.setHoraFin(horas[1]);

        datos.setIdsCategoriasIdentificadas(
                extraerCategorias(frase, categoriasDisponibles)
        );

        return datos;
    }

    private String extraerActividad(String frase) {
        Matcher matcher = ACTIVIDAD_EXPLICITA.matcher(frase);

        if (matcher.find()) {
            return limpiarActividad(matcher.group(1));
        }

        matcher = ACTIVIDAD_PARA.matcher(frase);

        if (matcher.find()) {
            String actividad = limpiarActividad(matcher.group(1));

            if (actividad != null && !actividad.matches("^\\d.*")) {
                return actividad;
            }
        }

        return null;
    }

    private String limpiarActividad(String texto) {
        if (texto == null) {
            return null;
        }

        String limpio = texto.trim()
                .replaceAll("^[\\\"']|[\\\"']$", "");

        return limpio.isEmpty() ? null : limpio;
    }

    private LocalDate extraerFecha(String frase) {
        String normalizada = normalizar(frase);

        if (normalizada.matches(".*\\bmanana\\b.*")) {
            return LocalDate.now().plusDays(1);
        }

        if (normalizada.matches(".*\\bhoy\\b.*")) {
            return LocalDate.now();
        }

        Matcher iso = FECHA_ISO.matcher(frase);

        if (iso.find()) {
            return crearFecha(
                    numero(iso.group(1)),
                    numero(iso.group(2)),
                    numero(iso.group(3))
            );
        }

        Matcher dmy = FECHA_DMY.matcher(frase);

        if (dmy.find()) {
            return crearFecha(
                    numero(dmy.group(3)),
                    numero(dmy.group(2)),
                    numero(dmy.group(1))
            );
        }

        return null;
    }

    private LocalDate crearFecha(int anio, int mes, int dia) {
        try {
            return LocalDate.of(anio, mes, dia);
        } catch (DateTimeException excepcion) {
            return null;
        }
    }

    private LocalTime[] extraerHoras(String frase) {
        LocalTime[] resultado = new LocalTime[]{null, null};

        Matcher matcher = RANGO_HORAS.matcher(frase);

        if (!matcher.find()) {
            return resultado;
        }

        String sufijoInicio = matcher.group(3);
        String sufijoFin = matcher.group(6);

        if (sufijoInicio == null && sufijoFin != null) {
            sufijoInicio = sufijoFin;
        }

        if (sufijoFin == null && sufijoInicio != null) {
            sufijoFin = sufijoInicio;
        }

        resultado[0] = crearHora(
                numero(matcher.group(1)),
                matcher.group(2) == null ? 0 : numero(matcher.group(2)),
                sufijoInicio
        );

        resultado[1] = crearHora(
                numero(matcher.group(4)),
                matcher.group(5) == null ? 0 : numero(matcher.group(5)),
                sufijoFin
        );

        return resultado;
    }

    private LocalTime crearHora(int hora, int minuto, String sufijo) {
        try {
            if (sufijo != null) {
                String limpio = sufijo
                        .toLowerCase(Locale.ROOT)
                        .replace(".", "")
                        .replace(" ", "");

                if (hora < 1 || hora > 12) {
                    return null;
                }

                if ("pm".equals(limpio) && hora != 12) {
                    hora += 12;
                }

                if ("am".equals(limpio) && hora == 12) {
                    hora = 0;
                }
            }

            return LocalTime.of(hora, minuto);

        } catch (DateTimeException excepcion) {
            return null;
        }
    }

    private List<String> extraerCategorias(
            String frase,
            List<Categoria> categoriasDisponibles) {

        List<String> ids = new ArrayList<>();

        if (categoriasDisponibles == null || categoriasDisponibles.isEmpty()) {
            return ids;
        }

        String fraseNormalizada = normalizar(frase);

        for (Categoria categoria : categoriasDisponibles) {

            if (categoria == null
                    || categoria.getId() == null
                    || categoria.getDescripcion() == null) {

                continue;
            }

            String descripcionNormalizada =
                    normalizar(categoria.getDescripcion());

            if (fraseNormalizada.contains(descripcionNormalizada)
                    || coincidePorPalabras(
                    fraseNormalizada,
                    descripcionNormalizada)) {

                ids.add(categoria.getId());
            }
        }

        return ids;
    }

    private boolean coincidePorPalabras(
            String frase,
            String descripcion) {

        String[] palabras = descripcion.split("\\s+");

        int relevantes = 0;
        int coincidencias = 0;

        for (String palabra : palabras) {

            if (palabra.length() < 4) {
                continue;
            }

            relevantes++;

            if (frase.contains(palabra)) {
                coincidencias++;
            }
        }

        return relevantes > 0
                && coincidencias >= Math.max(
                1,
                (relevantes + 1) / 2
        );
    }

    private String normalizar(String texto) {
        String base = Normalizer
                .normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);

        return base
                .replaceAll("\\s+", " ")
                .trim();
    }

    private int numero(String texto) {
        return Integer.parseInt(texto);
    }
}