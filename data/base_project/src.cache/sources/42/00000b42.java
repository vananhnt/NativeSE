package android.os;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import gov.nist.core.Separators;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: Parcel.class */
public final class Parcel {
    private static final boolean DEBUG_RECYCLE = false;
    private static final boolean DEBUG_ARRAY_MAP = false;
    private static final String TAG = "Parcel";
    private int mNativePtr;
    private boolean mOwnsNativeParcelObject;
    private RuntimeException mStack;
    private static final int POOL_SIZE = 6;
    private static final int VAL_NULL = -1;
    private static final int VAL_STRING = 0;
    private static final int VAL_INTEGER = 1;
    private static final int VAL_MAP = 2;
    private static final int VAL_BUNDLE = 3;
    private static final int VAL_PARCELABLE = 4;
    private static final int VAL_SHORT = 5;
    private static final int VAL_LONG = 6;
    private static final int VAL_FLOAT = 7;
    private static final int VAL_DOUBLE = 8;
    private static final int VAL_BOOLEAN = 9;
    private static final int VAL_CHARSEQUENCE = 10;
    private static final int VAL_LIST = 11;
    private static final int VAL_SPARSEARRAY = 12;
    private static final int VAL_BYTEARRAY = 13;
    private static final int VAL_STRINGARRAY = 14;
    private static final int VAL_IBINDER = 15;
    private static final int VAL_PARCELABLEARRAY = 16;
    private static final int VAL_OBJECTARRAY = 17;
    private static final int VAL_INTARRAY = 18;
    private static final int VAL_LONGARRAY = 19;
    private static final int VAL_BYTE = 20;
    private static final int VAL_SERIALIZABLE = 21;
    private static final int VAL_SPARSEBOOLEANARRAY = 22;
    private static final int VAL_BOOLEANARRAY = 23;
    private static final int VAL_CHARSEQUENCEARRAY = 24;
    private static final int EX_SECURITY = -1;
    private static final int EX_BAD_PARCELABLE = -2;
    private static final int EX_ILLEGAL_ARGUMENT = -3;
    private static final int EX_NULL_POINTER = -4;
    private static final int EX_ILLEGAL_STATE = -5;
    private static final int EX_NETWORK_MAIN_THREAD = -6;
    private static final int EX_HAS_REPLY_HEADER = -128;
    private static final Parcel[] sOwnedPool = new Parcel[6];
    private static final Parcel[] sHolderPool = new Parcel[6];
    public static final Parcelable.Creator<String> STRING_CREATOR = new Parcelable.Creator<String>() { // from class: android.os.Parcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public String createFromParcel(Parcel source) {
            return source.readString();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public String[] newArray(int size) {
            return new String[size];
        }
    };
    private static final HashMap<ClassLoader, HashMap<String, Parcelable.Creator>> mCreators = new HashMap<>();

    private static native int nativeDataSize(int i);

    private static native int nativeDataAvail(int i);

    private static native int nativeDataPosition(int i);

    private static native int nativeDataCapacity(int i);

    private static native void nativeSetDataSize(int i, int i2);

    private static native void nativeSetDataPosition(int i, int i2);

    private static native void nativeSetDataCapacity(int i, int i2);

    private static native boolean nativePushAllowFds(int i, boolean z);

    private static native void nativeRestoreAllowFds(int i, boolean z);

    private static native void nativeWriteByteArray(int i, byte[] bArr, int i2, int i3);

    private static native void nativeWriteInt(int i, int i2);

    private static native void nativeWriteLong(int i, long j);

    private static native void nativeWriteFloat(int i, float f);

    private static native void nativeWriteDouble(int i, double d);

    private static native void nativeWriteString(int i, String str);

    private static native void nativeWriteStrongBinder(int i, IBinder iBinder);

    private static native void nativeWriteFileDescriptor(int i, FileDescriptor fileDescriptor);

    private static native byte[] nativeCreateByteArray(int i);

    private static native int nativeReadInt(int i);

    private static native long nativeReadLong(int i);

    private static native float nativeReadFloat(int i);

    private static native double nativeReadDouble(int i);

    private static native String nativeReadString(int i);

    private static native IBinder nativeReadStrongBinder(int i);

