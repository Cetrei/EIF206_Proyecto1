package cr.ac.una.reservas.service;

public final class ServiceFactory {
    private static final CategoriaService CATEGORIA_SERVICE = new CategoriaService();
    private static final ReservaService RESERVA_SERVICE = new ReservaService();
    private static final RecursoService RECURSO_SERVICE = new RecursoService();
    private static final FuncionarioService FUNCIONARIO_SERVICE = new FuncionarioService();
    private static final AutenticacionService AUTENTICACION_SERVICE = new AutenticacionService();
    private static final EstadisticaService ESTADISTICA_SERVICE = new EstadisticaService();
    private static final ConfiguracionIaService CONFIGURACION_IA_SERVICE = new ConfiguracionIaService();

    private ServiceFactory() {
    }

    public static CategoriaService obtenerCategoriaService() {
        return CATEGORIA_SERVICE;
    }

    public static ReservaService obtenerReservaService() {
        return RESERVA_SERVICE;
    }

    public static RecursoService obtenerRecursoService() {
        return RECURSO_SERVICE;
    }

    public static FuncionarioService obtenerFuncionarioService() {
        return FUNCIONARIO_SERVICE;
    }

    public static AutenticacionService obtenerAutenticacionService() {
        return AUTENTICACION_SERVICE;
    }

    public static EstadisticaService obtenerEstadisticaService() {
        return ESTADISTICA_SERVICE;
    }

    public static ConfiguracionIaService obtenerConfiguracionIaService() {
        return CONFIGURACION_IA_SERVICE;
    }
}
