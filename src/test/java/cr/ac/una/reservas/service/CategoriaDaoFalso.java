package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.persistence.CategoriaDao;

import java.util.List;
import java.util.stream.Collectors;

public class CategoriaDaoFalso extends DaoFalso<Categoria, String> implements CategoriaDao {
    public CategoriaDaoFalso() {
        super(Categoria::getId);
    }

    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        return listarTodos().stream()
                .filter(categoria -> categoria.getDescripcion() != null && categoria.getDescripcion().contains(texto))
                .collect(Collectors.toList());
    }
}
