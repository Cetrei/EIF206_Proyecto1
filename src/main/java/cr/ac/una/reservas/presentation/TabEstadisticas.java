package cr.ac.una.reservas.presentation;

import cr.ac.una.reservas.model.EstadisticaCategoria;
import cr.ac.una.reservas.model.EstadisticaSemana;
import cr.ac.una.reservas.presentation.componentes.BotonSecundario;
import cr.ac.una.reservas.presentation.componentes.GraficoBarras;
import cr.ac.una.reservas.presentation.componentes.PanelEstadistica;
import cr.ac.una.reservas.presentation.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.JPanel;
import java.util.List;

public class TabEstadisticas implements CambioTemaListener {
    private JPanel panelRaiz;
    private JPanel TarjetaTitulo;
    private JPanel PanelRecursos;
    private JPanel PanelActividades;

    private Tarjeta tarjetaTituloReal;
    private BotonSecundario botonReporteReal;

    private PanelEstadistica<EstadisticaCategoria> panelRecursosReal;
    private PanelEstadistica<EstadisticaSemana> panelActividadesReal;

    public TabEstadisticas() {
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
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (panelRaiz == null) return;

        panelRaiz.setOpaque(true);
        panelRaiz.setBackground(tema.colorFondoVentana());
        panelRaiz.repaint();
    }
}
