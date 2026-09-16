package cr.ac.una.reservas.presentation.mvc;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.presentation.mvc.componentes.DatosCelda;
import cr.ac.una.reservas.presentation.mvc.componentes.MatrizFillStrategy;
import cr.ac.una.reservas.presentation.mvc.componentes.MatrizPanel;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.service.RecursoService;
import cr.ac.una.reservas.service.ReservaService;

import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class CalendarizacionRecursoStrategy implements MatrizFillStrategy {
    private final List<Recurso> recursos;
    private final List<Reserva> reservasDelDia;
    private final BiConsumer<Recurso, Reserva> alHacerClickCeldaOcupada;

    public CalendarizacionRecursoStrategy(
            RecursoService recursoService,
            ReservaService reservaService,
            Categoria categoria,
            LocalDate fecha,
            BiConsumer<Recurso, Reserva> alHacerClickCeldaOcupada
    ) {
        this.alHacerClickCeldaOcupada = alHacerClickCeldaOcupada;
        this.recursos = categoria == null
                ? new ArrayList<>()
                : recursoService.listarPorCategoria(categoria.getId());
        this.reservasDelDia = fecha == null
                ? new ArrayList<>()
                : reservaService.listarReservasActivasEnFecha(fecha);
    }

    @Override
    public List<String> obtenerColumnas() {
        List<String> nombres = new ArrayList<>();
        for (Recurso recurso : recursos) {
            nombres.add(recurso.getDescripcion());
        }
        return nombres;
    }

    @Override
    public DatosCelda obtenerCelda(int fila, int columna) {
        if (columna < 0 || columna >= recursos.size()) return null;
        Recurso recurso = recursos.get(columna);
        LocalTime hora = MatrizPanel.obtenerHoraDeFila(fila);
        if (hora == null) return null;

        Reserva reserva = buscarReservaQueOcupa(recurso, hora);
        if (reserva == null) return null;

        Color colorOcupado = GestorTema.obtenerInstancia().temaActivo().colorPrimario();
        return new DatosCelda(
                reserva.getActividad(),
                reserva.getHoraInicio() + " - " + reserva.getHoraFin(),
                colorOcupado
        );
    }

    @Override
    public void alHacerClickCelda(int fila, int columna, DatosCelda datos) {
        if (datos == null || columna < 0 || columna >= recursos.size()) return;
        Recurso recurso = recursos.get(columna);
        LocalTime hora = MatrizPanel.obtenerHoraDeFila(fila);
        if (hora == null) return;
        Reserva reserva = buscarReservaQueOcupa(recurso, hora);
        if (reserva != null && alHacerClickCeldaOcupada != null) {
            alHacerClickCeldaOcupada.accept(recurso, reserva);
        }
    }

    private Reserva buscarReservaQueOcupa(Recurso recurso, LocalTime hora) {
        for (Reserva reserva : reservasDelDia) {
            if (!reserva.getIdsRecursosAsignados().contains(recurso.getId())) {
                continue;
            }
            if (!hora.isBefore(reserva.getHoraInicio()) && hora.isBefore(reserva.getHoraFin())) {
                return reserva;
            }
        }
        return null;
    }
}
