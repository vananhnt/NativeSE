package java.util.prefs;

import java.io.Serializable;
import java.util.EventObject;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NodeChangeEvent.class */
public class NodeChangeEvent extends EventObject implements Serializable {
    public NodeChangeEvent(Preferences p, Preferences c) {
        super(null);
        throw new RuntimeException("Stub!");
    }

    public Preferences getParent() {
        throw new RuntimeException("Stub!");
    }

    public Preferences getChild() {
        throw new RuntimeException("Stub!");
    }
}