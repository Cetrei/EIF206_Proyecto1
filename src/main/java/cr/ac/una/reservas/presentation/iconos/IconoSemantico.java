package cr.ac.una.reservas.presentation.iconos;

public enum IconoSemantico {
    GUARDAR(Icono.GUARDAR),
    BORRAR(Icono.BASURA),
    LIMPIAR(Icono.BORRADOR),
    CONFIRMAR(Icono.CHECK),
    BUSCAR(Icono.BUSCAR),
    EDITAR(Icono.EDITAR),
    AGREGAR(Icono.MAS),

    // Cuenta / sesion.
    USUARIO(Icono.USUARIO),
    CONTRASENA(Icono.CANDADO),
    CERRAR_SESION(Icono.SALIR),
    AJUSTES_CUENTA(Icono.AJUSTES),

    // Chrome de ventana (BarraSuperior, popups).
    CERRAR_VENTANA(Icono.CERRAR),

    // Identidad de marca (LoginPanel, encabezado de VentanaPrincipal).
    LOGO_APP(Icono.CALENDARIO),

    // Reportes / estadisticas.
    REPORTE_PDF(Icono.PDF),
    GRAFICO(Icono.GRAFICO),

    // Estados / feedback (Popup, validaciones).
    INFO(Icono.INFO),
    ALERTA(Icono.ALERTA),

    // Categorizacion generica (encabezados de Tarjeta por tema).
    ETIQUETA(Icono.ETIQUETA),
    CAJA(Icono.CAJA),
    RAYO(Icono.RAYO);

    private final Icono icono;

    IconoSemantico(Icono icono) {
        this.icono = icono;
    }

    public Icono icono() {
        return icono;
    }
}
