package cr.ac.una.reservas.report;

import java.util.List;
import java.util.Map;

public interface GeneradorReporte<T> {

    /**
     * Genera el reporte en rutaDestino
     *
     * @param rutaDestino ruta completa del archivo .pdf a crear.
     * @param datos listado ya obtenido de service, en el mismo orden en que se quiere que aparezca en el reporte.
     * @param metadatos pares clave-valor opcionales especificos de cada implementacion; puede ser null o vacio.
     * @throws cr.ac.una.reservas.util.PersistenciaException si no se pudo escribir el archivo.
     */
    void generar(String rutaDestino, List<T> datos, Map<String, String> metadatos);
}
