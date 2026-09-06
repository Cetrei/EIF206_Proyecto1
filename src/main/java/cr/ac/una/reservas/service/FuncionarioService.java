package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.EstadoReserva;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.persistence.FuncionarioDao;
import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.util.ReglaDeNegocioException;

import java.util.List;
import java.util.Optional;

/**
 * Ver docs/02_service.md.
 */
public class FuncionarioService {

    private final FuncionarioDao funcionarioDao;
    private final ReservaDao reservaDao;

    public FuncionarioService() {
        this(DaoFactory.obtenerFuncionarioDao(), DaoFactory.obtenerReservaDao());
    }

    // Constructor para pruebas: permite inyectar Dao falsos.
    public FuncionarioService(FuncionarioDao funcionarioDao, ReservaDao reservaDao) {
        this.funcionarioDao = funcionarioDao;
        this.reservaDao = reservaDao;
    }

    public Optional<Funcionario> buscarPorId(String id) {
        return funcionarioDao.buscarPorId(id);
    }

    public List<Funcionario> buscarPorNombre(String nombre) {
        return funcionarioDao.buscarPorNombre(nombre);
    }

    public List<Funcionario> listarTodos() {
        return funcionarioDao.listarTodos();
    }

    /**
     * La clave inicial del funcionario queda igual a su id (ver
     * docs/01_model.md y docs/02_service.md); el propio funcionario
     * puede cambiarla despues con AutenticacionService.cambiarClave.
     */
    public void crear(Funcionario funcionario) {
        if (funcionario.getId() == null || funcionario.getId().isBlank()) {
            throw new ReglaDeNegocioException("El funcionario debe tener un ID.");
        }
        if (funcionarioDao.buscarPorId(funcionario.getId()).isPresent()) {
            throw new ReglaDeNegocioException("Ya existe un funcionario con ese ID.");
        }
        funcionario.setClave(funcionario.getId());
        funcionarioDao.guardar(funcionario);
    }

    public void modificar(Funcionario funcionario) {
        if (funcionarioDao.buscarPorId(funcionario.getId()).isEmpty()) {
            throw new ReglaDeNegocioException("No existe un funcionario con ese ID.");
        }
        funcionarioDao.guardar(funcionario);
    }

    /**
     * @throws ReglaDeNegocioException si el funcionario tiene reservas
     * activas, para no dejar reservas huerfanas (ver docs/07_convenciones.md,
     * "Manejo de errores").
     */
    public void eliminar(String id) {
        boolean tieneReservaActiva = reservaDao.listarPorFuncionario(id).stream()
                .anyMatch(reserva -> reserva.getEstado() == EstadoReserva.ACTIVA);
        if (tieneReservaActiva) {
            throw new ReglaDeNegocioException(
                    "No se puede eliminar el funcionario porque tiene reservas activas."
            );
        }
        funcionarioDao.eliminar(id);
    }
}
