package cr.ac.una.reservas.util;

public class ReglaDeNegocioException extends ReservaAppException {
    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }

    public ReglaDeNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
