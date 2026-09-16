package cr.ac.una.reservas.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ServiceFactoryTest {

    @Test
    void devuelveSiempreLaMismaInstanciaDeCategoriaService() {
        assertSame(ServiceFactory.obtenerCategoriaService(), ServiceFactory.obtenerCategoriaService());
    }

    @Test
    void devuelveSiempreLaMismaInstanciaDeReservaService() {
        assertSame(ServiceFactory.obtenerReservaService(), ServiceFactory.obtenerReservaService());
    }

    @Test
    void unObservadorRegistradoEnUnaLlamadaSeEnteraDeCambiosNotificadosPorOtra() {
        int[] notificaciones = {0};
        ServiceFactory.obtenerCategoriaService().agregarObservador(() -> notificaciones[0]++);

        ServiceFactory.obtenerCategoriaService().crear("Sala de prueba " + System.nanoTime());

        assertEquals(1, notificaciones[0]);
    }
}
