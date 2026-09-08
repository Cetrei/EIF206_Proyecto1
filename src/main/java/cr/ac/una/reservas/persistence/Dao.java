package cr.ac.una.reservas.persistence;

import java.util.List;
import java.util.Optional;

public interface Dao<T, ID> {
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    void guardar(T entidad);
    void eliminar(ID id);
}
