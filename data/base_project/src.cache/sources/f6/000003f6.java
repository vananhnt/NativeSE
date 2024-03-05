package android.database;

import android.content.res.Resources;
import android.database.sqlite.SQLiteClosable;
import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.util.Log;
import android.util.SparseIntArray;
import com.android.internal.R;
import dalvik.system.CloseGuard;
import gov.nist.core.Separators;

/* loaded from: CursorWindow.class */
public class CursorWindow extends SQLiteClosable implements Parcelable {
    private static final String STATS_TAG = "CursorWindowStats";
    public int mWindowPtr;
    private int mStartPos;
    private final String mName;
    private final CloseGuard mCloseGuard;
    private static final int sCursorWindowSize = Resources.getSystem().getInteger(R.integer.config_cursorWindowSize) * 1024;
    public static final Parcelable.Creator<CursorWindow> CREATOR = new Parcelable.Creator<CursorWindow>() { // from class: android.database.CursorWindow.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CursorWindow createFromParcel(Parcel source) {
            return new CursorWindow(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CursorWindow[] newArray(int size) {
            return new CursorWindow[size];
        }
    };
    private static final SparseIntArray sWindowToPidMap = new SparseIntArray();

    private static native int nativeCreate(String str, int i);

    private static native int nativeCreateFromParcel(Parcel parcel);

    private static native void nativeDispose(int i);

    private static native void nativeWriteToParcel(int i, Parcel parcel);

    private static native void nativeClear(int i);

    private static native int nativeGetNumRows(int i);

    private static native boolean nativeSetNumColumns(int i, int i2);

    private static native boolean nativeAllocRow(int i);

    private static native void nativeFreeLastRow(int i);

    private static native int nativeGetType(int i, int i2, int i3);

    private static native byte[] nativeGetBlob(int i, int i2, int i3);

    private static native String nativeGetString(int i, int i2, int i3);

    private static native long nativeGetLong(int i, int i2, int i3);

    private static native double nativeGetDouble(int i, int i2, int i3);

    private static native void nativeCopyStringToBuffer(int i, int i2, int i3, CharArrayBuffer charArrayBuffer);

    private static native boolean nativePutBlob(int i, byte[] bArr, int i2, int i3);

    private static native boolean nativePutString(int i, String str, int i2, int i3);

    private static native boolean nativePutLong(int i, long j, int i2, int i3);

    private static native boolean nativePutDouble(int i, double d, int i2, int i3);

    private static native boolean nativePutNull(int i, int i2, int i3);

    private static native String nativeGetName(int i);

    public CursorWindow(String name) {
        this.mCloseGuard = CloseGuard.get();
        this.mStartPos = 0;
        this.mName = (name == null || name.length() == 0) ? "<unnamed>" : name;
        this.mWindowPtr = nativeCreate(this.mName, sCursorWindowSize);
        if (this.mWindowPtr == 0) {
            throw new CursorWindowAllocationException("Cursor window allocation of " + (sCursorWindowSize / 1024) + " kb failed. " + printStats());
        }
        this.mCloseGuard.open("close");
        recordNewWindow(Binder.getCallingPid(), this.mWindowPtr);
    }

    @Deprecated
    public CursorWindow(boolean localWindow) {
        this((String) null);
    }

    private CursorWindow(Parcel source) {
        this.mCloseGuard = CloseGuard.get();
        this.mStartPos = source.readInt();
        this.mWindowPtr = nativeCreateFromParcel(source);
        if (this.mWindowPtr == 0) {
            throw new CursorWindowAllocationException("Cursor window could not be created from binder.");
        }
        this.mName = nativeGetName(this.mWindowPtr);
        this.mCloseGuard.open("close");
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mCloseGuard != null) {
                this.mCloseGuard.warnIfOpen();
            }
            dispose();
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
            throw th;
        }
    }

    private void dispose() {
        if (this.mCloseGuard != null) {
            this.mCloseGuard.close();
        }
        if (this.mWindowPtr != 0) {
            recordClosingOfWindow(this.mWindowPtr);
            nativeDispose(this.mWindowPtr);
            this.mWindowPtr = 0;
        }
    }

    public String getName() {
        return this.mName;
    }

    public void clear() {
        acquireReference();
        try {
            this.mStartPos = 0;
            nativeClear(this.mWindowPtr);
            releaseReference();
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public int getStartPosition() {
        return this.mStartPos;
    }

    public void setStartPosition(int pos) {
        this.mStartPos = pos;
    }

    public int getNumRows() {
        acquireReference();
        try {
            int nativeGetNumRows = nativeGetNumRows(this.mWindowPtr);
            releaseReference();
            return nativeGetNumRows;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public boolean setNumColumns(int columnNum) {
        acquireReference();
        try {
            boolean nativeSetNumColumns = nativeSetNumColumns(this.mWindowPtr, columnNum);
            releaseReference();
            return nativeSetNumColumns;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public boolean allocRow() {
        acquireReference();
        try {
            boolean nativeAllocRow = nativeAllocRow(this.mWindowPtr);
            releaseReference();
            return nativeAllocRow;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public void freeLastRow() {
        acquireReference();
        try {
            nativeFreeLastRow(this.mWindowPtr);
            releaseReference();
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    @Deprecated
    public boolean isNull(int row, int column) {
        return getType(row, column) == 0;
    }

    @Deprecated
    public boolean isBlob(int row, int column) {
        int type = getType(row, column);
        return type == 4 || type == 0;
    }

    @Deprecated
    public boolean isLong(int row, int column) {
        return getType(row, column) == 1;
    }

    @Deprecated
    public boolean isFloat(int row, int column) {
        return getType(row, column) == 2;
    }

    @Deprecated
    public boolean isString(int row, int column) {
        int type = getType(row, column);
        return type == 3 || type == 0;
    }

    public int getType(int row, int column) {
        acquireReference();
        try {
            int nativeGetType = nativeGetType(this.mWindowPtr, row - this.mStartPos, column);
            releaseReference();
            return nativeGetType;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public byte[] getBlob(int row, int column) {
        acquireReference();
        try {
            byte[] nativeGetBlob = nativeGetBlob(this.mWindowPtr, row - this.mStartPos, column);
            releaseReference();
            return nativeGetBlob;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public String getString(int row, int column) {
        acquireReference();
        try {
            String nativeGetString = nativeGetString(this.mWindowPtr, row - this.mStartPos, column);
            releaseReference();
            return nativeGetString;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public void copyStringToBuffer(int row, int column, CharArrayBuffer buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("CharArrayBuffer should not be null");
        }
        acquireReference();
        try {
            nativeCopyStringToBuffer(this.mWindowPtr, row - this.mStartPos, column, buffer);
            releaseReference();
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public long getLong(int row, int column) {
        acquireReference();
        try {
            long nativeGetLong = nativeGetLong(this.mWindowPtr, row - this.mStartPos, column);
            releaseReference();
            return nativeGetLong;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public double getDouble(int row, int column) {
        acquireReference();
        try {
            double nativeGetDouble = nativeGetDouble(this.mWindowPtr, row - this.mStartPos, column);
            releaseReference();
            return nativeGetDouble;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public short getShort(int row, int column) {
        return (short) getLong(row, column);
    }

    public int getInt(int row, int column) {
        return (int) getLong(row, column);
    }

    public float getFloat(int row, int column) {
        return (float) getDouble(row, column);
    }

    public boolean putBlob(byte[] value, int row, int column) {
        acquireReference();
        try {
            boolean nativePutBlob = nativePutBlob(this.mWindowPtr, value, row - this.mStartPos, column);
            releaseReference();
            return nativePutBlob;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public boolean putString(String value, int row, int column) {
        acquireReference();
        try {
            boolean nativePutString = nativePutString(this.mWindowPtr, value, row - this.mStartPos, column);
            releaseReference();
            return nativePutString;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public boolean putLong(long value, int row, int column) {
        acquireReference();
        try {
            boolean nativePutLong = nativePutLong(this.mWindowPtr, value, row - this.mStartPos, column);
            releaseReference();
            return nativePutLong;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public boolean putDouble(double value, int row, int column) {
        acquireReference();
        try {
            boolean nativePutDouble = nativePutDouble(this.mWindowPtr, value, row - this.mStartPos, column);
            releaseReference();
            return nativePutDouble;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public boolean putNull(int row, int column) {
        acquireReference();
        try {
            boolean nativePutNull = nativePutNull(this.mWindowPtr, row - this.mStartPos, column);
            releaseReference();
            return nativePutNull;
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    public static CursorWindow newFromParcel(Parcel p) {
        return CREATOR.createFromParcel(p);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        acquireReference();
        try {
            dest.writeInt(this.mStartPos);
            nativeWriteToParcel(this.mWindowPtr, dest);
            releaseReference();
            if ((flags & 1) != 0) {
                releaseReference();
            }
        } catch (Throwable th) {
            releaseReference();
            throw th;
        }
    }

    @Override // android.database.sqlite.SQLiteClosable
    protected void onAllReferencesReleased() {
        dispose();
    }

    private void recordNewWindow(int pid, int window) {
        synchronized (sWindowToPidMap) {
            sWindowToPidMap.put(window, pid);
            if (Log.isLoggable(STATS_TAG, 2)) {
                Log.i(STATS_TAG, "Created a new Cursor. " + printStats());
            }
        }
    }

    private void recordClosingOfWindow(int window) {
        synchronized (sWindowToPidMap) {
            if (sWindowToPidMap.size() == 0) {
                return;
            }
            sWindowToPidMap.delete(window);
        }
    }

    private String printStats() {
        StringBuilder buff = new StringBuilder();
        int myPid = Process.myPid();
        int total = 0;
        SparseIntArray pidCounts = new SparseIntArray();
        synchronized (sWindowToPidMap) {
            int size = sWindowToPidMap.size();
            if (size == 0) {
                return "";
            }
            for (int indx = 0; indx < size; indx++) {
                int pid = sWindowToPidMap.valueAt(indx);
                int value = pidCounts.get(pid);
                pidCounts.put(pid, value + 1);
            }
            int numPids = pidCounts.size();
            for (int i = 0; i < numPids; i++) {
                buff.append(" (# cursors opened by ");
                int pid2 = pidCounts.keyAt(i);
                if (pid2 == myPid) {
                    buff.append("this proc=");
                } else {
                    buff.append("pid " + pid2 + Separators.EQUALS);
                }
                int num = pidCounts.get(pid2);
                buff.append(num + Separators.RPAREN);
                total += num;
            }
            String s = buff.length() > 980 ? buff.substring(0, 980) : buff.toString();
            return "# Open Cursors=" + total + s;
        }
    }

    public String toString() {
        return getName() + " {" + Integer.toHexString(this.mWindowPtr) + "}";
    }
}