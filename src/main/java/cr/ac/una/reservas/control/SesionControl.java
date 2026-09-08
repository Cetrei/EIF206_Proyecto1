package cr.ac.una.reservas.control;

import cr.ac.una.reservas.model.RolUsuario;
import cr.ac.una.reservas.model.Usuario;

public final class SesionControl {

    private static final SesionControl INSTANCIA = new SesionControl();

    private Usuario usuarioActual;

    private SesionControl() {
    }

    public static SesionControl obtenerInstancia() {
        return INSTANCIA;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public Usuario usuarioActual() {
        return usuarioActual;
    }

    public RolUsuario rolActual() {
        return usuarioActual != null ? usuarioActual.getRol() : null;
    }

    public boolean esAdministrador() {
        return rolActual() == RolUsuario.ADMINISTRADOR;
    }
}
