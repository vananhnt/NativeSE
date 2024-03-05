package android.database.sqlite;

import android.database.CursorWindow;
import android.database.DatabaseUtils;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;

/* loaded from: SQLiteSession.class */
public final class SQLiteSession {
    private final SQLiteConnectionPool mConnectionPool;
    private SQLiteConnection mConnection;
    private int mConnectionFlags;
    private int mConnectionUseCount;
    private Transaction mTransactionPool;
    private Transaction mTransactionStack;
    public static final int TRANSACTION_MODE_DEFERRED = 0;
    public static final int TRANSACTION_MODE_IMMEDIATE = 1;
    public static final int TRANSACTION_MODE_EXCLUSIVE = 2;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !SQLiteSession.class.desiredAssertionStatus();
    }

    public SQLiteSession(SQLiteConnectionPool connectionPool) {
        if (connectionPool == null) {
            throw new IllegalArgumentException("connectionPool must not be null");
        }
        this.mConnectionPool = connectionPool;
    }

    public boolean hasTransaction() {
        return this.mTransactionStack != null;
    }

    public boolean hasNestedTransaction() {
        return (this.mTransactionStack == null || this.mTransactionStack.mParent == null) ? false : true;
    }

    public boolean hasConnection() {
        return this.mConnection != null;
    }

    public void beginTransaction(int transactionMode, SQLiteTransactionListener transactionListener, int connectionFlags, CancellationSignal cancellationSignal) {
        throwIfTransactionMarkedSuccessful();
        beginTransactionUnchecked(transactionMode, transactionListener, connectionFlags, cancellationSignal);
    }

    private void beginTransactionUnchecked(int transactionMode, SQLiteTransactionListener transactionListener, int connectionFlags, CancellationSignal cancellationSignal) {
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        if (this.mTransactionStack == null) {
            acquireConnection(null, connectionFlags, cancellationSignal);
        }
        try {
            if (this.mTransactionStack == null) {
                switch (transactionMode) {
                    case 1:
                        this.mConnection.execute("BEGIN IMMEDIATE;", null, cancellationSignal);
                        break;
                    case 2:
                        this.mConnection.execute("BEGIN EXCLUSIVE;", null, cancellationSignal);
                        break;
                    default:
                        this.mConnection.execute("BEGIN;", null, cancellationSignal);
                        break;
                }
            }
            if (transactionListener != null) {
                try {
                    transactionListener.onBegin();
                } catch (RuntimeException ex) {
                    if (this.mTransactionStack == null) {
                        this.mConnection.execute("ROLLBACK;", null, cancellationSignal);
                    }
                    throw ex;
                }
            }
            Transaction transaction = obtainTransaction(transactionMode, transactionListener);
            transaction.mParent = this.mTransactionStack;
            this.mTransactionStack = transaction;
            if (this.mTransactionStack == null) {
                releaseConnection();
            }
        } catch (Throwable th) {
            if (this.mTransactionStack == null) {
                releaseConnection();
            }
            throw th;
        }
    }

    public void setTransactionSuccessful() {
        throwIfNoTransaction();
        throwIfTransactionMarkedSuccessful();
        this.mTransactionStack.mMarkedSuccessful = true;
    }

    public void endTransaction(CancellationSignal cancellationSignal) {
        throwIfNoTransaction();
        if (!$assertionsDisabled && this.mConnection == null) {
            throw new AssertionError();
        }
        endTransactionUnchecked(cancellationSignal, false);
    }

    private void endTransactionUnchecked(CancellationSignal cancellationSignal, boolean yielding) {
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        Transaction top = this.mTransactionStack;
        boolean successful = (top.mMarkedSuccessful || yielding) && !top.mChildFailed;
        RuntimeException listenerException = null;
        SQLiteTransactionListener listener = top.mListener;
        if (listener != null) {
            try {
                if (successful) {
                    listener.onCommit();
                } else {
                    listener.onRollback();
                }
            } catch (RuntimeException ex) {
                listenerException = ex;
                successful = false;
            }
        }
        this.mTransactionStack = top.mParent;
        recycleTransaction(top);
        if (this.mTransactionStack != null) {
            if (!successful) {
                this.mTransactionStack.mChildFailed = true;
            }
        } else {
            try {
                if (successful) {
                    this.mConnection.execute("COMMIT;", null, cancellationSignal);
                } else {
                    this.mConnection.execute("ROLLBACK;", null, cancellationSignal);
                }
                releaseConnection();
            } catch (Throwable th) {
                releaseConnection();
                throw th;
            }
        }
        if (listenerException != null) {
            throw listenerException;
        }
    }

    public boolean yieldTransaction(long sleepAfterYieldDelayMillis, boolean throwIfUnsafe, CancellationSignal cancellationSignal) {
        if (throwIfUnsafe) {
            throwIfNoTransaction();
            throwIfTransactionMarkedSuccessful();
            throwIfNestedTransaction();
        } else if (this.mTransactionStack == null || this.mTransactionStack.mMarkedSuccessful || this.mTransactionStack.mParent != null) {
            return false;
        }
        if ($assertionsDisabled || this.mConnection != null) {
            if (this.mTransactionStack.mChildFailed) {
                return false;
            }
            return yieldTransactionUnchecked(sleepAfterYieldDelayMillis, cancellationSignal);
        }
        throw new AssertionError();
    }

    private boolean yieldTransactionUnchecked(long sleepAfterYieldDelayMillis, CancellationSignal cancellationSignal) {
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        if (!this.mConnectionPool.shouldYieldConnection(this.mConnection, this.mConnectionFlags)) {
            return false;
        }
        int transactionMode = this.mTransactionStack.mMode;
        SQLiteTransactionListener listener = this.mTransactionStack.mListener;
        int connectionFlags = this.mConnectionFlags;
        endTransactionUnchecked(cancellationSignal, true);
        if (sleepAfterYieldDelayMillis > 0) {
            try {
                Thread.sleep(sleepAfterYieldDelayMillis);
            } catch (InterruptedException e) {
            }
        }
        beginTransactionUnchecked(transactionMode, listener, connectionFlags, cancellationSignal);
        return true;
    }

    public void prepare(String sql, int connectionFlags, CancellationSignal cancellationSignal, SQLiteStatementInfo outStatementInfo) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        acquireConnection(sql, connectionFlags, cancellationSignal);
        try {
            this.mConnection.prepare(sql, outStatementInfo);
            releaseConnection();
        } catch (Throwable th) {
            releaseConnection();
            throw th;
        }
    }

    public void execute(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return;
        }
        acquireConnection(sql, connectionFlags, cancellationSignal);
        try {
            this.mConnection.execute(sql, bindArgs, cancellationSignal);
            releaseConnection();
        } catch (Throwable th) {
            releaseConnection();
            throw th;
        }
    }

    public long executeForLong(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return 0L;
        }
        acquireConnection(sql, connectionFlags, cancellationSignal);
        try {
            long executeForLong = this.mConnection.executeForLong(sql, bindArgs, cancellationSignal);
            releaseConnection();
            return executeForLong;
        } catch (Throwable th) {
            releaseConnection();
            throw th;
        }
    }

    public String executeForString(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return null;
        }
        acquireConnection(sql, connectionFlags, cancellationSignal);
        try {
            String executeForString = this.mConnection.executeForString(sql, bindArgs, cancellationSignal);
            releaseConnection();
            return executeForString;
        } catch (Throwable th) {
            releaseConnection();
            throw th;
        }
    }

    public ParcelFileDescriptor executeForBlobFileDescriptor(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return null;
        }
        acquireConnection(sql, connectionFlags, cancellationSignal);
        try {
            ParcelFileDescriptor executeForBlobFileDescriptor = this.mConnection.executeForBlobFileDescriptor(sql, bindArgs, cancellationSignal);
            releaseConnection();
            return executeForBlobFileDescriptor;
        } catch (Throwable th) {
            releaseConnection();
            throw th;
        }
    }

    public int executeForChangedRowCount(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return 0;
        }
        acquireConnection(sql, connectionFlags, cancellationSignal);
        try {
            int executeForChangedRowCount = this.mConnection.executeForChangedRowCount(sql, bindArgs, cancellationSignal);
            releaseConnection();
            return executeForChangedRowCount;
        } catch (Throwable th) {
            releaseConnection();
            throw th;
        }
    }

    public long executeForLastInsertedRowId(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return 0L;
        }
        acquireConnection(sql, connectionFlags, cancellationSignal);
        try {
            long executeForLastInsertedRowId = this.mConnection.executeForLastInsertedRowId(sql, bindArgs, cancellationSignal);
            releaseConnection();
            return executeForLastInsertedRowId;
        } catch (Throwable th) {
            releaseConnection();
            throw th;
        }
    }

    public int executeForCursorWindow(String sql, Object[] bindArgs, CursorWindow window, int startPos, int requiredPos, boolean countAllRows, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        if (window == null) {
            throw new IllegalArgumentException("window must not be null.");
        }
        if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            window.clear();
            return 0;
        }
        acquireConnection(sql, connectionFlags, cancellationSignal);
        try {
            int executeForCursorWindow = this.mConnection.executeForCursorWindow(sql, bindArgs, window, startPos, requiredPos, countAllRows, cancellationSignal);
            releaseConnection();
            return executeForCursorWindow;
        } catch (Throwable th) {
            releaseConnection();
            throw th;
        }
    }

    private boolean executeSpecial(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        int type = DatabaseUtils.getSqlStatementType(sql);
        switch (type) {
            case 4:
                beginTransaction(2, null, connectionFlags, cancellationSignal);
                return true;
            case 5:
                setTransactionSuccessful();
                endTransaction(cancellationSignal);
                return true;
            case 6:
                endTransaction(cancellationSignal);
                return true;
            default:
                return false;
        }
    }

    private void acquireConnection(String sql, int connectionFlags, CancellationSignal cancellationSignal) {
        if (this.mConnection == null) {
            if (!$assertionsDisabled && this.mConnectionUseCount != 0) {
                throw new AssertionError();
            }
            this.mConnection = this.mConnectionPool.acquireConnection(sql, connectionFlags, cancellationSignal);
            this.mConnectionFlags = connectionFlags;
        }
        this.mConnectionUseCount++;
    }

    private void releaseConnection() {
        if (!$assertionsDisabled && this.mConnection == null) {
            throw new AssertionError();
        }
        if (!$assertionsDisabled && this.mConnectionUseCount <= 0) {
            throw new AssertionError();
        }
        int i = this.mConnectionUseCount - 1;
        this.mConnectionUseCount = i;
        if (i == 0) {
            try {
                this.mConnectionPool.releaseConnection(this.mConnection);
                this.mConnection = null;
            } catch (Throwable th) {
                this.mConnection = null;
                throw th;
            }
        }
    }

    private void throwIfNoTransaction() {
        if (this.mTransactionStack == null) {
            throw new IllegalStateException("Cannot perform this operation because there is no current transaction.");
        }
    }

    private void throwIfTransactionMarkedSuccessful() {
        if (this.mTransactionStack != null && this.mTransactionStack.mMarkedSuccessful) {
            throw new IllegalStateException("Cannot perform this operation because the transaction has already been marked successful.  The only thing you can do now is call endTransaction().");
        }
    }

    private void throwIfNestedTransaction() {
        if (hasNestedTransaction()) {
            throw new IllegalStateException("Cannot perform this operation because a nested transaction is in progress.");
        }
    }

    private Transaction obtainTransaction(int mode, SQLiteTransactionListener listener) {
        Transaction transaction = this.mTransactionPool;
        if (transaction != null) {
            this.mTransactionPool = transaction.mParent;
            transaction.mParent = null;
            transaction.mMarkedSuccessful = false;
            transaction.mChildFailed = false;
        } else {
            transaction = new Transaction();
        }
        transaction.mMode = mode;
        transaction.mListener = listener;
        return transaction;
    }

    private void recycleTransaction(Transaction transaction) {
        transaction.mParent = this.mTransactionPool;
        transaction.mListener = null;
        this.mTransactionPool = transaction;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SQLiteSession$Transaction.class */
    public static final class Transaction {
        public Transaction mParent;
        public int mMode;
        public SQLiteTransactionListener mListener;
        public boolean mMarkedSuccessful;
        public boolean mChildFailed;

        private Transaction() {
        }
    }
}