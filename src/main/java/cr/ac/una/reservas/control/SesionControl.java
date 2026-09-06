package cr.ac.una.reservas.control;

import cr.ac.una.reservas.model.RolUsuario;
import cr.ac.una.reservas.model.Usuario;

/**
 * Guarda el Usuario actualmente logueado y su rol (ver docs/06_control_presentation.md).
 * Los demas controladores consultan aqui para saber si el usuario actual
 * tiene permiso de ejecutar cierta accion, por ejemplo que solo un
 * administrador pueda entrar a Funcionarios, Categorias o Recursos.
 */
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
