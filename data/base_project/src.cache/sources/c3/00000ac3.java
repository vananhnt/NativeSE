package android.os;

import android.util.AndroidRuntimeException;

/* loaded from: BadParcelableException.class */
public class BadParcelableException extends AndroidRuntimeException {
    public BadParcelableException(String msg) {
        super(msg);
    }

    public BadParcelableException(Exception cause) {
        super(cause);
    }
}