package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaService {
    private static final String PREFIJO_ID = "CAT-";
    private static final int LONGITUD_CONSECUTIVO = 6;

    private final CategoriaDao categoriaDao;
    private final RecursoDao recursoDao;
    private final List<CategoriaObserver> observadores = new ArrayList<>();

    public CategoriaService() {
        this(DaoFactory.obtenerCategoriaDao(), DaoFactory.obtenerRecursoDao());
    }

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
        notificarCategoriasCambiaron();
        return categoria;
    }

    public void modificar(Categoria categoria) {
        if (categoria.getDescripcion() == null || categoria.getDescripcion().isBlank()) {
            throw new ReglaDeNegocioException("La descripción de la categoría no puede estar vacía.");
        }
        if (categoriaDao.buscarPorId(categoria.getId()).isEmpty()) {
            throw new ReglaDeNegocioException("No existe una categoría con ese ID.");
        }
        categoriaDao.guardar(categoria);
        notificarCategoriasCambiaron();
    }

    public void eliminar(String id) {
        if (categoriaDao.buscarPorId(id).isEmpty()) {
            throw new ReglaDeNegocioException("No existe una categoría con ese ID.");
        }
        if (!recursoDao.listarPorCategoria(id).isEmpty()) {
            throw new ReglaDeNegocioException(
                    "No se puede eliminar la categoría porque tiene recursos asociados."
            );
        }
        categoriaDao.eliminar(id);
        notificarCategoriasCambiaron();
    }

    public void agregarObservador(CategoriaObserver observador) {
        observadores.add(observador);
    }

    public void quitarObservador(CategoriaObserver observador) {
        observadores.remove(observador);
    }

    private void notificarCategoriasCambiaron() {
        for (CategoriaObserver observador : observadores) {
            observador.onCategoriasCambiaron();
        }
    }

    private String generarSiguienteId() {
        int siguienteConsecutivo = ultimoConsecutivoUsado() + 1;
        String idPropuesto = PREFIJO_ID + formatearConsecutivo(siguienteConsecutivo);

        while (categoriaDao.buscarPorId(idPropuesto).isPresent()) {
            siguienteConsecutivo++;
            idPropuesto = PREFIJO_ID + formatearConsecutivo(siguienteConsecutivo);
        }
        return idPropuesto;
    }

    private int ultimoConsecutivoUsado() {
        int mayor = 0;
        for (Categoria categoria : categoriaDao.listarTodos()) {
            mayor = Math.max(mayor, consecutivoDe(categoria.getId()));
        }
        return mayor;
    }

    private static int consecutivoDe(String id) {
        if (id == null || !id.startsWith(PREFIJO_ID)) {
            return 0;
        }
        try {
            return Integer.parseInt(id.substring(PREFIJO_ID.length()));
        } catch (NumberFormatException idConFormatoDesconocido) {
            return 0;
        }
    }

    private static String formatearConsecutivo(int consecutivo) {
        return String.format("%0" + LONGITUD_CONSECUTIVO + "d", consecutivo);
    }

    Optional<Categoria> buscarPorId(String id) {
        return categoriaDao.buscarPorId(id);
    }
}
