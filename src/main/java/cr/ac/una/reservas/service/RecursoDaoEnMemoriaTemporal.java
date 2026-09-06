package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.persistence.RecursoDao;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Ver DaoEnMemoriaTemporal: parche temporal de arranque mientras
 * persistence (Companero A) no entrega RecursoDaoXml.
 */
class RecursoDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<Recurso, String> implements RecursoDao {
    RecursoDaoEnMemoriaTemporal() {
        super(Recurso::getId);
    }

    @Override
    public List<Recurso> listarPorCategoria(String idCategoria) {
        return listarTodos().stream()
                .filter(recurso -> idCategoria != null && idCategoria.equals(recurso.getIdCategoria()))
                .collect(Collectors.toList());
    }
}
