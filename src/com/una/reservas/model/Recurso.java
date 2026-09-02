package com.una.reservas.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Recurso fisico reservable, por ejemplo "Sala 1 primer piso" o "Laptop #238715".
 * El id lo asigna la persona que lo registra (numero de activo, por ejemplo).
 *
 * Se guarda la referencia a la categoria por su id (idCategoria), no el objeto
 * Categoria completo embebido, para evitar duplicar datos en el XML. Ver la nota
 * sobre referencias entre entidades en docs/01_model.md. El campo categoria
 * (objeto completo) es transient para JAXB y lo resuelve la capa de service
 * cuando se necesite el objeto Categoria completo, no la persistencia.
 */
@XmlRootElement(name = "recurso")
@XmlAccessorType(XmlAccessType.FIELD)
public class Recurso {

    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "idCategoria")
    private String idCategoria;

    @XmlElement(name = "descripcion")
    private String descripcion;

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

    /**
     * Objeto Categoria completo, resuelto por service a partir de idCategoria.
     * No es persistido directamente por JAXB (ver anotacion XmlTransient).
     */
    public Categoria getCategoria() {
        return categoria;
    }

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
