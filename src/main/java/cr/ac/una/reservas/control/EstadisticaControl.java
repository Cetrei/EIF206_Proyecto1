package cr.ac.una.reservas.control;

import cr.ac.una.reservas.model.EstadisticaCategoria;
import cr.ac.una.reservas.model.EstadisticaSemana;
import cr.ac.una.reservas.presentation.TabEstadisticas;
import cr.ac.una.reservas.presentation.componentes.Popup;
import cr.ac.una.reservas.report.DatosReporteEstadisticas;
import cr.ac.una.reservas.service.EstadisticaService;
import cr.ac.una.reservas.service.ReporteFactory;
import cr.ac.una.reservas.util.ReservaAppException;

import java.awt.Frame;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EstadisticaControl {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT);

    private final TabEstadisticas vista;
    private final Frame ventanaPropietaria;
    private final EstadisticaService estadisticaService;

    private List<EstadisticaCategoria> ultimosRecursos = List.of();
    private String ultimoSubtituloRecursos = "";
    private List<EstadisticaSemana> ultimasActividades = List.of();
    private String ultimoSubtituloActividades = "";

    public EstadisticaControl(TabEstadisticas vista, Frame ventanaPropietaria) {
        this(vista, ventanaPropietaria, new EstadisticaService());
    }

    // Constructor para pruebas: permite inyectar un EstadisticaService
    // construido con Dao falsos en vez del real de DaoFactory.
    public EstadisticaControl(TabEstadisticas vista, Frame ventanaPropietaria, EstadisticaService estadisticaService) {
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.estadisticaService = estadisticaService;

        this.vista.obtenerPanelRecursos().alCargar(this::cargarRecursos);
        this.vista.obtenerPanelActividades().alCargar(this::cargarActividades);
        this.vista.alGenerarReporte(this::generarReporte);

        cargarRecursos(vista.obtenerPanelRecursos().obtenerFechaDesde(), vista.obtenerPanelRecursos().obtenerFechaHasta());
        cargarActividades(vista.obtenerPanelActividades().obtenerFechaDesde(), vista.obtenerPanelActividades().obtenerFechaHasta());
    }

    private void cargarRecursos(LocalDate desde, LocalDate hasta) {
        try {
            ultimosRecursos = estadisticaService.recursosReservadosEnPeriodo(desde, hasta);
            ultimoSubtituloRecursos = descripcionPeriodo(desde, hasta);
            vista.obtenerPanelRecursos().mostrarDatos(ultimosRecursos);
        } catch (ReservaAppException excepcion) {
            Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo cargar", excepcion.getMessage());
        }
    }

    private void cargarActividades(LocalDate desde, LocalDate hasta) {
        try {
            ultimasActividades = estadisticaService.actividadesPorSemanaEnPeriodo(desde, hasta);
            ultimoSubtituloActividades = descripcionPeriodo(desde, hasta);
            vista.obtenerPanelActividades().mostrarDatos(ultimasActividades);
        } catch (ReservaAppException excepcion) {
            Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo cargar", excepcion.getMessage());
        }
    }

    // Un unico DatosReporteEstadisticas porque el reporte necesita ambas listas juntas para sus dos tablas.
    private void generarReporte() {
        DatosReporteEstadisticas datosReporte = new DatosReporteEstadisticas(
                ultimosRecursos, ultimoSubtituloRecursos,
                ultimasActividades, ultimoSubtituloActividades
        );

        GeneracionReporteControl.generarYGuardar(
                ventanaPropietaria,
                ReporteFactory.TipoReporte.ESTADISTICAS,
                "estadisticas",
                List.of(datosReporte),
                Map.of()
        );
    }

    private static String descripcionPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            return "";
        }
        return "Del " + desde.format(FORMATO_FECHA) + " al " + hasta.format(FORMATO_FECHA);
    }
}
