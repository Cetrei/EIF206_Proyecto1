package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Categoria;

import java.util.List;

/**
 * Ver docs/03_persistence.md. Responsable de la implementacion:
 * Companero A (CategoriaDaoXml).
 */
public interface CategoriaDao extends Dao<Categoria, String> {
    List<Categoria> buscarPorDescripcion(String texto);
}
