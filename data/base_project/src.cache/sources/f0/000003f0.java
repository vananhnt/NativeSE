package android.database;

/* loaded from: CursorIndexOutOfBoundsException.class */
public class CursorIndexOutOfBoundsException extends IndexOutOfBoundsException {
    public CursorIndexOutOfBoundsException(int index, int size) {
        super("Index " + index + " requested, with a size of " + size);
    }

    public CursorIndexOutOfBoundsException(String message) {
        super(message);
    }
}