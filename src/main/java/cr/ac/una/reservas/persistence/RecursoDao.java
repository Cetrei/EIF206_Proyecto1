package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Recurso;

import java.util.List;


public interface RecursoDao extends Dao<Recurso, String> {
    List<Recurso> listarPorCategoria(String idCategoria);
}
