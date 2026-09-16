package cr.ac.una.reservas.presentation.model;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.model.ResultadoReserva;

import java.util.List;

public class ReservaModel extends AbstractModel {

    public static final String PROP_CATEGORIAS_DISPONIBLES = "categoriasDisponibles";
    public static final String PROP_RESERVAS = "reservas";
    public static final String PROP_RESERVA_SELECCIONADA = "reservaSeleccionada";
    public static final String PROP_RESULTADO_INTENTO = "resultadoIntento";

    private List<Categoria> categoriasDisponibles = List.of();
    private List<Reserva> reservas = List.of();
    private Reserva reservaSeleccionada;
    private ResultadoReserva resultadoIntento;

    public List<Categoria> getCategoriasDisponibles() {
        return categoriasDisponibles;
    }

    public void setCategoriasDisponibles(List<Categoria> nuevaLista) {
        List<Categoria> anterior = this.categoriasDisponibles;
        this.categoriasDisponibles = nuevaLista;
        notificarCambioForzado(PROP_CATEGORIAS_DISPONIBLES, anterior, nuevaLista);
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> nuevaLista) {
        List<Reserva> anterior = this.reservas;
        this.reservas = nuevaLista;
        notificarCambioForzado(PROP_RESERVAS, anterior, nuevaLista);
    }

    public Reserva getReservaSeleccionada() {
        return reservaSeleccionada;
    }

    public void setReservaSeleccionada(Reserva nueva) {
        Reserva anterior = this.reservaSeleccionada;
        this.reservaSeleccionada = nueva;
        notificarCambio(PROP_RESERVA_SELECCIONADA, anterior, nueva);
    }

    public ResultadoReserva getResultadoIntento() {
        return resultadoIntento;
    }

    public void setResultadoIntento(ResultadoReserva nuevo) {
        ResultadoReserva anterior = this.resultadoIntento;
        this.resultadoIntento = nuevo;
        notificarCambio(PROP_RESULTADO_INTENTO, anterior, nuevo);
    }
}
