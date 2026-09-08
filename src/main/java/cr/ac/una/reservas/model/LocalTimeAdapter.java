package cr.ac.una.reservas.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

// JAXB no sabe convertir LocalTime a XML por su cuenta
public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {
    @Override
    public LocalTime unmarshal(String textoHora) {
        if (textoHora == null || textoHora.isEmpty()) {
            return null;
        }
        return LocalTime.parse(textoHora);
    }

    @Override
    public String marshal(LocalTime hora) {
        if (hora == null) {
            return null;
        }
        return hora.toString();
    }
}
