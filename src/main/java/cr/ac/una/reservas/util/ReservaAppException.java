package cr.ac.una.reservas.util;

public class ReservaAppException extends RuntimeException {
    public ReservaAppException(String mensaje) {
        super(mensaje);
    }

    public ReservaAppException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
