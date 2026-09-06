package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Administrador;

/**
 * Ver docs/03_persistence.md. Responsable de la implementacion:
 * Companero A. No esta descrita explicitamente en 03_persistence.md
 * (que solo menciona Funcionario/Categoria/Recurso/Reserva), pero
 * AutenticacionService (docs/02_service.md) necesita poder autenticar
 * tanto Administrador como Funcionario, y ambos son Usuario con id y
 * clave propios, asi que se necesita un Dao equivalente para
 * Administrador con la misma forma que los demas.
 */
public interface AdministradorDao extends Dao<Administrador, String> {
}
