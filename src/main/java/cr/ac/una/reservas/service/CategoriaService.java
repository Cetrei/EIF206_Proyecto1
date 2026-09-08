package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.util.List;
import java.util.Optional;

public class CategoriaService {
    private static final String PREFIJO_ID = "CAT-";
    private static final int LONGITUD_CONSECUTIVO = 6;

    private final CategoriaDao categoriaDao;
    private final RecursoDao recursoDao;

    public CategoriaService() {
        this(DaoFactory.obtenerCategoriaDao(), DaoFactory.obtenerRecursoDao());
    }

    // Constructor para pruebas: permite inyectar Dao falsos.
    public CategoriaService(CategoriaDao categoriaDao, RecursoDao recursoDao) {
        this.categoriaDao = categoriaDao;
        this.recursoDao = recursoDao;
    }

    public List<Categoria> buscarPorDescripcion(String texto) {
        return categoriaDao.buscarPorDescripcion(texto);
    }

    public List<Categoria> listarTodas() {
        return categoriaDao.listarTodos();
    }

    public Categoria crear(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new ReglaDeNegocioException("La descripción de la categoría no puede estar vacía.");
        }
        String id = generarSiguienteId();
        Categoria categoria = new Categoria(id, descripcion);
        categoriaDao.guardar(categoria);
        return categoria;
    }

    public void modificar(Categoria categoria) {
        if (categoriaDao.buscarPorId(categoria.getId()).isEmpty()) {
            throw new ReglaDeNegocioException("No existe una categoría con ese ID.");
        }
        categoriaDao.guardar(categoria);
    }

    public void eliminar(String id) {
        if (!recursoDao.listarPorCategoria(id).isEmpty()) {
            throw new ReglaDeNegocioException(
                    "No se puede eliminar la categoría porque tiene recursos asociados."
            );
        }
        categoriaDao.eliminar(id);
    }

    private String generarSiguienteId() {
        int siguienteConsecutivo = categoriaDao.listarTodos().size() + 1;
        String consecutivoFormateado = String.format("%0" + LONGITUD_CONSECUTIVO + "d", siguienteConsecutivo);
        String idPropuesto = PREFIJO_ID + consecutivoFormateado;

        // Por si el consecutivo por cantidad ya esta en uso
        while (categoriaDao.buscarPorId(idPropuesto).isPresent()) {
            siguienteConsecutivo++;
            consecutivoFormateado = String.format("%0" + LONGITUD_CONSECUTIVO + "d", siguienteConsecutivo);
            idPropuesto = PREFIJO_ID + consecutivoFormateado;
        }
        return idPropuesto;
    }

    Optional<Categoria> buscarPorId(String id) {
        return categoriaDao.buscarPorId(id);
    }
}
