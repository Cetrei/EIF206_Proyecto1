package com.una.reservas.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String texto) {
        if (texto == null || texto.isEmpty()) {
            return null;
        }
        return LocalDate.parse(texto);
    }

    @Override
    public String marshal(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.toString();
    }
}
