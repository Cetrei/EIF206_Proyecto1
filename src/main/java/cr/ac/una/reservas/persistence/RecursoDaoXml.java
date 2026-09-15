package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Recurso;
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

public class RecursoDaoXml implements RecursoDao {
    private final String pathRecurso;
    private final List<Recurso> recursos = new ArrayList<>();

    public RecursoDaoXml(String path) {
        this.pathRecurso = path;
        cargarDesdeXml();
    }

    private void cargarDesdeXml() {
        File file = new File(pathRecurso);
        if (!file.exists()) {
            return;
        }
        try {
            recursos.clear();
            recursos.addAll(obtenerRecursos());
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo cargar recursos desde " + pathRecurso, e);
        }
    }

    public void guardarRecursos() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(RecursosWrapper.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File archivo = new File(pathRecurso);
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs();
            }
            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                jaxbMarshaller.marshal(new RecursosWrapper(recursos), fileOut);
            }
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo guardar recursos en " + pathRecurso, e);
        }
    }

    public List<Recurso> obtenerRecursos() throws Exception {
        File file = new File(pathRecurso);
        JAXBContext jaxbContext = JAXBContext.newInstance(RecursosWrapper.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        RecursosWrapper wrapper = (RecursosWrapper) jaxbUnmarshaller.unmarshal(file);
        return wrapper.getRecursos();
    }

    @Override
    public List<Recurso> listarPorCategoria(String idCategoria) {
        List<Recurso> rec = new ArrayList<>();
        for (Recurso recurso : recursos) {
            if (recurso.getIdCategoria().equals(idCategoria)) {
                rec.add(recurso);
            }
        }
        return rec;
    }

    @Override
    public Optional<Recurso> buscarPorId(String s) {
        for (Recurso recurso : recursos) {
            if (recurso.getId().equals(s)) {
                return Optional.of(recurso);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Recurso> listarTodos() {
        return recursos;
    }

    @Override
    public void guardar(Recurso entidad) {
        if (buscarPorId(entidad.getId()).isEmpty()) {
            recursos.add(entidad);
        } else {
            for (int i = 0; i < recursos.size(); i++) {
                if (recursos.get(i).getId().equals(entidad.getId())) {
                    recursos.set(i, entidad);
                }
            }
        }
        guardarRecursos();
    }

    @Override
    public void eliminar(String s) {
        for (int i = 0; i < recursos.size(); i++) {
            if (recursos.get(i).getId().equals(s)) {
                recursos.remove(i);
                break;
            }
        }
        guardarRecursos();
    }

    @XmlRootElement(name = "recursos")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class RecursosWrapper {
        @XmlElement(name = "recurso")
        private List<Recurso> recursos = new ArrayList<>();

        public RecursosWrapper() {
        }
        public RecursosWrapper(List<Recurso> recursos) {
            this.recursos = recursos;
        }

        public List<Recurso> getRecursos() {
            return recursos;
        }
    }
}
