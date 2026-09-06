package cr.ac.una.reservas.presentation.iconos;

public enum Icono {
    USUARIO('\uf007'),
    CANDADO('\uf023'),
    BASURA('\uf2ed'),
    AJUSTES('\uf013'),
    PDF('\uf1c1'),
    EDITAR('\uf044'),
    BUSCAR('\uf002'),
    GUARDAR('\uf0c7'),
    SALIR('\uf2f5'),
    RAYO('\uf0e7'),
    CALENDARIO('\uf133'),
    GRAFICO('\uf080'),
    INFO('\uf05a'),
    ALERTA('\uf06a'),
    CHECK('\uf058'),
    ETIQUETA('\uf02b'),
    CAJA('\uf466'),
    MAS('\uf067'),
    BORRADOR('\uf12d'),
    CERRAR('\uf00d');

    private final char codigo;

    Icono(char codigo) {
        this.codigo = codigo;
    }
    public char getCodigo() {
        return codigo;
    }
}
