package android.database.sqlite;

import android.os.ParcelFileDescriptor;

/* loaded from: SQLiteStatement.class */
public final class SQLiteStatement extends SQLiteProgram {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SQLiteStatement(SQLiteDatabase db, String sql, Object[] bindArgs) {
        super(db, sql, bindArgs, null);
    }

    public void execute() {
        acquireReference();
        try {
            try {
                getSession().execute(getSql(), getBindArgs(), getConnectionFlags(), null);
                releaseReference();
            } catch (SQLiteDatabaseCorruptException ex) {
                onCorruption();
                throw ex;
            }
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public int executeUpdateDelete() {
        acquireReference();
        try {
            try {
                int executeForChangedRowCount = getSession().executeForChangedRowCount(getSql(), getBindArgs(), getConnectionFlags(), null);
                releaseReference();
                return executeForChangedRowCount;
            } catch (SQLiteDatabaseCorruptException ex) {
                onCorruption();
                throw ex;
            }
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public long executeInsert() {
        acquireReference();
        try {
            try {
                long executeForLastInsertedRowId = getSession().executeForLastInsertedRowId(getSql(), getBindArgs(), getConnectionFlags(), null);
                releaseReference();
                return executeForLastInsertedRowId;
            } catch (SQLiteDatabaseCorruptException ex) {
                onCorruption();
                throw ex;
            }
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public long simpleQueryForLong() {
        acquireReference();
        try {
            try {
                long executeForLong = getSession().executeForLong(getSql(), getBindArgs(), getConnectionFlags(), null);
                releaseReference();
                return executeForLong;
            } catch (SQLiteDatabaseCorruptException ex) {
                onCorruption();
                throw ex;
            }
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public String simpleQueryForString() {
        acquireReference();
        try {
            try {
                String executeForString = getSession().executeForString(getSql(), getBindArgs(), getConnectionFlags(), null);
                releaseReference();
                return executeForString;
            } catch (SQLiteDatabaseCorruptException ex) {
                onCorruption();
                throw ex;
            }
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public ParcelFileDescriptor simpleQueryForBlobFileDescriptor() {
        acquireReference();
        try {
            try {
                ParcelFileDescriptor executeForBlobFileDescriptor = getSession().executeForBlobFileDescriptor(getSql(), getBindArgs(), getConnectionFlags(), null);
                releaseReference();
                return executeForBlobFileDescriptor;
            } catch (SQLiteDatabaseCorruptException ex) {
                onCorruption();
                throw ex;
            }
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public String toString() {
        return "SQLiteProgram: " + getSql();
    }
}