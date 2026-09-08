package cr.ac.una.reservas.control;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.presentation.CalendarizacionRecursoStrategy;
import cr.ac.una.reservas.presentation.TabCalendarizacion;
import cr.ac.una.reservas.presentation.componentes.Popup;
import cr.ac.una.reservas.report.FilaCalendarizacion;
import cr.ac.una.reservas.service.CategoriaService;
import cr.ac.una.reservas.service.RecursoService;
import cr.ac.una.reservas.service.ReporteFactory;
import cr.ac.una.reservas.service.ReservaObserver;
import cr.ac.una.reservas.service.ReservaService;

import java.awt.Frame;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarizacionControl implements ReservaObserver {
    private final TabCalendarizacion vista;
    private final Frame ventanaPropietaria;
    private final RecursoService recursoService;
    private final ReservaService reservaService;
    private final CategoriaService categoriaService;

    public CalendarizacionControl(TabCalendarizacion vista, Frame ventanaPropietaria) {
        this(vista, ventanaPropietaria, new RecursoService(), new ReservaService(), new CategoriaService());
    }

    // Constructor para pruebas
    public CalendarizacionControl(
            TabCalendarizacion vista,
            Frame ventanaPropietaria,
            RecursoService recursoService,
            ReservaService reservaService,
            CategoriaService categoriaService
    ) {
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.recursoService = recursoService;
        this.reservaService = reservaService;
        this.categoriaService = categoriaService;

        this.vista.alCargarMatriz(this::cargarMatriz);
        this.vista.alGenerarReporte(this::generarReporte);

        this.reservaService.agregarObservador(this);

        refrescarCategorias();
        cargarMatriz();
    }

    private void cargarMatriz() {
        LocalDate fecha = vista.obtenerFecha();
        Categoria categoria = vista.obtenerCategoriaSeleccionada();

        CalendarizacionRecursoStrategy estrategia = new CalendarizacionRecursoStrategy(
                recursoService, reservaService, categoria, fecha, this::alHacerClickCeldaOcupada
        );
        vista.mostrarMatriz(estrategia);
    }

    private void alHacerClickCeldaOcupada(Recurso recurso, Reserva reserva) {
        Popup.mostrarAviso(
                ventanaPropietaria,
                Popup.Tipo.INFORMACION,
                "Recurso ocupado",
                "\"" + recurso.getDescripcion() + "\" está reservado para \"" + reserva.getActividad() + "\" de "
                        + reserva.getHoraInicio() + " a " + reserva.getHoraFin() + "."
        );
    }

    private void generarReporte() {
        LocalDate fecha = vista.obtenerFecha();
        Categoria categoria = vista.obtenerCategoriaSeleccionada();
        String subtitulo = "Fecha: " + fecha
                + (categoria == null ? "" : " · Categoría: " + categoria.getDescripcion());

        List<FilaCalendarizacion> filas = armarFilasReporte(categoria, fecha);

        GeneracionReporteControl.generarYGuardar(
                ventanaPropietaria,
                ReporteFactory.TipoReporte.CALENDARIZACION,
                "calendarizacion",
                filas,
                Map.of("subtitulo", subtitulo)
        );
    }

    private List<FilaCalendarizacion> armarFilasReporte(Categoria categoria, LocalDate fecha) {
        List<FilaCalendarizacion> filas = new ArrayList<>();
        if (categoria == null || fecha == null) return filas;

        List<Recurso> recursos = recursoService.listarPorCategoria(categoria.getId());
        List<Reserva> reservasDelDia = reservaService.listarReservasActivasEnFecha(fecha);

        for (Recurso recurso : recursos) {
            List<Reserva> reservasDelRecurso = new ArrayList<>();
            for (Reserva reserva : reservasDelDia) {
                if (reserva.getIdsRecursosAsignados().contains(recurso.getId())) {
                    reservasDelRecurso.add(reserva);
                }
            }

            if (reservasDelRecurso.isEmpty()) {
                filas.add(new FilaCalendarizacion(recurso.getDescripcion(), "Todo el día", "Disponible"));
                continue;
            }
            for (Reserva reserva : reservasDelRecurso) {
                filas.add(new FilaCalendarizacion(
                        recurso.getDescripcion(),
                        reserva.getHoraInicio() + " - " + reserva.getHoraFin(),
                        reserva.getActividad()
                ));
            }
        }
        return filas;
    }

    private void refrescarCategorias() {
        vista.cargarCategorias(categoriaService.listarTodas());
    }

    @Override
    public void onReservaCreada(Reserva reserva) {
        cargarMatriz();
    }

    @Override
    public void onReservaCancelada(Reserva reserva) {
        cargarMatriz();
    }
}
