package cr.ac.una.reservas.ai;

import cr.ac.una.reservas.model.Categoria;

import java.util.List;

// Implementaciones: GeminiExtractorReserva (API) y ReglasExtractorReserva
// (respaldo sin red). El servicio que decide cual usar vive en este paquete.
public interface ExtractorReserva {

    /**
     * @param categoriasDisponibles categorias entre las cuales el extractor puede identificar coincidencias
     * @return datos extraidos, con los campos que no se pudieron identificar en null o vacios
     */
    DatosReservaExtraidos extraer(String frase, List<Categoria> categoriasDisponibles);
}
