package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Reserva;

public interface ReservaObserver {
    void onReservaCreada(Reserva reserva);
    void onReservaCancelada(Reserva reserva);
}
