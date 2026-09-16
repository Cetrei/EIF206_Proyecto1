package cr.ac.una.reservas.ai;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    public void noLlamaAlExtractorDeRespaldoCuandoElPrincipalTieneExito() {
        AtomicInteger llamadasAlRespaldo = new AtomicInteger(0);

        ExtractorReserva extractorPrincipal = (frase, categorias) -> {
            DatosReservaExtraidos datos = new DatosReservaExtraidos();
            datos.setActividad("Actividad del principal");
            return datos;
        };

        ExtractorReserva extractorRespaldo = (frase, categorias) -> {
            llamadasAlRespaldo.incrementAndGet();
            return new DatosReservaExtraidos();
        };

        ExtractorReservaService service = new ExtractorReservaService(extractorPrincipal, extractorRespaldo);

        DatosReservaExtraidos resultado = service.extraer("Necesito reservar algo", List.of());

        assertEquals("Actividad del principal", resultado.getActividad());
        assertEquals(0, llamadasAlRespaldo.get());
        assertFalse(service.fueUsadoModoBasico());
    }

    @Test
    public void reflejaElEstadoDelUltimoIntentoNoElAcumulado() {
        ExtractorReserva extractorQueFallaSoloLaPrimeraVez = new ExtractorReserva() {
            private int llamadas = 0;

            @Override
            public DatosReservaExtraidos extraer(String frase, List<cr.ac.una.reservas.model.Categoria> categorias) {
                llamadas++;
                if (llamadas == 1) {
                    throw new IllegalStateException("Falla la primera vez");
                }
                DatosReservaExtraidos datos = new DatosReservaExtraidos();
                datos.setActividad("Segundo intento exitoso");
                return datos;
            }
        };

        ExtractorReserva extractorRespaldo = (frase, categorias) -> {
            DatosReservaExtraidos datos = new DatosReservaExtraidos();
            datos.setActividad("Respaldo");
            return datos;
        };

        ExtractorReservaService service = new ExtractorReservaService(extractorQueFallaSoloLaPrimeraVez, extractorRespaldo);

        service.extraer("Primer intento", List.of());
        assertTrue(service.fueUsadoModoBasico());

        DatosReservaExtraidos segundoResultado = service.extraer("Segundo intento", List.of());
        assertEquals("Segundo intento exitoso", segundoResultado.getActividad());
        assertFalse(service.fueUsadoModoBasico());
    }

    @Test
    public void noEstaConfiguradoParaGeminiCuandoElPrincipalNoEsGemini() {
        ExtractorReserva extractorPrincipal = (frase, categorias) -> new DatosReservaExtraidos();
        ExtractorReserva extractorRespaldo = (frase, categorias) -> new DatosReservaExtraidos();

        ExtractorReservaService service = new ExtractorReservaService(extractorPrincipal, extractorRespaldo);

        assertFalse(service.estaConfiguradoParaGemini());
    }

    @Test
    public void reflejaElEstadoDeConfiguracionDeGemini() {
        GeminiExtractorReserva extractorSinLlave = new GeminiExtractorReserva(null, null);
        GeminiExtractorReserva extractorConLlave = new GeminiExtractorReserva("llave-de-prueba", null);
        ExtractorReserva extractorRespaldo = (frase, categorias) -> new DatosReservaExtraidos();

        ExtractorReservaService servicioSinLlave = new ExtractorReservaService(extractorSinLlave, extractorRespaldo);
        ExtractorReservaService servicioConLlave = new ExtractorReservaService(extractorConLlave, extractorRespaldo);

        assertFalse(servicioSinLlave.estaConfiguradoParaGemini());
        assertTrue(servicioConLlave.estaConfiguradoParaGemini());
    }
}
