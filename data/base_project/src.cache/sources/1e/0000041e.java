package android.database.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/* loaded from: SQLiteCursorDriver.class */
public interface SQLiteCursorDriver {
    Cursor query(SQLiteDatabase.CursorFactory cursorFactory, String[] strArr);

    void cursorDeactivated();

    void cursorRequeried(Cursor cursor);

    void cursorClosed();

    void setBindArguments(String[] strArr);
}