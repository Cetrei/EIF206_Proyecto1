package cr.ac.una.reservas.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Administrador.class, Funcionario.class})
public abstract class Usuario {

    @XmlElement(name = "id")
    protected String id;

    @XmlElement(name = "clave")
    protected String clave;

    @XmlElement(name = "rol")
    protected RolUsuario rol;

    public Usuario() {
    }

    public Usuario(String id, String clave, RolUsuario rol) {
        this.id = id;
        this.clave = clave;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return String.format("Usuario{id=%s, rol=%s}", id, rol);
    }
}
