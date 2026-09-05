package cr.ac.una.reservas.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "recurso")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recurso {

    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "idCategoria")
    private String idCategoria;

    @XmlElement(name = "descripcion")
    private String descripcion;

    // No se serializa: la resuelve service a partir de idCategoria.
    @XmlTransient
    private Categoria categoria;

    public Recurso() {
    }

    public Recurso(String id, String idCategoria, String descripcion) {
        this.id = id;
        this.idCategoria = idCategoria;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(String idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    // Tambien actualiza idCategoria para mantenerlos consistentes.
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        if (categoria != null) {
            this.idCategoria = categoria.getId();
        }
    }

    @Override
    public String toString() {
        return String.format("Recurso{id=%s, idCategoria=%s, descripcion=%s}", id, idCategoria, descripcion);
    }
}
