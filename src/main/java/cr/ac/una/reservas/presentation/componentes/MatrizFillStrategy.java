package cr.ac.una.reservas.presentation.componentes;

import java.util.List;

/**
 * Contrato del patron Strategy usado por MatrizPanel (ver
 * docs/06_control_presentation.md). MatrizPanel dibuja la grilla, las
 * cabeceras de fila con las horas del dia, el scroll y el manejo general
 * de la tabla; una implementacion de esta interfaz resuelve lo que es
 * especifico de cada caso concreto:
 *
 * - cuales son las columnas a mostrar (por ejemplo los recursos de una
 *   categoria, o los dias de la semana).
 * - que contenido y color va en cada celda dado una fila (hora) y
 *   columna especificas.
 * - que pasa si el usuario hace click en una celda ocupada.
 *
 * Las dos implementaciones esperadas son CalendarizacionRecursoStrategy
 * y ProgramacionActividadStrategy, cada una en su propia pantalla dentro
 * de presentation, ya que dependen de datos que provee service (todavia
 * no existe) para resolver columnas y celdas.
 */
public interface MatrizFillStrategy {

    /**
     * Nombres de las columnas a mostrar, en orden. El tamano de esta
     * lista determina cuantas columnas dibuja MatrizPanel.
     */
    List<String> obtenerColumnas();

    /**
     * Datos a mostrar en la celda de la fila y columna dadas (fila es un
     * indice sobre las horas generadas por MatrizPanel, no la hora en
     * si). Debe devolver null si esa celda esta libre/vacia, en cuyo
     * caso MatrizPanel dibuja un marcador simple en vez de una
     * CeldaMatriz.
     */
    DatosCelda obtenerCelda(int fila, int columna);

    /**
     * Se invoca cuando el usuario hace click sobre una celda ya
     * dibujada (ocupada o no). Cada estrategia decide que hacer, por
     * ejemplo abrir el detalle de una reserva o iniciar la creacion de
     * una nueva.
     */
    void alHacerClickCelda(int fila, int columna, DatosCelda datos);
}
