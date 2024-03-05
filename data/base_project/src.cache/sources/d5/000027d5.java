package javax.net.ssl;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: SSLHandshakeException.class */
public class SSLHandshakeException extends SSLException {
    private static final long serialVersionUID = -5045881315018326890L;

    public SSLHandshakeException(String reason) {
        super(reason);
    }

    public SSLHandshakeException(Throwable cause) {
        super(cause);
    }

    public SSLHandshakeException(String reason, Throwable cause) {
        super(reason, cause);
    }
}