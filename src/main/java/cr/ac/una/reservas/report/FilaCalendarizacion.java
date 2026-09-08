package cr.ac.una.reservas.report;

public final class FilaCalendarizacion {
    private final String recurso;
    private final String horario;
    private final String actividad;

    public FilaCalendarizacion(String recurso, String horario, String actividad) {
        this.recurso = recurso;
        this.horario = horario;
        this.actividad = actividad;
    }

    public String getRecurso() {
        return recurso;
    }

    public String getHorario() {
        return horario;
    }

    public String getActividad() {
        return actividad;
    }
}
