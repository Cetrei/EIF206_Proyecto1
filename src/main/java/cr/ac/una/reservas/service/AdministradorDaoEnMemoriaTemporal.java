package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.persistence.AdministradorDao;

/**
 * Ver DaoEnMemoriaTemporal: parche temporal de arranque mientras
 * persistence (Companero A) no entrega AdministradorDaoXml.
 */
class AdministradorDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<Administrador, String> implements AdministradorDao {
    AdministradorDaoEnMemoriaTemporal() {
        super(Administrador::getId);
    }
}
