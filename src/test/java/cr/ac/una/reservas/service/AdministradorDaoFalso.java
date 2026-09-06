package cr.ac.una.reservas.service;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.persistence.AdministradorDao;

public class AdministradorDaoFalso extends DaoFalso<Administrador, String> implements AdministradorDao {
    public AdministradorDaoFalso() {
        super(Administrador::getId);
    }
}
