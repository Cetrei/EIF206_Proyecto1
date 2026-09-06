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
     * el "cromo" que ningun componente propio del sistema de diseno
     * cubre todavia: el borde del JTabbedPane y el fondo/borde de cada
     * pestana (activa e inactivas). Los botones (BotonPrimario,
     * BotonSecundario, BotonIcono) ya se pintan a mano y no dependen
     * de esto; el JTabbedPane si usa el Look and Feel por defecto
     * (Metal), que no respeta setBackground/setForeground para el area
     * de las pestanas, de ahi que se viera con bordes grises sin
     * relacion con el tema oscuro/claro.
     * <p>
     * Debe llamarse una vez al inicio (ver Main.iniciar) y de nuevo
     * cada vez que cambia el tema, ANTES de reconstruir/repintar la UI
     * que use JTabbedPane, para que tome los valores nuevos.
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
        // Insets mas bajos que el default de Metal, para que las
        // pestanas se vean delgadas como en el prototipo (ver captura
        // de referencia "Funcionarios (Administrador)" del enunciado)
        // en vez de infladas verticalmente.
        UIManager.put("TabbedPane.tabInsets", new java.awt.Insets(5, 12, 5, 12));
        UIManager.put("TabbedPane.contentBorderInsets", new java.awt.Insets(2, 0, 0, 0));
        UIManager.put("TabbedPane.font", tema.fuenteTexto());

        // JScrollBar y JTable/JViewport tampoco tienen un componente
        // propio en el sistema de diseno que los pinte a mano (a
        // diferencia de los botones): sin esto, el thumb/track del
        // scroll y el borde de encabezado de tabla quedan con el gris
        // claro/blanco por defecto de Metal, sin relacion con el tema.
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

        // Metal (el Look and Feel por defecto cuando no se llama a
        // UIManager.setLookAndFeel) tambien deja un borde propio, de 1
        // pixel gris, en JPanel/JButton via "Panel.border"/"Button.border"
        // en ciertas combinaciones; se limpia aqui para que el unico
        // borde visible sea el que cada componente del sistema de
        // diseno pinta a mano.
        UIManager.put("Panel.border", new EmptyBorder(0, 0, 0, 0));
    }
}
