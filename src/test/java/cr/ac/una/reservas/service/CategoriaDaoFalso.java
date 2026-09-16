package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.util.TextoBusqueda;

import java.util.List;
import java.util.stream.Collectors;

public class CategoriaDaoFalso extends DaoFalso<Categoria, String> implements CategoriaDao {
    public CategoriaDaoFalso() {
        super(Categoria::getId);
    }

    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        return listarTodos().stream()
                .filter(categoria -> TextoBusqueda.contiene(categoria.getDescripcion(), texto))
                .collect(Collectors.toList());
    }
}
