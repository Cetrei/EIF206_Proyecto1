package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.persistence.FuncionarioDao;

import java.util.List;
import java.util.stream.Collectors;

class FuncionarioDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<Funcionario, String> implements FuncionarioDao {
    FuncionarioDaoEnMemoriaTemporal() {
        super(Funcionario::getId);
    }

    @Override
    public List<Funcionario> buscarPorNombre(String nombre) {
        return listarTodos().stream()
                .filter(funcionario -> funcionario.getNombre() != null && funcionario.getNombre().contains(nombre))
                .collect(Collectors.toList());
    }
}
