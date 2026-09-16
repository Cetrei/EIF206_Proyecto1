package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.ai.DatosReservaExtraidos;
import cr.ac.una.reservas.ai.ExtractorReservaService;
import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.DatosNuevaReserva;
import cr.ac.una.reservas.model.EstadoReserva;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.model.ResultadoReserva;
import cr.ac.una.reservas.model.Usuario;
import cr.ac.una.reservas.presentation.mvc.TabReservas;
import cr.ac.una.reservas.presentation.mvc.componentes.Popup;
import cr.ac.una.reservas.presentation.model.ReservaModel;
import cr.ac.una.reservas.service.CategoriaObserver;
import cr.ac.una.reservas.service.CategoriaService;
import cr.ac.una.reservas.service.ReporteFactory;
import cr.ac.una.reservas.service.ReservaService;
import cr.ac.una.reservas.service.ServiceFactory;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.awt.Frame;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReservaControl implements CategoriaObserver {

    private final ReservaModel modelo;
    private final TabReservas vista;
    private final Frame ventanaPropietaria;
    private final ReservaService reservaService;
    private final CategoriaService categoriaService;
    private final ExtractorReservaService extractorReservaService;

    public ReservaControl(ReservaModel modelo, TabReservas vista, Frame ventanaPropietaria) {
        this(
                modelo,
                vista,
                ventanaPropietaria,
                ServiceFactory.obtenerReservaService(),
                ServiceFactory.obtenerCategoriaService(),
                new ExtractorReservaService()
        );
    }

    public ReservaControl(
            ReservaModel modelo,
            TabReservas vista,
            Frame ventanaPropietaria,
            ReservaService reservaService,
            CategoriaService categoriaService
    ) {
        this(
                modelo,
                vista,
                ventanaPropietaria,
                reservaService,
                categoriaService,
                new ExtractorReservaService()
        );
    }

    public ReservaControl(
            ReservaModel modelo,
            TabReservas vista,
            Frame ventanaPropietaria,
            ReservaService reservaService,
            CategoriaService categoriaService,
            ExtractorReservaService extractorReservaService
    ) {
        this.modelo = modelo;
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

        this.vista.mostrarEstadoIa(this.extractorReservaService.estaConfiguradoParaGemini());

        this.categoriaService.agregarObservador(this);

        refrescarCategorias();
        refrescarListado();
    }

    @Override
    public void onCategoriasCambiaron() {
        refrescarCategorias();
    }

    private void solicitarReserva() {
        Usuario usuarioActual = SesionControl.obtenerInstancia().usuarioActual();
        if (usuarioActual == null) return;

        Reserva reservaEnEdicion = reservaEditable(vista.obtenerReservaEnEdicion());
        String actividad = vista.obtenerActividad();
        LocalDate fecha = vista.obtenerFecha();
        List<Categoria> categoriasSeleccionadas = vista.obtenerCategoriasSeleccionadas();

        if (actividad == null || actividad.isBlank()) {
            mostrarError("No se pudo reservar", "Debe ingresar el nombre de la actividad.");
            return;
        }

        if (fecha == null) {
            mostrarError("No se pudo reservar", "Debe seleccionar una fecha para la reserva.");
            return;
        }

        if (categoriasSeleccionadas == null || categoriasSeleccionadas.isEmpty()) {
            mostrarError("No se pudo reservar", "Debe seleccionar al menos una categoría de recurso.");
            return;
        }

        DatosNuevaReserva datos = new DatosNuevaReserva(
                reservaEnEdicion == null ? null : reservaEnEdicion.getId(),
                usuarioActual.getId(),
                actividad,
                fecha,
                vista.obtenerHoraInicio(),
                vista.obtenerHoraFin(),
                categoriasSeleccionadas.stream().map(Categoria::getId).collect(Collectors.toList())
        );

        boolean modificando = reservaEnEdicion != null;

        try {
            ResultadoReserva resultado = modificando
                    ? reservaService.intentarModificar(datos)
                    : reservaService.intentarReservar(datos);
            modelo.setResultadoIntento(resultado);

            if (resultado.isExitoso()) {
                modelo.setReservaSeleccionada(null);
                refrescarListado();
                Popup.mostrarAviso(
                        ventanaPropietaria,
                        Popup.Tipo.CONFIRMACION,
                        modificando ? "Modificar reserva" : "Solicitar reserva",
                        modificando
                                ? "La reserva " + resultado.getReserva().getId() + " se actualizó correctamente."
                                : "La reserva se registró correctamente con el ID "
                                        + resultado.getReserva().getId() + "."
                );
            } else {
                String categoriasFallidas = resultado.getCategoriasNoDisponibles().stream()
                        .map(Categoria::getDescripcion)
                        .collect(Collectors.joining(", "));

                mostrarError(
                        "No hay disponibilidad",
                        "No hay recursos disponibles para: " + categoriasFallidas + ". "
                                + "Puede modificar la reserva e intentar de nuevo."
                );
            }
        } catch (ReglaDeNegocioException excepcion) {
            mostrarError(modificando ? "No se pudo modificar" : "No se pudo reservar", excepcion.getMessage());
        }
    }

    private void limpiar() {
        modelo.setReservaSeleccionada(null);
        vista.limpiarFormulario();
    }

    private Reserva reservaEditable(Reserva reserva) {
        if (reserva == null || reserva.getEstado() != EstadoReserva.ACTIVA) {
            return null;
        }
        Usuario usuarioActual = SesionControl.obtenerInstancia().usuarioActual();
        if (usuarioActual == null || !usuarioActual.getId().equals(reserva.getIdFuncionario())) {
            return null;
        }
        return reserva;
    }

    private void seleccionarFila(int indiceFila) {
        Reserva reserva = vista.reservaEnFila(indiceFila);
        if (reserva == null) return;
        modelo.setReservaSeleccionada(reserva);
    }

    private void confirmarCancelacion() {
        Reserva reservaSeleccionada = modelo.getReservaSeleccionada();
        if (reservaSeleccionada == null) {
            mostrarError("No se pudo cancelar", "Seleccione una reserva de la lista para cancelarla.");
            return;
        }

        Popup popup = new Popup(ventanaPropietaria);
        popup.setTipo(Popup.Tipo.ERROR);
        popup.setTitulo("Cancelar reserva");
        popup.setMensaje(
                "¿Seguro desea cancelar la reserva \"" + reservaSeleccionada.getActividad() + "\"? "
                        + "Se liberarán todos los recursos asignados y esta acción no se puede deshacer."
        );

        List<Popup.AccionPopup> acciones = new ArrayList<>();
        acciones.add(new Popup.AccionPopup("Volver", false, null));
        acciones.add(new Popup.AccionPopup("Cancelar Reserva", true, this::cancelar));
        popup.setAcciones(acciones);
        popup.mostrar();
    }

    private void cancelar() {
        try {
            reservaService.cancelarReserva(modelo.getReservaSeleccionada().getId());
            limpiar();
            refrescarListado();
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Cancelar reserva",
                    "La reserva se canceló correctamente."
            );
        } catch (ReglaDeNegocioException excepcion) {
            mostrarError("No se pudo cancelar", excepcion.getMessage());
        }
    }

    private void extraerConIA() {
        String frase = vista.obtenerFraseIa();

        if (frase == null || frase.isBlank()) {
            mostrarError("Extraer datos con IA", "Escriba primero una frase describiendo la reserva.");
            return;
        }

        try {
            List<Categoria> categorias = categoriaService.listarTodas();
            DatosReservaExtraidos datos = extractorReservaService.extraer(frase, categorias);

            boolean datoEncontrado = false;

            if (datos.getActividad() != null && !datos.getActividad().isBlank()) {
                vista.mostrarActividad(datos.getActividad());
                datoEncontrado = true;
            }

            if (datos.getFecha() != null) {
                vista.mostrarFecha(datos.getFecha());
                datoEncontrado = true;
            }

            if (datos.getHoraInicio() != null) {
                vista.mostrarHoraInicio(datos.getHoraInicio());
                datoEncontrado = true;
            }

            if (datos.getHoraFin() != null) {
                vista.mostrarHoraFin(datos.getHoraFin());
                datoEncontrado = true;
            }

            if (datos.getIdsCategoriasIdentificadas() != null && !datos.getIdsCategoriasIdentificadas().isEmpty()) {
                vista.mostrarCategoriasSeleccionadas(datos.getIdsCategoriasIdentificadas());
                datoEncontrado = true;
            }

            if (!datoEncontrado) {
                vista.mostrarEstadoIa(!extractorReservaService.fueUsadoModoBasico());

                String motivoFallo = extractorReservaService.motivoUltimoFalloPrincipal();

                String mensajeSinDatos = motivoFallo != null
                        ? "Gemini no pudo procesar la solicitud (" + motivoFallo + "). "
                                + "Se intentó con el modo básico de respaldo, pero tampoco identificó datos suficientes. "
                                + "Puede completar el formulario manualmente."
                        : extractorReservaService.fueUsadoModoBasico()
                                ? "El modo básico de respaldo no identificó datos suficientes en la frase. "
                                        + "Puede completar el formulario manualmente."
                                : "Gemini respondió correctamente pero no identificó datos suficientes en la frase. "
                                        + "Puede completar el formulario manualmente.";

                Popup.mostrarAviso(
                        ventanaPropietaria,
                        Popup.Tipo.INFORMACION,
                        "Extraer datos con IA",
                        mensajeSinDatos
                );
                return;
            }

            String mensaje;

            if (extractorReservaService.fueUsadoModoBasico()) {
                String motivoFallo = extractorReservaService.motivoUltimoFalloPrincipal();
                String detalleFallo = motivoFallo != null ? " Motivo: " + motivoFallo + "." : "";

                mensaje = "Gemini no estuvo disponible." + detalleFallo + " "
                        + "Los datos identificados fueron cargados usando el modo básico de respaldo. "
                        + "Revise la información antes de solicitar la reserva.";
            } else {
                mensaje = "Los datos identificados por Gemini fueron cargados en el formulario. "
                        + "Revise la información antes de solicitar la reserva.";
            }

            vista.mostrarEstadoIa(!extractorReservaService.fueUsadoModoBasico());
            Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.CONFIRMACION, "Datos extraídos", mensaje);
        } catch (RuntimeException excepcion) {
            vista.mostrarEstadoIa(!extractorReservaService.fueUsadoModoBasico());

            String motivoFallo = extractorReservaService.motivoUltimoFalloPrincipal();
            String detalle = motivoFallo != null ? " (" + motivoFallo + ")" : "";

            mostrarError(
                    "Extraer datos con IA",
                    "No se pudieron extraer los datos de la reserva" + detalle + ". "
                            + "Puede completar el formulario manualmente."
            );
        }
    }

    private void generarReporte() {
        GeneracionReporteControl.generarYGuardar(
                ventanaPropietaria,
                ReporteFactory.TipoReporte.RESERVAS,
                "reservas",
                modelo.getReservas(),
                metadatosFiltroActual()
        );
    }

    private Map<String, String> metadatosFiltroActual() {
        Usuario usuarioActual = SesionControl.obtenerInstancia().usuarioActual();

        if (SesionControl.obtenerInstancia().esAdministrador()) {
            return Map.of("subtitulo", "Todas las reservas del sistema");
        }

        String nombre = usuarioActual instanceof Funcionario
                ? ((Funcionario) usuarioActual).getNombre()
                : usuarioActual == null ? "" : usuarioActual.getId();

        return Map.of("subtitulo", "Funcionario: " + nombre);
    }

    private void refrescarCategorias() {
        modelo.setCategoriasDisponibles(categoriaService.listarTodas());
    }

    private void refrescarListado() {
        Usuario usuarioActual = SesionControl.obtenerInstancia().usuarioActual();

        if (usuarioActual == null) {
            modelo.setReservas(List.of());
            return;
        }

        List<Reserva> reservas = SesionControl.obtenerInstancia().esAdministrador()
                ? reservaService.listarTodasOrdenadas()
                : reservaService.listarReservasDeFuncionario(usuarioActual.getId());

        modelo.setReservas(reservas);
    }

    private void mostrarError(String titulo, String mensaje) {
        Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, titulo, mensaje);
    }
}
