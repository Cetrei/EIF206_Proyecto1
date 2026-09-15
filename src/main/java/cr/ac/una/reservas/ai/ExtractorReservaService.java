package cr.ac.una.reservas.ai;

import cr.ac.una.reservas.model.Categoria;

import java.util.List;

public class ExtractorReservaService {

    private final ExtractorReserva extractorPrincipal;
    private final ExtractorReserva extractorRespaldo;

    private boolean ultimaExtraccionConRespaldo;

    public ExtractorReservaService() {
        this(
                new GeminiExtractorReserva(),
                new ReglasExtractorReserva()
        );
    }

    public ExtractorReservaService(
            ExtractorReserva extractorPrincipal,
            ExtractorReserva extractorRespaldo) {

        this.extractorPrincipal = extractorPrincipal;
        this.extractorRespaldo = extractorRespaldo;
    }

    public DatosReservaExtraidos extraer(
            String frase,
            List<Categoria> categoriasDisponibles) {

        ultimaExtraccionConRespaldo = false;

        try {
            return extractorPrincipal.extraer(
                    frase,
                    categoriasDisponibles
            );

        } catch (RuntimeException excepcion) {

            ultimaExtraccionConRespaldo = true;

            return extractorRespaldo.extraer(
                    frase,
                    categoriasDisponibles
            );
        }
    }

    public boolean fueUsadoModoBasico() {
        return ultimaExtraccionConRespaldo;
    }
}