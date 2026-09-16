package cr.ac.una.reservas.service;

import cr.ac.una.reservas.report.GeneradorReporte;
import cr.ac.una.reservas.report.ReporteActividades;
import cr.ac.una.reservas.report.ReporteCalendarizacion;
import cr.ac.una.reservas.report.ReporteCategorias;
import cr.ac.una.reservas.report.ReporteEstadisticas;
import cr.ac.una.reservas.report.ReporteFuncionarios;
import cr.ac.una.reservas.report.ReporteRecursos;
import cr.ac.una.reservas.report.ReporteReservas;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ReporteFactoryTest {

    @Test
    void devuelveReporteDeFuncionariosParaElTipoFuncionarios() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.FUNCIONARIOS);
        assertInstanceOf(ReporteFuncionarios.class, generador);
    }

    @Test
    void devuelveReporteDeCategoriasParaElTipoCategorias() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.CATEGORIAS);
        assertInstanceOf(ReporteCategorias.class, generador);
    }

    @Test
    void devuelveReporteDeRecursosParaElTipoRecursos() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.RECURSOS);
        assertInstanceOf(ReporteRecursos.class, generador);
    }

    @Test
    void devuelveReporteDeReservasParaElTipoReservas() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.RESERVAS);
        assertInstanceOf(ReporteReservas.class, generador);
    }

    @Test
    void devuelveReporteDeCalendarizacionParaElTipoCalendarizacion() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.CALENDARIZACION);
        assertInstanceOf(ReporteCalendarizacion.class, generador);
    }

    @Test
    void devuelveReporteDeActividadesParaElTipoActividades() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.ACTIVIDADES);
        assertInstanceOf(ReporteActividades.class, generador);
    }

    @Test
    void devuelveReporteDeEstadisticasParaElTipoEstadisticas() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.ESTADISTICAS);
        assertInstanceOf(ReporteEstadisticas.class, generador);
    }

    @Test
    void cadaLlamadaDevuelveUnaInstanciaNueva() {
        GeneradorReporte<?> primera = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.CATEGORIAS);
        GeneradorReporte<?> segunda = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.CATEGORIAS);

        org.junit.jupiter.api.Assertions.assertNotSame(primera, segunda);
    }
}
