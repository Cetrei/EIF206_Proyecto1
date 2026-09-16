package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.persistence.AdministradorDao;
import cr.ac.una.reservas.persistence.AdministradorDaoXml;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.CategoriaDaoXml;
import cr.ac.una.reservas.persistence.FuncionarioDao;
import cr.ac.una.reservas.persistence.FuncionarioDaoXml;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.persistence.RecursoDaoXml;
import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.persistence.ReservaDaoXml;

import java.io.File;

public final class DaoFactory {
    private static final String DIRECTORIO_DATOS = System.getProperty("reservas.data.dir", "data");

    private static final FuncionarioDao FUNCIONARIO_DAO =
            new FuncionarioDaoXml(DIRECTORIO_DATOS + "/funcionarios.xml");
    private static final AdministradorDao ADMINISTRADOR_DAO =
            new AdministradorDaoXml(DIRECTORIO_DATOS + "/administradores.xml");
    private static final CategoriaDao CATEGORIA_DAO =
            new CategoriaDaoXml(DIRECTORIO_DATOS + "/categorias.xml");
    private static final RecursoDao RECURSO_DAO =
            new RecursoDaoXml(DIRECTORIO_DATOS + "/recursos.xml");
    private static final ReservaDao RESERVA_DAO =
            new ReservaDaoXml(DIRECTORIO_DATOS + "/reservas.xml");

    static {
        new File(DIRECTORIO_DATOS).mkdirs();
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
        if (FUNCIONARIO_DAO.listarTodos().isEmpty()) {
            FUNCIONARIO_DAO.guardar(new Funcionario("111", "111", "Juan Perez", "3323"));
        }
        if (ADMINISTRADOR_DAO.listarTodos().isEmpty()) {
            ADMINISTRADOR_DAO.guardar(new Administrador("admin", "123"));
        }
    }
}
