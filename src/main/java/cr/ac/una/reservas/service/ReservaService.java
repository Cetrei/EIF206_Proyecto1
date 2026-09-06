package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.DatosNuevaReserva;
import cr.ac.una.reservas.model.EstadoReserva;
import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.model.ResultadoReserva;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio central del proyecto (ver docs/02_service.md). Contiene la
 * regla de asignacion de recursos a una reserva: se verifica primero
 * disponibilidad de todas las categorias requeridas sin comprometer
 * ningun recurso, y solo si todas tienen disponibilidad se asignan los
 * recursos y se crea la reserva. Esto evita reservas parciales.
 */
public class ReservaService {

    private static final String PREFIJO_ID = "RES-";
    private static final int LONGITUD_CONSECUTIVO = 6;

    private final ReservaDao reservaDao;
    private final RecursoDao recursoDao;
    private final CategoriaDao categoriaDao;
    private final List<ReservaObserver> observadores = new ArrayList<>();

    public ReservaService() {
        this(DaoFactory.obtenerReservaDao(), DaoFactory.obtenerRecursoDao(), DaoFactory.obtenerCategoriaDao());
    }

    // Constructor para pruebas: permite inyectar Dao falsos.
    public ReservaService(ReservaDao reservaDao, RecursoDao recursoDao, CategoriaDao categoriaDao) {
        this.reservaDao = reservaDao;
        this.recursoDao = recursoDao;
        this.categoriaDao = categoriaDao;
    }

    public List<Reserva> listarReservasDeFuncionario(String idFuncionario) {
        return reservaDao.listarPorFuncionario(idFuncionario);
    }

    /**
     * Intenta crear una reserva a partir de los datos recibidos.
     * Primero valida las reglas basicas de la propia reserva (fecha no
     * en el pasado, hora fin posterior a hora inicio, al menos una
     * categoria requerida), despues verifica disponibilidad de al
     * menos un recurso libre de cada categoria requerida en ese rango
     * de fecha/hora sin comprometer nada todavia. Si todas las
     * categorias tienen disponibilidad, se asigna el primer recurso
     * disponible de cada una y se crea la reserva con estado ACTIVA,
     * notificando a los observadores. Si alguna categoria no tiene
     * disponibilidad, devuelve un ResultadoReserva de fracaso con la
     * lista de categorias que fallaron, sin crear ninguna reserva ni
     * comprometer ningun recurso.
     */
    public ResultadoReserva intentarReservar(DatosNuevaReserva datos) {
        validarDatosBasicos(datos);

        List<Categoria> categoriasNoDisponibles = new ArrayList<>();
        List<Recurso> recursosAAsignar = new ArrayList<>();

        for (String idCategoria : datos.getIdsCategoriasRequeridas()) {
            Optional<Recurso> recursoDisponible = buscarPrimerRecursoDisponible(
                    idCategoria, datos.getFecha(), datos.getHoraInicio(), datos.getHoraFin()
            );
            if (recursoDisponible.isPresent()) {
                recursosAAsignar.add(recursoDisponible.get());
            } else {
                categoriaDao.buscarPorId(idCategoria).ifPresent(categoriasNoDisponibles::add);
            }
        }

        if (!categoriasNoDisponibles.isEmpty()) {
            return ResultadoReserva.fracaso(categoriasNoDisponibles);
        }

        Reserva reserva = new Reserva(datosConId(datos));
        reserva.setIdsRecursosAsignados(
                recursosAAsignar.stream().map(Recurso::getId).collect(java.util.stream.Collectors.toList())
        );
        reservaDao.guardar(reserva);

        notificarReservaCreada(reserva);
        return ResultadoReserva.exito(reserva);
    }

    /**
     * Libera todos los recursos asignados y cambia el estado a
     * CANCELADA. Solo aplica a reservas futuras (ver enunciado); una
     * reserva ya pasada o ya cancelada no puede cancelarse de nuevo.
     */
    public void cancelarReserva(String idReserva) {
        Reserva reserva = reservaDao.buscarPorId(idReserva)
                .orElseThrow(() -> new ReglaDeNegocioException("No existe una reserva con ese ID."));

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReglaDeNegocioException("La reserva ya está cancelada.");
        }
        if (reserva.getFecha() != null && reserva.getFecha().isBefore(LocalDate.now())) {
            throw new ReglaDeNegocioException("No se puede cancelar una reserva que ya pasó.");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaDao.guardar(reserva);

        notificarReservaCancelada(reserva);
    }

