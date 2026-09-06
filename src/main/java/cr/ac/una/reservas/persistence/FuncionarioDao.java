package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Funcionario;

import java.util.List;

/**
 * Ver docs/03_persistence.md. Responsable de la implementacion:
 * Companero A (FuncionarioDaoXml). service depende unicamente de esta
 * interfaz, obtenida a traves de DaoFactory.
 */
public interface FuncionarioDao extends Dao<Funcionario, String> {
    List<Funcionario> buscarPorNombre(String nombre);
}
