package android.security;

/* loaded from: KeyChainException.class */
public class KeyChainException extends Exception {
    public KeyChainException() {
    }

    public KeyChainException(String detailMessage) {
        super(detailMessage);
    }

    public KeyChainException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyChainException(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause);
    }
}