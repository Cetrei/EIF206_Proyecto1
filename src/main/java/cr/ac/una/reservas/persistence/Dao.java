package cr.ac.una.reservas.persistence;

import java.util.List;
import java.util.Optional;

/**
 * Contrato generico de acceso a datos (ver docs/03_persistence.md).
 * <p>
 * Esta interfaz, junto con las interfaces especificas de este mismo
 * paquete (FuncionarioDao, CategoriaDao, RecursoDao, ReservaDao), es el
 * contrato de sustitucion de Liskov del proyecto: cualquier
 * implementacion (XML u otra) debe comportarse igual desde la
 * perspectiva de quien la usa (service, a traves de DaoFactory).
 * <p>
 * Responsable de la(s) implementacion(es) concretas: Companero A (ver
 * docs/03_persistence.md). service programa unicamente contra esta
 * interfaz, nunca contra una clase concreta.
 *
 * @param <T>  tipo de entidad.
 * @param <ID> tipo del identificador de la entidad.
 */
public interface Dao<T, ID> {
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    void guardar(T entidad);
    void eliminar(ID id);
}
