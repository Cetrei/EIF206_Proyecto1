package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.persistence.CategoriaDao;

import java.util.List;
import java.util.stream.Collectors;

class CategoriaDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<Categoria, String> implements CategoriaDao {
    CategoriaDaoEnMemoriaTemporal() {
        super(Categoria::getId);
    }

    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        String textoNormalizado = texto == null ? "" : texto.toLowerCase();
        return listarTodos().stream()
                .filter(categoria -> categoria.getDescripcion() != null
                        && categoria.getDescripcion().toLowerCase().contains(textoNormalizado))
                .collect(Collectors.toList());
    }
}
