package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Recurso;

import java.util.List;

/**
 * Ver docs/03_persistence.md. Responsable de la implementacion:
 * Companero A (RecursoDaoXml).
 */
public interface RecursoDao extends Dao<Recurso, String> {
    List<Recurso> listarPorCategoria(String idCategoria);
}