    private static native FileDescriptor nativeReadFileDescriptor(int i);

    private static native int nativeCreate();

    private static native void nativeFreeBuffer(int i);

    private static native void nativeDestroy(int i);

    private static native byte[] nativeMarshall(int i);

    private static native void nativeUnmarshall(int i, byte[] bArr, int i2, int i3);

    private static native void nativeAppendFrom(int i, int i2, int i3, int i4);

    private static native boolean nativeHasFileDescriptors(int i);

    private static native void nativeWriteInterfaceToken(int i, String str);

    private static native void nativeEnforceInterface(int i, String str);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native FileDescriptor openFileDescriptor(String str, int i) throws FileNotFoundException;

    static native FileDescriptor dupFileDescriptor(FileDescriptor fileDescriptor) throws IOException;

    static native void closeFileDescriptor(FileDescriptor fileDescriptor) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static native void clearFileDescriptor(FileDescriptor fileDescriptor);

    public static Parcel obtain() {
        Parcel[] pool = sOwnedPool;
        synchronized (pool) {
            for (int i = 0; i < 6; i++) {
                Parcel p = pool[i];
                if (p != null) {
                    pool[i] = null;
                    return p;
                }
            }
            return new Parcel(0);
        }
    }

    public final void recycle() {
        Parcel[] pool;
        freeBuffer();
        if (this.mOwnsNativeParcelObject) {
            pool = sOwnedPool;
        } else {
            this.mNativePtr = 0;
            pool = sHolderPool;
        }
        synchronized (pool) {
            for (int i = 0; i < 6; i++) {
                if (pool[i] == null) {
                    pool[i] = this;
                    return;
                }
            }
        }
    }

    public final int dataSize() {
        return nativeDataSize(this.mNativePtr);
    }

    public final int dataAvail() {
        return nativeDataAvail(this.mNativePtr);
    }

    public final int dataPosition() {
        return nativeDataPosition(this.mNativePtr);
    }

    public final int dataCapacity() {
        return nativeDataCapacity(this.mNativePtr);
    }

    public final void setDataSize(int size) {
        nativeSetDataSize(this.mNativePtr, size);
    }

    public final void setDataPosition(int pos) {
        nativeSetDataPosition(this.mNativePtr, pos);
    }

    public final void setDataCapacity(int size) {
        nativeSetDataCapacity(this.mNativePtr, size);
    }

    public final boolean pushAllowFds(boolean allowFds) {
        return nativePushAllowFds(this.mNativePtr, allowFds);
    }

    public final void restoreAllowFds(boolean lastValue) {
        nativeRestoreAllowFds(this.mNativePtr, lastValue);
    }

    public final byte[] marshall() {
        return nativeMarshall(this.mNativePtr);
    }

    public final void unmarshall(byte[] data, int offest, int length) {
        nativeUnmarshall(this.mNativePtr, data, offest, length);
    }

    public final void appendFrom(Parcel parcel, int offset, int length) {
        nativeAppendFrom(this.mNativePtr, parcel.mNativePtr, offset, length);
    }

    public final boolean hasFileDescriptors() {
        return nativeHasFileDescriptors(this.mNativePtr);
    }

    public final void writeInterfaceToken(String interfaceName) {
        nativeWriteInterfaceToken(this.mNativePtr, interfaceName);
    }

    public final void enforceInterface(String interfaceName) {
        nativeEnforceInterface(this.mNativePtr, interfaceName);
    }

    public final void writeByteArray(byte[] b) {
        writeByteArray(b, 0, b != null ? b.length : 0);
    }

    public final void writeByteArray(byte[] b, int offset, int len) {
        if (b == null) {
            writeInt(-1);
            return;
        }
        Arrays.checkOffsetAndCount(b.length, offset, len);
        nativeWriteByteArray(this.mNativePtr, b, offset, len);
    }

    public final void writeInt(int val) {
        nativeWriteInt(this.mNativePtr, val);
    }

    public final void writeLong(long val) {
        nativeWriteLong(this.mNativePtr, val);
    }

    public final void writeFloat(float val) {
        nativeWriteFloat(this.mNativePtr, val);
    }

    public final void writeDouble(double val) {
        nativeWriteDouble(this.mNativePtr, val);
    }

