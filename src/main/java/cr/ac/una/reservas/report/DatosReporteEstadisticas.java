package cr.ac.una.reservas.report;

import cr.ac.una.reservas.model.EstadisticaCategoria;
import cr.ac.una.reservas.model.EstadisticaSemana;

import java.util.List;

public final class DatosReporteEstadisticas {
    private final List<EstadisticaCategoria> recursosPorCategoria;
    private final String periodoRecursos;
    private final List<EstadisticaSemana> actividadesPorSemana;
    private final String periodoActividades;

    public DatosReporteEstadisticas(
            List<EstadisticaCategoria> recursosPorCategoria,
            String periodoRecursos,
            List<EstadisticaSemana> actividadesPorSemana,
            String periodoActividades
    ) {
        this.recursosPorCategoria = recursosPorCategoria;
        this.periodoRecursos = periodoRecursos;
        this.actividadesPorSemana = actividadesPorSemana;
        this.periodoActividades = periodoActividades;
    }

    public List<EstadisticaCategoria> getRecursosPorCategoria() {
        return recursosPorCategoria;
    }

    public String getPeriodoRecursos() {
        return periodoRecursos;
    }

    public List<EstadisticaSemana> getActividadesPorSemana() {
        return actividadesPorSemana;
    }

    public String getPeriodoActividades() {
        return periodoActividades;
    }
}
