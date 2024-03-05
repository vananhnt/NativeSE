package android.database;

import android.database.AbstractCursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

/* loaded from: BulkCursorToCursorAdaptor.class */
public final class BulkCursorToCursorAdaptor extends AbstractWindowedCursor {
    private static final String TAG = "BulkCursor";
    private AbstractCursor.SelfContentObserver mObserverBridge = new AbstractCursor.SelfContentObserver(this);
    private IBulkCursor mBulkCursor;
    private String[] mColumns;
    private boolean mWantsAllOnMoveCalls;
    private int mCount;

    public void initialize(BulkCursorDescriptor d) {
        this.mBulkCursor = d.cursor;
        this.mColumns = d.columnNames;
        this.mRowIdColumnIndex = DatabaseUtils.findRowIdColumnIndex(this.mColumns);
        this.mWantsAllOnMoveCalls = d.wantsAllOnMoveCalls;
        this.mCount = d.count;
        if (d.window != null) {
            setWindow(d.window);
        }
    }

    public IContentObserver getObserver() {
        return this.mObserverBridge.getContentObserver();
    }

    private void throwIfCursorIsClosed() {
        if (this.mBulkCursor == null) {
            throw new StaleDataException("Attempted to access a cursor after it has been closed.");
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public int getCount() {
        throwIfCursorIsClosed();
        return this.mCount;
    }

    @Override // android.database.AbstractCursor, android.database.CrossProcessCursor
    public boolean onMove(int oldPosition, int newPosition) {
        throwIfCursorIsClosed();
        try {
            if (this.mWindow == null || newPosition < this.mWindow.getStartPosition() || newPosition >= this.mWindow.getStartPosition() + this.mWindow.getNumRows()) {
                setWindow(this.mBulkCursor.getWindow(newPosition));
            } else if (this.mWantsAllOnMoveCalls) {
                this.mBulkCursor.onMove(newPosition);
            }
            if (this.mWindow == null) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to get window because the remote process is dead");
            return false;
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public void deactivate() {
        super.deactivate();
        if (this.mBulkCursor != null) {
            try {
                this.mBulkCursor.deactivate();
            } catch (RemoteException e) {
                Log.w(TAG, "Remote process exception when deactivating");
            }
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor, java.io.Closeable
    public void close() {
        super.close();
        if (this.mBulkCursor != null) {
            try {
                try {
                    this.mBulkCursor.close();
                    this.mBulkCursor = null;
                } catch (RemoteException e) {
                    Log.w(TAG, "Remote process exception when closing");
                    this.mBulkCursor = null;
                }
            } catch (Throwable th) {
                this.mBulkCursor = null;
                throw th;
            }
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public boolean requery() {
        throwIfCursorIsClosed();
        try {
            this.mCount = this.mBulkCursor.requery(getObserver());
            if (this.mCount != -1) {
                this.mPos = -1;
                closeWindow();
                super.requery();
                return true;
            }
            deactivate();
            return false;
        } catch (Exception ex) {
            Log.e(TAG, "Unable to requery because the remote process exception " + ex.getMessage());
            deactivate();
            return false;
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public String[] getColumnNames() {
        throwIfCursorIsClosed();
        return this.mColumns;
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public Bundle getExtras() {
        throwIfCursorIsClosed();
        try {
            return this.mBulkCursor.getExtras();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // android.database.AbstractCursor, android.database.Cursor
    public Bundle respond(Bundle extras) {
        throwIfCursorIsClosed();
        try {
            return this.mBulkCursor.respond(extras);
        } catch (RemoteException e) {
            Log.w(TAG, "respond() threw RemoteException, returning an empty bundle.", e);
            return Bundle.EMPTY;
        }
    }
}