package cr.ac.una.reservas.presentation.model;

import cr.ac.una.reservas.model.EstadisticaCategoria;
import cr.ac.una.reservas.model.EstadisticaSemana;

import java.util.List;

public class EstadisticaModel extends AbstractModel {

    public static final String PROP_RECURSOS = "recursos";
    public static final String PROP_SUBTITULO_RECURSOS = "subtituloRecursos";
    public static final String PROP_ACTIVIDADES = "actividades";
    public static final String PROP_SUBTITULO_ACTIVIDADES = "subtituloActividades";

    private List<EstadisticaCategoria> recursos = List.of();
    private String subtituloRecursos = "";
    private List<EstadisticaSemana> actividades = List.of();
    private String subtituloActividades = "";

    public List<EstadisticaCategoria> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<EstadisticaCategoria> nuevaLista) {
        List<EstadisticaCategoria> anterior = this.recursos;
        this.recursos = nuevaLista;
        notificarCambioForzado(PROP_RECURSOS, anterior, nuevaLista);
    }

    public String getSubtituloRecursos() {
        return subtituloRecursos;
    }

    public void setSubtituloRecursos(String nuevoSubtitulo) {
        String anterior = this.subtituloRecursos;
        this.subtituloRecursos = nuevoSubtitulo;
        notificarCambio(PROP_SUBTITULO_RECURSOS, anterior, nuevoSubtitulo);
    }

    public List<EstadisticaSemana> getActividades() {
        return actividades;
    }

    public void setActividades(List<EstadisticaSemana> nuevaLista) {
        List<EstadisticaSemana> anterior = this.actividades;
        this.actividades = nuevaLista;
        notificarCambioForzado(PROP_ACTIVIDADES, anterior, nuevaLista);
    }

    public String getSubtituloActividades() {
        return subtituloActividades;
    }

    public void setSubtituloActividades(String nuevoSubtitulo) {
        String anterior = this.subtituloActividades;
        this.subtituloActividades = nuevoSubtitulo;
        notificarCambio(PROP_SUBTITULO_ACTIVIDADES, anterior, nuevoSubtitulo);
    }
}
