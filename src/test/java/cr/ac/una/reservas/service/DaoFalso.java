package cr.ac.una.reservas.service;

import cr.ac.una.reservas.persistence.Dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementacion en memoria de Dao, usada unicamente en pruebas de
 * unidad de service (ver docs/07_convenciones.md, seccion Pruebas),
 * para no depender de la implementacion real de persistence
 * (Companero A, todavia no entregada) ni de archivos en disco. No es
 * parte del sistema, vive en src/test.
 */
public class DaoFalso<T, ID> implements Dao<T, ID> {

    private final Map<ID, T> almacen = new LinkedHashMap<>();
    private final Function<T, ID> extractorId;

    public DaoFalso(Function<T, ID> extractorId) {
        this.extractorId = extractorId;
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<T> listarTodos() {
        return almacen.values().stream().collect(Collectors.toList());
    }

    @Override
    public void guardar(T entidad) {
        almacen.put(extractorId.apply(entidad), entidad);
    }

    @Override
    public void eliminar(ID id) {
        almacen.remove(id);
    }
}
