package java.util.prefs;

import java.util.EventListener;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: NodeChangeListener.class */
public interface NodeChangeListener extends EventListener {
    void childAdded(NodeChangeEvent nodeChangeEvent);

    void childRemoved(NodeChangeEvent nodeChangeEvent);
}