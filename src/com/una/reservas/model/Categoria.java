package com.una.reservas.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Categoria de recurso, por ejemplo "Sala para 10 personas" o "Laptop windows 11".
 * El id se autogenera con formato tipo CAT-000001.
 *
 * Ver docs/01_model.md.
 */
@XmlRootElement(name = "categoria")
@XmlAccessorType(XmlAccessType.FIELD)
public class Categoria {

    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "descripcion")
    private String descripcion;

    public Categoria() {
    }

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
}
