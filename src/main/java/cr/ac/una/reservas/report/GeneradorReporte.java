package cr.ac.una.reservas.report;

import java.util.List;
import java.util.Map;

public interface GeneradorReporte<T> {

    void generar(String rutaDestino, List<T> datos, Map<String, String> metadatos);
}
