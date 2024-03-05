package android.database.sqlite;

import android.database.CursorWindow;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDebug;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.LruCache;
import android.util.Printer;
import dalvik.system.BlockGuard;
import dalvik.system.CloseGuard;
import gov.nist.core.Separators;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

/* loaded from: SQLiteConnection.class */
public final class SQLiteConnection implements CancellationSignal.OnCancelListener {
    private static final String TAG = "SQLiteConnection";
    private static final boolean DEBUG = false;
    private static final String[] EMPTY_STRING_ARRAY;
    private static final byte[] EMPTY_BYTE_ARRAY;
    private static final Pattern TRIM_SQL_PATTERN;
    private final SQLiteConnectionPool mPool;
    private final SQLiteDatabaseConfiguration mConfiguration;
    private final int mConnectionId;
    private final boolean mIsPrimaryConnection;
    private final boolean mIsReadOnlyConnection;
    private final PreparedStatementCache mPreparedStatementCache;
    private PreparedStatement mPreparedStatementPool;
    private int mConnectionPtr;
    private boolean mOnlyAllowReadOnlyOperations;
    private int mCancellationSignalAttachCount;
    static final /* synthetic */ boolean $assertionsDisabled;
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private final OperationLog mRecentOperations = new OperationLog();

    private static native int nativeOpen(String str, int i, String str2, boolean z, boolean z2);

    private static native void nativeClose(int i);

    private static native void nativeRegisterCustomFunction(int i, SQLiteCustomFunction sQLiteCustomFunction);

    private static native void nativeRegisterLocalizedCollators(int i, String str);

    private static native int nativePrepareStatement(int i, String str);

    private static native void nativeFinalizeStatement(int i, int i2);

    private static native int nativeGetParameterCount(int i, int i2);

    private static native boolean nativeIsReadOnly(int i, int i2);

    private static native int nativeGetColumnCount(int i, int i2);

    private static native String nativeGetColumnName(int i, int i2, int i3);

    private static native void nativeBindNull(int i, int i2, int i3);

    private static native void nativeBindLong(int i, int i2, int i3, long j);

    private static native void nativeBindDouble(int i, int i2, int i3, double d);

    private static native void nativeBindString(int i, int i2, int i3, String str);

    private static native void nativeBindBlob(int i, int i2, int i3, byte[] bArr);

    private static native void nativeResetStatementAndClearBindings(int i, int i2);

    private static native void nativeExecute(int i, int i2);

    private static native long nativeExecuteForLong(int i, int i2);

    private static native String nativeExecuteForString(int i, int i2);

    private static native int nativeExecuteForBlobFileDescriptor(int i, int i2);

    private static native int nativeExecuteForChangedRowCount(int i, int i2);

    private static native long nativeExecuteForLastInsertedRowId(int i, int i2);

    private static native long nativeExecuteForCursorWindow(int i, int i2, int i3, int i4, int i5, boolean z);

    private static native int nativeGetDbLookaside(int i);

    private static native void nativeCancel(int i);

    private static native void nativeResetCancel(int i, boolean z);

    static {
        $assertionsDisabled = !SQLiteConnection.class.desiredAssertionStatus();
        EMPTY_STRING_ARRAY = new String[0];
        EMPTY_BYTE_ARRAY = new byte[0];
        TRIM_SQL_PATTERN = Pattern.compile("[\\s]*\\n+[\\s]*");
    }

