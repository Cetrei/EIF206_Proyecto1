package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Recurso;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecursoDaoXml implements RecursoDao{
    private final String pathRecurso;
    private final List<Recurso> recursos = new ArrayList<>();

    public RecursoDaoXml(String path) {
        this.pathRecurso = path;
    }

    public void guardarRecursos() throws Exception{
        JAXBContext jaxbContext = JAXBContext.newInstance(Recurso.class);
        FileOutputStream fileOut = new FileOutputStream(pathRecurso);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.marshal(recursos, fileOut);
        fileOut.flush();
        fileOut.close();
    }

    public List<Recurso> obtenerRecursos() throws Exception{
        File file = new File(pathRecurso);
        JAXBContext jaxbContext = JAXBContext.newInstance(Recurso.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        return (List<Recurso>) jaxbUnmarshaller.unmarshal(file);
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
        }else{
            for  (int i = 0; i< recursos.size(); i++) {
                if(recursos.get(i).getId().equals(entidad.getId())) {
                    recursos.set(i, entidad);
                }
            }
        }
    }

    @Override
    public void eliminar(String s) {
        for  (int i = 0; i< recursos.size(); i++) {
            if(recursos.get(i).getId().equals(s)) {
                recursos.remove(i);
                break;
            }
        }
    }
}
