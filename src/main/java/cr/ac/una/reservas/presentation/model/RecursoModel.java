package cr.ac.una.reservas.presentation.model;

import cr.ac.una.reservas.model.Categoria;
import cr.ac.una.reservas.model.Recurso;

import java.util.ArrayList;
import java.util.List;

public class RecursoModel extends AbstractModel {

    public static final String PROP_RECURSOS = "recursos";
    public static final String PROP_RECURSO_SELECCIONADO = "recursoSeleccionado";
    public static final String PROP_CATEGORIAS_DISPONIBLES = "categoriasDisponibles";

    private List<Recurso> recursos = new ArrayList<>();
    private Recurso recursoSeleccionado;
    private List<Categoria> categoriasDisponibles = new ArrayList<>();

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<Recurso> nuevaLista) {
        List<Recurso> anterior = this.recursos;
        this.recursos = nuevaLista;
        notificarCambio(PROP_RECURSOS, anterior, nuevaLista);
    }

    public Recurso getRecursoSeleccionado() {
        return recursoSeleccionado;
    }

    public void setRecursoSeleccionado(Recurso nuevo) {
        Recurso anterior = this.recursoSeleccionado;
        this.recursoSeleccionado = nuevo;
        notificarCambio(PROP_RECURSO_SELECCIONADO, anterior, nuevo);
    }

    public List<Categoria> getCategoriasDisponibles() {
        return categoriasDisponibles;
    }

    public void setCategoriasDisponibles(List<Categoria> nuevaLista) {
        List<Categoria> anterior = this.categoriasDisponibles;
        this.categoriasDisponibles = nuevaLista;
        notificarCambio(PROP_CATEGORIAS_DISPONIBLES, anterior, nuevaLista);
    }
}
