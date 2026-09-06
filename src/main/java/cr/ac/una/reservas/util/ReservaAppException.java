package cr.ac.una.reservas.util;

/**
 * Excepcion base de la aplicacion. Las excepciones propias de service
 * (ver docs/02_service.md y docs/07_convenciones.md) deben extender de
 * esta para que control pueda capturarlas de forma generica cuando lo
 * necesite, sin acoplarse a cada tipo especifico.
 *
 * El mensaje debe ser claro y redactado para mostrarse directamente al
 * usuario final, sin jerga tecnica ni referencias a clases o archivos
 * internos.
 */
public class ReservaAppException extends RuntimeException {

    public ReservaAppException(String mensaje) {
        super(mensaje);
    }

    public ReservaAppException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
