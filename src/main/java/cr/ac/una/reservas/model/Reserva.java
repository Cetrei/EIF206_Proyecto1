package cr.ac.una.reservas.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Los campos funcionario, categoriasRequeridas y recursosAsignados son
 * XmlTransient: guardan solo los ids en XML, y service los resuelve a
 * objetos completos.
 */
@XmlRootElement(name = "reserva")
@XmlAccessorType(XmlAccessType.FIELD)
public class Reserva {

    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "idFuncionario")
    private String idFuncionario;

    @XmlElement(name = "actividad")
    private String actividad;

    @XmlElement(name = "fecha")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate fecha;

    @XmlElement(name = "horaInicio")
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaInicio;

    @XmlElement(name = "horaFin")
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaFin;

    @XmlElementWrapper(name = "idsCategoriasRequeridas")
    @XmlElement(name = "idCategoria")
    private List<String> idsCategoriasRequeridas = new ArrayList<>();

    @XmlElementWrapper(name = "idsRecursosAsignados")
    @XmlElement(name = "idRecurso")
    private List<String> idsRecursosAsignados = new ArrayList<>();

    @XmlElement(name = "estado")
    private EstadoReserva estado;

    @XmlTransient
    private Funcionario funcionario;

    @XmlTransient
    private List<Categoria> categoriasRequeridas = new ArrayList<>();

    @XmlTransient
    private List<Recurso> recursosAsignados = new ArrayList<>();

    public Reserva() {
    }

    public Reserva(String id, String idFuncionario, String actividad, LocalDate fecha,
                    LocalTime horaInicio, LocalTime horaFin, List<String> idsCategoriasRequeridas) {
        this.id = id;
        this.idFuncionario = idFuncionario;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.idsCategoriasRequeridas = idsCategoriasRequeridas != null ? idsCategoriasRequeridas : new ArrayList<>();
        this.estado = EstadoReserva.ACTIVA;
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

    public List<String> getIdsRecursosAsignados() {
        return idsRecursosAsignados;
    }

    public void setIdsRecursosAsignados(List<String> idsRecursosAsignados) {
        this.idsRecursosAsignados = idsRecursosAsignados;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        if (funcionario != null) {
            this.idFuncionario = funcionario.getId();
        }
    }

    public List<Categoria> getCategoriasRequeridas() {
        return categoriasRequeridas;
    }

    public void setCategoriasRequeridas(List<Categoria> categoriasRequeridas) {
        this.categoriasRequeridas = categoriasRequeridas;
    }

    public List<Recurso> getRecursosAsignados() {
        return recursosAsignados;
    }

    public void setRecursosAsignados(List<Recurso> recursosAsignados) {
        this.recursosAsignados = recursosAsignados;
    }

    @Override
    public String toString() {
        return String.format("Reserva{id=%s, actividad=%s, fecha=%s, horaInicio=%s, horaFin=%s, estado=%s}",
                id, actividad, fecha, horaInicio, horaFin, estado);
    }
}
