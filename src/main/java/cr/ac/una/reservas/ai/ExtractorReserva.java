package cr.ac.una.reservas.ai;

import cr.ac.una.reservas.model.Categoria;

import java.util.List;

public interface ExtractorReserva {

    /**
     * @param categoriasDisponibles categorias entre las cuales el extractor puede identificar coincidencias
     * @return datos extraidos, con los campos que no se pudieron identificar en null o vacios
     */
    DatosReservaExtraidos extraer(String frase, List<Categoria> categoriasDisponibles);
}
