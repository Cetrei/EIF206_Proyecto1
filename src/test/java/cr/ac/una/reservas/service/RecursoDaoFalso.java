package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.persistence.RecursoDao;

import java.util.List;
import java.util.stream.Collectors;

public class RecursoDaoFalso extends DaoFalso<Recurso, String> implements RecursoDao {
    public RecursoDaoFalso() {
        super(Recurso::getId);
    }

    @Override
    public List<Recurso> listarPorCategoria(String idCategoria) {
        return listarTodos().stream()
                .filter(recurso -> idCategoria.equals(recurso.getIdCategoria()))
                .collect(Collectors.toList());
    }
}
