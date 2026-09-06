package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.model.Usuario;
import cr.ac.una.reservas.persistence.AdministradorDao;
import cr.ac.una.reservas.persistence.FuncionarioDao;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.util.Optional;

/**
 * Ver docs/02_service.md. Un usuario puede ser Administrador o
 * Funcionario (ambos comparten id, clave y rol via Usuario), asi que
 * este servicio busca en ambos Dao antes de fallar. No contiene
 * acceso a archivos ni a XML, eso vive exclusivamente en persistence
 * (docs/03_persistence.md); tampoco contiene codigo de interfaz
 * grafica, eso vive en control (LoginControl la consume).
 */
public class AutenticacionService {

    private final AdministradorDao administradorDao;
    private final FuncionarioDao funcionarioDao;

    public AutenticacionService() {
        this(DaoFactory.obtenerAdministradorDao(), DaoFactory.obtenerFuncionarioDao());
    }

    // Constructor para pruebas de unidad: permite inyectar Dao falsos
    // en vez de los reales de DaoFactory (ver docs/07_convenciones.md,
    // seccion Pruebas).
    public AutenticacionService(AdministradorDao administradorDao, FuncionarioDao funcionarioDao) {
        this.administradorDao = administradorDao;
        this.funcionarioDao = funcionarioDao;
    }

    /**
     * @return el Usuario (Administrador o Funcionario) si el id existe
     * y la clave coincide.
     * @throws ReglaDeNegocioException si el id no existe o la clave no
     * coincide. El mensaje es deliberadamente generico ("credenciales
     * incorrectas") para no revelar si el problema fue el id o la
     * clave, evitando confirmar a quien intenta ingresar si un id en
     * particular existe en el sistema.
     */
    public Usuario autenticar(String id, String clave) {
        Optional<Usuario> usuario = buscarUsuarioPorId(id);
        if (usuario.isEmpty() || !usuario.get().getClave().equals(clave)) {
            throw new ReglaDeNegocioException("El ID o la contraseña son incorrectos.");
        }
        return usuario.get();
    }

    /**
     * @throws ReglaDeNegocioException si el id no existe o la clave
     * actual no coincide con la registrada.
     */
    public void cambiarClave(String idUsuario, String claveActual, String claveNueva) {
        Optional<Usuario> usuarioOpt = buscarUsuarioPorId(idUsuario);
        if (usuarioOpt.isEmpty() || !usuarioOpt.get().getClave().equals(claveActual)) {
            throw new ReglaDeNegocioException("La contraseña actual es incorrecta.");
        }
        if (claveNueva == null || claveNueva.isBlank()) {
            throw new ReglaDeNegocioException("La nueva contraseña no puede estar vacía.");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setClave(claveNueva);
        guardar(usuario);
    }

    private Optional<Usuario> buscarUsuarioPorId(String id) {
        Optional<Administrador> administrador = administradorDao.buscarPorId(id);
        if (administrador.isPresent()) {
            return Optional.of(administrador.get());
        }
        return funcionarioDao.buscarPorId(id).map(funcionario -> funcionario);
    }

    private void guardar(Usuario usuario) {
        if (usuario instanceof Administrador) {
            administradorDao.guardar((Administrador) usuario);
        } else if (usuario instanceof Funcionario) {
            funcionarioDao.guardar((Funcionario) usuario);
        }
    }
}
