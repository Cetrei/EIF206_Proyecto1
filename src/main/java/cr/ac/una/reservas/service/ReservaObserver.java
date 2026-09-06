package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Reserva;

/**
 * Patron Observer para cambios en reservas (ver docs/02_service.md y
 * docs/06_control_presentation.md). Las vistas de presentation que
 * necesiten refrescarse automaticamente (calendarizacion, actividades,
 * estadisticas si esta abierta) implementan esta interfaz y se
 * registran contra ReservaService.agregarObservador al abrirse, y se
 * desregistran con quitarObservador al cerrarse.
 */
public interface ReservaObserver {
    void onReservaCreada(Reserva reserva);
    void onReservaCancelada(Reserva reserva);
}
