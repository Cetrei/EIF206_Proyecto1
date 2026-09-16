package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Funcionario;
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

public class FuncionarioDaoXml implements FuncionarioDao {
    private final String pathFuncionario;
    private final List<Funcionario> funcionarios = new ArrayList<>();

    public FuncionarioDaoXml(String path) {
        this.pathFuncionario = path;
        cargarDesdeXml();
    }

    private void cargarDesdeXml() {
        File file = new File(pathFuncionario);
        if (!file.exists()) {
            return;
        }
        try {
            funcionarios.clear();
            funcionarios.addAll(obtenerFuncionarios());
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo cargar funcionarios desde " + pathFuncionario, e);
        }
    }

    public void guardarFuncionarios() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(FuncionariosWrapper.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File archivo = new File(pathFuncionario);
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs();
            }
            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                jaxbMarshaller.marshal(new FuncionariosWrapper(funcionarios), fileOut);
            }
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo guardar funcionarios en " + pathFuncionario, e);
        }
    }

    public List<Funcionario> obtenerFuncionarios() throws Exception {
        File file = new File(pathFuncionario);
        JAXBContext jaxbContext = JAXBContext.newInstance(FuncionariosWrapper.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        FuncionariosWrapper wrapper = (FuncionariosWrapper) jaxbUnmarshaller.unmarshal(file);
        return wrapper.getFuncionarios();
    }

    @Override
    public List<Funcionario> buscarPorNombre(String nombre) {
        List<Funcionario> fun = new ArrayList<>();
        for (Funcionario funcionario : funcionarios) {
            if (funcionario.getNombre().equals(nombre)) {
                fun.add(funcionario);
                return fun;
            }
        }
        return fun;
    }

    @Override
    public Optional<Funcionario> buscarPorId(String s) {
        for (Funcionario funcionario : funcionarios) {
            if (funcionario.getId().equals(s)) {
                return Optional.of(funcionario);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Funcionario> listarTodos() {
        return new ArrayList<>(funcionarios);
    }

    @Override
    public void guardar(Funcionario entidad) {
        if (buscarPorId(entidad.getId()).isEmpty()) {
            funcionarios.add(entidad);
        } else {
            for (int i = 0; i < funcionarios.size(); i++) {
                if (funcionarios.get(i).getId().equals(entidad.getId())) {
                    funcionarios.set(i, entidad);
                }
            }
        }
        guardarFuncionarios();
    }

    @Override
    public void eliminar(String s) {
        for (int i = 0; i < funcionarios.size(); i++) {
            if (funcionarios.get(i).getId().equals(s)) {
                funcionarios.remove(i);
                break;
            }
        }
        guardarFuncionarios();
    }

    @XmlRootElement(name = "funcionarios")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class FuncionariosWrapper {
        @XmlElement(name = "funcionario")
        private List<Funcionario> funcionarios = new ArrayList<>();

        public FuncionariosWrapper() {
        }
        public FuncionariosWrapper(List<Funcionario> funcionarios) {
            this.funcionarios = funcionarios;
        }

        public List<Funcionario> getFuncionarios() {
            return funcionarios;
        }
    }
}
