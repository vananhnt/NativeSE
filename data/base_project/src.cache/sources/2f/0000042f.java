package android.database.sqlite;

import android.database.SQLException;

/* loaded from: SQLiteException.class */
public class SQLiteException extends SQLException {
    public SQLiteException() {
    }

    public SQLiteException(String error) {
        super(error);
    }

    public SQLiteException(String error, Throwable cause) {
        super(error, cause);
    }
}