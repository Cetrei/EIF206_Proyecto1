package cr.ac.una.reservas.presentation.mvc.componentes;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.text.DateFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class EditorFechaFlexible {

    public static final String PATRON_MOSTRADO = "dd/MM/yyyy";

    private static final String[] PATRONES_ACEPTADOS = {
            "dd/MM/yy",
            "d/M/yy",
            "dd/MM/yyyy",
            "d/M/yyyy",
            "yyyy-MM-dd"
    };

    private EditorFechaFlexible() {
    }

    public static void aplicar(JSpinner spinner) {
        if (spinner == null) return;

        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, PATRON_MOSTRADO);
        JFormattedTextField campoTexto = editor.getTextField();
        FormatterFechaTolerante formatterTolerante = new FormatterFechaTolerante();
        formatterTolerante.setFormat(new SimpleDateFormat(PATRON_MOSTRADO, Locale.forLanguageTag("es-CR")));
        campoTexto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(formatterTolerante));
        spinner.setEditor(editor);
    }

    public static void confirmarEdicion(JSpinner spinner) {
        if (spinner == null) return;
        if (!(spinner.getEditor() instanceof JSpinner.DefaultEditor)) return;

        JFormattedTextField campoTexto = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        try {
            campoTexto.commitEdit();
        } catch (ParseException excepcion) {
            try {
                campoTexto.setText(campoTexto.getFormatter().valueToString(campoTexto.getValue()));
            } catch (ParseException excepcionFormato) {
                campoTexto.setValue(campoTexto.getValue());
            }
        }
    }

    private static final class FormatterFechaTolerante extends DateFormatter {

        private FormatterFechaTolerante() {
            setOverwriteMode(false);
            setAllowsInvalid(true);
            setCommitsOnValidEdit(false);
        }

        @Override
        public Object stringToValue(String texto) throws ParseException {
            if (texto == null || texto.trim().isEmpty()) {
                throw new ParseException("Fecha vacia.", 0);
            }

            String textoNormalizado = texto.trim();
            for (String patron : PATRONES_ACEPTADOS) {
                Date fecha = intentarParsear(textoNormalizado, patron);
                if (fecha != null) {
                    return fecha;
                }
            }
            throw new ParseException("No se pudo interpretar la fecha \"" + texto + "\".", 0);
        }

        @Override
        public String valueToString(Object valor) throws ParseException {
            if (!(valor instanceof Date)) {
                return "";
            }
            SimpleDateFormat formatoSalida = new SimpleDateFormat(PATRON_MOSTRADO, Locale.forLanguageTag("es-CR"));
            return formatoSalida.format((Date) valor);
        }

        private Date intentarParsear(String texto, String patron) {
            SimpleDateFormat formato = new SimpleDateFormat(patron, Locale.forLanguageTag("es-CR"));
            formato.setLenient(false);
            if (patron.contains("yy") && !patron.contains("yyyy")) {
                Calendar pivote = Calendar.getInstance();
                pivote.setTime(new Date());
                pivote.add(Calendar.YEAR, -80);
                formato.set2DigitYearStart(pivote.getTime());
            }
            try {
                Date fecha = formato.parse(texto);
                return fecha;
            } catch (ParseException excepcion) {
                return null;
            }
        }
    }
}
