package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.persistence.ReservaDao;

import java.util.List;
import java.util.stream.Collectors;

class ReservaDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<Reserva, String> implements ReservaDao {
    ReservaDaoEnMemoriaTemporal() {
        super(Reserva::getId);
    }

    @Override
    public List<Reserva> listarPorFuncionario(String idFuncionario) {
        return listarTodos().stream()
                .filter(reserva -> idFuncionario != null && idFuncionario.equals(reserva.getIdFuncionario()))
                .collect(Collectors.toList());
    }
}
