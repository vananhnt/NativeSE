package android.database;

import android.database.sqlite.SQLiteDatabase;

/* loaded from: DatabaseErrorHandler.class */
public interface DatabaseErrorHandler {
    void onCorruption(SQLiteDatabase sQLiteDatabase);
}