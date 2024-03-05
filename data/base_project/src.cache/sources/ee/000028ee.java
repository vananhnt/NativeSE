package libcore.io;

import android.widget.ExpandableListView;
import java.nio.ByteOrder;

/* loaded from: Memory.class */
public final class Memory {
    public static native void unsafeBulkGet(Object obj, int i, int i2, byte[] bArr, int i3, int i4, boolean z);

    public static native void unsafeBulkPut(byte[] bArr, int i, int i2, Object obj, int i3, int i4, boolean z);

    public static native void memmove(Object obj, int i, Object obj2, int i2, long j);

    public static native byte peekByte(long j);

    public static native int peekInt(long j, boolean z);

    public static native long peekLong(long j, boolean z);

    public static native short peekShort(long j, boolean z);

    public static native void peekByteArray(long j, byte[] bArr, int i, int i2);

    public static native void peekCharArray(long j, char[] cArr, int i, int i2, boolean z);

    public static native void peekDoubleArray(long j, double[] dArr, int i, int i2, boolean z);

    public static native void peekFloatArray(long j, float[] fArr, int i, int i2, boolean z);

    public static native void peekIntArray(long j, int[] iArr, int i, int i2, boolean z);

    public static native void peekLongArray(long j, long[] jArr, int i, int i2, boolean z);

    public static native void peekShortArray(long j, short[] sArr, int i, int i2, boolean z);

    public static native void pokeByte(long j, byte b);

    public static native void pokeInt(long j, int i, boolean z);

    public static native void pokeLong(long j, long j2, boolean z);

    public static native void pokeShort(long j, short s, boolean z);

    public static native void pokeByteArray(long j, byte[] bArr, int i, int i2);

    public static native void pokeCharArray(long j, char[] cArr, int i, int i2, boolean z);

    public static native void pokeDoubleArray(long j, double[] dArr, int i, int i2, boolean z);

    public static native void pokeFloatArray(long j, float[] fArr, int i, int i2, boolean z);

    public static native void pokeIntArray(long j, int[] iArr, int i, int i2, boolean z);

    public static native void pokeLongArray(long j, long[] jArr, int i, int i2, boolean z);

    public static native void pokeShortArray(long j, short[] sArr, int i, int i2, boolean z);

    private Memory() {
    }

    public static int peekInt(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int offset2 = offset + 1;
            int offset3 = offset2 + 1;
            return ((src[offset] & 255) << 24) | ((src[offset2] & 255) << 16) | ((src[offset3] & 255) << 8) | ((src[offset3 + 1] & 255) << 0);
        }
        int offset4 = offset + 1;
        int offset5 = offset4 + 1;
        return ((src[offset] & 255) << 0) | ((src[offset4] & 255) << 8) | ((src[offset5] & 255) << 16) | ((src[offset5 + 1] & 255) << 24);
    }

    public static long peekLong(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int offset2 = offset + 1;
            int offset3 = offset2 + 1;
            int offset4 = offset3 + 1;
            int offset5 = offset4 + 1;
            int h = ((src[offset] & 255) << 24) | ((src[offset2] & 255) << 16) | ((src[offset3] & 255) << 8) | ((src[offset4] & 255) << 0);
            int offset6 = offset5 + 1;
            int offset7 = offset6 + 1;
            int l = ((src[offset5] & 255) << 24) | ((src[offset6] & 255) << 16) | ((src[offset7] & 255) << 8) | ((src[offset7 + 1] & 255) << 0);
            return (h << 32) | (l & ExpandableListView.PACKED_POSITION_VALUE_NULL);
        }
        int offset8 = offset + 1;
        int offset9 = offset8 + 1;
        int offset10 = offset9 + 1;
        int offset11 = offset10 + 1;
        int l2 = ((src[offset] & 255) << 0) | ((src[offset8] & 255) << 8) | ((src[offset9] & 255) << 16) | ((src[offset10] & 255) << 24);
        int offset12 = offset11 + 1;
        int offset13 = offset12 + 1;
        int h2 = ((src[offset11] & 255) << 0) | ((src[offset12] & 255) << 8) | ((src[offset13] & 255) << 16) | ((src[offset13 + 1] & 255) << 24);
        return (h2 << 32) | (l2 & ExpandableListView.PACKED_POSITION_VALUE_NULL);
    }

    public static short peekShort(byte[] src, int offset, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            return (short) ((src[offset] << 8) | (src[offset + 1] & 255));
        }
        return (short) ((src[offset + 1] << 8) | (src[offset] & 255));
    }

    public static void pokeInt(byte[] dst, int offset, int value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int offset2 = offset + 1;
            dst[offset] = (byte) ((value >> 24) & 255);
            int offset3 = offset2 + 1;
            dst[offset2] = (byte) ((value >> 16) & 255);
            dst[offset3] = (byte) ((value >> 8) & 255);
            dst[offset3 + 1] = (byte) ((value >> 0) & 255);
            return;
        }
        int offset4 = offset + 1;
        dst[offset] = (byte) ((value >> 0) & 255);
        int offset5 = offset4 + 1;
        dst[offset4] = (byte) ((value >> 8) & 255);
        dst[offset5] = (byte) ((value >> 16) & 255);
        dst[offset5 + 1] = (byte) ((value >> 24) & 255);
    }

    public static void pokeLong(byte[] dst, int offset, long value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            int i = (int) (value >> 32);
            int offset2 = offset + 1;
            dst[offset] = (byte) ((i >> 24) & 255);
            int offset3 = offset2 + 1;
            dst[offset2] = (byte) ((i >> 16) & 255);
            int offset4 = offset3 + 1;
            dst[offset3] = (byte) ((i >> 8) & 255);
            int offset5 = offset4 + 1;
            dst[offset4] = (byte) ((i >> 0) & 255);
            int i2 = (int) value;
            int offset6 = offset5 + 1;
            dst[offset5] = (byte) ((i2 >> 24) & 255);
            int offset7 = offset6 + 1;
            dst[offset6] = (byte) ((i2 >> 16) & 255);
            dst[offset7] = (byte) ((i2 >> 8) & 255);
            dst[offset7 + 1] = (byte) ((i2 >> 0) & 255);
            return;
        }
        int i3 = (int) value;
        int offset8 = offset + 1;
        dst[offset] = (byte) ((i3 >> 0) & 255);
        int offset9 = offset8 + 1;
        dst[offset8] = (byte) ((i3 >> 8) & 255);
        int offset10 = offset9 + 1;
        dst[offset9] = (byte) ((i3 >> 16) & 255);
        int offset11 = offset10 + 1;
        dst[offset10] = (byte) ((i3 >> 24) & 255);
        int i4 = (int) (value >> 32);
        int offset12 = offset11 + 1;
        dst[offset11] = (byte) ((i4 >> 0) & 255);
        int offset13 = offset12 + 1;
        dst[offset12] = (byte) ((i4 >> 8) & 255);
        dst[offset13] = (byte) ((i4 >> 16) & 255);
        dst[offset13 + 1] = (byte) ((i4 >> 24) & 255);
    }

    public static void pokeShort(byte[] dst, int offset, short value, ByteOrder order) {
        if (order == ByteOrder.BIG_ENDIAN) {
            dst[offset] = (byte) ((value >> 8) & 255);
            dst[offset + 1] = (byte) ((value >> 0) & 255);
            return;
        }
        dst[offset] = (byte) ((value >> 0) & 255);
        dst[offset + 1] = (byte) ((value >> 8) & 255);
    }
}