    public final void writeString(String val) {
        nativeWriteString(this.mNativePtr, val);
    }

    public final void writeCharSequence(CharSequence val) {
        TextUtils.writeToParcel(val, this, 0);
    }

    public final void writeStrongBinder(IBinder val) {
        nativeWriteStrongBinder(this.mNativePtr, val);
    }

    public final void writeStrongInterface(IInterface val) {
        writeStrongBinder(val == null ? null : val.asBinder());
    }

    public final void writeFileDescriptor(FileDescriptor val) {
        nativeWriteFileDescriptor(this.mNativePtr, val);
    }

    public final void writeByte(byte val) {
        writeInt(val);
    }

    public final void writeMap(Map val) {
        writeMapInternal(val);
    }

    void writeMapInternal(Map<String, Object> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        Set<Map.Entry<String, Object>> entries = val.entrySet();
        writeInt(entries.size());
        for (Map.Entry<String, Object> e : entries) {
            writeValue(e.getKey());
            writeValue(e.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeArrayMapInternal(ArrayMap<String, Object> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeValue(val.keyAt(i));
            writeValue(val.valueAt(i));
        }
    }

    public final void writeBundle(Bundle val) {
        if (val == null) {
            writeInt(-1);
        } else {
            val.writeToParcel(this, 0);
        }
    }

    public final void writeList(List val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeValue(val.get(i));
        }
    }

    public final void writeArray(Object[] val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.length;
        writeInt(N);
        for (Object obj : val) {
            writeValue(obj);
        }
    }

    public final void writeSparseArray(SparseArray<Object> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeInt(val.keyAt(i));
            writeValue(val.valueAt(i));
        }
    }

