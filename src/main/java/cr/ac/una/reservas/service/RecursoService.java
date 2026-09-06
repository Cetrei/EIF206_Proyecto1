package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Ver docs/02_service.md. El id del recurso se ingresa manualmente
 * (por ejemplo un numero de activo, ver docs/01_model.md), no se
 * autogenera como el de Categoria.
 */
public class RecursoService {

    private final RecursoDao recursoDao;
    private final CategoriaDao categoriaDao;

    public RecursoService() {
        this(DaoFactory.obtenerRecursoDao(), DaoFactory.obtenerCategoriaDao());
    }

    // Constructor para pruebas: permite inyectar Dao falsos.
    public RecursoService(RecursoDao recursoDao, CategoriaDao categoriaDao) {
        this.recursoDao = recursoDao;
        this.categoriaDao = categoriaDao;
    }

    public List<Recurso> listarPorCategoria(String idCategoria) {
        return recursoDao.listarPorCategoria(idCategoria);
    }

    public List<Recurso> buscarPorDescripcion(String texto) {
        String textoNormalizado = texto == null ? "" : texto.toLowerCase();
        return recursoDao.listarTodos().stream()
                .filter(recurso -> recurso.getDescripcion() != null
                        && recurso.getDescripcion().toLowerCase().contains(textoNormalizado))
                .collect(Collectors.toList());
    }

    public void crear(Recurso recurso) {
        if (recurso.getId() == null || recurso.getId().isBlank()) {
            throw new ReglaDeNegocioException("El recurso debe tener un ID (número de activo).");
        }
        if (recursoDao.buscarPorId(recurso.getId()).isPresent()) {
            throw new ReglaDeNegocioException("Ya existe un recurso con ese ID.");
        }
        validarCategoriaExiste(recurso.getIdCategoria());
        recursoDao.guardar(recurso);
    }

    public void modificar(Recurso recurso) {
        if (recursoDao.buscarPorId(recurso.getId()).isEmpty()) {
            throw new ReglaDeNegocioException("No existe un recurso con ese ID.");
        }
        validarCategoriaExiste(recurso.getIdCategoria());
        recursoDao.guardar(recurso);
    }

    public void eliminar(String id) {
        recursoDao.eliminar(id);
    }

    private void validarCategoriaExiste(String idCategoria) {
        if (idCategoria == null || categoriaDao.buscarPorId(idCategoria).isEmpty()) {
            throw new ReglaDeNegocioException("Debe seleccionar una categoría válida para el recurso.");
        }
    }
}
