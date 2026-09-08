package cr.ac.una.reservas.control;

import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.presentation.ProgramacionActividadStrategy;
import cr.ac.una.reservas.presentation.TabActividades;
import cr.ac.una.reservas.presentation.componentes.Popup;
import cr.ac.una.reservas.service.ReporteFactory;
import cr.ac.una.reservas.service.ReservaObserver;
import cr.ac.una.reservas.service.ReservaService;

import java.awt.Frame;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActividadControl implements ReservaObserver {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT);

    private final TabActividades vista;
    private final Frame ventanaPropietaria;
    private final ReservaService reservaService;

    public ActividadControl(TabActividades vista, Frame ventanaPropietaria) {
        this(vista, ventanaPropietaria, new ReservaService());
    }

    // Constructor para pruebas
    public ActividadControl(TabActividades vista, Frame ventanaPropietaria, ReservaService reservaService) {
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.reservaService = reservaService;

        this.vista.alCargarSemana(this::cargarSemana);
        this.vista.alGenerarReporte(this::generarReporte);

        this.reservaService.agregarObservador(this);

        cargarSemana();
    }

    private void cargarSemana() {
        LocalDate fechaReferencia = vista.obtenerFechaReferencia();
        ProgramacionActividadStrategy estrategia = new ProgramacionActividadStrategy(
                reservaService, fechaReferencia, this::alHacerClickActividad
        );
        vista.mostrarMatriz(estrategia);
    }

    private void alHacerClickActividad(Reserva reserva) {
        Popup.mostrarAviso(
                ventanaPropietaria,
                Popup.Tipo.INFORMACION,
                "Actividad programada",
                "\"" + reserva.getActividad() + "\" el " + reserva.getFecha() + " de "
                        + reserva.getHoraInicio() + " a " + reserva.getHoraFin() + "."
        );
    }

    private void generarReporte() {
        LocalDate fechaReferencia = vista.obtenerFechaReferencia();
        LocalDate lunes = fechaReferencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = lunes.plusDays(6);

        List<Reserva> reservasDeLaSemana = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            reservasDeLaSemana.addAll(reservaService.listarReservasActivasEnFecha(lunes.plusDays(i)));
        }
        reservasDeLaSemana.sort((a, b) -> {
            int comparacionFecha = a.getFecha().compareTo(b.getFecha());
            if (comparacionFecha != 0) return comparacionFecha;
            return a.getHoraInicio().compareTo(b.getHoraInicio());
        });

        String subtitulo = "Semana del " + lunes.format(FORMATO_FECHA) + " al " + domingo.format(FORMATO_FECHA);

        GeneracionReporteControl.generarYGuardar(
                ventanaPropietaria,
                ReporteFactory.TipoReporte.ACTIVIDADES,
                "programacion_actividades",
                reservasDeLaSemana,
                Map.of("subtitulo", subtitulo)
        );
    }

    @Override
    public void onReservaCreada(Reserva reserva) {
        cargarSemana();
    }

    @Override
    public void onReservaCancelada(Reserva reserva) {
        cargarSemana();
    }
}
