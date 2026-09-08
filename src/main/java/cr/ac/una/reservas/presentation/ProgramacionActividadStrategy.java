package cr.ac.una.reservas.presentation;

import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.presentation.componentes.DatosCelda;
import cr.ac.una.reservas.presentation.componentes.MatrizFillStrategy;
import cr.ac.una.reservas.presentation.componentes.MatrizPanel;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.service.ReservaService;

import java.awt.Color;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ProgramacionActividadStrategy implements MatrizFillStrategy {
    private static final DateTimeFormatter FORMATO_ENCABEZADO_DIA = DateTimeFormatter.ofPattern("dd-MMM", new Locale("es", "ES"));

    private final List<LocalDate> diasDeLaSemana;
    private final List<List<Reserva>> reservasPorDia;
    private final Consumer<Reserva> alHacerClickActividad;

    /**
     * @param reservaService usado para listar las reservas activas de cada dia de la semana que contiene fechaReferencia.
     * @param fechaReferencia cualquier fecha dentro de la semana a mostrar; se normaliza al lunes de esa semana.
     */
    public ProgramacionActividadStrategy(
            ReservaService reservaService,
            LocalDate fechaReferencia,
            Consumer<Reserva> alHacerClickActividad
    ) {
        this.alHacerClickActividad = alHacerClickActividad;
        this.diasDeLaSemana = new ArrayList<>();
        this.reservasPorDia = new ArrayList<>();

        if (fechaReferencia != null) {
            LocalDate lunes = fechaReferencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            for (int i = 0; i < 7; i++) {
                LocalDate dia = lunes.plusDays(i);
                diasDeLaSemana.add(dia);
                reservasPorDia.add(reservaService.listarReservasActivasEnFecha(dia));
            }
        }
    }

    @Override
    public List<String> obtenerColumnas() {
        List<String> nombres = new ArrayList<>();
        for (LocalDate dia : diasDeLaSemana) {
            String nombreDia = dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
            nombreDia = nombreDia.substring(0, 1).toUpperCase(Locale.ROOT) + nombreDia.substring(1);
            nombres.add(nombreDia + " (" + dia.format(FORMATO_ENCABEZADO_DIA) + ")");
        }
        return nombres;
    }

    @Override
    public DatosCelda obtenerCelda(int fila, int columna) {
        List<Reserva> actividades = actividadesEnCelda(fila, columna);
        if (actividades.isEmpty()) return null;

        List<String[]> lineas = new ArrayList<>();
        for (Reserva reserva : actividades) {
            String responsable = reserva.getIdFuncionario() == null ? "" : "(" + reserva.getIdFuncionario() + ")";
            lineas.add(new String[]{reserva.getActividad(), responsable});
        }

        Color color = actividades.size() > 1
                ? GestorTema.obtenerInstancia().temaActivo().colorAcentoSecundario()
                : GestorTema.obtenerInstancia().temaActivo().colorPrimario();
        return DatosCelda.apilada(lineas, color);
    }

    @Override
    public void alHacerClickCelda(int fila, int columna, DatosCelda datos) {
        if (datos == null) return;
        List<Reserva> actividades = actividadesEnCelda(fila, columna);
        if (!actividades.isEmpty() && alHacerClickActividad != null) {
            alHacerClickActividad.accept(actividades.get(0));
        }
    }

    private List<Reserva> actividadesEnCelda(int fila, int columna) {
        List<Reserva> resultado = new ArrayList<>();
        if (columna < 0 || columna >= reservasPorDia.size()) return resultado;
        LocalTime hora = MatrizPanel.obtenerHoraDeFila(fila);
        if (hora == null) return resultado;
        for (Reserva reserva : reservasPorDia.get(columna)) {
            if (!hora.isBefore(reserva.getHoraInicio()) && hora.isBefore(reserva.getHoraFin())) {
                resultado.add(reserva);
            }
        }
        return resultado;
    }
}
