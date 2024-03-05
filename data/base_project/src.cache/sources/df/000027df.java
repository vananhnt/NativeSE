package javax.net.ssl;

import java.util.EventListener;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLSessionBindingListener.class */
public interface SSLSessionBindingListener extends EventListener {
    void valueBound(SSLSessionBindingEvent sSLSessionBindingEvent);

    void valueUnbound(SSLSessionBindingEvent sSLSessionBindingEvent);
}