package com.una.reservas.ai;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de intentar extraer los datos de una reserva a partir de una frase
 * en lenguaje natural. Cualquier campo que no se haya podido identificar en la
 * frase queda nulo o vacio, y el usuario lo completa manualmente en el formulario.
 *
 * Ver docs/05_ai_extraction.md.
 */
public class DatosReservaExtraidos {

    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<String> idsCategoriasIdentificadas = new ArrayList<>();

    public DatosReservaExtraidos() {
    }

    public DatosReservaExtraidos(String actividad, LocalDate fecha, LocalTime horaInicio,
                                  LocalTime horaFin, List<String> idsCategoriasIdentificadas) {
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.idsCategoriasIdentificadas = idsCategoriasIdentificadas != null
                ? idsCategoriasIdentificadas
                : new ArrayList<>();
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    /**
     * Ids de las categorias identificadas en la frase, tomadas de la lista de
     * categorias disponibles que se le paso a ExtractorReserva.extraer.
     */
    public List<String> getIdsCategoriasIdentificadas() {
        return idsCategoriasIdentificadas;
    }

    public void setIdsCategoriasIdentificadas(List<String> idsCategoriasIdentificadas) {
        this.idsCategoriasIdentificadas = idsCategoriasIdentificadas;
    }

    @Override
    public String toString() {
        return String.format(
                "DatosReservaExtraidos{actividad=%s, fecha=%s, horaInicio=%s, horaFin=%s, categorias=%s}",
                actividad, fecha, horaInicio, horaFin, idsCategoriasIdentificadas);
    }
}
