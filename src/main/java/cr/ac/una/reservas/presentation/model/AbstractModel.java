package cr.ac.una.reservas.presentation.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractModel {
    private final PropertyChangeSupport soporte = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener oyente) {
        soporte.addPropertyChangeListener(oyente);
    }

    public void removePropertyChangeListener(PropertyChangeListener oyente) {
        soporte.removePropertyChangeListener(oyente);
    }

    protected void notificarCambio(String propiedad, Object valorAnterior, Object valorNuevo) {
        soporte.firePropertyChange(propiedad, valorAnterior, valorNuevo);
    }

    protected void notificarCambioForzado(String propiedad, Object valorAnterior, Object valorNuevo) {
        soporte.firePropertyChange(new PropertyChangeEvent(this, propiedad, null, valorNuevo));
    }
}
