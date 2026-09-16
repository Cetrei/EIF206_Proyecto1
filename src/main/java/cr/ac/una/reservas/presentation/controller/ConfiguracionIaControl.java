package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.ConfiguracionIa;
import cr.ac.una.reservas.presentation.mvc.TabConfiguracionIa;
import cr.ac.una.reservas.presentation.mvc.componentes.Popup;
import cr.ac.una.reservas.service.ConfiguracionIaService;
import cr.ac.una.reservas.service.ServiceFactory;
import cr.ac.una.reservas.util.ReglaDeNegocioException;
import cr.ac.una.reservas.util.ReservaAppException;

import java.awt.Frame;
import java.util.Optional;

public class ConfiguracionIaControl {
    private final TabConfiguracionIa vista;
    private final Frame ventanaPropietaria;
    private final ConfiguracionIaService configuracionIaService;

    public ConfiguracionIaControl(TabConfiguracionIa vista, Frame ventanaPropietaria) {
        this(vista, ventanaPropietaria, ServiceFactory.obtenerConfiguracionIaService());
    }

    public ConfiguracionIaControl(
            TabConfiguracionIa vista, Frame ventanaPropietaria, ConfiguracionIaService configuracionIaService
    ) {
        if (!SesionControl.obtenerInstancia().esAdministrador()) {
            throw new ReglaDeNegocioException("Esta funcionalidad solo está disponible para administradores.");
        }

        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.configuracionIaService = configuracionIaService;

        this.vista.alGuardar(this::guardar);

        cargarConfiguracionActual();
    }

    private void cargarConfiguracionActual() {
        Optional<ConfiguracionIa> configuracion = configuracionIaService.obtenerConfiguracion();
        if (configuracion.isPresent()) {
            vista.mostrarApiKey(configuracion.get().getApiKey());
            vista.mostrarModelo(configuracion.get().getModelo());
        } else {
            vista.mostrarModelo(TabConfiguracionIa.MODELO_GEMINI_3_FLASH_PREVIEW);
        }
    }

    private void guardar() {
        try {
            configuracionIaService.guardar(vista.obtenerApiKey(), vista.obtenerModelo());
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Configuración de IA",
                    "La configuración se guardó correctamente."
            );
        } catch (ReservaAppException excepcion) {
            Popup.mostrarAviso(
                    ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo guardar", excepcion.getMessage()
            );
        }
    }
}
