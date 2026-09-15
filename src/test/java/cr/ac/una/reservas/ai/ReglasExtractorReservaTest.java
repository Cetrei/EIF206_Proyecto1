package cr.ac.una.reservas.ai;

import cr.ac.una.reservas.model.Categoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReglasExtractorReservaTest {

    @Test
    public void extraeDatosPrincipalesDeLaFrase() {

        ReglasExtractorReserva extractor =
                new ReglasExtractorReserva();

        List<Categoria> categorias =
                List.of(
                        new Categoria(
                                "CAT-SALA",
                                "Sala de reuniones"
                        ),
                        new Categoria(
                                "CAT-PROYECTOR",
                                "Proyector"
                        )
                );

        String frase =
                "Actividad: Reunion de proyecto "
                        + "el 15/09/2026 "
                        + "de 8:00 a 10:00 "
                        + "con Sala de reuniones y Proyector";

        DatosReservaExtraidos resultado =
                extractor.extraer(
                        frase,
                        categorias
                );

        assertEquals(
                "Reunion de proyecto",
                resultado.getActividad()
        );

        assertEquals(
                LocalDate.of(
                        2026,
                        9,
                        15
                ),
                resultado.getFecha()
        );

        assertEquals(
                LocalTime.of(
                        8,
                        0
                ),
                resultado.getHoraInicio()
        );

        assertEquals(
                LocalTime.of(
                        10,
                        0
                ),
                resultado.getHoraFin()
        );

        assertTrue(
                resultado
                        .getIdsCategoriasIdentificadas()
                        .contains("CAT-SALA")
        );

        assertTrue(
                resultado
                        .getIdsCategoriasIdentificadas()
                        .contains("CAT-PROYECTOR")
        );
    }

    @Test
    public void retornaDatosVaciosSiLaFraseEstaVacia() {

        ReglasExtractorReserva extractor =
                new ReglasExtractorReserva();

        DatosReservaExtraidos resultado =
                extractor.extraer(
                        "",
                        List.of()
                );

        assertNull(resultado.getActividad());
        assertNull(resultado.getFecha());

        assertTrue(
                resultado
                        .getIdsCategoriasIdentificadas()
                        .isEmpty()
        );
    }
}
