package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.presentation.mvc.TabFuncionarios;
import cr.ac.una.reservas.presentation.mvc.componentes.Popup;
import cr.ac.una.reservas.presentation.model.FuncionarioModel;
import cr.ac.una.reservas.service.FuncionarioService;
import cr.ac.una.reservas.service.ReporteFactory;
import cr.ac.una.reservas.service.ServiceFactory;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import cr.ac.una.reservas.util.ReservaAppException;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FuncionarioControl {
    private final FuncionarioModel modelo;
    private final TabFuncionarios vista;
    private final Frame ventanaPropietaria;
    private final FuncionarioService funcionarioService;

    public FuncionarioControl(FuncionarioModel modelo, TabFuncionarios vista, Frame ventanaPropietaria) {
        this(modelo, vista, ventanaPropietaria, ServiceFactory.obtenerFuncionarioService());
    }

    public FuncionarioControl(
            FuncionarioModel modelo, TabFuncionarios vista, Frame ventanaPropietaria, FuncionarioService funcionarioService
    ) {
        if (!SesionControl.obtenerInstancia().esAdministrador()) {
            throw new ReglaDeNegocioException("Esta funcionalidad solo está disponible para administradores.");
        }

        this.modelo = modelo;
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.funcionarioService = funcionarioService;

        this.vista.alGuardar(this::guardar);
        this.vista.alBorrar(this::confirmarBorrado);
        this.vista.alLimpiar(this::limpiar);
        this.vista.alBuscar(this::buscar);
        this.vista.alSeleccionarFila(this::seleccionarFila);
        this.vista.alGenerarReporte(this::generarReporte);

        refrescarListado();
    }

    private void guardar() {
        String id = vista.obtenerId();
        String nombre = vista.obtenerNombre();
        String telefono = vista.obtenerTelefono();

        if (id == null || id.isBlank()) {
            mostrarError("No se pudo guardar", "Debe ingresar una identificación (ID) para el funcionario.");
            return;
        }
        if (nombre == null || nombre.isBlank()) {
            mostrarError("No se pudo guardar", "Debe ingresar el nombre completo del funcionario.");
            return;
        }

        try {
            Funcionario funcionarioSeleccionado = modelo.getFuncionarioSeleccionado();
            if (funcionarioSeleccionado == null) {
                Funcionario nuevo = new Funcionario(id, id, nombre, telefono);
                funcionarioService.crear(nuevo);
            } else {
                funcionarioSeleccionado.setNombre(nombre);
                funcionarioSeleccionado.setTelefono(telefono);
                funcionarioService.modificar(funcionarioSeleccionado);
            }
            limpiar();
            refrescarListado();
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Guardar funcionario",
                    "El funcionario se guardó correctamente."
            );
        } catch (ReservaAppException excepcion) {
            mostrarError("No se pudo guardar", excepcion.getMessage());
        }
    }

    private void confirmarBorrado() {
        Funcionario funcionarioSeleccionado = modelo.getFuncionarioSeleccionado();
        if (funcionarioSeleccionado == null) {
            mostrarError("No se pudo borrar", "Seleccione un funcionario de la lista para borrarlo.");
            return;
        }

        Popup popup = new Popup(ventanaPropietaria);
        popup.setTipo(Popup.Tipo.ERROR);
        popup.setTitulo("Borrar funcionario");
        popup.setMensaje(
                "¿Seguro desea borrar al funcionario \"" + funcionarioSeleccionado.getNombre() + "\"? "
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
            funcionarioService.eliminar(modelo.getFuncionarioSeleccionado().getId());
            limpiar();
            refrescarListado();
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Borrar funcionario",
                    "El funcionario se borró correctamente."
            );
        } catch (ReservaAppException excepcion) {
            mostrarError("No se pudo borrar", excepcion.getMessage());
        }
    }

    private void limpiar() {
        modelo.setFuncionarioSeleccionado(null);
        vista.limpiarFormulario();
    }

    private void buscar() {
        refrescarListado();
    }

    private void seleccionarFila(int indiceFila) {
        Funcionario funcionario = vista.funcionarioEnFila(indiceFila);
        if (funcionario == null) return;
        modelo.setFuncionarioSeleccionado(funcionario);
    }

    private void generarReporte() {
        String texto = vista.obtenerTextoBusqueda();
        Map<String, String> metadatos = texto == null || texto.isBlank()
                ? null
                : Map.of("subtitulo", "Filtrado por: " + texto);
        GeneracionReporteControl.generarYGuardar(
                ventanaPropietaria,
                ReporteFactory.TipoReporte.FUNCIONARIOS,
                "funcionarios",
                modelo.getFuncionarios(),
                metadatos
        );
    }

    private void refrescarListado() {
        String texto = vista.obtenerTextoBusqueda();
        if (texto == null || texto.isBlank()) {
            modelo.setFuncionarios(funcionarioService.listarTodos());
        } else {
            modelo.setFuncionarios(filtrarPorIdONombre(texto));
        }
    }

    private List<Funcionario> filtrarPorIdONombre(String texto) {
        String textoNormalizado = texto.toLowerCase(Locale.ROOT).trim();
        List<Funcionario> resultado = new ArrayList<>();
        for (Funcionario funcionario : funcionarioService.listarTodos()) {
            boolean coincideId = funcionario.getId() != null
                    && funcionario.getId().toLowerCase(Locale.ROOT).contains(textoNormalizado);
            boolean coincideNombre = funcionario.getNombre() != null
                    && funcionario.getNombre().toLowerCase(Locale.ROOT).contains(textoNormalizado);
            if (coincideId || coincideNombre) {
                resultado.add(funcionario);
            }
        }
        return resultado;
    }

    private void mostrarError(String titulo, String mensaje) {
        Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, titulo, mensaje);
    }
}
