package cr.ac.una.reservas.control;

import cr.ac.una.reservas.ai.DatosReservaExtraidos;
import cr.ac.una.reservas.ai.ExtractorReservaService;
import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.DatosNuevaReserva;
import cr.ac.una.reservas.model.EstadoReserva;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.model.ResultadoReserva;
import cr.ac.una.reservas.model.Usuario;
import cr.ac.una.reservas.presentation.TabReservas;
import cr.ac.una.reservas.presentation.componentes.Popup;
import cr.ac.una.reservas.service.CategoriaService;
import cr.ac.una.reservas.service.ReporteFactory;
import cr.ac.una.reservas.service.ReservaService;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.awt.Frame;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReservaControl {

    private final TabReservas vista;
    private final Frame ventanaPropietaria;
    private final ReservaService reservaService;
    private final CategoriaService categoriaService;
    private final ExtractorReservaService extractorReservaService;

    private final List<Reserva> reservasMostradas = new ArrayList<>();
    private Reserva reservaSeleccionada;

    public ReservaControl(TabReservas vista, Frame ventanaPropietaria) {
        this(
                vista,
                ventanaPropietaria,
                new ReservaService(),
                new CategoriaService(),
                new ExtractorReservaService()
        );
    }

    // Constructor para pruebas
    public ReservaControl(
            TabReservas vista,
            Frame ventanaPropietaria,
            ReservaService reservaService,
            CategoriaService categoriaService
    ) {
        this(
                vista,
                ventanaPropietaria,
                reservaService,
                categoriaService,
                new ExtractorReservaService()
        );
    }

    // Constructor que permite inyectar el extractor.
    public ReservaControl(
            TabReservas vista,
            Frame ventanaPropietaria,
            ReservaService reservaService,
            CategoriaService categoriaService,
            ExtractorReservaService extractorReservaService
    ) {
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.reservaService = reservaService;
        this.categoriaService = categoriaService;
        this.extractorReservaService = extractorReservaService;

        this.vista.alSolicitarReserva(this::solicitarReserva);
        this.vista.alLimpiar(this::limpiar);
        this.vista.alCancelarSeleccionada(this::confirmarCancelacion);
        this.vista.alSeleccionarFila(this::seleccionarFila);
        this.vista.alGenerarReporte(this::generarReporte);
        this.vista.alExtraerConIA(this::extraerConIA);

        refrescarCategorias();
        refrescarListado();
    }

    private void solicitarReserva() {
        Usuario usuarioActual = SesionControl.obtenerInstancia().usuarioActual();
        if (usuarioActual == null) return;

        String actividad = vista.obtenerActividad();
        LocalDate fecha = vista.obtenerFecha();
        List<Categoria> categoriasSeleccionadas = vista.obtenerCategoriasSeleccionadas();

        if (actividad == null || actividad.isBlank()) {
            mostrarError(
                    "No se pudo reservar",
                    "Debe ingresar el nombre de la actividad."
            );
            return;
        }

        if (fecha == null) {
            mostrarError(
                    "No se pudo reservar",
                    "Debe seleccionar una fecha para la reserva."
            );
            return;
        }

        if (categoriasSeleccionadas == null
                || categoriasSeleccionadas.isEmpty()) {

            mostrarError(
                    "No se pudo reservar",
                    "Debe seleccionar al menos una categoría de recurso."
            );
            return;
        }

        DatosNuevaReserva datos = new DatosNuevaReserva(
                null,
                usuarioActual.getId(),
                actividad,
                fecha,
                vista.obtenerHoraInicio(),
                vista.obtenerHoraFin(),
                categoriasSeleccionadas.stream()
                        .map(Categoria::getId)
                        .collect(Collectors.toList())
        );

        try {
            ResultadoReserva resultado =
                    reservaService.intentarReservar(datos);

            if (resultado.isExitoso()) {
                limpiar();
                refrescarListado();

                Popup.mostrarAviso(
                        ventanaPropietaria,
                        Popup.Tipo.CONFIRMACION,
                        "Solicitar reserva",
                        "La reserva se registró correctamente con el ID "
                                + resultado.getReserva().getId()
                                + "."
                );

            } else {
                String categoriasFallidas =
                        resultado.getCategoriasNoDisponibles()
                                .stream()
                                .map(Categoria::getDescripcion)
                                .collect(Collectors.joining(", "));

                mostrarError(
                        "No hay disponibilidad",
                        "No hay recursos disponibles para: "
                                + categoriasFallidas + ". "
                                + "Puede modificar la reserva e intentar de nuevo."
                );
            }

        } catch (ReglaDeNegocioException excepcion) {
            mostrarError(
                    "No se pudo reservar",
                    excepcion.getMessage()
            );
        }
    }

    private void limpiar() {
        reservaSeleccionada = null;
        vista.limpiarFormulario();
    }

    private void seleccionarFila(int indiceFila) {
        if (indiceFila < 0
                || indiceFila >= reservasMostradas.size()) {
            return;
        }

        reservaSeleccionada =
                reservasMostradas.get(indiceFila);
    }

    private void confirmarCancelacion() {
        if (reservaSeleccionada == null) {
            mostrarError(
                    "No se pudo cancelar",
                    "Seleccione una reserva de la lista para cancelarla."
            );
            return;
        }

        Popup popup = new Popup(ventanaPropietaria);

        popup.setTipo(Popup.Tipo.ERROR);
        popup.setTitulo("Cancelar reserva");

        popup.setMensaje(
                "¿Seguro desea cancelar la reserva \""
                        + reservaSeleccionada.getActividad()
                        + "\"? "
                        + "Se liberarán todos los recursos asignados "
                        + "y esta acción no se puede deshacer."
        );

        List<Popup.AccionPopup> acciones =
                new ArrayList<>();

        acciones.add(
                new Popup.AccionPopup(
                        "Volver",
                        false,
                        null
                )
        );

        acciones.add(
                new Popup.AccionPopup(
                        "Cancelar Reserva",
                        true,
                        this::cancelar
                )
        );

        popup.setAcciones(acciones);
        popup.mostrar();
    }

    private void cancelar() {
        try {
            reservaService.cancelarReserva(
                    reservaSeleccionada.getId()
            );

            limpiar();
            refrescarListado();

            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Cancelar reserva",
                    "La reserva se canceló correctamente."
            );

        } catch (ReglaDeNegocioException excepcion) {
            mostrarError(
                    "No se pudo cancelar",
                    excepcion.getMessage()
            );
        }
    }

    private void extraerConIA() {
        String frase = vista.obtenerFraseIa();

        if (frase == null || frase.isBlank()) {
            mostrarError(
                    "Extraer datos con IA",
                    "Escriba primero una frase describiendo la reserva."
            );
            return;
        }

        try {
            List<Categoria> categorias =
                    categoriaService.listarTodas();

            DatosReservaExtraidos datos =
                    extractorReservaService.extraer(
                            frase,
                            categorias
                    );

            boolean datoEncontrado = false;

            if (datos.getActividad() != null
                    && !datos.getActividad().isBlank()) {

                vista.mostrarActividad(
                        datos.getActividad()
                );

                datoEncontrado = true;
            }

            if (datos.getFecha() != null) {
                vista.mostrarFecha(
                        datos.getFecha()
                );

                datoEncontrado = true;
            }

            if (datos.getHoraInicio() != null) {
                vista.mostrarHoraInicio(
                        datos.getHoraInicio()
                );

                datoEncontrado = true;
            }

            if (datos.getHoraFin() != null) {
                vista.mostrarHoraFin(
                        datos.getHoraFin()
                );

                datoEncontrado = true;
            }

            if (datos.getIdsCategoriasIdentificadas() != null
                    && !datos.getIdsCategoriasIdentificadas().isEmpty()) {

                vista.mostrarCategoriasSeleccionadas(
                        datos.getIdsCategoriasIdentificadas()
                );

                datoEncontrado = true;
            }

            if (!datoEncontrado) {
                Popup.mostrarAviso(
                        ventanaPropietaria,
                        Popup.Tipo.INFORMACION,
                        "Extraer datos con IA",
                        "No se pudieron identificar datos suficientes. "
                                + "Puede completar el formulario manualmente."
                );

                return;
            }

            String mensaje;

            if (extractorReservaService.fueUsadoModoBasico()) {

                mensaje =
                        "Los datos identificados fueron cargados usando "
                                + "el modo básico de respaldo. "
                                + "Revise la información antes de solicitar la reserva.";

            } else {

                mensaje =
                        "Los datos identificados por Gemini fueron cargados "
                                + "en el formulario. "
                                + "Revise la información antes de solicitar la reserva.";
            }

            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Datos extraídos",
                    mensaje
            );

        } catch (RuntimeException excepcion) {
            mostrarError(
                    "Extraer datos con IA",
                    "No se pudieron extraer los datos de la reserva."
            );
        }
    }

    private void generarReporte() {
        GeneracionReporteControl.generarYGuardar(
                ventanaPropietaria,
                ReporteFactory.TipoReporte.RESERVAS,
                "reservas",
                reservasMostradas,
                metadatosFiltroActual()
        );
    }

    private Map<String, String> metadatosFiltroActual() {
        Usuario usuarioActual =
                SesionControl.obtenerInstancia().usuarioActual();

        if (SesionControl.obtenerInstancia().esAdministrador()) {
            return Map.of(
                    "subtitulo",
                    "Todas las reservas del sistema"
            );
        }

        String nombre =
                usuarioActual instanceof Funcionario
                        ? ((Funcionario) usuarioActual).getNombre()
                        : usuarioActual == null
                        ? ""
                        : usuarioActual.getId();

        return Map.of(
                "subtitulo",
                "Funcionario: " + nombre
        );
    }

    private void refrescarCategorias() {
        vista.cargarCategorias(
                categoriaService.listarTodas()
        );
    }

    private void refrescarListado() {
        Usuario usuarioActual =
                SesionControl.obtenerInstancia().usuarioActual();

        if (usuarioActual == null) {
            cargarListado(List.of());
            return;
        }

        List<Reserva> reservas =
                SesionControl.obtenerInstancia().esAdministrador()
                        ? reservaService.listarTodasOrdenadas()
                        : reservaService.listarReservasDeFuncionario(
                        usuarioActual.getId()
                );

        cargarListado(reservas);
    }

    private void cargarListado(List<Reserva> reservas) {
        reservasMostradas.clear();
        reservasMostradas.addAll(reservas);

        List<List<Object>> filas =
                new ArrayList<>();

        for (Reserva reserva : reservas) {
            filas.add(
                    List.of(
                            reserva.getId(),
                            reserva.getActividad(),
                            reserva.getFecha() == null
                                    ? ""
                                    : reserva.getFecha().toString(),
                            horario(reserva),
                            String.join(
                                    ", ",
                                    reserva.getIdsRecursosAsignados()
                            ),
                            reserva.getEstado() == EstadoReserva.ACTIVA
                                    ? "ACTIVA"
                                    : "CANCELADA"
                    )
            );
        }

        vista.mostrarReservas(filas);
    }

    private static String horario(Reserva reserva) {
        if (reserva.getHoraInicio() == null
                || reserva.getHoraFin() == null) {
            return "";
        }

        return reserva.getHoraInicio()
                + " - "
                + reserva.getHoraFin();
    }

    private void mostrarError(
            String titulo,
            String mensaje) {

        Popup.mostrarAviso(
                ventanaPropietaria,
                Popup.Tipo.ERROR,
                titulo,
                mensaje
        );
    }
}
