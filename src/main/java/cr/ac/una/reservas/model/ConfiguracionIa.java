package cr.ac.una.reservas.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuracionIa")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfiguracionIa {
    public static final String ID_UNICO = "CONFIG-IA";

    @XmlElement(name = "id")
    private String id = ID_UNICO;

    @XmlElement(name = "apiKey")
    private String apiKey;

    @XmlElement(name = "modelo")
    private String modelo;

    public ConfiguracionIa() {
    }

    public ConfiguracionIa(String apiKey, String modelo) {
        this.apiKey = apiKey;
        this.modelo = modelo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
}
