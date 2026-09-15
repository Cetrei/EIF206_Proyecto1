package cr.ac.una.reservas.report;

import cr.ac.una.reservas.util.PersistenciaException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class EscritorPdfBasico {
    private static final float ANCHO_PAGINA = 612f;
    private static final float ALTO_PAGINA = 792f;
    private static final float MARGEN_IZQUIERDO = 48f;
    private static final float MARGEN_DERECHO = 48f;
    private static final float MARGEN_SUPERIOR = 56f;
    private static final float MARGEN_INFERIOR = 48f;

    private static final float TAMANO_TITULO = 16f;
    private static final float TAMANO_SUBTITULO = 10f;
    private static final float TAMANO_SUBTITULO_SECCION = 12f;
    private static final float TAMANO_ENCABEZADO_TABLA = 10f;
    private static final float TAMANO_CELDA = 9.5f;

    private static final float ALTO_LINEA_TITULO = 22f;
    private static final float ALTO_LINEA_SUBTITULO = 16f;
    private static final float ALTO_LINEA_SUBTITULO_SECCION = 18f;
    private static final float ALTO_LINEA_FILA = 16f;
    private static final float ESPACIO_ANTES_TABLA = 18f;

    private EscritorPdfBasico() {
    }

    public static final class Tabla {
        private final List<String> encabezados;
        private final List<List<String>> filas;

        public Tabla(List<String> encabezados, List<List<String>> filas) {
            this.encabezados = encabezados;
            this.filas = filas;
        }
    }

    public static void generar(String rutaDestino, String titulo, String subtitulo, Tabla tabla) {
        generar(rutaDestino, titulo, subtitulo, List.of(new Seccion(null, tabla)));
    }

    public static final class Seccion {
        private final String subtituloSeccion;
        private final Tabla tabla;

        public Seccion(String subtituloSeccion, Tabla tabla) {
            this.subtituloSeccion = subtituloSeccion;
            this.tabla = tabla;
        }
    }

    public static void generar(String rutaDestino, String titulo, String subtitulo, List<Seccion> secciones) {
        List<String[]> lineas = calcularLineasDeTexto(titulo, subtitulo, secciones);
        List<List<String[]>> paginas = paginar(lineas);

        byte[] contenidoPdf = construirDocumento(paginas);

        try (OutputStream salida = new FileOutputStream(rutaDestino)) {
            salida.write(contenidoPdf);
        } catch (IOException excepcion) {
            throw new PersistenciaException(
                    "No se pudo escribir el archivo PDF en la ruta indicada.", excepcion
            );
        }
    }

    private static List<String[]> calcularLineasDeTexto(String titulo, String subtitulo, List<Seccion> secciones) {
        List<String[]> lineas = new ArrayList<>();
        lineas.add(new String[]{titulo, String.valueOf(TAMANO_TITULO), String.valueOf(ALTO_LINEA_TITULO), "true"});
        if (subtitulo != null && !subtitulo.isBlank()) {
            lineas.add(new String[]{
                    subtitulo, String.valueOf(TAMANO_SUBTITULO), String.valueOf(ALTO_LINEA_SUBTITULO), "false"
            });
        }

        for (Seccion seccion : secciones) {
            agregarSeccion(lineas, seccion);
        }

        return lineas;
    }

    private static void agregarSeccion(List<String[]> lineas, Seccion seccion) {
        Tabla tabla = seccion.tabla;
        float[] anchosColumna = calcularAnchosColumna(tabla.encabezados.size());

        lineas.add(new String[]{
                "\u0000ESPACIO", String.valueOf(ESPACIO_ANTES_TABLA), String.valueOf(ESPACIO_ANTES_TABLA), "false"
        });
        if (seccion.subtituloSeccion != null && !seccion.subtituloSeccion.isBlank()) {
            lineas.add(new String[]{
                    seccion.subtituloSeccion, String.valueOf(TAMANO_SUBTITULO_SECCION),
                    String.valueOf(ALTO_LINEA_SUBTITULO_SECCION), "true"
            });
        }
        lineas.add(new String[]{
                "\u0000FILA:" + codificarFila(tabla.encabezados, anchosColumna),
                String.valueOf(TAMANO_ENCABEZADO_TABLA), String.valueOf(ALTO_LINEA_FILA), "true"
        });

        for (List<String> fila : tabla.filas) {
            lineas.add(new String[]{
                    "\u0000FILA:" + codificarFila(fila, anchosColumna),
                    String.valueOf(TAMANO_CELDA), String.valueOf(ALTO_LINEA_FILA), "false"
            });
        }

        if (tabla.filas.isEmpty()) {
            lineas.add(new String[]{
                    "(Sin registros para mostrar)",
                    String.valueOf(TAMANO_CELDA), String.valueOf(ALTO_LINEA_FILA), "false"
            });
        }
    }

    private static float[] calcularAnchosColumna(int cantidadColumnas) {
        float anchoDisponible = ANCHO_PAGINA - MARGEN_IZQUIERDO - MARGEN_DERECHO;
        float anchoPorColumna = anchoDisponible / cantidadColumnas;
        float[] anchos = new float[cantidadColumnas];
        for (int i = 0; i < cantidadColumnas; i++) {
            anchos[i] = anchoPorColumna;
        }
        return anchos;
    }

    private static String codificarFila(List<String> valores, float[] anchosColumna) {
        StringBuilder constructor = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) {
                constructor.append('\u0001');
            }
            constructor.append(anchosColumna[i]).append('\u0002').append(valorSeguro(valores.get(i)));
        }
        return constructor.toString();
    }

    private static String valorSeguro(String valor) {
        return valor == null ? "" : valor;
    }

    private static List<List<String[]>> paginar(List<String[]> lineas) {
        List<List<String[]>> paginas = new ArrayList<>();
        List<String[]> paginaActual = new ArrayList<>();
        float altoUsado = 0f;
        float altoDisponible = ALTO_PAGINA - MARGEN_SUPERIOR - MARGEN_INFERIOR;

        String[] lineaEncabezadoTabla = null;

        for (String[] linea : lineas) {
            float altoLinea = Float.parseFloat(linea[2]);
            boolean esFilaEncabezado = linea[0].startsWith("\u0000FILA:") && "true".equals(linea[3])
                    && lineaEncabezadoTabla == null;

            if (altoUsado + altoLinea > altoDisponible && !paginaActual.isEmpty()) {
                paginas.add(paginaActual);
                paginaActual = new ArrayList<>();
                altoUsado = 0f;
                // Repite el encabezado de tabla al inicio de la pagina
                if (lineaEncabezadoTabla != null) {
                    paginaActual.add(lineaEncabezadoTabla);
                    altoUsado += Float.parseFloat(lineaEncabezadoTabla[2]);
                }
            }

            paginaActual.add(linea);
            altoUsado += altoLinea;

            if (esFilaEncabezado) {
                lineaEncabezadoTabla = linea;
            }
        }

        if (!paginaActual.isEmpty()) {
            paginas.add(paginaActual);
        }
        if (paginas.isEmpty()) {
            paginas.add(new ArrayList<>());
        }
        return paginas;
    }

    private static byte[] construirDocumento(List<List<String[]>> paginas) {
        List<byte[]> objetos = new ArrayList<>();
        // El objeto 1 (Catalog) y el 2 (Pages) se agregan al final,
        objetos.add(null); // 1: Catalog (se completa mas abajo)
        objetos.add(null); // 2: Pages (se completa mas abajo)
        objetos.add(objetoFuente(3)); // 3: fuente normal (Helvetica)
        objetos.add(objetoFuenteNegrita(4)); // 4: fuente en negrita (Helvetica-Bold)

        List<Integer> numerosObjetoPagina = new ArrayList<>();
        List<Integer> numerosObjetoContenido = new ArrayList<>();
        int siguienteNumeroObjeto = 5;
        for (List<String[]> pagina : paginas) {
            numerosObjetoPagina.add(siguienteNumeroObjeto);
            siguienteNumeroObjeto++;
            numerosObjetoContenido.add(siguienteNumeroObjeto);
            siguienteNumeroObjeto++;
        }

        for (int i = 0; i < paginas.size(); i++) {
            int numeroObjetoPagina = numerosObjetoPagina.get(i);
            int numeroObjetoContenido = numerosObjetoContenido.get(i);
            String streamContenido = construirStreamPagina(paginas.get(i));
            objetos.add(objetoPagina(numeroObjetoPagina, numeroObjetoContenido));
            objetos.add(objetoContenido(numeroObjetoContenido, streamContenido));
        }

        objetos.set(0, objetoCatalog(1, 2));
        objetos.set(1, objetoPages(2, numerosObjetoPagina));

        return ensamblarPdf(objetos);
    }

    private static byte[] objetoCatalog(int numero, int numeroObjetoPages) {
        return objetoTexto(numero, "<< /Type /Catalog /Pages " + numeroObjetoPages + " 0 R >>");
    }

    private static byte[] objetoPages(int numero, List<Integer> numerosObjetoPagina) {
        StringBuilder listaKids = new StringBuilder();
        for (int numeroPagina : numerosObjetoPagina) {
            listaKids.append(numeroPagina).append(" 0 R ");
        }
        return objetoTexto(numero, String.format(
                java.util.Locale.ROOT, "<< /Type /Pages /Kids [ %s] /Count %d >>", listaKids, numerosObjetoPagina.size()
        ));
    }

    private static byte[] objetoPagina(int numero, int numeroObjetoContenido) {
        return objetoTexto(numero, String.format(
                java.util.Locale.ROOT,
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 %.0f %.0f] "
                        + "/Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /Contents %d 0 R >>",
                ANCHO_PAGINA, ALTO_PAGINA, numeroObjetoContenido
        ));
    }

    private static byte[] objetoFuente(int numero) {
        return objetoTexto(
                numero,
                "<< /Type /Font /Subtype /Type1 "
                        + "/BaseFont /Helvetica "
                        + "/Encoding /WinAnsiEncoding >>"
        );
    }

    private static byte[] objetoFuenteNegrita(int numero) {
        return objetoTexto(
                numero,
                "<< /Type /Font /Subtype /Type1 "
                        + "/BaseFont /Helvetica-Bold "
                        + "/Encoding /WinAnsiEncoding >>"
        );
    }

    private static byte[] objetoContenido(int numero, String streamContenido) {
        byte[] bytesStream = streamContenido.getBytes(StandardCharsets.ISO_8859_1);
        String encabezado = numero + " 0 obj\n<< /Length " + bytesStream.length + " >>\nstream\n";
        String pie = "\nendstream\nendobj\n";
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        escribirAscii(buffer, encabezado);
        escribirBytes(buffer, bytesStream);
        escribirAscii(buffer, pie);
        return buffer.toByteArray();
    }

    private static byte[] objetoTexto(int numero, String cuerpo) {
        return (numero + " 0 obj\n" + cuerpo + "\nendobj\n").getBytes(StandardCharsets.ISO_8859_1);
    }

    private static String construirStreamPagina(List<String[]> lineasPagina) {
        StringBuilder stream = new StringBuilder();
        float y = ALTO_PAGINA - MARGEN_SUPERIOR;

        for (String[] linea : lineasPagina) {
            float tamanoFuente = Float.parseFloat(linea[1]);
            float altoLinea = Float.parseFloat(linea[2]);
            boolean negrita = "true".equals(linea[3]);
            y -= altoLinea;

            if (linea[0].equals("\u0000ESPACIO")) {
                continue;
            }
            if (linea[0].startsWith("\u0000FILA:")) {
                escribirFila(stream, linea[0].substring("\u0000FILA:".length()), y, tamanoFuente, negrita);
                continue;
            }
            escribirTexto(stream, linea[0], MARGEN_IZQUIERDO, y, tamanoFuente, negrita);
        }

        return stream.toString();
    }

    private static void escribirFila(StringBuilder stream, String filaCodificada, float y, float tamanoFuente, boolean negrita) {
        String[] celdas = filaCodificada.split("\u0001");
        float x = MARGEN_IZQUIERDO;
        for (String celda : celdas) {
            int separador = celda.indexOf('\u0002');
            float anchoColumna = Float.parseFloat(celda.substring(0, separador));
            String texto = celda.substring(separador + 1);
            escribirTexto(stream, recortarSiNecesario(texto, anchoColumna, tamanoFuente), x, y, tamanoFuente, negrita);
            x += anchoColumna;
        }
    }

    private static String recortarSiNecesario(String texto, float anchoColumnaDisponible, float tamanoFuente) {
        float anchoPromedioCaracter = tamanoFuente * 0.52f;
        int maximoCaracteres = Math.max(1, (int) ((anchoColumnaDisponible - 6f) / anchoPromedioCaracter));
        if (texto.length() <= maximoCaracteres) {
            return texto;
        }
        if (maximoCaracteres <= 3) {
            return texto.substring(0, maximoCaracteres);
        }
        return texto.substring(0, maximoCaracteres - 3) + "...";
    }

    private static void escribirTexto(StringBuilder stream, String texto, float x, float y, float tamanoFuente, boolean negrita) {
        String fuente = negrita ? "/F2" : "/F1";
        stream.append("BT\n")
                .append(fuente).append(' ').append(tamanoFuente).append(" Tf\n")
                .append(x).append(' ').append(y).append(" Td\n")
                .append('(').append(escaparTextoPdf(texto)).append(") Tj\n")
                .append("ET\n");
    }

    private static String escaparTextoPdf(String texto) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            if (caracter == '(' || caracter == ')' || caracter == '\\') {
                resultado.append('\\').append(caracter);
            } else if (caracter > 0xFF) {
                resultado.append(reemplazoAcentoLatin1(caracter));
            } else {
                resultado.append(caracter);
            }
        }
        return resultado.toString();
    }

    private static char reemplazoAcentoLatin1(char caracter) {
        for (EscapeUnicode escape : EscapeUnicode.values()) {
            if (escape.coincideCon(caracter)) {
                return escape.reemplazo;
            }
        }
        return '?';
    }

    private enum EscapeUnicode {
        COMILLA_SIMPLE('\'', '\u2018', '\u2019'),
        COMILLA_DOBLE('"', '\u201C', '\u201D'),
        GUION('-', '\u2013', '\u2014');

        private final char reemplazo;
        private final char[] origenes;

        EscapeUnicode(char reemplazo, char... origenes) {
            this.reemplazo = reemplazo;
            this.origenes = origenes;
        }

        private boolean coincideCon(char caracter) {
            for (char origen : origenes) {
                if (origen == caracter) {
                    return true;
                }
            }
            return false;
        }
    }

    private static void escribirAscii(ByteArrayOutputStream buffer, String texto) {
        escribirBytes(buffer, texto.getBytes(StandardCharsets.ISO_8859_1));
    }

    private static void escribirBytes(ByteArrayOutputStream buffer, byte[] bytes) {
        buffer.write(bytes, 0, bytes.length);
    }

    private static byte[] ensamblarPdf(List<byte[]> objetos) {
        ByteArrayOutputStream documento = new ByteArrayOutputStream();
        escribirAscii(documento, "%PDF-1.4\n");

        List<Integer> offsets = new ArrayList<>();
        for (byte[] objeto : objetos) {
            offsets.add(documento.size());
            escribirBytes(documento, objeto);
        }

        int offsetXref = documento.size();
        int totalObjetos = objetos.size() + 1; // +1 por el objeto 0 reservado del formato PDF.
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(totalObjetos).append('\n');
        xref.append("0000000000 65535 f \n");
        for (int offset : offsets) {
            xref.append(String.format(java.util.Locale.ROOT, "%010d 00000 n \n", offset));
        }
        escribirAscii(documento, xref.toString());

        escribirAscii(documento, String.format(
                java.util.Locale.ROOT,
                "trailer\n<< /Size %d /Root 1 0 R >>\nstartxref\n%d\n%%%%EOF",
                totalObjetos, offsetXref
        ));

        return documento.toByteArray();
    }
}
