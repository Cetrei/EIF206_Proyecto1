package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.Recurso;
import cr.ac.una.reservas.presentation.mvc.TabRecursos;
import cr.ac.una.reservas.presentation.mvc.componentes.Popup;
import cr.ac.una.reservas.presentation.model.RecursoModel;
import cr.ac.una.reservas.service.CategoriaObserver;
import cr.ac.una.reservas.service.CategoriaService;
import cr.ac.una.reservas.service.RecursoService;
import cr.ac.una.reservas.service.ReporteFactory;
import cr.ac.una.reservas.service.ServiceFactory;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import cr.ac.una.reservas.util.ReservaAppException;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecursoControl implements CategoriaObserver {
    private final RecursoModel modelo;
    private final TabRecursos vista;
    private final Frame ventanaPropietaria;
    private final RecursoService recursoService;
    private final CategoriaService categoriaService;

    public RecursoControl(RecursoModel modelo, TabRecursos vista, Frame ventanaPropietaria) {
        this(
                modelo,
                vista,
                ventanaPropietaria,
                ServiceFactory.obtenerRecursoService(),
                ServiceFactory.obtenerCategoriaService()
        );
    }

    public RecursoControl(
            RecursoModel modelo,
            TabRecursos vista,
            Frame ventanaPropietaria,
            RecursoService recursoService,
            CategoriaService categoriaService
    ) {
        if (!SesionControl.obtenerInstancia().esAdministrador()) {
            throw new ReglaDeNegocioException("Esta funcionalidad solo está disponible para administradores.");
        }

        this.modelo = modelo;
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.recursoService = recursoService;
        this.categoriaService = categoriaService;

        this.vista.alGuardar(this::guardar);
        this.vista.alBorrar(this::confirmarBorrado);
        this.vista.alLimpiar(this::limpiar);
        this.vista.alFiltrar(this::refrescarListado);
        this.vista.alSeleccionarFila(this::seleccionarFila);
        this.vista.alGenerarReporte(this::generarReporte);

        this.categoriaService.agregarObservador(this);

        refrescarCategorias();
        refrescarListado();
    }

    @Override
    public void onCategoriasCambiaron() {
        refrescarCategorias();
    }

    private void guardar() {
        String id = vista.obtenerId();
        Categoria categoria = vista.obtenerCategoriaSeleccionada();
        String descripcion = vista.obtenerDescripcion();

        if (id == null || id.isBlank()) {
            mostrarError("No se pudo guardar", "Debe ingresar un ID / Nº de activo para el recurso.");
            return;
        }
        if (categoria == null) {
            mostrarError("No se pudo guardar", "Debe seleccionar una categoría para el recurso.");
            return;
        }
        if (descripcion == null || descripcion.isBlank()) {
            mostrarError("No se pudo guardar", "Debe ingresar una descripción para el recurso.");
            return;
        }

        try {
            Recurso recursoSeleccionado = modelo.getRecursoSeleccionado();
            if (recursoSeleccionado == null) {
                Recurso nuevo = new Recurso(id, categoria.getId(), descripcion);
                recursoService.crear(nuevo);
            } else {
                if (!recursoSeleccionado.getId().equals(id)) {
                    mostrarError(
                            "No se pudo guardar",
                            "El ID / Nº de activo no se puede cambiar. Use Limpiar para registrar un recurso nuevo."
                    );
                    return;
                }
                recursoSeleccionado.setCategoria(categoria);
                recursoSeleccionado.setDescripcion(descripcion);
                recursoService.modificar(recursoSeleccionado);
            }
            limpiar();
            refrescarListado();
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Guardar recurso",
                    "El recurso se guardó correctamente."
            );
        } catch (ReservaAppException excepcion) {
            mostrarError("No se pudo guardar", excepcion.getMessage());
        }
    }

    private void confirmarBorrado() {
        Recurso recursoSeleccionado = modelo.getRecursoSeleccionado();
        if (recursoSeleccionado == null) {
            mostrarError("No se pudo borrar", "Seleccione un recurso de la lista para borrarlo.");
            return;
        }

        Popup popup = new Popup(ventanaPropietaria);
        popup.setTipo(Popup.Tipo.ERROR);
        popup.setTitulo("Borrar recurso");
        popup.setMensaje(
                "¿Seguro desea borrar el recurso \"" + recursoSeleccionado.getDescripcion() + "\"? "
                        + "Esta acción no se puede deshacer."
        );
        List<Popup.AccionPopup> acciones = new ArrayList<>();
        acciones.add(new Popup.AccionPopup("Cancelar", false, null));
        acciones.add(new Popup.AccionPopup("Borrar", true, this::borrar));
        popup.setAcciones(acciones);
        popup.mostrar();
    }

    private void borrar() {
        try {
            recursoService.eliminar(modelo.getRecursoSeleccionado().getId());
            limpiar();
            refrescarListado();
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Borrar recurso",
                    "El recurso se borró correctamente."
            );
        } catch (ReservaAppException excepcion) {
            mostrarError("No se pudo borrar", excepcion.getMessage());
        }
    }

    private void limpiar() {
        modelo.setRecursoSeleccionado(null);
        vista.limpiarFormulario();
    }

    private void seleccionarFila(int indiceFila) {
        Recurso recurso = vista.recursoEnFila(indiceFila);
        if (recurso == null) return;
        modelo.setRecursoSeleccionado(recurso);
    }

    private void generarReporte() {
        GeneracionReporteControl.generarYGuardar(
                ventanaPropietaria,
                ReporteFactory.TipoReporte.RECURSOS,
                "recursos",
                modelo.getRecursos(),
                metadatosFiltroActual()
        );
    }

    private Map<String, String> metadatosFiltroActual() {
        Categoria categoriaFiltro = vista.obtenerCategoriaFiltro();
        String textoBusqueda = vista.obtenerTextoBusqueda();

        List<String> partes = new ArrayList<>();
        if (categoriaFiltro != null) {
            partes.add("Categoria: " + categoriaFiltro.getDescripcion());
        }
        if (textoBusqueda != null && !textoBusqueda.isBlank()) {
            partes.add("Descripcion: " + textoBusqueda);
        }
        if (partes.isEmpty()) {
            return null;
        }
        return Map.of("subtitulo", "Filtrado por " + String.join(", ", partes));
    }

    private void refrescarCategorias() {
        modelo.setCategoriasDisponibles(categoriaService.listarTodas());
    }

    private void refrescarListado() {
        Categoria categoriaFiltro = vista.obtenerCategoriaFiltro();
        String idCategoriaFiltro = categoriaFiltro == null ? null : categoriaFiltro.getId();
        String textoBusqueda = vista.obtenerTextoBusqueda();
        modelo.setRecursos(recursoService.filtrar(idCategoriaFiltro, textoBusqueda));
    }

    private void mostrarError(String titulo, String mensaje) {
        Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, titulo, mensaje);
    }
}
