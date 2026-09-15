package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Categoria;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaDaoXml implements CategoriaDao {
    private final String pathCategoria;
    private final List<Categoria> categorias = new ArrayList<>();

    public CategoriaDaoXml(String path) {
        this.pathCategoria = path;
    }

    public void guardarCategorias() throws Exception{
        JAXBContext jaxbContext = JAXBContext.newInstance(Categoria.class);
        FileOutputStream fileOut = new FileOutputStream(pathCategoria);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.marshal(categorias, fileOut);
        fileOut.flush();
        fileOut.close();
    }

    public List<Categoria> obtenerCategorias() throws Exception{
        File file = new File(pathCategoria);
        JAXBContext jaxbContext = JAXBContext.newInstance(Categoria.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        return (List<Categoria>) jaxbUnmarshaller.unmarshal(file);
    }

    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        List<Categoria> cat = new ArrayList<>();
        for (Categoria categoria : categorias) {
            if (categoria.getDescripcion().equals(texto)) {
                cat.add(categoria);
                return cat;
            }
        }
        return cat;
    }

    @Override
    public Optional<Categoria> buscarPorId(String s) {
        for (Categoria categoria : categorias) {
            if (categoria.getId().equals(s)) {
                return Optional.of(categoria);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Categoria> listarTodos() {
        return categorias;
    }

    @Override
    public void guardar(Categoria entidad) {
        if (buscarPorId(entidad.getId()).isEmpty()) {
            categorias.add(entidad);
        }else{
            for  (int i = 0; i< categorias.size(); i++) {
                if(categorias.get(i).getId().equals(entidad.getId())) {
                    categorias.set(i, entidad);
                }
            }
        }
    }

    @Override
    public void eliminar(String s) {
        for  (int i = 0; i< categorias.size(); i++) {
            if(categorias.get(i).getId().equals(s)) {
                categorias.remove(i);
                break;
            }
        }
    }
}
