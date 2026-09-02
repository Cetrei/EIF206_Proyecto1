package com.una.reservas.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Funcionario del sistema. Puede ver, crear y cancelar sus propias reservas.
 *
 * Regla de negocio (se aplica en service, no aqui): al crear un funcionario,
 * la clave inicial queda igual al id. El funcionario puede cambiarla despues.
 *
 * Ver docs/01_model.md.
 */
@XmlRootElement(name = "funcionario")
@XmlAccessorType(XmlAccessType.FIELD)
public class Funcionario extends Usuario {

    @XmlElement(name = "nombre")
    private String nombre;

    @XmlElement(name = "telefono")
    private String telefono;

    public Funcionario() {
        super();
    }

    public Funcionario(String id, String clave, String nombre, String telefono) {
        super(id, clave, RolUsuario.FUNCIONARIO);
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return String.format("Funcionario{id=%s, nombre=%s, telefono=%s}", id, nombre, telefono);
    }
}
