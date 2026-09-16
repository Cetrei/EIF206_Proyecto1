package cr.ac.una.reservas.presentation.mvc.iconos;

public enum IconoSemantico {
    GUARDAR(Icono.GUARDAR),
    BORRAR(Icono.BASURA),
    LIMPIAR(Icono.BORRADOR),
    CONFIRMAR(Icono.CHECK),
    BUSCAR(Icono.BUSCAR),
    EDITAR(Icono.EDITAR),
    AGREGAR(Icono.MAS),

    USUARIO(Icono.USUARIO),
    CONTRASENA(Icono.CANDADO),
    CERRAR_SESION(Icono.SALIR),
    AJUSTES_CUENTA(Icono.AJUSTES),

    CERRAR_VENTANA(Icono.CERRAR),

    LOGO_APP(Icono.CALENDARIO),

    REPORTE_PDF(Icono.PDF),
    GRAFICO(Icono.GRAFICO),

    INFO(Icono.INFO),
    ALERTA(Icono.ALERTA),

    ETIQUETA(Icono.ETIQUETA),
    CAJA(Icono.CAJA),
    RAYO(Icono.RAYO),

    IA_GEMINI(Icono.NUBE),
    IA_LOCAL(Icono.CHIP);

    private final Icono icono;

    IconoSemantico(Icono icono) {
        this.icono = icono;
    }

    public Icono icono() {
        return icono;
    }
}
