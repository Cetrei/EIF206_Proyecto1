package cr.ac.una.reservas.util;

/**
 * Excepcion propia de la capa persistence (ver docs/03_persistence.md),
 * para errores reales de lectura/escritura o de formato del archivo
 * XML (no para el caso de "el archivo no existe todavia", que la
 * implementacion debe resolver creando el archivo vacio en vez de
 * fallar). Extiende de ReservaAppException para que control pueda
 * capturarla de forma generica si es necesario, aunque normalmente
 * service la traduce antes a una excepcion de negocio con un mensaje
 * mas claro para el usuario final.
 */
public class PersistenciaException extends ReservaAppException {

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
