package cr.ac.una.reservas.presentation.mvc;

import cr.ac.una.reservas.model.EstadisticaCategoria;
import cr.ac.una.reservas.model.EstadisticaSemana;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.mvc.componentes.GraficoBarras;
import cr.ac.una.reservas.presentation.mvc.componentes.PanelEstadistica;
import cr.ac.una.reservas.presentation.mvc.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.mvc.iconos.Icono;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.model.EstadisticaModel;
import cr.ac.una.reservas.presentation.mvc.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.presentation.mvc.tema.Tema;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class TabEstadisticas implements CambioTemaListener, PropertyChangeListener {
    private JPanel panelRaiz;
    private JPanel TarjetaTitulo;
    private JPanel PanelRecursos;
    private JPanel PanelActividades;

    private Tarjeta tarjetaTituloReal;
    private BotonSecundario botonReporteReal;

    private PanelEstadistica<EstadisticaCategoria> panelRecursosReal;
    private PanelEstadistica<EstadisticaSemana> panelActividadesReal;

    public TabEstadisticas(EstadisticaModel modelo) {
        $$$setupUI$$$();
        modelo.addPropertyChangeListener(this);
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        armarTarjetaTitulo();
        armarPanelesEstadistica();
        TarjetaTitulo = tarjetaTituloReal.obtenerPanel();
        PanelRecursos = panelRecursosReal.obtenerPanel();
        PanelActividades = panelActividadesReal.obtenerPanel();
    }

    private void armarTarjetaTitulo() {
        tarjetaTituloReal = new Tarjeta();
        tarjetaTituloReal.setIcono(IconoSemantico.GRAFICO.icono());
        tarjetaTituloReal.setTitulo("Estadísticas y Gráficos");
        tarjetaTituloReal.setSubtitulo("Métricas acumuladas de uso de recursos y frecuencia de actividades");

        botonReporteReal = new BotonSecundario();
        botonReporteReal.setVariante(BotonSecundario.Variante.PELIGRO);
        botonReporteReal.setTexto("Generar Reporte PDF");
        botonReporteReal.setIcono(IconoSemantico.REPORTE_PDF.icono());
        tarjetaTituloReal.setAccion(botonReporteReal.obtenerPanel());
    }

    private void armarPanelesEstadistica() {
        panelRecursosReal = new PanelEstadistica<>(
                Icono.CAJA,
                "Recursos Usados por Categoría",
                "Cantidad de recursos reservados por categoría en el período consultado",
                PanelEstadistica.SerieEstadistica.PRINCIPAL
        );
        panelRecursosReal.configurar(
                List.of("Categoría", "Cantidad Reservada"),
                fila -> List.of(descripcionCategoria(fila), fila.getCantidad()),
                fila -> new GraficoBarras.EntradaGrafico(descripcionCategoria(fila), fila.getCantidad(), null)
        );

        panelActividadesReal = new PanelEstadistica<>(
                Icono.GRAFICO,
                "Actividades por Semana",
                "Cantidad de actividades programadas por semana en el período consultado",
                PanelEstadistica.SerieEstadistica.SECUNDARIA
        );
        panelActividadesReal.configurar(
                List.of("Semana (Inicio)", "Cantidad Actividades"),
                fila -> List.of(String.valueOf(fila.getInicioSemana()), fila.getCantidad()),
                fila -> new GraficoBarras.EntradaGrafico(String.valueOf(fila.getInicioSemana()), fila.getCantidad(), null)
        );
    }

    private static String descripcionCategoria(EstadisticaCategoria fila) {
        return fila.getCategoria() == null ? "" : fila.getCategoria().getDescripcion();
    }

    // ------------------------------------------------------------------
    // Datos de los filtros / contenido de cada seccion
    // ------------------------------------------------------------------

    public PanelEstadistica<EstadisticaCategoria> obtenerPanelRecursos() {
        return panelRecursosReal;
    }

    public PanelEstadistica<EstadisticaSemana> obtenerPanelActividades() {
        return panelActividadesReal;
    }

    // ------------------------------------------------------------------
    // Enganches de eventos
    // ------------------------------------------------------------------

    public void alGenerarReporte(Runnable accion) {
        botonReporteReal.alHacerClick(accion);
    }

    public JPanel obtenerPanel() {
        return panelRaiz;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evento) {
        if (EstadisticaModel.PROP_RECURSOS.equals(evento.getPropertyName())) {
            @SuppressWarnings("unchecked")
            List<EstadisticaCategoria> nuevaLista = (List<EstadisticaCategoria>) evento.getNewValue();
            panelRecursosReal.mostrarDatos(nuevaLista);
        } else if (EstadisticaModel.PROP_ACTIVIDADES.equals(evento.getPropertyName())) {
            @SuppressWarnings("unchecked")
            List<EstadisticaSemana> nuevaLista = (List<EstadisticaSemana>) evento.getNewValue();
            panelActividadesReal.mostrarDatos(nuevaLista);
        }
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (panelRaiz == null) return;

        panelRaiz.setOpaque(true);
        panelRaiz.setBackground(tema.colorFondoVentana());
        panelRaiz.repaint();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panelRaiz = new JPanel();
        panelRaiz.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(20, 20, 20, 20), 16, 16));
        panelRaiz.add(TarjetaTitulo, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelRaiz.add(PanelRecursos, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelRaiz.add(PanelActividades, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panelRaiz.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelRaiz;
    }
}