    public void agregarObservador(ReservaObserver observador) {
        observadores.add(observador);
    }

    public void quitarObservador(ReservaObserver observador) {
        observadores.remove(observador);
    }

    private void notificarReservaCreada(Reserva reserva) {
        for (ReservaObserver observador : observadores) {
            observador.onReservaCreada(reserva);
        }
    }

    private void notificarReservaCancelada(Reserva reserva) {
        for (ReservaObserver observador : observadores) {
            observador.onReservaCancelada(reserva);
        }
    }

    private void validarDatosBasicos(DatosNuevaReserva datos) {
        if (datos.getActividad() == null || datos.getActividad().isBlank()) {
            throw new ReglaDeNegocioException("Debe indicar la actividad de la reserva.");
        }
        if (datos.getFecha() == null) {
            throw new ReglaDeNegocioException("Debe indicar la fecha de la reserva.");
        }
        if (datos.getFecha().isBefore(LocalDate.now())) {
            throw new ReglaDeNegocioException("La fecha de la reserva no puede ser en el pasado.");
        }
        if (datos.getHoraInicio() == null || datos.getHoraFin() == null) {
            throw new ReglaDeNegocioException("Debe indicar la hora de inicio y de finalización.");
        }
        if (!datos.getHoraFin().isAfter(datos.getHoraInicio())) {
            throw new ReglaDeNegocioException("La hora de finalización debe ser posterior a la hora de inicio.");
        }
        if (datos.getIdsCategoriasRequeridas() == null || datos.getIdsCategoriasRequeridas().isEmpty()) {
            throw new ReglaDeNegocioException("Debe seleccionar al menos una categoría de recurso.");
        }
    }

    /**
     * Primer recurso de la categoria dada que no tenga ninguna reserva
     * activa cuyo rango de fecha/hora se solape con el solicitado.
     */
    private Optional<Recurso> buscarPrimerRecursoDisponible(
            String idCategoria, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin
    ) {
        List<Recurso> recursosDeLaCategoria = recursoDao.listarPorCategoria(idCategoria);
        List<Reserva> reservasActivasEseDia = reservaDao.listarTodos().stream()
                .filter(reserva -> reserva.getEstado() == EstadoReserva.ACTIVA)
                .filter(reserva -> fecha.equals(reserva.getFecha()))
                .filter(reserva -> seSolapan(horaInicio, horaFin, reserva.getHoraInicio(), reserva.getHoraFin()))
                .collect(java.util.stream.Collectors.toList());

        for (Recurso recurso : recursosDeLaCategoria) {
            boolean ocupado = reservasActivasEseDia.stream()
                    .anyMatch(reserva -> reserva.getIdsRecursosAsignados().contains(recurso.getId()));
            if (!ocupado) {
                return Optional.of(recurso);
            }
        }
        return Optional.empty();
    }

    private boolean seSolapan(LocalTime inicioA, LocalTime finA, LocalTime inicioB, LocalTime finB) {
        return inicioA.isBefore(finB) && inicioB.isBefore(finA);
    }

    private DatosNuevaReserva datosConId(DatosNuevaReserva datos) {
        datos.setId(generarSiguienteId());
        return datos;
    }

    private String generarSiguienteId() {
        int siguienteConsecutivo = reservaDao.listarTodos().size() + 1;
        String consecutivoFormateado = String.format("%0" + LONGITUD_CONSECUTIVO + "d", siguienteConsecutivo);
        String idPropuesto = PREFIJO_ID + consecutivoFormateado;

        while (reservaDao.buscarPorId(idPropuesto).isPresent()) {
            siguienteConsecutivo++;
            consecutivoFormateado = String.format("%0" + LONGITUD_CONSECUTIVO + "d", siguienteConsecutivo);
            idPropuesto = PREFIJO_ID + consecutivoFormateado;
        }
        return idPropuesto;
    }
}
