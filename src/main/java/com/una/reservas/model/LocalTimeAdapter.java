package com.una.reservas.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalTime;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

    @Override
    public LocalTime unmarshal(String texto) {
        if (texto == null || texto.isEmpty()) {
            return null;
        }
        return LocalTime.parse(texto);
    }

    @Override
    public String marshal(LocalTime hora) {
        if (hora == null) {
            return null;
        }
        return hora.toString();
    }
}
