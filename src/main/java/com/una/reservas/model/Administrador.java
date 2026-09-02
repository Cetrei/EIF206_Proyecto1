package com.una.reservas.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "administrador")
@XmlAccessorType(XmlAccessType.FIELD)
public class Administrador extends Usuario {

    public Administrador() {
        super();
    }

    public Administrador(String id, String clave) {
        super(id, clave, RolUsuario.ADMINISTRADOR);
    }

    @Override
    public String toString() {
        return String.format("Administrador{id=%s}", id);
    }
}
