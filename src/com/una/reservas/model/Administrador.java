package com.una.reservas.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Administrador del sistema. Gestiona funcionarios, categorias y recursos.
 * Sin campos adicionales por ahora respecto a Usuario.
 *
 * Ver docs/01_model.md.
 */
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
