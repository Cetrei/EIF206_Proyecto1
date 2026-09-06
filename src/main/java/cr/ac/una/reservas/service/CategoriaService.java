package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.util.List;
import java.util.Optional;

/**
 * Ver docs/02_service.md.
 */
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

    /**
     * El id se autogenera con formato CAT-000001 (ver docs/01_model.md,
     * siguiendo el ejemplo del enunciado), como el siguiente consecutivo
     * disponible segun la cantidad de categorias existentes.
     */
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

    /**
     * @throws ReglaDeNegocioException si existen recursos asociados a
     * esta categoria, para no dejar recursos huerfanos.
     */
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

        // Por si el consecutivo por cantidad ya esta en uso (por ejemplo,
        // se elimino una categoria intermedia), se busca el siguiente
        // libre en vez de arriesgarse a un id duplicado.
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
