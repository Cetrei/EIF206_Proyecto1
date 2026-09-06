package cr.ac.una.reservas.control;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.presentation.TabCategorias;
import cr.ac.una.reservas.presentation.componentes.Popup;
import cr.ac.una.reservas.service.CategoriaService;
import cr.ac.una.reservas.util.ReservaAppException;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la pestana "Categorias de Recursos" (funcionalidad 4
 * del enunciado, ver docs/06_control_presentation.md). Deliberadamente
 * delgado: toma los eventos de TabCategorias, valida formato basico,
 * delega en CategoriaService y refresca la vista con el resultado.
 * <p>
 * Mantiene una copia en memoria (categoriasMostradas) de las
 * categorias actualmente listadas en la tabla, en el mismo orden que
 * sus filas, para poder resolver "el usuario hizo click en la fila i"
 * a la Categoria correspondiente sin exponer un buscarPorId publico en
 * CategoriaService (buscarPorId es intencionalmente package-private,
 * de uso interno del propio service).
 */
public class CategoriaControl {

    private final TabCategorias vista;
    private final Frame ventanaPropietaria;
    private final CategoriaService categoriaService;

    private final List<Categoria> categoriasMostradas = new ArrayList<>();
    private Categoria categoriaSeleccionada;

    public CategoriaControl(TabCategorias vista, Frame ventanaPropietaria) {
        this(vista, ventanaPropietaria, new CategoriaService());
    }

    // Constructor para pruebas: permite inyectar un CategoriaService
    // construido con Dao falsos en vez del real de DaoFactory.
    public CategoriaControl(TabCategorias vista, Frame ventanaPropietaria, CategoriaService categoriaService) {
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

    /**
     * Guarda la categoria del formulario: si no hay una seleccionada
     * (no se ha hecho click en ninguna fila desde el ultimo Limpiar)
     * crea una nueva con CategoriaService.crear, que autogenera el ID;
     * si hay una seleccionada, modifica la existente. La regla de
     * negocio (descripcion no vacia, id existente al modificar) vive
     * en CategoriaService.
     */
    private void guardar() {
        String descripcion = vista.obtenerDescripcion();

        // Validacion de formato basico (control), segun
        // docs/07_convenciones.md: la regla de negocio completa (no
        // vacia) la vuelve a validar CategoriaService.
        if (descripcion == null || descripcion.isBlank()) {
            mostrarError("No se pudo guardar", "Debe ingresar una descripción para la categoría.");
            return;
        }

        try {
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

    /**
     * Pide confirmacion antes de borrar (accion destructiva), y solo
     * si hay una categoria seleccionada. CategoriaService.eliminar
     * rechaza el borrado si existen recursos asociados a la categoria
     * (ver docs/02_service.md), en cuyo caso el mensaje de esa
     * excepcion se muestra tal cual al usuario.
     */
    private void confirmarBorrado() {
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
            categoriaService.eliminar(categoriaSeleccionada.getId());
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

    /**
     * Limpia el formulario y quita la seleccion actual, dejando la
     * vista lista para capturar una categoria nueva (el ID se
     * autogenera al guardar).
     */
    private void limpiar() {
        categoriaSeleccionada = null;
        vista.limpiarFormulario();
    }

    /**
     * Filtra el listado por la descripcion escrita en el buscador. Se
     * apoya en CategoriaService.buscarPorDescripcion (y no en un
     * filtro puramente visual de TablaDatos) para mantener la busqueda
     * consistente con la fuente de datos real, siguiendo el mismo
     * criterio que pide la funcionalidad 4 del enunciado.
     */
    private void buscar() {
        refrescarListado();
    }

    /**
     * Carga en el formulario la categoria correspondiente a la fila
     * clickeada (ver TabCategorias.alSeleccionarFila), igual que hacer
     * click en el icono de editar de la captura de referencia.
     */
    private void seleccionarFila(int indiceFila) {
        if (indiceFila < 0 || indiceFila >= categoriasMostradas.size()) {
            return;
        }
        categoriaSeleccionada = categoriasMostradas.get(indiceFila);
        vista.mostrarId(categoriaSeleccionada.getId());
        vista.mostrarDescripcion(categoriaSeleccionada.getDescripcion());
    }

    /**
     * TODO(report): ReporteFactory/ReporteCategorias todavia no
     * existen (el paquete report esta vacio, ver docs/04_report.md).
     * Cuando existan, este metodo debe pedir el generador a
     * ReporteFactory, pasarle categoriasMostradas (el listado
     * actualmente visible, respetando el filtro de busqueda activo,
     * igual criterio que el ejemplo de reporte del enunciado) y
     * escribir el PDF en una ruta elegida por el usuario (por ejemplo
     * con un JFileChooser). No se debe reemplazar este TODO por una
     * generacion de PDF improvisada en esta capa: la generacion de
     * reportes vive exclusivamente en el paquete report.
     */
    private void generarReporte() {
        Popup.mostrarAviso(
                ventanaPropietaria,
                Popup.Tipo.INFORMACION,
                "Generar reporte",
                "La generación de reportes en PDF todavía no está disponible."
        );
    }

    private void refrescarListado() {
        String texto = vista.obtenerTextoBusqueda();
        if (texto == null || texto.isBlank()) {
            cargarListado(categoriaService.listarTodas());
        } else {
            cargarListado(categoriaService.buscarPorDescripcion(texto));
        }
    }

    private void cargarListado(List<Categoria> categorias) {
        categoriasMostradas.clear();
        categoriasMostradas.addAll(categorias);

        List<List<Object>> filas = new ArrayList<>();
        for (Categoria categoria : categorias) {
            filas.add(List.of(categoria.getId(), categoria.getDescripcion(), ""));
        }
        vista.mostrarCategorias(filas);
    }

    private void mostrarError(String titulo, String mensaje) {
        Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, titulo, mensaje);
    }
}
