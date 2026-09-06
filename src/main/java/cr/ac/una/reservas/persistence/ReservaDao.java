package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Reserva;

import java.util.List;

/**
 * Ver docs/03_persistence.md. Responsable de la implementacion:
 * Companero A (ReservaDaoXml).
 */
public interface ReservaDao extends Dao<Reserva, String> {
    List<Reserva> listarPorFuncionario(String idFuncionario);
}
