package cr.ac.una.reservas.util;

public class PersistenciaException extends ReservaAppException {
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
