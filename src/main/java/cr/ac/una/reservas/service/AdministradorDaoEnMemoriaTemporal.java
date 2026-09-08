package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.persistence.AdministradorDao;

class AdministradorDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<Administrador, String> implements AdministradorDao {
    AdministradorDaoEnMemoriaTemporal() {
        super(Administrador::getId);
    }
}
