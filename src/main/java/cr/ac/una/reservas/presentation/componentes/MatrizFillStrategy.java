package cr.ac.una.reservas.presentation.componentes;

import java.util.List;

public interface MatrizFillStrategy {

    /**
     * Nombres de las columnas a mostrar, en orden
     */
    List<String> obtenerColumnas();

    /**
     * Datos a mostrar en la celda de la fila y columna dadas
     */
    DatosCelda obtenerCelda(int fila, int columna);

    void alHacerClickCelda(int fila, int columna, DatosCelda datos);
}
