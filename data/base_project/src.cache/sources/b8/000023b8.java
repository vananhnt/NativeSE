package java.nio;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ByteBuffer.class */
public abstract class ByteBuffer extends Buffer implements Comparable<ByteBuffer> {
    public abstract CharBuffer asCharBuffer();

    public abstract DoubleBuffer asDoubleBuffer();

    public abstract FloatBuffer asFloatBuffer();

    public abstract IntBuffer asIntBuffer();

    public abstract LongBuffer asLongBuffer();

    public abstract ByteBuffer asReadOnlyBuffer();

    public abstract ShortBuffer asShortBuffer();

    public abstract ByteBuffer compact();

    public abstract ByteBuffer duplicate();

    public abstract byte get();

    public abstract byte get(int i);

    public abstract char getChar();

    public abstract char getChar(int i);

    public abstract double getDouble();

    public abstract double getDouble(int i);

    public abstract float getFloat();

    public abstract float getFloat(int i);

    public abstract int getInt();

    public abstract int getInt(int i);

    public abstract long getLong();

    public abstract long getLong(int i);

    public abstract short getShort();

    public abstract short getShort(int i);

    @Override // java.nio.Buffer
    public abstract boolean isDirect();

    public abstract ByteBuffer put(byte b);

    public abstract ByteBuffer put(int i, byte b);

    public abstract ByteBuffer putChar(char c);

    public abstract ByteBuffer putChar(int i, char c);

    public abstract ByteBuffer putDouble(double d);

    public abstract ByteBuffer putDouble(int i, double d);

    public abstract ByteBuffer putFloat(float f);

    public abstract ByteBuffer putFloat(int i, float f);

    public abstract ByteBuffer putInt(int i);

    public abstract ByteBuffer putInt(int i, int i2);

    public abstract ByteBuffer putLong(long j);

    public abstract ByteBuffer putLong(int i, long j);

    public abstract ByteBuffer putShort(short s);

    public abstract ByteBuffer putShort(int i, short s);

    public abstract ByteBuffer slice();

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteBuffer() {
        throw new RuntimeException("Stub!");
    }

    public static ByteBuffer allocate(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public static ByteBuffer allocateDirect(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public static ByteBuffer wrap(byte[] array) {
        throw new RuntimeException("Stub!");
    }

    public static ByteBuffer wrap(byte[] array, int start, int byteCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final byte[] array() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final int arrayOffset() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(ByteBuffer otherBuffer) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public ByteBuffer get(byte[] dst) {
        throw new RuntimeException("Stub!");
    }

    public ByteBuffer get(byte[] dst, int dstOffset, int byteCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final boolean hasArray() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public final ByteOrder order() {
        throw new RuntimeException("Stub!");
    }

    public final ByteBuffer order(ByteOrder byteOrder) {
        throw new RuntimeException("Stub!");
    }

    public final ByteBuffer put(byte[] src) {
        throw new RuntimeException("Stub!");
    }

    public ByteBuffer put(byte[] src, int srcOffset, int byteCount) {
        throw new RuntimeException("Stub!");
    }

    public ByteBuffer put(ByteBuffer src) {
        throw new RuntimeException("Stub!");
    }
}