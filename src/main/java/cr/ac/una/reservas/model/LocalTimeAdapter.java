package cr.ac.una.reservas.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

// JAXB no sabe convertir LocalTime a XML por su cuenta, este adaptador
// hace de traductor entre el texto del XML y LocalTime.
public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {
    // Nombres de metodo fijados por XmlAdapter, JAXB los invoca por firma exacta.
    // Deserializa el texto leido del XML a LocalTime al cargar una Reserva.
    @Override
    public LocalTime unmarshal(String textoHora) {
        if (textoHora == null || textoHora.isEmpty()) {
            return null;
        }
        return LocalTime.parse(textoHora);
    }

    // Serializa el LocalTime a texto para escribirlo en el XML al guardar una Reserva.
    @Override
    public String marshal(LocalTime hora) {
        if (hora == null) {
            return null;
        }
        return hora.toString();
    }
}
