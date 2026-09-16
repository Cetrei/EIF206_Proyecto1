package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.ConfiguracionIa;
import cr.ac.una.reservas.persistence.ConfiguracionIaDao;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.util.Optional;

public class ConfiguracionIaService {
    private final ConfiguracionIaDao configuracionIaDao;

    public ConfiguracionIaService() {
        this(DaoFactory.obtenerConfiguracionIaDao());
    }

    public ConfiguracionIaService(ConfiguracionIaDao configuracionIaDao) {
        this.configuracionIaDao = configuracionIaDao;
    }

    public Optional<ConfiguracionIa> obtenerConfiguracion() {
        return configuracionIaDao.buscarPorId(ConfiguracionIa.ID_UNICO);
    }

    public void guardar(String apiKey, String modelo) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ReglaDeNegocioException("La API key no puede estar vacia.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new ReglaDeNegocioException("Debe seleccionar un modelo de IA.");
        }
        configuracionIaDao.guardar(new ConfiguracionIa(apiKey.trim(), modelo.trim()));
    }
}
