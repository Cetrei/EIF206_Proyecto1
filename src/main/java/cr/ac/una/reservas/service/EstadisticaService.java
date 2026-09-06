package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.EstadisticaCategoria;
import cr.ac.una.reservas.model.EstadisticaSemana;
import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ver docs/02_service.md.
 */
public class EstadisticaService {

    private final ReservaDao reservaDao;
    private final RecursoDao recursoDao;
    private final CategoriaDao categoriaDao;

    public EstadisticaService() {
        this(DaoFactory.obtenerReservaDao(), DaoFactory.obtenerRecursoDao(), DaoFactory.obtenerCategoriaDao());
    }

    // Constructor para pruebas: permite inyectar Dao falsos.
    public EstadisticaService(ReservaDao reservaDao, RecursoDao recursoDao, CategoriaDao categoriaDao) {
        this.reservaDao = reservaDao;
        this.recursoDao = recursoDao;
        this.categoriaDao = categoriaDao;
    }

    /**
     * Por cada categoria, cuantas veces se reservo un recurso de esa
     * categoria en el rango [desde, hasta] (ambos inclusive). Solo
     * cuenta reservas activas o canceladas por igual, ya que el
     * enunciado pide "recursos reservados", no "recursos actualmente
     * activos"; una reserva cancelada igual ocupo el recurso en algun
     * momento del periodo consultado.
     */
    public List<EstadisticaCategoria> recursosReservadosEnPeriodo(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);

        Map<String, Recurso> recursosPorId = new LinkedHashMap<>();
        for (Recurso recurso : recursoDao.listarTodos()) {
            recursosPorId.put(recurso.getId(), recurso);
        }

        Map<String, Long> cantidadPorCategoria = new LinkedHashMap<>();
        for (Reserva reserva : reservaDao.listarTodos()) {
            if (reserva.getFecha() == null
                    || reserva.getFecha().isBefore(desde)
                    || reserva.getFecha().isAfter(hasta)) {
                continue;
            }
            for (String idRecurso : reserva.getIdsRecursosAsignados()) {
                Recurso recurso = recursosPorId.get(idRecurso);
                if (recurso == null) {
                    continue;
                }
                cantidadPorCategoria.merge(recurso.getIdCategoria(), 1L, Long::sum);
            }
        }

        List<EstadisticaCategoria> resultado = new ArrayList<>();
        for (Map.Entry<String, Long> entrada : cantidadPorCategoria.entrySet()) {
            categoriaDao.buscarPorId(entrada.getKey())
                    .ifPresent(categoria -> resultado.add(new EstadisticaCategoria(categoria, entrada.getValue())));
        }
        return resultado;
    }

    /**
     * Por cada semana (identificada por su lunes) dentro del rango
     * [desde, hasta], cuantas actividades (reservas) hay programadas
     * en esa semana.
     */
    public List<EstadisticaSemana> actividadesPorSemanaEnPeriodo(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);

        Map<LocalDate, Long> cantidadPorSemana = new LinkedHashMap<>();
        for (Reserva reserva : reservaDao.listarTodos()) {
            if (reserva.getFecha() == null
                    || reserva.getFecha().isBefore(desde)
                    || reserva.getFecha().isAfter(hasta)) {
                continue;
            }
            LocalDate inicioSemana = reserva.getFecha().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            cantidadPorSemana.merge(inicioSemana, 1L, Long::sum);
        }

        List<EstadisticaSemana> resultado = new ArrayList<>();
        cantidadPorSemana.forEach((inicioSemana, cantidad) ->
                resultado.add(new EstadisticaSemana(inicioSemana, cantidad)));
        resultado.sort((a, b) -> a.getInicioSemana().compareTo(b.getInicioSemana()));
        return resultado;
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new ReglaDeNegocioException("Debe indicar las fechas \"desde\" y \"hasta\".");
        }
        if (hasta.isBefore(desde)) {
            throw new ReglaDeNegocioException("La fecha \"hasta\" no puede ser anterior a la fecha \"desde\".");
        }
    }
}
