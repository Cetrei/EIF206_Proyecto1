package cr.ac.una.reservas.presentation.model;

public class LoginModel extends AbstractModel {

    public static final String PROP_MENSAJE_ERROR = "mensajeError";

    private String mensajeError;

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String nuevo) {
        String anterior = this.mensajeError;
        this.mensajeError = nuevo;
        notificarCambio(PROP_MENSAJE_ERROR, anterior, nuevo);
    }
}
