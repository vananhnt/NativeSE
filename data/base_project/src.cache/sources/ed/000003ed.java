package android.database;

/* loaded from: CrossProcessCursor.class */
public interface CrossProcessCursor extends Cursor {
    CursorWindow getWindow();

    void fillWindow(int i, CursorWindow cursorWindow);

    boolean onMove(int i, int i2);
}