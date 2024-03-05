package android.util;

/* loaded from: AndroidException.class */
public class AndroidException extends Exception {
    public AndroidException() {
    }

    public AndroidException(String name) {
        super(name);
    }

    public AndroidException(String name, Throwable cause) {
        super(name, cause);
    }

    public AndroidException(Exception cause) {
        super(cause);
    }
}