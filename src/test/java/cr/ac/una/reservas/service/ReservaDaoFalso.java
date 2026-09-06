package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.persistence.ReservaDao;

import java.util.List;
import java.util.stream.Collectors;

public class ReservaDaoFalso extends DaoFalso<Reserva, String> implements ReservaDao {
    public ReservaDaoFalso() {
        super(Reserva::getId);
    }

    @Override
    public List<Reserva> listarPorFuncionario(String idFuncionario) {
        return listarTodos().stream()
                .filter(reserva -> idFuncionario.equals(reserva.getIdFuncionario()))
                .collect(Collectors.toList());
    }
}
