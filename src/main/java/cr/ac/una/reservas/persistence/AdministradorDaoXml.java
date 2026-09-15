package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Administrador;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
    }

    public void guardarAdministradores() throws Exception{
        JAXBContext jaxbContext = JAXBContext.newInstance(Administrador.class);
        FileOutputStream fileOut = new FileOutputStream(pathAdministradores);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.marshal(administradores, fileOut);
        fileOut.flush();
        fileOut.close();
    }

    public List<Administrador> obtenerAdministradores() throws Exception{
        File file = new File(pathAdministradores);
        JAXBContext jaxbContext = JAXBContext.newInstance(Administrador.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        return (List<Administrador>) jaxbUnmarshaller.unmarshal(file);
    }


    @Override
    public Optional<Administrador> buscarPorId(String s) {
        for (Administrador administrador: administradores) {
            if (administrador.getId().equals(s)) {
                return Optional.of(administrador);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Administrador> listarTodos() {
        return administradores;
    }

    @Override
    public void guardar(Administrador entidad) {
        if (buscarPorId(entidad.getId()).isEmpty()) {
            administradores.add(entidad);
        }else{
            for  (int i = 0; i< administradores.size(); i++) {
                if(administradores.get(i).getId().equals(entidad.getId())) {
                    administradores.set(i, entidad);
                }
            }
        }
    }

    @Override
    public void eliminar(String s) {
        for  (int i = 0; i< administradores.size(); i++) {
            if(administradores.get(i).getId().equals(s)) {
                administradores.remove(i);
                break;
            }
        }
    }
}
