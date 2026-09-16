package cr.ac.una.reservas.presentation.model;

public class VentanaPrincipalModel extends AbstractModel {

    public static final String PROP_USUARIO = "usuario";

    private String nombreUsuario;
    private String rolTexto;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getRolTexto() {
        return rolTexto;
    }

    public void setUsuario(String nombreUsuario, String rolTexto) {
        String anterior = this.nombreUsuario;
        this.nombreUsuario = nombreUsuario;
        this.rolTexto = rolTexto;
        notificarCambio(PROP_USUARIO, anterior, nombreUsuario);
    }
}
