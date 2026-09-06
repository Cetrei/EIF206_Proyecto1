package cr.ac.una.reservas.util;

/**
 * Excepcion para violaciones de reglas de negocio dentro de service
 * (ver docs/02_service.md), por ejemplo clave incorrecta, fecha de
 * reserva en el pasado, o intentar eliminar un funcionario con
 * reservas activas. El mensaje debe ser claro y estar redactado para
 * mostrarse directamente al usuario final (ver docs/07_convenciones.md).
 */
public class ReglaDeNegocioException extends ReservaAppException {

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }

    public ReglaDeNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
