package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.util.TextoBusqueda;

import java.util.List;
import java.util.stream.Collectors;

class CategoriaDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<Categoria, String> implements CategoriaDao {
    CategoriaDaoEnMemoriaTemporal() {
        super(Categoria::getId);
    }

    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        return listarTodos().stream()
                .filter(categoria -> TextoBusqueda.contiene(categoria.getDescripcion(), texto))
                .collect(Collectors.toList());
    }
}
