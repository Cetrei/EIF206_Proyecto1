package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.ConfiguracionIa;
import cr.ac.una.reservas.util.PersistenciaException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

public class ConfiguracionIaDaoXml implements ConfiguracionIaDao {
    private final String pathConfiguracionIa;
    private ConfiguracionIa configuracion;

    public ConfiguracionIaDaoXml(String path) {
        this.pathConfiguracionIa = path;
        cargarDesdeXml();
    }

    private void cargarDesdeXml() {
        File archivo = new File(pathConfiguracionIa);
        if (!archivo.exists()) {
            return;
        }
        try {
            JAXBContext contexto = JAXBContext.newInstance(ConfiguracionIa.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();
            configuracion = (ConfiguracionIa) unmarshaller.unmarshal(archivo);
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo cargar la configuracion de IA desde " + pathConfiguracionIa, e);
        }
    }

    private void guardarEnXml() {
        try {
            JAXBContext contexto = JAXBContext.newInstance(ConfiguracionIa.class);
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File archivo = new File(pathConfiguracionIa);
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs();
            }
            try (FileOutputStream salida = new FileOutputStream(archivo)) {
                marshaller.marshal(configuracion, salida);
            }
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo guardar la configuracion de IA en " + pathConfiguracionIa, e);
        }
    }

    @Override
    public Optional<ConfiguracionIa> buscarPorId(String id) {
        if (configuracion == null || !configuracion.getId().equals(id)) {
            return Optional.empty();
        }
        return Optional.of(configuracion);
    }

    @Override
    public List<ConfiguracionIa> listarTodos() {
        return configuracion == null ? List.of() : List.of(configuracion);
    }

    @Override
    public void guardar(ConfiguracionIa entidad) {
        entidad.setId(ConfiguracionIa.ID_UNICO);
        configuracion = entidad;
        guardarEnXml();
    }

    @Override
    public void eliminar(String id) {
        if (configuracion != null && configuracion.getId().equals(id)) {
            configuracion = null;
            File archivo = new File(pathConfiguracionIa);
            if (archivo.exists()) {
                archivo.delete();
            }
        }
    }
}
