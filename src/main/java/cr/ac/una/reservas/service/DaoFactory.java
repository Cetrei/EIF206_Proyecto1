package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.persistence.AdministradorDao;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.FuncionarioDao;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.persistence.ReservaDao;

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


    private static void sembrarUsuariosDePrueba() {
        FUNCIONARIO_DAO.guardar(new Funcionario("funcionario", "123", "Funcionario de Prueba", "0000-0000"));
        ADMINISTRADOR_DAO.guardar(new Administrador("admin", "123"));
    }
}
