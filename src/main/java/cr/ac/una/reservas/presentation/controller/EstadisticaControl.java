package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.presentation.mvc.TabEstadisticas;
import cr.ac.una.reservas.presentation.mvc.componentes.Popup;
import cr.ac.una.reservas.presentation.model.EstadisticaModel;
import cr.ac.una.reservas.report.DatosReporteEstadisticas;
import cr.ac.una.reservas.service.EstadisticaService;
import cr.ac.una.reservas.service.ReporteFactory;
import cr.ac.una.reservas.service.ServiceFactory;
import cr.ac.una.reservas.util.ReservaAppException;

import java.awt.Frame;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EstadisticaControl {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT);

    private final EstadisticaModel modelo;
    private final TabEstadisticas vista;
    private final Frame ventanaPropietaria;
    private final EstadisticaService estadisticaService;

    public EstadisticaControl(EstadisticaModel modelo, TabEstadisticas vista, Frame ventanaPropietaria) {
        this(modelo, vista, ventanaPropietaria, ServiceFactory.obtenerEstadisticaService());
    }

    public EstadisticaControl(
            EstadisticaModel modelo, TabEstadisticas vista, Frame ventanaPropietaria, EstadisticaService estadisticaService
    ) {
        this.modelo = modelo;
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
        if (desde == null || hasta == null) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.ERROR,
                    "No se pudo cargar",
                    "Debe seleccionar fechas validas para consultar los recursos."
            );
            return;
        }
        try {
            modelo.setRecursos(estadisticaService.recursosReservadosEnPeriodo(desde, hasta));
            modelo.setSubtituloRecursos(descripcionPeriodo(desde, hasta));
        } catch (ReservaAppException excepcion) {
            Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo cargar", excepcion.getMessage());
        } catch (RuntimeException excepcion) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.ERROR,
                    "No se pudo cargar",
                    "No fue posible consultar los recursos en ese periodo."
            );
        }
    }

    private void cargarActividades(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.ERROR,
                    "No se pudo cargar",
                    "Debe seleccionar fechas validas para consultar las actividades."
            );
            return;
        }
        try {
            modelo.setActividades(estadisticaService.actividadesPorSemanaEnPeriodo(desde, hasta));
            modelo.setSubtituloActividades(descripcionPeriodo(desde, hasta));
        } catch (ReservaAppException excepcion) {
            Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo cargar", excepcion.getMessage());
        } catch (RuntimeException excepcion) {
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.ERROR,
                    "No se pudo cargar",
                    "No fue posible consultar las actividades en ese periodo."
            );
        }
    }

    private void generarReporte() {
        DatosReporteEstadisticas datosReporte = new DatosReporteEstadisticas(
                modelo.getRecursos(), modelo.getSubtituloRecursos(),
                modelo.getActividades(), modelo.getSubtituloActividades()
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
