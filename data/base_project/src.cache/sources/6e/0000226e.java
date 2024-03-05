package java.beans;

import java.util.EventListenerProxy;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PropertyChangeListenerProxy.class */
public class PropertyChangeListenerProxy extends EventListenerProxy implements PropertyChangeListener {
    public PropertyChangeListenerProxy(String propertyName, PropertyChangeListener listener) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public String getPropertyName() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent event) {
        throw new RuntimeException("Stub!");
    }
}