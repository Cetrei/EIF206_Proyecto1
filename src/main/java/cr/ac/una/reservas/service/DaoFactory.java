package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.persistence.AdministradorDao;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.FuncionarioDao;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.persistence.ReservaDao;

/**
 * Factory Method para obtener los Dao concretos (ver
 * docs/00_arquitectura_general.md y docs/02_service.md). Este es el
 * unico punto del sistema donde se deberia instanciar una clase
 * concreta de persistence, por ejemplo FuncionarioDaoXml; el resto de
 * service programa unicamente contra las interfaces Dao.
 * <p>
 * TODO(persistence): el Companero A todavia no ha entregado las
 * implementaciones XML (FuncionarioDaoXml, CategoriaDaoXml,
 * RecursoDaoXml, ReservaDaoXml, AdministradorDaoXml, ver
 * docs/03_persistence.md); el paquete persistence solo contiene las
 * interfaces (el contrato) por ahora.
 * <p>
 * Mientras tanto, cada metodo de abajo devuelve una implementacion EN
 * MEMORIA (ver *DaoEnMemoriaTemporal en este mismo paquete), unica por
 * tipo de entidad y compartida durante toda la ejecucion de la
 * aplicacion (de ahi los campos static), para poder correr la app de
 * verdad (Main) y probar el login y el resto de pantallas sin esperar
 * a persistence. Esto NO reemplaza el trabajo del Companero A: no
 * escribe a disco, los datos se pierden al cerrar la aplicacion, y
 * vive en service (no en persistence) para que quede claro que es un
 * parche de arranque, no una implementacion de la capa persistence.
 * <p>
 * El dia que las implementaciones XML reales lleguen, el cambio es
 * unicamente en este archivo (cambiar "new XxxDaoEnMemoriaTemporal()"
 * por "new XxxDaoXml(...)" y borrar las clases *EnMemoriaTemporal);
 * ningun otro archivo de service necesita tocarse.
 */
public final class DaoFactory {

    private static final FuncionarioDao FUNCIONARIO_DAO = new FuncionarioDaoEnMemoriaTemporal();
    private static final AdministradorDao ADMINISTRADOR_DAO = new AdministradorDaoEnMemoriaTemporal();
    private static final CategoriaDao CATEGORIA_DAO = new CategoriaDaoEnMemoriaTemporal();
    private static final RecursoDao RECURSO_DAO = new RecursoDaoEnMemoriaTemporal();
    private static final ReservaDao RESERVA_DAO = new ReservaDaoEnMemoriaTemporal();

    static {
        sembrarUsuariosDePrueba();
    }

    private DaoFactory() {
    }

    public static FuncionarioDao obtenerFuncionarioDao() {
        return FUNCIONARIO_DAO;
    }

    public static AdministradorDao obtenerAdministradorDao() {
        return ADMINISTRADOR_DAO;
    }

    public static CategoriaDao obtenerCategoriaDao() {
        return CATEGORIA_DAO;
    }

    public static RecursoDao obtenerRecursoDao() {
        return RECURSO_DAO;
    }

    public static ReservaDao obtenerReservaDao() {
        return RESERVA_DAO;
    }

    /**
     * Usuarios de prueba pedidos para poder loguearse mientras
     * persistence no esta lista: un Funcionario (id "funcionario") y
     * un Administrador (id "admin"), ambos con clave "123". Solo
     * existen en memoria durante la ejecucion de la aplicacion; se
     * pierden al cerrarla. Cuando persistence entregue las
     * implementaciones XML reales, sembrar estos mismos id/clave una
     * vez en data/funcionarios.xml y data/administradores.xml (ver
     * la nota en FuncionarioDaoXmlIT) y borrar este metodo.
     */
    private static void sembrarUsuariosDePrueba() {
        FUNCIONARIO_DAO.guardar(new Funcionario("funcionario", "123", "Funcionario de Prueba", "0000-0000"));
        ADMINISTRADOR_DAO.guardar(new Administrador("admin", "123"));
    }
}
