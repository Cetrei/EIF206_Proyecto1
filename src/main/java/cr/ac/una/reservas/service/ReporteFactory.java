package cr.ac.una.reservas.service;

import cr.ac.una.reservas.report.GeneradorReporte;
import cr.ac.una.reservas.report.ReporteActividades;
import cr.ac.una.reservas.report.ReporteCalendarizacion;
import cr.ac.una.reservas.report.ReporteCategorias;
import cr.ac.una.reservas.report.ReporteEstadisticas;
import cr.ac.una.reservas.report.ReporteFuncionarios;
import cr.ac.una.reservas.report.ReporteRecursos;
import cr.ac.una.reservas.report.ReporteReservas;

public final class ReporteFactory {
    public enum TipoReporte {
        FUNCIONARIOS,
        CATEGORIAS,
        RECURSOS,
        RESERVAS,
        CALENDARIZACION,
        ACTIVIDADES,
        ESTADISTICAS
    }

    private ReporteFactory() {
    }

    public static GeneradorReporte<?> obtenerGenerador(TipoReporte tipoReporte) {
        switch (tipoReporte) {
            case FUNCIONARIOS:
                return new ReporteFuncionarios();
            case CATEGORIAS:
                return new ReporteCategorias();
            case RECURSOS:
                return new ReporteRecursos();
            case RESERVAS:
                return new ReporteReservas();
            case CALENDARIZACION:
                return new ReporteCalendarizacion();
            case ACTIVIDADES:
                return new ReporteActividades();
            case ESTADISTICAS:
                return new ReporteEstadisticas();
            default:
                throw new IllegalStateException(
                        "Todavía no existe un generador de reporte para " + tipoReporte + "."
                );
        }
    }
}
