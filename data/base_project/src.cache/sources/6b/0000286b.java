package javax.sql;

import java.util.EventListener;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ConnectionEventListener.class */
public interface ConnectionEventListener extends EventListener {
    void connectionClosed(ConnectionEvent connectionEvent);

    void connectionErrorOccurred(ConnectionEvent connectionEvent);
}