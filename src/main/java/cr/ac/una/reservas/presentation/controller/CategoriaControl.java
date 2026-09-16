package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.presentation.mvc.TabCategorias;
import cr.ac.una.reservas.presentation.mvc.componentes.Popup;
import cr.ac.una.reservas.presentation.model.CategoriaModel;
import cr.ac.una.reservas.service.CategoriaService;
import cr.ac.una.reservas.service.ReporteFactory;
import cr.ac.una.reservas.service.ServiceFactory;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import cr.ac.una.reservas.util.ReservaAppException;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoriaControl {
    private final CategoriaModel modelo;
    private final TabCategorias vista;
    private final Frame ventanaPropietaria;
    private final CategoriaService categoriaService;

    public CategoriaControl(CategoriaModel modelo, TabCategorias vista, Frame ventanaPropietaria) {
        this(modelo, vista, ventanaPropietaria, ServiceFactory.obtenerCategoriaService());
    }

    public CategoriaControl(
            CategoriaModel modelo, TabCategorias vista, Frame ventanaPropietaria, CategoriaService categoriaService
    ) {
        if (!SesionControl.obtenerInstancia().esAdministrador()) {
            throw new ReglaDeNegocioException("Esta funcionalidad solo está disponible para administradores.");
        }

        this.modelo = modelo;
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.categoriaService = categoriaService;

        this.vista.alGuardar(this::guardar);
        this.vista.alBorrar(this::confirmarBorrado);
        this.vista.alLimpiar(this::limpiar);
        this.vista.alBuscar(this::buscar);
        this.vista.alSeleccionarFila(this::seleccionarFila);
        this.vista.alGenerarReporte(this::generarReporte);

        refrescarListado();
    }

    private void guardar() {
        String descripcion = vista.obtenerDescripcion();

        if (descripcion == null || descripcion.isBlank()) {
            mostrarError("No se pudo guardar", "Debe ingresar una descripción para la categoría.");
            return;
        }

        descripcion = descripcion.trim();

        try {
            Categoria categoriaSeleccionada = modelo.getCategoriaSeleccionada();
            if (categoriaSeleccionada == null) {
                categoriaService.crear(descripcion);
            } else {
                categoriaSeleccionada.setDescripcion(descripcion);
                categoriaService.modificar(categoriaSeleccionada);
            }
            limpiar();
            refrescarListado();
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Guardar categoría",
                    "La categoría se guardó correctamente."
            );
        } catch (ReservaAppException excepcion) {
            mostrarError("No se pudo guardar", excepcion.getMessage());
        }
    }

    private void confirmarBorrado() {
        Categoria categoriaSeleccionada = modelo.getCategoriaSeleccionada();
        if (categoriaSeleccionada == null) {
            mostrarError("No se pudo borrar", "Seleccione una categoría de la lista para borrarla.");
            return;
        }

        Popup popup = new Popup(ventanaPropietaria);
        popup.setTipo(Popup.Tipo.ERROR);
        popup.setTitulo("Borrar categoría");
        popup.setMensaje(
                "¿Seguro desea borrar la categoría \"" + categoriaSeleccionada.getDescripcion() + "\"? "
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
            categoriaService.eliminar(modelo.getCategoriaSeleccionada().getId());
            limpiar();
            refrescarListado();
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Borrar categoría",
                    "La categoría se borró correctamente."
            );
        } catch (ReservaAppException excepcion) {
            mostrarError("No se pudo borrar", excepcion.getMessage());
        }
    }

    private void limpiar() {
        modelo.setCategoriaSeleccionada(null);
        vista.limpiarFormulario();
    }

    private void buscar() {
        refrescarListado();
    }

    private void seleccionarFila(int indiceFila) {
        Categoria categoria = vista.categoriaEnFila(indiceFila);
        if (categoria == null) return;
        modelo.setCategoriaSeleccionada(categoria);
    }

    private void generarReporte() {
        String textoBusqueda = vista.obtenerTextoBusqueda();
        String subtitulo = textoBusqueda == null || textoBusqueda.isBlank()
                ? null
                : "Filtrado por: \"" + textoBusqueda + "\"";
        GeneracionReporteControl.generarYGuardar(
                ventanaPropietaria,
                ReporteFactory.TipoReporte.CATEGORIAS,
                "categorias",
                modelo.getCategorias(),
                subtitulo == null ? null : Map.of("subtitulo", subtitulo)
        );
    }

    private void refrescarListado() {
        String texto = vista.obtenerTextoBusqueda();
        if (texto == null || texto.isBlank()) {
            modelo.setCategorias(categoriaService.listarTodas());
        } else {
            modelo.setCategorias(categoriaService.buscarPorDescripcion(texto));
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, titulo, mensaje);
    }
}
