package android.database;

/* loaded from: SQLException.class */
public class SQLException extends RuntimeException {
    public SQLException() {
    }

    public SQLException(String error) {
        super(error);
    }

    public SQLException(String error, Throwable cause) {
        super(error, cause);
    }
}