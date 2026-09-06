package cr.ac.una.reservas.service;

import cr.ac.una.reservas.persistence.Dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementacion EN MEMORIA (no persiste a disco) de la interfaz Dao,
 * usada UNICAMENTE mientras la capa persistence (Companero A, ver
 * docs/03_persistence.md) todavia no entrega FuncionarioDaoXml,
 * CategoriaDaoXml, etc.
 * <p>
 * Esto NO es la implementacion real de persistence: vive en el paquete
 * service (no en persistence) precisamente para dejar claro que es un
 * parche temporal de arranque, no el trabajo del Companero A. Cuando
 * el Companero A entregue sus clases *Xml, esta clase y su uso en
 * DaoFactory se eliminan sin tocar ningun otro archivo de service (ver
 * el TODO en DaoFactory).
 */
class DaoEnMemoriaTemporal<T, ID> implements Dao<T, ID> {

    private final Map<ID, T> almacen = new LinkedHashMap<>();
    private final Function<T, ID> extractorId;

    DaoEnMemoriaTemporal(Function<T, ID> extractorId) {
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
