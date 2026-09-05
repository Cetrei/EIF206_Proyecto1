package cr.ac.una.reservas.ai;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Campos no identificados en la frase quedan en null o vacios, no lanzan error.
public class DatosReservaExtraidos {

    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<String> idsCategoriasIdentificadas = new ArrayList<>();

    public DatosReservaExtraidos() {
    }

    public DatosReservaExtraidos(String actividad, LocalDate fecha, LocalTime horaInicio,
                                  LocalTime horaFin, List<String> idsCategoriasIdentificadas) {
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.idsCategoriasIdentificadas = idsCategoriasIdentificadas != null
                ? idsCategoriasIdentificadas
                : new ArrayList<>();
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public List<String> getIdsCategoriasIdentificadas() {
        return idsCategoriasIdentificadas;
    }

    public void setIdsCategoriasIdentificadas(List<String> idsCategoriasIdentificadas) {
        this.idsCategoriasIdentificadas = idsCategoriasIdentificadas;
    }

    @Override
    public String toString() {
        return String.format(
                "DatosReservaExtraidos{actividad=%s, fecha=%s, horaInicio=%s, horaFin=%s, categorias=%s}",
                actividad, fecha, horaInicio, horaFin, idsCategoriasIdentificadas);
    }
}
