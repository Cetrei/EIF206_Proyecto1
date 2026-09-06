package cr.ac.una.reservas.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Datos de entrada para crear una Reserva nueva (ver docs/01_model.md).
 * Es un objeto simple de transporte, sin logica de negocio: junta lo
 * que control recibe de la vista (actividad, fecha, horas, funcionario
 * y categorias requeridas) para pasarlo de una sola vez a
 * ReservaService.intentarReservar(datosReserva) (ver docs/02_service.md)
 * y al constructor de Reserva.
 *
 * No incluye recursosAsignados ni estado: esos los decide el propio
 * proceso de reserva en service, no quien la solicita.
 */
public class DatosNuevaReserva {

    private String id;
    private String idFuncionario;
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<String> idsCategoriasRequeridas = new ArrayList<>();

    public DatosNuevaReserva() {
    }

    public DatosNuevaReserva(String id, String idFuncionario, String actividad, LocalDate fecha,
                              LocalTime horaInicio, LocalTime horaFin, List<String> idsCategoriasRequeridas) {
        this.id = id;
        this.idFuncionario = idFuncionario;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.idsCategoriasRequeridas = idsCategoriasRequeridas;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getIdFuncionario() {
        return idFuncionario;
    }
    public void setIdFuncionario(String idFuncionario) {
        this.idFuncionario = idFuncionario;
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

    public List<String> getIdsCategoriasRequeridas() {
        return idsCategoriasRequeridas;
    }
    public void setIdsCategoriasRequeridas(List<String> idsCategoriasRequeridas) {
        this.idsCategoriasRequeridas = idsCategoriasRequeridas;
    }
}
