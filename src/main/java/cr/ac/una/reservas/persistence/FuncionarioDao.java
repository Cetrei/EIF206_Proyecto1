package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Funcionario;

import java.util.List;

public interface FuncionarioDao extends Dao<Funcionario, String> {
    List<Funcionario> buscarPorNombre(String nombre);
}
