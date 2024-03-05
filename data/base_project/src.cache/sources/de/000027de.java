package javax.net.ssl;

import java.util.EventObject;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLSessionBindingEvent.class */
public class SSLSessionBindingEvent extends EventObject {
    private final String name;

    public SSLSessionBindingEvent(SSLSession session, String name) {
        super(session);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public SSLSession getSession() {
        return (SSLSession) this.source;
    }
}