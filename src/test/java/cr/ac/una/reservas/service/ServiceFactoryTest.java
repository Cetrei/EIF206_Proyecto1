package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Categoria;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        CategoriaObserver observador = () -> notificaciones[0]++;
        ServiceFactory.obtenerCategoriaService().agregarObservador(observador);

        List<Categoria> antes = ServiceFactory.obtenerCategoriaService().listarTodas();
        Categoria creada = null;
        try {
            creada = ServiceFactory.obtenerCategoriaService().crear("Sala de prueba " + System.nanoTime());
            assertEquals(1, notificaciones[0]);
        } finally {
            if (creada != null) {
                ServiceFactory.obtenerCategoriaService().eliminar(creada.getId());
            }
            ServiceFactory.obtenerCategoriaService().quitarObservador(observador);
        }

        List<Categoria> despues = ServiceFactory.obtenerCategoriaService().listarTodas();
        assertEquals(antes.size(), despues.size());
        assertTrue(despues.stream().noneMatch(categoria -> categoria.getDescripcion().startsWith("Sala de prueba ")));
    }
}
