package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.util.PersistenciaException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdministradorDaoXml implements AdministradorDao {
    private final String pathAdministradores;
    private final List<Administrador> administradores = new ArrayList<>();

    public AdministradorDaoXml(String path) {
        this.pathAdministradores = path;
        cargarDesdeXml();
    }

    private void cargarDesdeXml() {
        File file = new File(pathAdministradores);
        if (!file.exists()) {
            return;
        }
        try {
            administradores.clear();
            administradores.addAll(obtenerAdministradores());
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo cargar administradores desde " + pathAdministradores, e);
        }
    }

    public void guardarAdministradores() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(AdministradoresWrapper.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File archivo = new File(pathAdministradores);
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs();
            }
            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                jaxbMarshaller.marshal(new AdministradoresWrapper(administradores), fileOut);
            }
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo guardar administradores en " + pathAdministradores, e);
        }
    }

    public List<Administrador> obtenerAdministradores() throws Exception {
        File file = new File(pathAdministradores);
        JAXBContext jaxbContext = JAXBContext.newInstance(AdministradoresWrapper.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        AdministradoresWrapper wrapper = (AdministradoresWrapper) jaxbUnmarshaller.unmarshal(file);
        return wrapper.getAdministradores();
    }

    @Override
    public Optional<Administrador> buscarPorId(String s) {
        for (Administrador administrador : administradores) {
            if (administrador.getId().equals(s)) {
                return Optional.of(administrador);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Administrador> listarTodos() {
        return new ArrayList<>(administradores);
    }

    @Override
    public void guardar(Administrador entidad) {
        if (buscarPorId(entidad.getId()).isEmpty()) {
            administradores.add(entidad);
        } else {
            for (int i = 0; i < administradores.size(); i++) {
                if (administradores.get(i).getId().equals(entidad.getId())) {
                    administradores.set(i, entidad);
                }
            }
        }
        guardarAdministradores();
    }

    @Override
    public void eliminar(String s) {
        for (int i = 0; i < administradores.size(); i++) {
            if (administradores.get(i).getId().equals(s)) {
                administradores.remove(i);
                break;
            }
        }
        guardarAdministradores();
    }

    @XmlRootElement(name = "administradores")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class AdministradoresWrapper {
        @XmlElement(name = "administrador")
        private List<Administrador> administradores = new ArrayList<>();

        public AdministradoresWrapper() {
        }
        public AdministradoresWrapper(List<Administrador> administradores) {
            this.administradores = administradores;
        }

        public List<Administrador> getAdministradores() {
            return administradores;
        }
    }
}
