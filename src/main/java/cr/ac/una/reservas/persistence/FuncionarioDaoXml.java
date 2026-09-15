package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Funcionario;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
    }

    public void guardarFuncionarios() throws Exception{
        JAXBContext jaxbContext = JAXBContext.newInstance(Funcionario.class);
        FileOutputStream fileOut = new FileOutputStream(pathFuncionario);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.marshal(funcionarios, fileOut);
        fileOut.flush();
        fileOut.close();
    }

    public List<Funcionario> obtenerFuncionarios() throws Exception{
        File file = new File(pathFuncionario);
        JAXBContext jaxbContext = JAXBContext.newInstance(Funcionario.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        return (List<Funcionario>) jaxbUnmarshaller.unmarshal(file);
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
        return funcionarios;
    }

    @Override
    public void guardar(Funcionario entidad) {
        if (buscarPorId(entidad.getId()).isEmpty()) {
            funcionarios.add(entidad);
        }else{
            for  (int i = 0; i< funcionarios.size(); i++) {
                if(funcionarios.get(i).getId().equals(entidad.getId())) {
                    funcionarios.set(i, entidad);
                }
            }
        }
    }

    @Override
    public void eliminar(String s) {
        for  (int i = 0; i< funcionarios.size(); i++) {
            if(funcionarios.get(i).getId().equals(s)) {
                funcionarios.remove(i);
                break;
            }
        }
    }
}
