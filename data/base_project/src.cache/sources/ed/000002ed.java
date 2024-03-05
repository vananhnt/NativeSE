package android.content;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/* loaded from: ContentQueryMap.class */
public class ContentQueryMap extends Observable {
    private volatile Cursor mCursor;
    private String[] mColumnNames;
    private int mKeyColumn;
    private Handler mHandlerForUpdateNotifications;
    private ContentObserver mContentObserver;
    private boolean mKeepUpdated = false;
    private Map<String, ContentValues> mValues = null;
    private boolean mDirty = false;

    public ContentQueryMap(Cursor cursor, String columnNameOfKey, boolean keepUpdated, Handler handlerForUpdateNotifications) {
        this.mHandlerForUpdateNotifications = null;
        this.mCursor = cursor;
        this.mColumnNames = this.mCursor.getColumnNames();
        this.mKeyColumn = this.mCursor.getColumnIndexOrThrow(columnNameOfKey);
        this.mHandlerForUpdateNotifications = handlerForUpdateNotifications;
        setKeepUpdated(keepUpdated);
        if (!keepUpdated) {
            readCursorIntoCache(cursor);
        }
    }

    public void setKeepUpdated(boolean keepUpdated) {
        if (keepUpdated == this.mKeepUpdated) {
            return;
        }
        this.mKeepUpdated = keepUpdated;
        if (!this.mKeepUpdated) {
            this.mCursor.unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
            return;
        }
        if (this.mHandlerForUpdateNotifications == null) {
            this.mHandlerForUpdateNotifications = new Handler();
        }
        if (this.mContentObserver == null) {
            this.mContentObserver = new ContentObserver(this.mHandlerForUpdateNotifications) { // from class: android.content.ContentQueryMap.1
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange) {
                    if (ContentQueryMap.this.countObservers() == 0) {
                        ContentQueryMap.this.mDirty = true;
                    } else {
                        ContentQueryMap.this.requery();
                    }
                }
            };
        }
        this.mCursor.registerContentObserver(this.mContentObserver);
        this.mDirty = true;
    }

    public synchronized ContentValues getValues(String rowName) {
        if (this.mDirty) {
            requery();
        }
        return this.mValues.get(rowName);
    }

    public void requery() {
        Cursor cursor = this.mCursor;
        if (cursor == null) {
            return;
        }
        this.mDirty = false;
        if (!cursor.requery()) {
            return;
        }
        readCursorIntoCache(cursor);
        setChanged();
        notifyObservers();
    }

    private synchronized void readCursorIntoCache(Cursor cursor) {
        int capacity = this.mValues != null ? this.mValues.size() : 0;
        this.mValues = new HashMap(capacity);
        while (cursor.moveToNext()) {
            ContentValues values = new ContentValues();
            for (int i = 0; i < this.mColumnNames.length; i++) {
                if (i != this.mKeyColumn) {
                    values.put(this.mColumnNames[i], cursor.getString(i));
                }
            }
            this.mValues.put(cursor.getString(this.mKeyColumn), values);
        }
    }

    public synchronized Map<String, ContentValues> getRows() {
        if (this.mDirty) {
            requery();
        }
        return this.mValues;
    }

    public synchronized void close() {
        if (this.mContentObserver != null) {
            this.mCursor.unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
        }
        this.mCursor.close();
        this.mCursor = null;
    }

    protected void finalize() throws Throwable {
        if (this.mCursor != null) {
            close();
        }
        super.finalize();
    }
}