    public final void writeSparseBooleanArray(SparseBooleanArray val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeInt(val.keyAt(i));
            writeByte((byte) (val.valueAt(i) ? 1 : 0));
        }
    }

    public final void writeBooleanArray(boolean[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (boolean z : val) {
                writeInt(z ? 1 : 0);
            }
            return;
        }
        writeInt(-1);
    }

    public final boolean[] createBooleanArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 2)) {
            boolean[] val = new boolean[N];
            for (int i = 0; i < N; i++) {
                val[i] = readInt() != 0;
            }
            return val;
        }
        return null;
    }

    public final void readBooleanArray(boolean[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readInt() != 0;
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeCharArray(char[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (char c : val) {
                writeInt(c);
            }
            return;
        }
        writeInt(-1);
    }

    public final char[] createCharArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 2)) {
            char[] val = new char[N];
            for (int i = 0; i < N; i++) {
                val[i] = (char) readInt();
            }
            return val;
        }
        return null;
    }

    public final void readCharArray(char[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = (char) readInt();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeIntArray(int[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i : val) {
                writeInt(i);
            }
            return;
        }
        writeInt(-1);
    }

    public final int[] createIntArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 2)) {
            int[] val = new int[N];
            for (int i = 0; i < N; i++) {
                val[i] = readInt();
            }
            return val;
        }
        return null;
    }

    public final void readIntArray(int[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readInt();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeLongArray(long[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (long j : val) {
                writeLong(j);
            }
            return;
        }
        writeInt(-1);
    }

    public final long[] createLongArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 3)) {
            long[] val = new long[N];
            for (int i = 0; i < N; i++) {
                val[i] = readLong();
            }
            return val;
        }
        return null;
    }

    public final void readLongArray(long[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readLong();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeFloatArray(float[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (float f : val) {
                writeFloat(f);
            }
            return;
        }
        writeInt(-1);
    }

    public final float[] createFloatArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 2)) {
            float[] val = new float[N];
            for (int i = 0; i < N; i++) {
                val[i] = readFloat();
            }
            return val;
        }
        return null;
    }

    public final void readFloatArray(float[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readFloat();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeDoubleArray(double[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (double d : val) {
                writeDouble(d);
            }
            return;
        }
        writeInt(-1);
    }

    public final double[] createDoubleArray() {
        int N = readInt();
        if (N >= 0 && N <= (dataAvail() >> 3)) {
            double[] val = new double[N];
            for (int i = 0; i < N; i++) {
                val[i] = readDouble();
            }
            return val;
        }
        return null;
    }

    public final void readDoubleArray(double[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readDouble();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeStringArray(String[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (String str : val) {
                writeString(str);
            }
            return;
        }
        writeInt(-1);
    }

    public final String[] createStringArray() {
        int N = readInt();
        if (N >= 0) {
            String[] val = new String[N];
            for (int i = 0; i < N; i++) {
                val[i] = readString();
            }
            return val;
        }
        return null;
    }

    public final void readStringArray(String[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readString();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeBinderArray(IBinder[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (IBinder iBinder : val) {
                writeStrongBinder(iBinder);
            }
            return;
        }
        writeInt(-1);
    }

    public final void writeCharSequenceArray(CharSequence[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (CharSequence charSequence : val) {
                writeCharSequence(charSequence);
            }
            return;
        }
        writeInt(-1);
    }

    public final IBinder[] createBinderArray() {
        int N = readInt();
        if (N >= 0) {
            IBinder[] val = new IBinder[N];
            for (int i = 0; i < N; i++) {
                val[i] = readStrongBinder();
            }
            return val;
        }
        return null;
    }

    public final void readBinderArray(IBinder[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                val[i] = readStrongBinder();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final <T extends Parcelable> void writeTypedList(List<T> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            T item = val.get(i);
            if (item != null) {
                writeInt(1);
                item.writeToParcel(this, 0);
            } else {
                writeInt(0);
            }
        }
    }

    public final void writeStringList(List<String> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeString(val.get(i));
        }
    }

    public final void writeBinderList(List<IBinder> val) {
        if (val == null) {
            writeInt(-1);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = 0; i < N; i++) {
            writeStrongBinder(val.get(i));
        }
    }

    public final <T extends Parcelable> void writeTypedArray(T[] val, int parcelableFlags) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (T item : val) {
                if (item != null) {
                    writeInt(1);
                    item.writeToParcel(this, parcelableFlags);
                } else {
                    writeInt(0);
                }
            }
            return;
        }
        writeInt(-1);
    }

    public final void writeValue(Object v) {
        if (v == null) {
            writeInt(-1);
        } else if (v instanceof String) {
            writeInt(0);
            writeString((String) v);
        } else if (v instanceof Integer) {
            writeInt(1);
            writeInt(((Integer) v).intValue());
        } else if (v instanceof Map) {
            writeInt(2);
            writeMap((Map) v);
        } else if (v instanceof Bundle) {
            writeInt(3);
            writeBundle((Bundle) v);
        } else if (v instanceof Parcelable) {
            writeInt(4);
            writeParcelable((Parcelable) v, 0);
        } else if (v instanceof Short) {
            writeInt(5);
            writeInt(((Short) v).intValue());
        } else if (v instanceof Long) {
            writeInt(6);
            writeLong(((Long) v).longValue());
        } else if (v instanceof Float) {
            writeInt(7);
            writeFloat(((Float) v).floatValue());
        } else if (v instanceof Double) {
            writeInt(8);
            writeDouble(((Double) v).doubleValue());
        } else if (v instanceof Boolean) {
            writeInt(9);
            writeInt(((Boolean) v).booleanValue() ? 1 : 0);
        } else if (v instanceof CharSequence) {
            writeInt(10);
            writeCharSequence((CharSequence) v);
        } else if (v instanceof List) {
            writeInt(11);
            writeList((List) v);
        } else if (v instanceof SparseArray) {
            writeInt(12);
            writeSparseArray((SparseArray) v);
        } else if (v instanceof boolean[]) {
            writeInt(23);
            writeBooleanArray((boolean[]) v);
        } else if (v instanceof byte[]) {
            writeInt(13);
            writeByteArray((byte[]) v);
        } else if (v instanceof String[]) {
            writeInt(14);
            writeStringArray((String[]) v);
        } else if (v instanceof CharSequence[]) {
            writeInt(24);
            writeCharSequenceArray((CharSequence[]) v);
        } else if (v instanceof IBinder) {
            writeInt(15);
            writeStrongBinder((IBinder) v);
        } else if (v instanceof Parcelable[]) {
            writeInt(16);
            writeParcelableArray((Parcelable[]) v, 0);
        } else if (v instanceof Object[]) {
            writeInt(17);
            writeArray((Object[]) v);
        } else if (v instanceof int[]) {
            writeInt(18);
            writeIntArray((int[]) v);
        } else if (v instanceof long[]) {
            writeInt(19);
            writeLongArray((long[]) v);
        } else if (v instanceof Byte) {
            writeInt(20);
            writeInt(((Byte) v).byteValue());
        } else if (v instanceof Serializable) {
            writeInt(21);
            writeSerializable((Serializable) v);
        } else {
            throw new RuntimeException("Parcel: unable to marshal value " + v);
        }
    }

    public final void writeParcelable(Parcelable p, int parcelableFlags) {
        if (p == null) {
            writeString(null);
            return;
        }
        String name = p.getClass().getName();
        writeString(name);
        p.writeToParcel(this, parcelableFlags);
    }

    public final void writeParcelableCreator(Parcelable p) {
        String name = p.getClass().getName();
        writeString(name);
    }

    public final void writeSerializable(Serializable s) {
        if (s == null) {
            writeString(null);
            return;
        }
        String name = s.getClass().getName();
        writeString(name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(s);
            oos.close();
            writeByteArray(baos.toByteArray());
        } catch (IOException ioe) {
            throw new RuntimeException("Parcelable encountered IOException writing serializable object (name = " + name + Separators.RPAREN, ioe);
        }
    }

    public final void writeException(Exception e) {
        int code = 0;
        if (e instanceof SecurityException) {
            code = -1;
        } else if (e instanceof BadParcelableException) {
            code = -2;
        } else if (e instanceof IllegalArgumentException) {
            code = -3;
        } else if (e instanceof NullPointerException) {
            code = -4;
        } else if (e instanceof IllegalStateException) {
            code = -5;
        } else if (e instanceof NetworkOnMainThreadException) {
            code = -6;
        }
        writeInt(code);
        StrictMode.clearGatheredViolations();
        if (code == 0) {
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            throw new RuntimeException(e);
        }
        writeString(e.getMessage());
    }

    public final void writeNoException() {
        if (StrictMode.hasGatheredViolations()) {
            writeInt(-128);
            int sizePosition = dataPosition();
            writeInt(0);
            StrictMode.writeGatheredViolationsToParcel(this);
            int payloadPosition = dataPosition();
            setDataPosition(sizePosition);
            writeInt(payloadPosition - sizePosition);
            setDataPosition(payloadPosition);
            return;
        }
        writeInt(0);
    }

    public final void readException() {
        int code = readExceptionCode();
        if (code != 0) {
            String msg = readString();
            readException(code, msg);
        }
    }

    public final int readExceptionCode() {
        int code = readInt();
        if (code == -128) {
            int headerSize = readInt();
            if (headerSize == 0) {
                Log.e(TAG, "Unexpected zero-sized Parcel reply header.");
                return 0;
            }
            StrictMode.readAndHandleBinderCallViolations(this);
            return 0;
        }
        return code;
    }

    public final void readException(int code, String msg) {
        switch (code) {
            case -6:
                throw new NetworkOnMainThreadException();
            case -5:
                throw new IllegalStateException(msg);
            case -4:
                throw new NullPointerException(msg);
            case -3:
                throw new IllegalArgumentException(msg);
            case -2:
                throw new BadParcelableException(msg);
            case -1:
                throw new SecurityException(msg);
            default:
                throw new RuntimeException("Unknown exception code: " + code + " msg " + msg);
        }
    }

    public final int readInt() {
        return nativeReadInt(this.mNativePtr);
    }

    public final long readLong() {
        return nativeReadLong(this.mNativePtr);
    }

    public final float readFloat() {
        return nativeReadFloat(this.mNativePtr);
    }

    public final double readDouble() {
        return nativeReadDouble(this.mNativePtr);
    }

    public final String readString() {
        return nativeReadString(this.mNativePtr);
    }

    public final CharSequence readCharSequence() {
        return TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(this);
    }

    public final IBinder readStrongBinder() {
        return nativeReadStrongBinder(this.mNativePtr);
    }

    public final ParcelFileDescriptor readFileDescriptor() {
        FileDescriptor fd = nativeReadFileDescriptor(this.mNativePtr);
        if (fd != null) {
            return new ParcelFileDescriptor(fd);
        }
        return null;
    }

    public final FileDescriptor readRawFileDescriptor() {
        return nativeReadFileDescriptor(this.mNativePtr);
    }

    public final byte readByte() {
        return (byte) (readInt() & 255);
    }

    public final void readMap(Map outVal, ClassLoader loader) {
        int N = readInt();
        readMapInternal(outVal, N, loader);
    }

    public final void readList(List outVal, ClassLoader loader) {
        int N = readInt();
        readListInternal(outVal, N, loader);
    }

    public final HashMap readHashMap(ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        HashMap m = new HashMap(N);
        readMapInternal(m, N, loader);
        return m;
    }

    public final Bundle readBundle() {
        return readBundle(null);
    }

    public final Bundle readBundle(ClassLoader loader) {
        int length = readInt();
        if (length < 0) {
            return null;
        }
        Bundle bundle = new Bundle(this, length);
        if (loader != null) {
            bundle.setClassLoader(loader);
        }
        return bundle;
    }

    public final byte[] createByteArray() {
        return nativeCreateByteArray(this.mNativePtr);
    }

    public final void readByteArray(byte[] val) {
        byte[] ba = createByteArray();
        if (ba.length == val.length) {
            System.arraycopy(ba, 0, val, 0, ba.length);
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final String[] readStringArray() {
        String[] array = null;
        int length = readInt();
        if (length >= 0) {
            array = new String[length];
            for (int i = 0; i < length; i++) {
                array[i] = readString();
            }
        }
        return array;
    }

    public final CharSequence[] readCharSequenceArray() {
        CharSequence[] array = null;
        int length = readInt();
        if (length >= 0) {
            array = new CharSequence[length];
            for (int i = 0; i < length; i++) {
                array[i] = readCharSequence();
            }
        }
        return array;
    }

    public final ArrayList readArrayList(ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList l = new ArrayList(N);
        readListInternal(l, N, loader);
        return l;
    }

    public final Object[] readArray(ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        Object[] l = new Object[N];
        readArrayInternal(l, N, loader);
        return l;
    }

    public final SparseArray readSparseArray(ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        SparseArray sa = new SparseArray(N);
        readSparseArrayInternal(sa, N, loader);
        return sa;
    }

    public final SparseBooleanArray readSparseBooleanArray() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        SparseBooleanArray sa = new SparseBooleanArray(N);
        readSparseBooleanArrayInternal(sa, N);
        return sa;
    }

    public final <T> ArrayList<T> createTypedArrayList(Parcelable.Creator<T> c) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList<T> l = new ArrayList<>(N);
        while (N > 0) {
            if (readInt() != 0) {
                l.add(c.createFromParcel(this));
            } else {
                l.add(null);
            }
            N--;
        }
        return l;
    }

    public final <T> void readTypedList(List<T> list, Parcelable.Creator<T> c) {
        int M = list.size();
        int N = readInt();
        int i = 0;
        while (i < M && i < N) {
            if (readInt() != 0) {
                list.set(i, c.createFromParcel(this));
            } else {
                list.set(i, null);
            }
            i++;
        }
        while (i < N) {
            if (readInt() != 0) {
                list.add(c.createFromParcel(this));
            } else {
                list.add(null);
            }
            i++;
        }
        while (i < M) {
            list.remove(N);
            i++;
        }
    }

    public final ArrayList<String> createStringArrayList() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList<String> l = new ArrayList<>(N);
        while (N > 0) {
            l.add(readString());
            N--;
        }
        return l;
    }

    public final ArrayList<IBinder> createBinderArrayList() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList<IBinder> l = new ArrayList<>(N);
        while (N > 0) {
            l.add(readStrongBinder());
            N--;
        }
        return l;
    }

    public final void readStringList(List<String> list) {
        int M = list.size();
        int N = readInt();
        int i = 0;
        while (i < M && i < N) {
            list.set(i, readString());
            i++;
        }
        while (i < N) {
            list.add(readString());
            i++;
        }
        while (i < M) {
            list.remove(N);
            i++;
        }
    }

    public final void readBinderList(List<IBinder> list) {
        int M = list.size();
        int N = readInt();
        int i = 0;
        while (i < M && i < N) {
            list.set(i, readStrongBinder());
            i++;
        }
        while (i < N) {
            list.add(readStrongBinder());
            i++;
        }
        while (i < M) {
            list.remove(N);
            i++;
        }
    }

    public final <T> T[] createTypedArray(Parcelable.Creator<T> c) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        T[] l = c.newArray(N);
        for (int i = 0; i < N; i++) {
            if (readInt() != 0) {
                l[i] = c.createFromParcel(this);
            }
        }
        return l;
    }

    public final <T> void readTypedArray(T[] val, Parcelable.Creator<T> c) {
        int N = readInt();
        if (N == val.length) {
            for (int i = 0; i < N; i++) {
                if (readInt() != 0) {
                    val[i] = c.createFromParcel(this);
                } else {
                    val[i] = null;
                }
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    @Deprecated
    public final <T> T[] readTypedArray(Parcelable.Creator<T> c) {
        return (T[]) createTypedArray(c);
    }

    public final <T extends Parcelable> void writeParcelableArray(T[] value, int parcelableFlags) {
        if (value != null) {
            int N = value.length;
            writeInt(N);
            for (T t : value) {
                writeParcelable(t, parcelableFlags);
            }
            return;
        }
        writeInt(-1);
    }

    public final Object readValue(ClassLoader loader) {
        int type = readInt();
        switch (type) {
            case -1:
                return null;
            case 0:
                return readString();
            case 1:
                return Integer.valueOf(readInt());
            case 2:
                return readHashMap(loader);
            case 3:
                return readBundle(loader);
            case 4:
                return readParcelable(loader);
            case 5:
                return Short.valueOf((short) readInt());
            case 6:
                return Long.valueOf(readLong());
            case 7:
                return Float.valueOf(readFloat());
            case 8:
                return Double.valueOf(readDouble());
            case 9:
                return Boolean.valueOf(readInt() == 1);
            case 10:
                return readCharSequence();
            case 11:
                return readArrayList(loader);
            case 12:
                return readSparseArray(loader);
            case 13:
                return createByteArray();
            case 14:
                return readStringArray();
            case 15:
                return readStrongBinder();
            case 16:
                return readParcelableArray(loader);
            case 17:
                return readArray(loader);
            case 18:
                return createIntArray();
            case 19:
                return createLongArray();
            case 20:
                return Byte.valueOf(readByte());
            case 21:
                return readSerializable();
            case 22:
                return readSparseBooleanArray();
            case 23:
                return createBooleanArray();
            case 24:
                return readCharSequenceArray();
            default:
                int off = dataPosition() - 4;
                throw new RuntimeException("Parcel " + this + ": Unmarshalling unknown type code " + type + " at offset " + off);
        }
    }

    public final <T extends Parcelable> T readParcelable(ClassLoader loader) {
        Parcelable.Creator<T> creator = readParcelableCreator(loader);
        if (creator == null) {
            return null;
        }
        if (creator instanceof Parcelable.ClassLoaderCreator) {
            return (T) ((Parcelable.ClassLoaderCreator) creator).createFromParcel(this, loader);
        }
        return creator.createFromParcel(this);
    }

    public final <T extends Parcelable> T readCreator(Parcelable.Creator<T> creator, ClassLoader loader) {
        if (creator instanceof Parcelable.ClassLoaderCreator) {
            return (T) ((Parcelable.ClassLoaderCreator) creator).createFromParcel(this, loader);
        }
        return creator.createFromParcel(this);
    }

    public final <T extends Parcelable> Parcelable.Creator<T> readParcelableCreator(ClassLoader loader) {
        Parcelable.Creator<T> creator;
        String name = readString();
        if (name == null) {
            return null;
        }
        synchronized (mCreators) {
            HashMap<String, Parcelable.Creator> map = mCreators.get(loader);
            if (map == null) {
                map = new HashMap<>();
                mCreators.put(loader, map);
            }
            creator = map.get(name);
            if (creator == null) {
                try {
                    try {
                        try {
                            try {
                                Class c = loader == null ? Class.forName(name) : Class.forName(name, true, loader);
                                Field f = c.getField("CREATOR");
                                creator = (Parcelable.Creator) f.get(null);
                                if (creator == null) {
                                    throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class " + name);
                                }
                                map.put(name, creator);
                            } catch (ClassNotFoundException e) {
                                Log.e(TAG, "Class not found when unmarshalling: " + name, e);
                                throw new BadParcelableException("ClassNotFoundException when unmarshalling: " + name);
                            } catch (NoSuchFieldException e2) {
                                throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class " + name);
                            }
                        } catch (NullPointerException e3) {
                            throw new BadParcelableException("Parcelable protocol requires the CREATOR object to be static on class " + name);
                        }
                    } catch (IllegalAccessException e4) {
                        Log.e(TAG, "Illegal access when unmarshalling: " + name, e4);
                        throw new BadParcelableException("IllegalAccessException when unmarshalling: " + name);
                    }
                } catch (ClassCastException e5) {
                    throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class " + name);
                }
            }
        }
        return creator;
    }

    public final Parcelable[] readParcelableArray(ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        Parcelable[] p = new Parcelable[N];
        for (int i = 0; i < N; i++) {
            p[i] = readParcelable(loader);
        }
        return p;
    }

    public final Serializable readSerializable() {
        String name = readString();
        if (name == null) {
            return null;
        }
        byte[] serializedData = createByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Serializable) ois.readObject();
        } catch (IOException ioe) {
            throw new RuntimeException("Parcelable encountered IOException reading a Serializable object (name = " + name + Separators.RPAREN, ioe);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException("Parcelable encounteredClassNotFoundException reading a Serializable object (name = " + name + Separators.RPAREN, cnfe);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static final Parcel obtain(int obj) {
        Parcel[] pool = sHolderPool;
        synchronized (pool) {
            for (int i = 0; i < 6; i++) {
                Parcel p = pool[i];
                if (p != null) {
                    pool[i] = null;
                    p.init(obj);
                    return p;
                }
            }
            return new Parcel(obj);
        }
    }

    private Parcel(int nativePtr) {
        init(nativePtr);
    }

    private void init(int nativePtr) {
        if (nativePtr != 0) {
            this.mNativePtr = nativePtr;
            this.mOwnsNativeParcelObject = false;
            return;
        }
        this.mNativePtr = nativeCreate();
        this.mOwnsNativeParcelObject = true;
    }

    private void freeBuffer() {
        if (this.mOwnsNativeParcelObject) {
            nativeFreeBuffer(this.mNativePtr);
        }
    }

    private void destroy() {
        if (this.mNativePtr != 0) {
            if (this.mOwnsNativeParcelObject) {
                nativeDestroy(this.mNativePtr);
            }
            this.mNativePtr = 0;
        }
    }

    protected void finalize() throws Throwable {
        destroy();
    }

    void readMapInternal(Map outVal, int N, ClassLoader loader) {
        while (N > 0) {
            Object key = readValue(loader);
            Object value = readValue(loader);
            outVal.put(key, value);
            N--;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void readArrayMapInternal(ArrayMap outVal, int N, ClassLoader loader) {
        while (N > 0) {
            Object key = readValue(loader);
            Object value = readValue(loader);
            outVal.append(key, value);
            N--;
        }
    }

    void readArrayMapSafelyInternal(ArrayMap outVal, int N, ClassLoader loader) {
        while (N > 0) {
            Object key = readValue(loader);
            Object value = readValue(loader);
            outVal.put(key, value);
            N--;
        }
    }

    private void readListInternal(List outVal, int N, ClassLoader loader) {
        while (N > 0) {
            Object value = readValue(loader);
            outVal.add(value);
            N--;
        }
    }

    private void readArrayInternal(Object[] outVal, int N, ClassLoader loader) {
        for (int i = 0; i < N; i++) {
            Object value = readValue(loader);
            outVal[i] = value;
        }
    }

    private void readSparseArrayInternal(SparseArray outVal, int N, ClassLoader loader) {
        while (N > 0) {
            int key = readInt();
            Object value = readValue(loader);
            outVal.append(key, value);
            N--;
        }
    }

    private void readSparseBooleanArrayInternal(SparseBooleanArray outVal, int N) {
        while (N > 0) {
            int key = readInt();
            boolean value = readByte() == 1;
            outVal.append(key, value);
            N--;
        }
    }
}