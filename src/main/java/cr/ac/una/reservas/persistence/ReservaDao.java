package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Reserva;

import java.util.List;

public interface ReservaDao extends Dao<Reserva, String> {
    List<Reserva> listarPorFuncionario(String idFuncionario);
}
