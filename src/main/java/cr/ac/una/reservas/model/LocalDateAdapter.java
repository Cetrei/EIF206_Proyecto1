package cr.ac.una.reservas.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

// JAXB no sabe convertir LocalDate a XML por su cuenta
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(String textoFecha) {
        if (textoFecha == null || textoFecha.isEmpty()) {
            return null;
        }
        return LocalDate.parse(textoFecha);
    }

    @Override
    public String marshal(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.toString();
    }
}
