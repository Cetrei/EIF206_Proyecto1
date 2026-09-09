package cr.ac.una.reservas.ai;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtractorReservaServiceTest {

    @Test
    public void usaExtractorDeRespaldoCuandoElPrincipalFalla() {

        ExtractorReserva extractorPrincipal =
                (frase, categorias) -> {
                    throw new IllegalStateException(
                            "API no disponible"
                    );
                };

        ExtractorReserva extractorRespaldo =
                (frase, categorias) -> {

                    DatosReservaExtraidos datos =
                            new DatosReservaExtraidos();

                    datos.setActividad(
                            "Actividad de respaldo"
                    );

                    return datos;
                };

        ExtractorReservaService service =
                new ExtractorReservaService(
                        extractorPrincipal,
                        extractorRespaldo
                );

        DatosReservaExtraidos resultado =
                service.extraer(
                        "Necesito reservar algo",
                        List.of()
                );

        assertEquals(
                "Actividad de respaldo",
                resultado.getActividad()
        );

        assertTrue(
                service.fueUsadoModoBasico()
        );
    }
}
