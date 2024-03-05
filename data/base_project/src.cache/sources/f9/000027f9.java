package javax.security.auth.callback;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: UnsupportedCallbackException.class */
public class UnsupportedCallbackException extends Exception {
    private static final long serialVersionUID = -6873556327655666839L;
    private Callback callback;

    public UnsupportedCallbackException(Callback callback) {
        this.callback = callback;
    }

    public UnsupportedCallbackException(Callback callback, String message) {
        super(message);
        this.callback = callback;
    }

    public Callback getCallback() {
        return this.callback;
    }
}