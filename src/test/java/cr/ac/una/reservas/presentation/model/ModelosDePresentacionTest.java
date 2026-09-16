package cr.ac.una.reservas.presentation.model;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.DatosNuevaReserva;
import cr.ac.una.reservas.model.Reserva;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelosDePresentacionTest {

    @Test
    void notificaLaListaDeCategoriasAunqueElContenidoSeaIgual() {
        CategoriaModel modelo = new CategoriaModel();
        List<PropertyChangeEvent> eventos = escuchar(modelo);
        List<Categoria> lista = List.of(new Categoria("CAT-000001", "Sala para 10 personas"));

        modelo.setCategorias(lista);
        modelo.setCategorias(lista);

        assertEquals(2, eventos.size());
        assertEquals(CategoriaModel.PROP_CATEGORIAS, eventos.get(0).getPropertyName());
        assertNull(eventos.get(0).getOldValue());
    }

    @Test
    void notificaLaCategoriaSeleccionadaSoloCuandoCambia() {
        CategoriaModel modelo = new CategoriaModel();
        List<PropertyChangeEvent> eventos = escuchar(modelo);
        Categoria categoria = new Categoria("CAT-000001", "Sala para 10 personas");

        modelo.setCategoriaSeleccionada(categoria);
        modelo.setCategoriaSeleccionada(categoria);

        assertEquals(1, eventos.size());
        assertEquals(CategoriaModel.PROP_CATEGORIA_SELECCIONADA, eventos.get(0).getPropertyName());
        assertEquals(categoria, modelo.getCategoriaSeleccionada());
    }

    @Test
    void notificaLaReservaSeleccionadaParaQueLaVistaLlenElFormulario() {
        ReservaModel modelo = new ReservaModel();
        List<PropertyChangeEvent> eventos = escuchar(modelo);
        Reserva reserva = reservaDeEjemplo();

        modelo.setReservaSeleccionada(reserva);

        assertEquals(1, eventos.size());
        assertEquals(ReservaModel.PROP_RESERVA_SELECCIONADA, eventos.get(0).getPropertyName());
        assertEquals(reserva, eventos.get(0).getNewValue());
        assertEquals(reserva, modelo.getReservaSeleccionada());
    }

    @Test
    void elModeloDeReservasArrancaConListasVacias() {
        ReservaModel modelo = new ReservaModel();

        assertTrue(modelo.getReservas().isEmpty());
        assertTrue(modelo.getCategoriasDisponibles().isEmpty());
        assertNull(modelo.getReservaSeleccionada());
        assertNull(modelo.getResultadoIntento());
    }

    @Test
    void dejaDeNotificarAlOyenteRetirado() {
        CategoriaModel modelo = new CategoriaModel();
        List<PropertyChangeEvent> eventos = new ArrayList<>();
        PropertyChangeListener oyente = eventos::add;
        modelo.addPropertyChangeListener(oyente);

        modelo.removePropertyChangeListener(oyente);
        modelo.setCategorias(List.of(new Categoria("CAT-000001", "Sala para 10 personas")));

        assertTrue(eventos.isEmpty());
    }

    private static List<PropertyChangeEvent> escuchar(AbstractModel modelo) {
        List<PropertyChangeEvent> eventos = new ArrayList<>();
        modelo.addPropertyChangeListener(eventos::add);
        return eventos;
    }

    private static Reserva reservaDeEjemplo() {
        return new Reserva(new DatosNuevaReserva(
                "RES-000001",
                "111",
                "Sesion de Junta Directiva",
                LocalDate.of(2026, 8, 5),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                List.of("CAT-000001")
        ));
    }
}
