package android.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import java.io.Closeable;

/* loaded from: Cursor.class */
public interface Cursor extends Closeable {
    public static final int FIELD_TYPE_NULL = 0;
    public static final int FIELD_TYPE_INTEGER = 1;
    public static final int FIELD_TYPE_FLOAT = 2;
    public static final int FIELD_TYPE_STRING = 3;
    public static final int FIELD_TYPE_BLOB = 4;

    int getCount();

    int getPosition();

    boolean move(int i);

    boolean moveToPosition(int i);

    boolean moveToFirst();

    boolean moveToLast();

    boolean moveToNext();

    boolean moveToPrevious();

    boolean isFirst();

    boolean isLast();

    boolean isBeforeFirst();

    boolean isAfterLast();

    int getColumnIndex(String str);

    int getColumnIndexOrThrow(String str) throws IllegalArgumentException;

    String getColumnName(int i);

    String[] getColumnNames();

    int getColumnCount();

    byte[] getBlob(int i);

    String getString(int i);

    void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer);

    short getShort(int i);

    int getInt(int i);

    long getLong(int i);

    float getFloat(int i);

    double getDouble(int i);

    int getType(int i);

    boolean isNull(int i);

    void deactivate();

    @Deprecated
    boolean requery();

    @Override // java.io.Closeable
    void close();

    boolean isClosed();

    void registerContentObserver(ContentObserver contentObserver);

    void unregisterContentObserver(ContentObserver contentObserver);

    void registerDataSetObserver(DataSetObserver dataSetObserver);

    void unregisterDataSetObserver(DataSetObserver dataSetObserver);

    void setNotificationUri(ContentResolver contentResolver, Uri uri);

    Uri getNotificationUri();

    boolean getWantsAllOnMoveCalls();

    Bundle getExtras();

    Bundle respond(Bundle bundle);
}