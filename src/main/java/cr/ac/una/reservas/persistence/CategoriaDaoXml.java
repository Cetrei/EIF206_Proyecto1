package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.util.PersistenciaException;
import cr.ac.una.reservas.util.TextoBusqueda;

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

public class CategoriaDaoXml implements CategoriaDao {
    private final String pathCategoria;
    private final List<Categoria> categorias = new ArrayList<>();

    public CategoriaDaoXml(String path) {
        this.pathCategoria = path;
        cargarDesdeXml();
    }

    private void cargarDesdeXml() {
        File file = new File(pathCategoria);
        if (!file.exists()) {
            return;
        }
        try {
            categorias.clear();
            categorias.addAll(obtenerCategorias());
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo cargar categorias desde " + pathCategoria, e);
        }
    }

    public void guardarCategorias() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CategoriasWrapper.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File archivo = new File(pathCategoria);
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs();
            }
            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                jaxbMarshaller.marshal(new CategoriasWrapper(categorias), fileOut);
            }
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo guardar categorias en " + pathCategoria, e);
        }
    }

    public List<Categoria> obtenerCategorias() throws Exception {
        File file = new File(pathCategoria);
        JAXBContext jaxbContext = JAXBContext.newInstance(CategoriasWrapper.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        CategoriasWrapper wrapper = (CategoriasWrapper) jaxbUnmarshaller.unmarshal(file);
        return wrapper.getCategorias();
    }

    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        String textoNormalizado = TextoBusqueda.normalizar(texto);
        List<Categoria> coincidencias = new ArrayList<>();
        for (Categoria categoria : categorias) {
            if (TextoBusqueda.normalizar(categoria.getDescripcion()).contains(textoNormalizado)) {
                coincidencias.add(categoria);
            }
        }
        return coincidencias;
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
        return new ArrayList<>(categorias);
    }

    @Override
    public void guardar(Categoria entidad) {
        if (buscarPorId(entidad.getId()).isEmpty()) {
            categorias.add(entidad);
        } else {
            for (int i = 0; i < categorias.size(); i++) {
                if (categorias.get(i).getId().equals(entidad.getId())) {
                    categorias.set(i, entidad);
                }
            }
        }
        guardarCategorias();
    }

    @Override
    public void eliminar(String s) {
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId().equals(s)) {
                categorias.remove(i);
                break;
            }
        }
        guardarCategorias();
    }

    @XmlRootElement(name = "categorias")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class CategoriasWrapper {
        @XmlElement(name = "categoria")
        private List<Categoria> categorias = new ArrayList<>();

        public CategoriasWrapper() {
        }
        public CategoriasWrapper(List<Categoria> categorias) {
            this.categorias = categorias;
        }

        public List<Categoria> getCategorias() {
            return categorias;
        }
    }
}
