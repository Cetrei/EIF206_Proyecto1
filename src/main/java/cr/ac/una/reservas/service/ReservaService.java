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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ReservaService(ReservaDao reservaDao, RecursoDao recursoDao, CategoriaDao categoriaDao) {
        this.reservaDao = reservaDao;
        this.recursoDao = recursoDao;
        this.categoriaDao = categoriaDao;
    }

    public List<Reserva> listarReservasDeFuncionario(String idFuncionario) {
        return reservaDao.listarPorFuncionario(idFuncionario);
    }

    public List<Reserva> listarTodasOrdenadas() {
        return reservaDao.listarTodos().stream()
                .sorted(Comparator
                        .comparing(Reserva::getFecha, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Reserva::getHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public List<Reserva> listarReservasActivasEnFecha(LocalDate fecha) {
        return reservaDao.listarTodos().stream()
                .filter(reserva -> reserva.getEstado() == EstadoReserva.ACTIVA)
                .filter(reserva -> fecha.equals(reserva.getFecha()))
                .collect(Collectors.toList());
    }

    public ResultadoReserva intentarReservar(DatosNuevaReserva datos) {
        validarDatosBasicos(datos);

        List<Categoria> categoriasNoDisponibles = new ArrayList<>();
        List<Recurso> recursosAAsignar = new ArrayList<>();

        for (String idCategoria : datos.getIdsCategoriasRequeridas()) {
            Optional<Recurso> recursoDisponible = buscarPrimerRecursoDisponible(
                    idCategoria, datos.getFecha(), datos.getHoraInicio(), datos.getHoraFin(), null
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
                recursosAAsignar.stream().map(Recurso::getId).collect(Collectors.toList())
        );
        reservaDao.guardar(reserva);

        notificarReservaCreada(reserva);
        return ResultadoReserva.exito(reserva);
    }

    public ResultadoReserva intentarModificar(DatosNuevaReserva datos) {
        if (datos.getId() == null || datos.getId().isBlank()) {
            throw new ReglaDeNegocioException("Debe indicar la reserva que se desea modificar.");
        }
        validarDatosBasicos(datos);

        Reserva reservaExistente = reservaDao.buscarPorId(datos.getId())
                .orElseThrow(() -> new ReglaDeNegocioException("No existe una reserva con ese ID."));

        if (reservaExistente.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReglaDeNegocioException("No se puede modificar una reserva cancelada.");
        }

        if (datos.getIdFuncionario() != null
                && !datos.getIdFuncionario().equals(reservaExistente.getIdFuncionario())) {
            throw new ReglaDeNegocioException("Solo el funcionario dueño de la reserva puede modificarla.");
        }

        List<Categoria> categoriasNoDisponibles = new ArrayList<>();
        List<Recurso> recursosAAsignar = new ArrayList<>();

        for (String idCategoria : datos.getIdsCategoriasRequeridas()) {
            Optional<Recurso> recursoDisponible = buscarPrimerRecursoDisponible(
                    idCategoria, datos.getFecha(), datos.getHoraInicio(), datos.getHoraFin(), reservaExistente.getId()
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

        reservaExistente.setActividad(datos.getActividad());
        reservaExistente.setFecha(datos.getFecha());
        reservaExistente.setHoraInicio(datos.getHoraInicio());
        reservaExistente.setHoraFin(datos.getHoraFin());
        reservaExistente.setIdsCategoriasRequeridas(new ArrayList<>(datos.getIdsCategoriasRequeridas()));
        reservaExistente.setIdsRecursosAsignados(
                recursosAAsignar.stream().map(Recurso::getId).collect(Collectors.toList())
        );
        reservaDao.guardar(reservaExistente);

        notificarReservaModificada(reservaExistente);
        return ResultadoReserva.exito(reservaExistente);
    }

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

    private void notificarReservaModificada(Reserva reserva) {
        for (ReservaObserver observador : observadores) {
            observador.onReservaModificada(reserva);
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

    private Optional<Recurso> buscarPrimerRecursoDisponible(
            String idCategoria, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, String idReservaExcluida
    ) {
        List<Recurso> recursosDeLaCategoria = recursoDao.listarPorCategoria(idCategoria);
        List<Reserva> reservasActivasEseDia = reservaDao.listarTodos().stream()
                .filter(reserva -> reserva.getEstado() == EstadoReserva.ACTIVA)
                .filter(reserva -> idReservaExcluida == null || !idReservaExcluida.equals(reserva.getId()))
                .filter(reserva -> fecha.equals(reserva.getFecha()))
                .filter(reserva -> seSolapan(horaInicio, horaFin, reserva.getHoraInicio(), reserva.getHoraFin()))
                .collect(Collectors.toList());

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
        int siguienteConsecutivo = ultimoConsecutivoUsado() + 1;
        String idPropuesto = PREFIJO_ID + formatearConsecutivo(siguienteConsecutivo);

        while (reservaDao.buscarPorId(idPropuesto).isPresent()) {
            siguienteConsecutivo++;
            idPropuesto = PREFIJO_ID + formatearConsecutivo(siguienteConsecutivo);
        }
        return idPropuesto;
    }

    private int ultimoConsecutivoUsado() {
        int mayor = 0;
        for (Reserva reserva : reservaDao.listarTodos()) {
            mayor = Math.max(mayor, consecutivoDe(reserva.getId()));
        }
        return mayor;
    }

    private static int consecutivoDe(String id) {
        if (id == null || !id.startsWith(PREFIJO_ID)) {
            return 0;
        }
        try {
            return Integer.parseInt(id.substring(PREFIJO_ID.length()));
        } catch (NumberFormatException idConFormatoDesconocido) {
            return 0;
        }
    }

    private static String formatearConsecutivo(int consecutivo) {
        return String.format("%0" + LONGITUD_CONSECUTIVO + "d", consecutivo);
    }
}
