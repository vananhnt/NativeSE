package javax.net.ssl;

import java.util.EventListener;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: HandshakeCompletedListener.class */
public interface HandshakeCompletedListener extends EventListener {
    void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent);
}