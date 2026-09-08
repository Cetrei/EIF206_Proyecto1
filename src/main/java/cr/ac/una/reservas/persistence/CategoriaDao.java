package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Categoria;

import java.util.List;

public interface CategoriaDao extends Dao<Categoria, String> {
    List<Categoria> buscarPorDescripcion(String texto);
}
