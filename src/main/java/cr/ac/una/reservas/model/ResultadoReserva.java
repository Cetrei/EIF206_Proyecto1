package cr.ac.una.reservas.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado de ReservaService.intentarReservar(datosReserva) (ver
 * docs/02_service.md). El enunciado pide que el sistema indique si la
 * reserva tuvo exito, o si no lo tuvo, indicando las categorias de
 * recursos no disponibles; este objeto transporta exactamente eso de
 * vuelta a control, sin logica de negocio propia.
 * <p>
 * No es una entidad persistible (no tiene anotaciones JAXB): es un
 * objeto de transporte igual que DatosNuevaReserva, vive en el mismo
 * paquete por ser parte del vocabulario compartido entre service y
 * control/presentation.
 */
public final class ResultadoReserva {

    private final boolean exitoso;
    private final Reserva reserva;
    private final List<Categoria> categoriasNoDisponibles;

    private ResultadoReserva(boolean exitoso, Reserva reserva, List<Categoria> categoriasNoDisponibles) {
        this.exitoso = exitoso;
        this.reserva = reserva;
        this.categoriasNoDisponibles = categoriasNoDisponibles;
    }

    public static ResultadoReserva exito(Reserva reserva) {
        return new ResultadoReserva(true, reserva, Collections.emptyList());
    }

    public static ResultadoReserva fracaso(List<Categoria> categoriasNoDisponibles) {
        return new ResultadoReserva(false, null, new ArrayList<>(categoriasNoDisponibles));
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public List<Categoria> getCategoriasNoDisponibles() {
        return categoriasNoDisponibles;
    }
}
