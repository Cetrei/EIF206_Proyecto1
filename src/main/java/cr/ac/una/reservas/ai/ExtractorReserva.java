package cr.ac.una.reservas.ai;

import cr.ac.una.reservas.model.Categoria;

import java.util.List;

public interface ExtractorReserva {

    DatosReservaExtraidos extraer(String frase, List<Categoria> categoriasDisponibles);
}
