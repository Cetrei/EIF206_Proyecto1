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

    private static final float ALTO_GRAFICO = 150f;
    private static final float MARGEN_INFERIOR_GRAFICO = 26f;
    private static final float MARGEN_SUPERIOR_GRAFICO = 14f;
    private static final float TAMANO_ETIQUETA_GRAFICO = 8.5f;
    private static final float TAMANO_VALOR_GRAFICO = 8.5f;
    private static final float FACTOR_RELLENO_BARRA = 0.6f;
    private static final float COLOR_BARRA_R = 0.30f;
    private static final float COLOR_BARRA_G = 0.45f;
    private static final float COLOR_BARRA_B = 0.85f;

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

    public static final class Grafico {
        private final List<String> etiquetas;
        private final List<Double> valores;

        public Grafico(List<String> etiquetas, List<Double> valores) {
            this.etiquetas = etiquetas;
            this.valores = valores;
        }
    }

    public static void generar(String rutaDestino, String titulo, String subtitulo, Tabla tabla) {
        generar(rutaDestino, titulo, subtitulo, List.of(new Seccion(null, tabla, null)));
    }

    public static final class Seccion {
        private final String subtituloSeccion;
        private final Tabla tabla;
        private final Grafico grafico;

        public Seccion(String subtituloSeccion, Tabla tabla) {
            this(subtituloSeccion, tabla, null);
        }

        public Seccion(String subtituloSeccion, Tabla tabla, Grafico grafico) {
            this.subtituloSeccion = subtituloSeccion;
            this.tabla = tabla;
            this.grafico = grafico;
        }
    }

    public static void generar(String rutaDestino, String titulo, String subtitulo, List<Seccion> secciones) {
        List<ElementoPagina> elementos = calcularElementos(titulo, subtitulo, secciones);
        List<List<ElementoPagina>> paginas = paginar(elementos);

        byte[] contenidoPdf = construirDocumento(paginas);

        try (OutputStream salida = new FileOutputStream(rutaDestino)) {
            salida.write(contenidoPdf);
        } catch (IOException excepcion) {
            throw new PersistenciaException(
                    "No se pudo escribir el archivo PDF en la ruta indicada.", excepcion
            );
        }
    }

    private static List<ElementoPagina> calcularElementos(String titulo, String subtitulo, List<Seccion> secciones) {
        List<ElementoPagina> elementos = new ArrayList<>();
        elementos.add(ElementoLinea.texto(titulo, TAMANO_TITULO, ALTO_LINEA_TITULO, true));
        if (subtitulo != null && !subtitulo.isBlank()) {
            elementos.add(ElementoLinea.texto(subtitulo, TAMANO_SUBTITULO, ALTO_LINEA_SUBTITULO, false));
        }

        for (Seccion seccion : secciones) {
            agregarSeccion(elementos, seccion);
        }

        return elementos;
    }

    private static void agregarSeccion(List<ElementoPagina> elementos, Seccion seccion) {
        Tabla tabla = seccion.tabla;
        float[] anchosColumna = calcularAnchosColumna(tabla.encabezados.size());

        elementos.add(ElementoLinea.espacio(ESPACIO_ANTES_TABLA));
        if (seccion.subtituloSeccion != null && !seccion.subtituloSeccion.isBlank()) {
            elementos.add(ElementoLinea.texto(
                    seccion.subtituloSeccion, TAMANO_SUBTITULO_SECCION, ALTO_LINEA_SUBTITULO_SECCION, true
            ));
        }
        elementos.add(ElementoLinea.fila(
                codificarFila(tabla.encabezados, anchosColumna), TAMANO_ENCABEZADO_TABLA, ALTO_LINEA_FILA, true
        ));

        for (List<String> fila : tabla.filas) {
            elementos.add(ElementoLinea.fila(
                    codificarFila(fila, anchosColumna), TAMANO_CELDA, ALTO_LINEA_FILA, false
            ));
        }

        if (tabla.filas.isEmpty()) {
            elementos.add(ElementoLinea.texto(
                    "(Sin registros para mostrar)", TAMANO_CELDA, ALTO_LINEA_FILA, false
            ));
        }

        if (seccion.grafico != null && !seccion.grafico.etiquetas.isEmpty()) {
            elementos.add(ElementoLinea.espacio(ESPACIO_ANTES_TABLA));
            elementos.add(new ElementoGrafico(seccion.grafico));
        }
    }

    private interface ElementoPagina {
        float altura();
    }

    private static final class ElementoLinea implements ElementoPagina {
        private final String contenido;
        private final float tamanoFuente;
        private final float altoLinea;
        private final boolean negrita;
        private final boolean esEspacio;
        private final boolean esFila;

        private ElementoLinea(String contenido, float tamanoFuente, float altoLinea, boolean negrita,
                               boolean esEspacio, boolean esFila) {
            this.contenido = contenido;
            this.tamanoFuente = tamanoFuente;
            this.altoLinea = altoLinea;
            this.negrita = negrita;
            this.esEspacio = esEspacio;
            this.esFila = esFila;
        }

        private static ElementoLinea texto(String contenido, float tamanoFuente, float altoLinea, boolean negrita) {
            return new ElementoLinea(contenido, tamanoFuente, altoLinea, negrita, false, false);
        }

        private static ElementoLinea fila(String filaCodificada, float tamanoFuente, float altoLinea, boolean negrita) {
            return new ElementoLinea(filaCodificada, tamanoFuente, altoLinea, negrita, false, true);
        }

        private static ElementoLinea espacio(float altoLinea) {
            return new ElementoLinea(null, altoLinea, altoLinea, false, true, false);
        }

        @Override
        public float altura() {
            return altoLinea;
        }
    }

    private static final class ElementoGrafico implements ElementoPagina {
        private final Grafico grafico;

        private ElementoGrafico(Grafico grafico) {
            this.grafico = grafico;
        }

        @Override
        public float altura() {
            return ALTO_GRAFICO;
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

    private static List<List<ElementoPagina>> paginar(List<ElementoPagina> elementos) {
        List<List<ElementoPagina>> paginas = new ArrayList<>();
        List<ElementoPagina> paginaActual = new ArrayList<>();
        float altoUsado = 0f;
        float altoDisponible = ALTO_PAGINA - MARGEN_SUPERIOR - MARGEN_INFERIOR;

        ElementoLinea lineaEncabezadoTabla = null;

        for (ElementoPagina elemento : elementos) {
            float altura = elemento.altura();
            boolean esFilaEncabezado = elemento instanceof ElementoLinea
                    && ((ElementoLinea) elemento).esFila
                    && ((ElementoLinea) elemento).negrita
                    && lineaEncabezadoTabla == null;

            if (altoUsado + altura > altoDisponible && !paginaActual.isEmpty()) {
                paginas.add(paginaActual);
                paginaActual = new ArrayList<>();
                altoUsado = 0f;
                if (lineaEncabezadoTabla != null) {
                    paginaActual.add(lineaEncabezadoTabla);
                    altoUsado += lineaEncabezadoTabla.altura();
                }
            }

            paginaActual.add(elemento);
            altoUsado += altura;

            if (esFilaEncabezado) {
                lineaEncabezadoTabla = (ElementoLinea) elemento;
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

    private static byte[] construirDocumento(List<List<ElementoPagina>> paginas) {
        List<byte[]> objetos = new ArrayList<>();
        objetos.add(null);
        objetos.add(null);
        objetos.add(objetoFuente(3));
        objetos.add(objetoFuenteNegrita(4));

        List<Integer> numerosObjetoPagina = new ArrayList<>();
        List<Integer> numerosObjetoContenido = new ArrayList<>();
        int siguienteNumeroObjeto = 5;
        for (List<ElementoPagina> pagina : paginas) {
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

    private static String construirStreamPagina(List<ElementoPagina> paginaElementos) {
        StringBuilder stream = new StringBuilder();
        float y = ALTO_PAGINA - MARGEN_SUPERIOR;

        for (ElementoPagina elemento : paginaElementos) {
            y -= elemento.altura();

            if (elemento instanceof ElementoGrafico) {
                escribirGrafico(stream, ((ElementoGrafico) elemento).grafico, y);
                continue;
            }

            ElementoLinea linea = (ElementoLinea) elemento;
            if (linea.esEspacio) {
                continue;
            }
            if (linea.esFila) {
                escribirFila(stream, linea.contenido, y, linea.tamanoFuente, linea.negrita);
                continue;
            }
            escribirTexto(stream, linea.contenido, MARGEN_IZQUIERDO, y, linea.tamanoFuente, linea.negrita);
        }

        return stream.toString();
    }

    private static void escribirGrafico(StringBuilder stream, Grafico grafico, float yBaseBloque) {
        int cantidadBarras = grafico.etiquetas.size();
        float anchoDisponible = ANCHO_PAGINA - MARGEN_IZQUIERDO - MARGEN_DERECHO;
        float anchoBarra = anchoDisponible / cantidadBarras;
        float alturaDisponible = ALTO_GRAFICO - MARGEN_SUPERIOR_GRAFICO - MARGEN_INFERIOR_GRAFICO;
        float yLineaBase = yBaseBloque + MARGEN_INFERIOR_GRAFICO;

        double valorMaximo = 0;
        for (Double valor : grafico.valores) {
            valorMaximo = Math.max(valorMaximo, valor);
        }
        if (valorMaximo <= 0) {
            valorMaximo = 1;
        }

        escribirLineaBase(stream, MARGEN_IZQUIERDO, yLineaBase, ANCHO_PAGINA - MARGEN_DERECHO);

        for (int indice = 0; indice < cantidadBarras; indice++) {
            double valor = grafico.valores.get(indice);
            float alturaBarra = (float) ((valor / valorMaximo) * alturaDisponible);
            float anchoDibujado = anchoBarra * FACTOR_RELLENO_BARRA;
            float x = MARGEN_IZQUIERDO + indice * anchoBarra + (anchoBarra - anchoDibujado) / 2f;

            escribirRectangulo(stream, x, yLineaBase, anchoDibujado, alturaBarra);

            String etiqueta = recortarSiNecesario(grafico.etiquetas.get(indice), anchoBarra, TAMANO_ETIQUETA_GRAFICO);
            escribirTexto(
                    stream, etiqueta, MARGEN_IZQUIERDO + indice * anchoBarra + 2f,
                    yLineaBase - 12f, TAMANO_ETIQUETA_GRAFICO, false
            );

            String valorTexto = formatearValorGrafico(valor);
            escribirTexto(
                    stream, valorTexto, x, yLineaBase + alturaBarra + 4f, TAMANO_VALOR_GRAFICO, true
            );
        }
    }

    private static String formatearValorGrafico(double valor) {
        if (valor == Math.floor(valor)) {
            return String.valueOf((long) valor);
        }
        return String.format(java.util.Locale.ROOT, "%.1f", valor);
    }

    private static void escribirLineaBase(StringBuilder stream, float x, float y, float xFinal) {
        stream.append("0.7 0.7 0.7 rg\n")
                .append(x).append(' ').append(y).append(' ')
                .append(xFinal - x).append(" 1 re\nf\n");
    }

    private static void escribirRectangulo(StringBuilder stream, float x, float y, float ancho, float alto) {
        stream.append(COLOR_BARRA_R).append(' ').append(COLOR_BARRA_G).append(' ').append(COLOR_BARRA_B)
                .append(" rg\n")
                .append(x).append(' ').append(y).append(' ').append(ancho).append(' ').append(alto)
                .append(" re\nf\n")
                .append("0 0 0 rg\n");
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
        int totalObjetos = objetos.size() + 1;
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