    private SQLiteConnection(SQLiteConnectionPool pool, SQLiteDatabaseConfiguration configuration, int connectionId, boolean primaryConnection) {
        this.mPool = pool;
        this.mConfiguration = new SQLiteDatabaseConfiguration(configuration);
        this.mConnectionId = connectionId;
        this.mIsPrimaryConnection = primaryConnection;
        this.mIsReadOnlyConnection = (configuration.openFlags & 1) != 0;
        this.mPreparedStatementCache = new PreparedStatementCache(this.mConfiguration.maxSqlCacheSize);
        this.mCloseGuard.open("close");
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mPool != null && this.mConnectionPtr != 0) {
                this.mPool.onConnectionLeaked();
            }
            dispose(true);
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SQLiteConnection open(SQLiteConnectionPool pool, SQLiteDatabaseConfiguration configuration, int connectionId, boolean primaryConnection) {
        SQLiteConnection connection = new SQLiteConnection(pool, configuration, connectionId, primaryConnection);
        try {
            connection.open();
            return connection;
        } catch (SQLiteException ex) {
            connection.dispose(false);
            throw ex;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        dispose(false);
    }

    private void open() {
        this.mConnectionPtr = nativeOpen(this.mConfiguration.path, this.mConfiguration.openFlags, this.mConfiguration.label, SQLiteDebug.DEBUG_SQL_STATEMENTS, SQLiteDebug.DEBUG_SQL_TIME);
        setPageSize();
        setForeignKeyModeFromConfiguration();
        setWalModeFromConfiguration();
        setJournalSizeLimit();
        setAutoCheckpointInterval();
        setLocaleFromConfiguration();
        int functionCount = this.mConfiguration.customFunctions.size();
        for (int i = 0; i < functionCount; i++) {
            SQLiteCustomFunction function = this.mConfiguration.customFunctions.get(i);
            nativeRegisterCustomFunction(this.mConnectionPtr, function);
        }
    }

    private void dispose(boolean finalized) {
        if (this.mCloseGuard != null) {
            if (finalized) {
                this.mCloseGuard.warnIfOpen();
            }
            this.mCloseGuard.close();
        }
        if (this.mConnectionPtr != 0) {
            int cookie = this.mRecentOperations.beginOperation("close", null, null);
            try {
                this.mPreparedStatementCache.evictAll();
                nativeClose(this.mConnectionPtr);
                this.mConnectionPtr = 0;
                this.mRecentOperations.endOperation(cookie);
            } catch (Throwable th) {
                this.mRecentOperations.endOperation(cookie);
                throw th;
            }
        }
    }

    private void setPageSize() {
        if (!this.mConfiguration.isInMemoryDb() && !this.mIsReadOnlyConnection) {
            long newValue = SQLiteGlobal.getDefaultPageSize();
            long value = executeForLong("PRAGMA page_size", null, null);
            if (value != newValue) {
                execute("PRAGMA page_size=" + newValue, null, null);
            }
        }
    }

    private void setAutoCheckpointInterval() {
        if (!this.mConfiguration.isInMemoryDb() && !this.mIsReadOnlyConnection) {
            long newValue = SQLiteGlobal.getWALAutoCheckpoint();
            long value = executeForLong("PRAGMA wal_autocheckpoint", null, null);
            if (value != newValue) {
                executeForLong("PRAGMA wal_autocheckpoint=" + newValue, null, null);
            }
        }
    }

    private void setJournalSizeLimit() {
        if (!this.mConfiguration.isInMemoryDb() && !this.mIsReadOnlyConnection) {
            long newValue = SQLiteGlobal.getJournalSizeLimit();
            long value = executeForLong("PRAGMA journal_size_limit", null, null);
            if (value != newValue) {
                executeForLong("PRAGMA journal_size_limit=" + newValue, null, null);
            }
        }
    }

    private void setForeignKeyModeFromConfiguration() {
        if (!this.mIsReadOnlyConnection) {
            long newValue = this.mConfiguration.foreignKeyConstraintsEnabled ? 1L : 0L;
            long value = executeForLong("PRAGMA foreign_keys", null, null);
            if (value != newValue) {
                execute("PRAGMA foreign_keys=" + newValue, null, null);
            }
        }
    }

    private void setWalModeFromConfiguration() {
        if (!this.mConfiguration.isInMemoryDb() && !this.mIsReadOnlyConnection) {
            if ((this.mConfiguration.openFlags & 536870912) != 0) {
                setJournalMode("WAL");
                setSyncMode(SQLiteGlobal.getWALSyncMode());
                return;
            }
            setJournalMode(SQLiteGlobal.getDefaultJournalMode());
            setSyncMode(SQLiteGlobal.getDefaultSyncMode());
        }
    }

    private void setSyncMode(String newValue) {
        String value = executeForString("PRAGMA synchronous", null, null);
        if (!canonicalizeSyncMode(value).equalsIgnoreCase(canonicalizeSyncMode(newValue))) {
            execute("PRAGMA synchronous=" + newValue, null, null);
        }
    }

    private static String canonicalizeSyncMode(String value) {
        if (value.equals("0")) {
            return "OFF";
        }
        if (value.equals("1")) {
            return "NORMAL";
        }
        if (value.equals("2")) {
            return "FULL";
        }
        return value;
    }

    private void setJournalMode(String newValue) {
        String value = executeForString("PRAGMA journal_mode", null, null);
        if (!value.equalsIgnoreCase(newValue)) {
            try {
                String result = executeForString("PRAGMA journal_mode=" + newValue, null, null);
                if (result.equalsIgnoreCase(newValue)) {
                    return;
                }
            } catch (SQLiteDatabaseLockedException e) {
            }
            Log.w(TAG, "Could not change the database journal mode of '" + this.mConfiguration.label + "' from '" + value + "' to '" + newValue + "' because the database is locked.  This usually means that there are other open connections to the database which prevents the database from enabling or disabling write-ahead logging mode.  Proceeding without changing the journal mode.");
        }
    }

    private void setLocaleFromConfiguration() {
        if ((this.mConfiguration.openFlags & 16) != 0) {
            return;
        }
        String newLocale = this.mConfiguration.locale.toString();
        nativeRegisterLocalizedCollators(this.mConnectionPtr, newLocale);
        if (this.mIsReadOnlyConnection) {
            return;
        }
        try {
            execute("CREATE TABLE IF NOT EXISTS android_metadata (locale TEXT)", null, null);
            String oldLocale = executeForString("SELECT locale FROM android_metadata UNION SELECT NULL ORDER BY locale DESC LIMIT 1", null, null);
            if (oldLocale != null && oldLocale.equals(newLocale)) {
                return;
            }
            execute("BEGIN", null, null);
            execute("DELETE FROM android_metadata", null, null);
            execute("INSERT INTO android_metadata (locale) VALUES(?)", new Object[]{newLocale}, null);
            execute("REINDEX LOCALIZED", null, null);
            execute(1 != 0 ? "COMMIT" : "ROLLBACK", null, null);
        } catch (RuntimeException ex) {
            throw new SQLiteException("Failed to change locale for db '" + this.mConfiguration.label + "' to '" + newLocale + "'.", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reconfigure(SQLiteDatabaseConfiguration configuration) {
        this.mOnlyAllowReadOnlyOperations = false;
        int functionCount = configuration.customFunctions.size();
        for (int i = 0; i < functionCount; i++) {
            SQLiteCustomFunction function = configuration.customFunctions.get(i);
            if (!this.mConfiguration.customFunctions.contains(function)) {
                nativeRegisterCustomFunction(this.mConnectionPtr, function);
            }
        }
        boolean foreignKeyModeChanged = configuration.foreignKeyConstraintsEnabled != this.mConfiguration.foreignKeyConstraintsEnabled;
        boolean walModeChanged = ((configuration.openFlags ^ this.mConfiguration.openFlags) & 536870912) != 0;
        boolean localeChanged = !configuration.locale.equals(this.mConfiguration.locale);
        this.mConfiguration.updateParametersFrom(configuration);
        this.mPreparedStatementCache.resize(configuration.maxSqlCacheSize);
        if (foreignKeyModeChanged) {
            setForeignKeyModeFromConfiguration();
        }
        if (walModeChanged) {
            setWalModeFromConfiguration();
        }
        if (localeChanged) {
            setLocaleFromConfiguration();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnlyAllowReadOnlyOperations(boolean readOnly) {
        this.mOnlyAllowReadOnlyOperations = readOnly;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isPreparedStatementInCache(String sql) {
        return this.mPreparedStatementCache.get(sql) != null;
    }

    public int getConnectionId() {
        return this.mConnectionId;
    }

    public boolean isPrimaryConnection() {
        return this.mIsPrimaryConnection;
    }

    /* JADX WARN: Finally extract failed */
    public void prepare(String sql, SQLiteStatementInfo outStatementInfo) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int cookie = this.mRecentOperations.beginOperation("prepare", sql, null);
        try {
            try {
                PreparedStatement statement = acquirePreparedStatement(sql);
                if (outStatementInfo != null) {
                    try {
                        outStatementInfo.numParameters = statement.mNumParameters;
                        outStatementInfo.readOnly = statement.mReadOnly;
                        int columnCount = nativeGetColumnCount(this.mConnectionPtr, statement.mStatementPtr);
                        if (columnCount == 0) {
                            outStatementInfo.columnNames = EMPTY_STRING_ARRAY;
                        } else {
                            outStatementInfo.columnNames = new String[columnCount];
                            for (int i = 0; i < columnCount; i++) {
                                outStatementInfo.columnNames[i] = nativeGetColumnName(this.mConnectionPtr, statement.mStatementPtr, i);
                            }
                        }
                    } catch (Throwable th) {
                        releasePreparedStatement(statement);
                        throw th;
                    }
                }
                releasePreparedStatement(statement);
            } catch (RuntimeException ex) {
                this.mRecentOperations.failOperation(cookie, ex);
                throw ex;
            }
        } finally {
            this.mRecentOperations.endOperation(cookie);
        }
    }

    /* JADX WARN: Finally extract failed */
    public void execute(String sql, Object[] bindArgs, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int cookie = this.mRecentOperations.beginOperation("execute", sql, bindArgs);
        try {
            try {
                PreparedStatement statement = acquirePreparedStatement(sql);
                try {
                    throwIfStatementForbidden(statement);
                    bindArguments(statement, bindArgs);
                    applyBlockGuardPolicy(statement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        nativeExecute(this.mConnectionPtr, statement.mStatementPtr);
                        detachCancellationSignal(cancellationSignal);
                        releasePreparedStatement(statement);
                    } catch (Throwable th) {
                        detachCancellationSignal(cancellationSignal);
                        throw th;
                    }
                } catch (Throwable th2) {
                    releasePreparedStatement(statement);
                    throw th2;
                }
            } catch (RuntimeException ex) {
                this.mRecentOperations.failOperation(cookie, ex);
                throw ex;
            }
        } finally {
            this.mRecentOperations.endOperation(cookie);
        }
    }

    public long executeForLong(String sql, Object[] bindArgs, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int cookie = this.mRecentOperations.beginOperation("executeForLong", sql, bindArgs);
        try {
            try {
                PreparedStatement statement = acquirePreparedStatement(sql);
                try {
                    throwIfStatementForbidden(statement);
                    bindArguments(statement, bindArgs);
                    applyBlockGuardPolicy(statement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        long nativeExecuteForLong = nativeExecuteForLong(this.mConnectionPtr, statement.mStatementPtr);
                        detachCancellationSignal(cancellationSignal);
                        releasePreparedStatement(statement);
                        this.mRecentOperations.endOperation(cookie);
                        return nativeExecuteForLong;
                    } catch (Throwable th) {
                        detachCancellationSignal(cancellationSignal);
                        throw th;
                    }
                } catch (Throwable th2) {
                    releasePreparedStatement(statement);
                    throw th2;
                }
            } catch (RuntimeException ex) {
                this.mRecentOperations.failOperation(cookie, ex);
                throw ex;
            }
        } catch (Throwable th3) {
            this.mRecentOperations.endOperation(cookie);
            throw th3;
        }
    }

    public String executeForString(String sql, Object[] bindArgs, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int cookie = this.mRecentOperations.beginOperation("executeForString", sql, bindArgs);
        try {
            try {
                PreparedStatement statement = acquirePreparedStatement(sql);
                try {
                    throwIfStatementForbidden(statement);
                    bindArguments(statement, bindArgs);
                    applyBlockGuardPolicy(statement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        String nativeExecuteForString = nativeExecuteForString(this.mConnectionPtr, statement.mStatementPtr);
                        detachCancellationSignal(cancellationSignal);
                        releasePreparedStatement(statement);
                        this.mRecentOperations.endOperation(cookie);
                        return nativeExecuteForString;
                    } catch (Throwable th) {
                        detachCancellationSignal(cancellationSignal);
                        throw th;
                    }
                } catch (Throwable th2) {
                    releasePreparedStatement(statement);
                    throw th2;
                }
            } catch (RuntimeException ex) {
                this.mRecentOperations.failOperation(cookie, ex);
                throw ex;
            }
        } catch (Throwable th3) {
            this.mRecentOperations.endOperation(cookie);
            throw th3;
        }
    }

    public ParcelFileDescriptor executeForBlobFileDescriptor(String sql, Object[] bindArgs, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int cookie = this.mRecentOperations.beginOperation("executeForBlobFileDescriptor", sql, bindArgs);
        try {
            try {
                PreparedStatement statement = acquirePreparedStatement(sql);
                try {
                    throwIfStatementForbidden(statement);
                    bindArguments(statement, bindArgs);
                    applyBlockGuardPolicy(statement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        int fd = nativeExecuteForBlobFileDescriptor(this.mConnectionPtr, statement.mStatementPtr);
                        ParcelFileDescriptor adoptFd = fd >= 0 ? ParcelFileDescriptor.adoptFd(fd) : null;
                        releasePreparedStatement(statement);
                        this.mRecentOperations.endOperation(cookie);
                        return adoptFd;
                    } finally {
                        detachCancellationSignal(cancellationSignal);
                    }
                } catch (Throwable th) {
                    releasePreparedStatement(statement);
                    throw th;
                }
            } catch (RuntimeException ex) {
                this.mRecentOperations.failOperation(cookie, ex);
                throw ex;
            }
        } catch (Throwable th2) {
            this.mRecentOperations.endOperation(cookie);
            throw th2;
        }
    }

    public int executeForChangedRowCount(String sql, Object[] bindArgs, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int cookie = this.mRecentOperations.beginOperation("executeForChangedRowCount", sql, bindArgs);
        try {
            try {
                PreparedStatement statement = acquirePreparedStatement(sql);
                try {
                    throwIfStatementForbidden(statement);
                    bindArguments(statement, bindArgs);
                    applyBlockGuardPolicy(statement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        int changedRows = nativeExecuteForChangedRowCount(this.mConnectionPtr, statement.mStatementPtr);
                        detachCancellationSignal(cancellationSignal);
                        releasePreparedStatement(statement);
                        if (this.mRecentOperations.endOperationDeferLog(cookie)) {
                            this.mRecentOperations.logOperation(cookie, "changedRows=" + changedRows);
                        }
                        return changedRows;
                    } catch (Throwable th) {
                        detachCancellationSignal(cancellationSignal);
                        throw th;
                    }
                } catch (Throwable th2) {
                    releasePreparedStatement(statement);
                    throw th2;
                }
            } catch (RuntimeException ex) {
                this.mRecentOperations.failOperation(cookie, ex);
                throw ex;
            }
        } catch (Throwable th3) {
            if (this.mRecentOperations.endOperationDeferLog(cookie)) {
                this.mRecentOperations.logOperation(cookie, "changedRows=0");
            }
            throw th3;
        }
    }

    public long executeForLastInsertedRowId(String sql, Object[] bindArgs, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        int cookie = this.mRecentOperations.beginOperation("executeForLastInsertedRowId", sql, bindArgs);
        try {
            try {
                PreparedStatement statement = acquirePreparedStatement(sql);
                try {
                    throwIfStatementForbidden(statement);
                    bindArguments(statement, bindArgs);
                    applyBlockGuardPolicy(statement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        long nativeExecuteForLastInsertedRowId = nativeExecuteForLastInsertedRowId(this.mConnectionPtr, statement.mStatementPtr);
                        detachCancellationSignal(cancellationSignal);
                        releasePreparedStatement(statement);
                        this.mRecentOperations.endOperation(cookie);
                        return nativeExecuteForLastInsertedRowId;
                    } catch (Throwable th) {
                        detachCancellationSignal(cancellationSignal);
                        throw th;
                    }
                } catch (Throwable th2) {
                    releasePreparedStatement(statement);
                    throw th2;
                }
            } catch (RuntimeException ex) {
                this.mRecentOperations.failOperation(cookie, ex);
                throw ex;
            }
        } catch (Throwable th3) {
            this.mRecentOperations.endOperation(cookie);
            throw th3;
        }
    }

    public int executeForCursorWindow(String sql, Object[] bindArgs, CursorWindow window, int startPos, int requiredPos, boolean countAllRows, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        if (window == null) {
            throw new IllegalArgumentException("window must not be null.");
        }
        window.acquireReference();
        try {
            int cookie = this.mRecentOperations.beginOperation("executeForCursorWindow", sql, bindArgs);
            try {
                PreparedStatement statement = acquirePreparedStatement(sql);
                try {
                    throwIfStatementForbidden(statement);
                    bindArguments(statement, bindArgs);
                    applyBlockGuardPolicy(statement);
                    attachCancellationSignal(cancellationSignal);
                    try {
                        long result = nativeExecuteForCursorWindow(this.mConnectionPtr, statement.mStatementPtr, window.mWindowPtr, startPos, requiredPos, countAllRows);
                        int actualPos = (int) (result >> 32);
                        int countedRows = (int) result;
                        int filledRows = window.getNumRows();
                        window.setStartPosition(actualPos);
                        detachCancellationSignal(cancellationSignal);
                        releasePreparedStatement(statement);
                        if (this.mRecentOperations.endOperationDeferLog(cookie)) {
                            this.mRecentOperations.logOperation(cookie, "window='" + window + "', startPos=" + startPos + ", actualPos=" + actualPos + ", filledRows=" + filledRows + ", countedRows=" + countedRows);
                        }
                        return countedRows;
                    } catch (Throwable th) {
                        detachCancellationSignal(cancellationSignal);
                        throw th;
                    }
                } catch (Throwable th2) {
                    releasePreparedStatement(statement);
                    throw th2;
                }
            } catch (RuntimeException ex) {
                this.mRecentOperations.failOperation(cookie, ex);
                throw ex;
            }
        } finally {
            window.releaseReference();
        }
    }

    private PreparedStatement acquirePreparedStatement(String sql) {
        PreparedStatement statement = this.mPreparedStatementCache.get(sql);
        boolean skipCache = false;
        if (statement != null) {
            if (!statement.mInUse) {
                return statement;
            }
            skipCache = true;
        }
        int statementPtr = nativePrepareStatement(this.mConnectionPtr, sql);
        try {
            int numParameters = nativeGetParameterCount(this.mConnectionPtr, statementPtr);
            int type = DatabaseUtils.getSqlStatementType(sql);
            boolean readOnly = nativeIsReadOnly(this.mConnectionPtr, statementPtr);
            statement = obtainPreparedStatement(sql, statementPtr, numParameters, type, readOnly);
            if (!skipCache && isCacheable(type)) {
                this.mPreparedStatementCache.put(sql, statement);
                statement.mInCache = true;
            }
            statement.mInUse = true;
            return statement;
        } catch (RuntimeException ex) {
            if (statement == null || !statement.mInCache) {
                nativeFinalizeStatement(this.mConnectionPtr, statementPtr);
            }
            throw ex;
        }
    }

    private void releasePreparedStatement(PreparedStatement statement) {
        statement.mInUse = false;
        if (statement.mInCache) {
            try {
                nativeResetStatementAndClearBindings(this.mConnectionPtr, statement.mStatementPtr);
                return;
            } catch (SQLiteException e) {
                this.mPreparedStatementCache.remove(statement.mSql);
                return;
            }
        }
        finalizePreparedStatement(statement);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finalizePreparedStatement(PreparedStatement statement) {
        nativeFinalizeStatement(this.mConnectionPtr, statement.mStatementPtr);
        recyclePreparedStatement(statement);
    }

    private void attachCancellationSignal(CancellationSignal cancellationSignal) {
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
            this.mCancellationSignalAttachCount++;
            if (this.mCancellationSignalAttachCount == 1) {
                nativeResetCancel(this.mConnectionPtr, true);
                cancellationSignal.setOnCancelListener(this);
            }
        }
    }

    private void detachCancellationSignal(CancellationSignal cancellationSignal) {
        if (cancellationSignal != null) {
            if (!$assertionsDisabled && this.mCancellationSignalAttachCount <= 0) {
                throw new AssertionError();
            }
            this.mCancellationSignalAttachCount--;
            if (this.mCancellationSignalAttachCount == 0) {
                cancellationSignal.setOnCancelListener(null);
                nativeResetCancel(this.mConnectionPtr, false);
            }
        }
    }

    @Override // android.os.CancellationSignal.OnCancelListener
    public void onCancel() {
        nativeCancel(this.mConnectionPtr);
    }

    private void bindArguments(PreparedStatement statement, Object[] bindArgs) {
        int count = bindArgs != null ? bindArgs.length : 0;
        if (count != statement.mNumParameters) {
            throw new SQLiteBindOrColumnIndexOutOfRangeException("Expected " + statement.mNumParameters + " bind arguments but " + count + " were provided.");
        }
        if (count == 0) {
            return;
        }
        int statementPtr = statement.mStatementPtr;
        for (int i = 0; i < count; i++) {
            Object arg = bindArgs[i];
            switch (DatabaseUtils.getTypeOfObject(arg)) {
                case 0:
                    nativeBindNull(this.mConnectionPtr, statementPtr, i + 1);
                    break;
                case 1:
                    nativeBindLong(this.mConnectionPtr, statementPtr, i + 1, ((Number) arg).longValue());
                    break;
                case 2:
                    nativeBindDouble(this.mConnectionPtr, statementPtr, i + 1, ((Number) arg).doubleValue());
                    break;
                case 3:
                default:
                    if (arg instanceof Boolean) {
                        nativeBindLong(this.mConnectionPtr, statementPtr, i + 1, ((Boolean) arg).booleanValue() ? 1L : 0L);
                        break;
                    } else {
                        nativeBindString(this.mConnectionPtr, statementPtr, i + 1, arg.toString());
                        break;
                    }
                case 4:
                    nativeBindBlob(this.mConnectionPtr, statementPtr, i + 1, (byte[]) arg);
                    break;
            }
        }
    }

    private void throwIfStatementForbidden(PreparedStatement statement) {
        if (this.mOnlyAllowReadOnlyOperations && !statement.mReadOnly) {
            throw new SQLiteException("Cannot execute this statement because it might modify the database but the connection is read-only.");
        }
    }

    private static boolean isCacheable(int statementType) {
        if (statementType == 2 || statementType == 1) {
            return true;
        }
        return false;
    }

    private void applyBlockGuardPolicy(PreparedStatement statement) {
        if (!this.mConfiguration.isInMemoryDb()) {
            if (statement.mReadOnly) {
                BlockGuard.getThreadPolicy().onReadFromDisk();
            } else {
                BlockGuard.getThreadPolicy().onWriteToDisk();
            }
        }
    }

    public void dump(Printer printer, boolean verbose) {
        dumpUnsafe(printer, verbose);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpUnsafe(Printer printer, boolean verbose) {
        printer.println("Connection #" + this.mConnectionId + Separators.COLON);
        if (verbose) {
            printer.println("  connectionPtr: 0x" + Integer.toHexString(this.mConnectionPtr));
        }
        printer.println("  isPrimaryConnection: " + this.mIsPrimaryConnection);
        printer.println("  onlyAllowReadOnlyOperations: " + this.mOnlyAllowReadOnlyOperations);
        this.mRecentOperations.dump(printer, verbose);
        if (verbose) {
            this.mPreparedStatementCache.dump(printer);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String describeCurrentOperationUnsafe() {
        return this.mRecentOperations.describeCurrentOperation();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void collectDbStats(ArrayList<SQLiteDebug.DbStats> dbStatsList) {
        int lookaside = nativeGetDbLookaside(this.mConnectionPtr);
        long pageCount = 0;
        long pageSize = 0;
        try {
            pageCount = executeForLong("PRAGMA page_count;", null, null);
            pageSize = executeForLong("PRAGMA page_size;", null, null);
        } catch (SQLiteException e) {
        }
        dbStatsList.add(getMainDbStatsUnsafe(lookaside, pageCount, pageSize));
        CursorWindow window = new CursorWindow("collectDbStats");
        try {
            executeForCursorWindow("PRAGMA database_list;", null, window, 0, 0, false, null);
            for (int i = 1; i < window.getNumRows(); i++) {
                String name = window.getString(i, 1);
                String path = window.getString(i, 2);
                long pageCount2 = 0;
                long pageSize2 = 0;
                try {
                    pageCount2 = executeForLong("PRAGMA " + name + ".page_count;", null, null);
                    pageSize2 = executeForLong("PRAGMA " + name + ".page_size;", null, null);
                } catch (SQLiteException e2) {
                }
                String label = "  (attached) " + name;
                if (!path.isEmpty()) {
                    label = label + ": " + path;
                }
                dbStatsList.add(new SQLiteDebug.DbStats(label, pageCount2, pageSize2, 0, 0, 0, 0));
            }
            window.close();
        } catch (SQLiteException e3) {
            window.close();
        } catch (Throwable th) {
            window.close();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void collectDbStatsUnsafe(ArrayList<SQLiteDebug.DbStats> dbStatsList) {
        dbStatsList.add(getMainDbStatsUnsafe(0, 0L, 0L));
    }

    private SQLiteDebug.DbStats getMainDbStatsUnsafe(int lookaside, long pageCount, long pageSize) {
        String label = this.mConfiguration.path;
        if (!this.mIsPrimaryConnection) {
            label = label + " (" + this.mConnectionId + Separators.RPAREN;
        }
        return new SQLiteDebug.DbStats(label, pageCount, pageSize, lookaside, this.mPreparedStatementCache.hitCount(), this.mPreparedStatementCache.missCount(), this.mPreparedStatementCache.size());
    }

    public String toString() {
        return "SQLiteConnection: " + this.mConfiguration.path + " (" + this.mConnectionId + Separators.RPAREN;
    }

    private PreparedStatement obtainPreparedStatement(String sql, int statementPtr, int numParameters, int type, boolean readOnly) {
        PreparedStatement statement = this.mPreparedStatementPool;
        if (statement != null) {
            this.mPreparedStatementPool = statement.mPoolNext;
            statement.mPoolNext = null;
            statement.mInCache = false;
        } else {
            statement = new PreparedStatement();
        }
        statement.mSql = sql;
        statement.mStatementPtr = statementPtr;
        statement.mNumParameters = numParameters;
        statement.mType = type;
        statement.mReadOnly = readOnly;
        return statement;
    }

    private void recyclePreparedStatement(PreparedStatement statement) {
        statement.mSql = null;
        statement.mPoolNext = this.mPreparedStatementPool;
        this.mPreparedStatementPool = statement;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String trimSqlForDisplay(String sql) {
        return TRIM_SQL_PATTERN.matcher(sql).replaceAll(Separators.SP);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SQLiteConnection$PreparedStatement.class */
    public static final class PreparedStatement {
        public PreparedStatement mPoolNext;
        public String mSql;
        public int mStatementPtr;
        public int mNumParameters;
        public int mType;
        public boolean mReadOnly;
        public boolean mInCache;
        public boolean mInUse;

        private PreparedStatement() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SQLiteConnection$PreparedStatementCache.class */
    public final class PreparedStatementCache extends LruCache<String, PreparedStatement> {
        public PreparedStatementCache(int size) {
            super(size);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.util.LruCache
        public void entryRemoved(boolean evicted, String key, PreparedStatement oldValue, PreparedStatement newValue) {
            oldValue.mInCache = false;
            if (!oldValue.mInUse) {
                SQLiteConnection.this.finalizePreparedStatement(oldValue);
            }
        }

        public void dump(Printer printer) {
            printer.println("  Prepared statement cache:");
            Map<String, PreparedStatement> cache = snapshot();
            if (!cache.isEmpty()) {
                int i = 0;
                for (Map.Entry<String, PreparedStatement> entry : cache.entrySet()) {
                    PreparedStatement statement = entry.getValue();
                    if (statement.mInCache) {
                        String sql = entry.getKey();
                        printer.println("    " + i + ": statementPtr=0x" + Integer.toHexString(statement.mStatementPtr) + ", numParameters=" + statement.mNumParameters + ", type=" + statement.mType + ", readOnly=" + statement.mReadOnly + ", sql=\"" + SQLiteConnection.trimSqlForDisplay(sql) + Separators.DOUBLE_QUOTE);
                    }
                    i++;
                }
                return;
            }
            printer.println("    <none>");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SQLiteConnection$OperationLog.class */
    public static final class OperationLog {
        private static final int MAX_RECENT_OPERATIONS = 20;
        private static final int COOKIE_GENERATION_SHIFT = 8;
        private static final int COOKIE_INDEX_MASK = 255;
        private final Operation[] mOperations;
        private int mIndex;
        private int mGeneration;

        private OperationLog() {
            this.mOperations = new Operation[20];
        }

        public int beginOperation(String kind, String sql, Object[] bindArgs) {
            int i;
            synchronized (this.mOperations) {
                int index = (this.mIndex + 1) % 20;
                Operation operation = this.mOperations[index];
                if (operation == null) {
                    operation = new Operation();
                    this.mOperations[index] = operation;
                } else {
                    operation.mFinished = false;
                    operation.mException = null;
                    if (operation.mBindArgs != null) {
                        operation.mBindArgs.clear();
                    }
                }
                operation.mStartTime = System.currentTimeMillis();
                operation.mKind = kind;
                operation.mSql = sql;
                if (bindArgs != null) {
                    if (operation.mBindArgs == null) {
                        operation.mBindArgs = new ArrayList<>();
                    } else {
                        operation.mBindArgs.clear();
                    }
                    for (Object arg : bindArgs) {
                        if (arg != null && (arg instanceof byte[])) {
                            operation.mBindArgs.add(SQLiteConnection.EMPTY_BYTE_ARRAY);
                        } else {
                            operation.mBindArgs.add(arg);
                        }
                    }
                }
                operation.mCookie = newOperationCookieLocked(index);
                this.mIndex = index;
                i = operation.mCookie;
            }
            return i;
        }

        public void failOperation(int cookie, Exception ex) {
            synchronized (this.mOperations) {
                Operation operation = getOperationLocked(cookie);
                if (operation != null) {
                    operation.mException = ex;
                }
            }
        }

        public void endOperation(int cookie) {
            synchronized (this.mOperations) {
                if (endOperationDeferLogLocked(cookie)) {
                    logOperationLocked(cookie, null);
                }
            }
        }

        public boolean endOperationDeferLog(int cookie) {
            boolean endOperationDeferLogLocked;
            synchronized (this.mOperations) {
                endOperationDeferLogLocked = endOperationDeferLogLocked(cookie);
            }
            return endOperationDeferLogLocked;
        }

        public void logOperation(int cookie, String detail) {
            synchronized (this.mOperations) {
                logOperationLocked(cookie, detail);
            }
        }

        private boolean endOperationDeferLogLocked(int cookie) {
            Operation operation = getOperationLocked(cookie);
            if (operation != null) {
                operation.mEndTime = System.currentTimeMillis();
                operation.mFinished = true;
                return SQLiteDebug.DEBUG_LOG_SLOW_QUERIES && SQLiteDebug.shouldLogSlowQuery(operation.mEndTime - operation.mStartTime);
            }
            return false;
        }

        private void logOperationLocked(int cookie, String detail) {
            Operation operation = getOperationLocked(cookie);
            StringBuilder msg = new StringBuilder();
            operation.describe(msg, false);
            if (detail != null) {
                msg.append(", ").append(detail);
            }
            Log.d(SQLiteConnection.TAG, msg.toString());
        }

        private int newOperationCookieLocked(int index) {
            int generation = this.mGeneration;
            this.mGeneration = generation + 1;
            return (generation << 8) | index;
        }

        private Operation getOperationLocked(int cookie) {
            int index = cookie & 255;
            Operation operation = this.mOperations[index];
            if (operation.mCookie == cookie) {
                return operation;
            }
            return null;
        }

        public String describeCurrentOperation() {
            synchronized (this.mOperations) {
                Operation operation = this.mOperations[this.mIndex];
                if (operation != null && !operation.mFinished) {
                    StringBuilder msg = new StringBuilder();
                    operation.describe(msg, false);
                    return msg.toString();
                }
                return null;
            }
        }

        public void dump(Printer printer, boolean verbose) {
            synchronized (this.mOperations) {
                printer.println("  Most recently executed operations:");
                int index = this.mIndex;
                Operation operation = this.mOperations[index];
                if (operation != null) {
                    int n = 0;
                    do {
                        StringBuilder msg = new StringBuilder();
                        msg.append("    ").append(n).append(": [");
                        msg.append(operation.getFormattedStartTime());
                        msg.append("] ");
                        operation.describe(msg, verbose);
                        printer.println(msg.toString());
                        if (index > 0) {
                            index--;
                        } else {
                            index = 19;
                        }
                        n++;
                        operation = this.mOperations[index];
                        if (operation == null) {
                            break;
                        }
                    } while (n < 20);
                } else {
                    printer.println("    <none>");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SQLiteConnection$Operation.class */
    public static final class Operation {
        private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        public long mStartTime;
        public long mEndTime;
        public String mKind;
        public String mSql;
        public ArrayList<Object> mBindArgs;
        public boolean mFinished;
        public Exception mException;
        public int mCookie;

        private Operation() {
        }

        public void describe(StringBuilder msg, boolean verbose) {
            msg.append(this.mKind);
            if (this.mFinished) {
                msg.append(" took ").append(this.mEndTime - this.mStartTime).append("ms");
            } else {
                msg.append(" started ").append(System.currentTimeMillis() - this.mStartTime).append("ms ago");
            }
            msg.append(" - ").append(getStatus());
            if (this.mSql != null) {
                msg.append(", sql=\"").append(SQLiteConnection.trimSqlForDisplay(this.mSql)).append(Separators.DOUBLE_QUOTE);
            }
            if (verbose && this.mBindArgs != null && this.mBindArgs.size() != 0) {
                msg.append(", bindArgs=[");
                int count = this.mBindArgs.size();
                for (int i = 0; i < count; i++) {
                    Object arg = this.mBindArgs.get(i);
                    if (i != 0) {
                        msg.append(", ");
                    }
                    if (arg == null) {
                        msg.append("null");
                    } else if (arg instanceof byte[]) {
                        msg.append("<byte[]>");
                    } else if (arg instanceof String) {
                        msg.append(Separators.DOUBLE_QUOTE).append((String) arg).append(Separators.DOUBLE_QUOTE);
                    } else {
                        msg.append(arg);
                    }
                }
                msg.append("]");
            }
            if (this.mException != null) {
                msg.append(", exception=\"").append(this.mException.getMessage()).append(Separators.DOUBLE_QUOTE);
            }
        }

        private String getStatus() {
            if (this.mFinished) {
                return this.mException != null ? "failed" : "succeeded";
            }
            return "running";
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getFormattedStartTime() {
            return sDateFormat.format(new Date(this.mStartTime));
        }
    }
}