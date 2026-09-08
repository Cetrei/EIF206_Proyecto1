package cr.ac.una.reservas.presentation.tema;

import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.List;

public final class GestorTema {
    private static final GestorTema INSTANCIA = new GestorTema();

    private Tema temaActivo = new TemaOscuro();
    private final List<CambioTemaListener> listeners = new ArrayList<>();

    private GestorTema() {
    }

    public static GestorTema obtenerInstancia() {
        return INSTANCIA;
    }

    public Tema temaActivo() {
        return temaActivo;
    }

    public void establecerTema(Tema nuevoTema) {
        this.temaActivo = nuevoTema;
        aplicarUIManager(nuevoTema);
        for (CambioTemaListener listener : listeners) {
            listener.onCambioTema(temaActivo);
        }
    }

    public void agregarListener(CambioTemaListener listener) {
        listeners.add(listener);
    }
    public void quitarListener(CambioTemaListener listener) {
        listeners.remove(listener);
    }

    /**
     * Sobreescribe las claves de UIManager que Swing usa para pintar
     */
    public static void aplicarUIManager(Tema tema) {
        UIManager.put("TabbedPane.contentAreaColor", tema.colorFondoVentana());
        UIManager.put("TabbedPane.background", tema.colorFondoVentana());
        UIManager.put("TabbedPane.foreground", tema.colorTexto());
        UIManager.put("TabbedPane.selected", tema.colorFondoTarjeta());
        UIManager.put("TabbedPane.selectHighlight", tema.colorFondoTarjeta());
        UIManager.put("TabbedPane.unselectedBackground", tema.colorFondoVentana());
        UIManager.put("TabbedPane.highlight", tema.colorBorde());
        UIManager.put("TabbedPane.light", tema.colorBorde());
        UIManager.put("TabbedPane.shadow", tema.colorBorde());
        UIManager.put("TabbedPane.darkShadow", tema.colorBorde());
        UIManager.put("TabbedPane.borderHightlightColor", tema.colorBorde());
        UIManager.put("TabbedPane.focus", tema.colorPrimario());
        UIManager.put("TabbedPane.tabAreaBackground", tema.colorFondoVentana());
        UIManager.put("TabbedPane.tabsOverlapBorder", false);
        // Insets mas bajos que el default de Metal, para que las pestanas se vean delgadas
        UIManager.put("TabbedPane.tabInsets", new java.awt.Insets(5, 12, 5, 12));
        UIManager.put("TabbedPane.contentBorderInsets", new java.awt.Insets(2, 0, 0, 0));
        UIManager.put("TabbedPane.font", tema.fuenteTexto());

        UIManager.put("ScrollBar.thumb", tema.colorSuperficie());
        UIManager.put("ScrollBar.thumbDarkShadow", tema.colorSuperficie());
        UIManager.put("ScrollBar.thumbHighlight", tema.colorSuperficie());
        UIManager.put("ScrollBar.thumbShadow", tema.colorSuperficie());
        UIManager.put("ScrollBar.track", tema.colorFondoVentana());
        UIManager.put("ScrollBar.trackHighlight", tema.colorFondoVentana());
        UIManager.put("ScrollBar.background", tema.colorFondoVentana());
        UIManager.put("ScrollBar.foreground", tema.colorTextoSecundario());
        UIManager.put("ScrollBar.width", 14);

        UIManager.put("Table.background", tema.colorFondoTarjeta());
        UIManager.put("Table.foreground", tema.colorTexto());
        UIManager.put("Table.gridColor", tema.colorBorde());
        UIManager.put("Table.selectionBackground", tema.colorPrimario());
        UIManager.put("Table.selectionForeground", tema.colorTexto());
        UIManager.put("TableHeader.background", tema.colorFondoCampo());
        UIManager.put("TableHeader.foreground", tema.colorTextoSecundario());

        UIManager.put("Viewport.background", tema.colorFondoVentana());

        // Evitaa que los looks and feel defaults dejen bordes que arruinen la interfaaz
        UIManager.put("Panel.border", new EmptyBorder(0, 0, 0, 0));
    }
}
