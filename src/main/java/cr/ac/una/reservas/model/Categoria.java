package cr.ac.una.reservas.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

// El id se autogenera en CategoriaService
@XmlRootElement(name = "categoria")
@XmlAccessorType(XmlAccessType.FIELD)
public class Categoria {
    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "descripcion")
    private String descripcion;

    public Categoria() {}
    public Categoria(String id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return String.format("Categoria{id=%s, descripcion=%s}", id, descripcion);
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Categoria)) {
            return false;
        }
        return Objects.equals(id, ((Categoria) otro).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
