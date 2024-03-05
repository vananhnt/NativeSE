package java.beans;

import java.util.EventObject;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: PropertyChangeEvent.class */
public class PropertyChangeEvent extends EventObject {
    public PropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public String getPropertyName() {
        throw new RuntimeException("Stub!");
    }

    public void setPropagationId(Object propagationId) {
        throw new RuntimeException("Stub!");
    }

    public Object getPropagationId() {
        throw new RuntimeException("Stub!");
    }

    public Object getOldValue() {
        throw new RuntimeException("Stub!");
    }

    public Object getNewValue() {
        throw new RuntimeException("Stub!");
    }
}