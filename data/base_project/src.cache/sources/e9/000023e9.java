package java.nio;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: ShortBuffer.class */
public abstract class ShortBuffer extends Buffer implements Comparable<ShortBuffer> {
    public abstract ShortBuffer asReadOnlyBuffer();

    public abstract ShortBuffer compact();

    public abstract ShortBuffer duplicate();

    public abstract short get();

    public abstract short get(int i);

    @Override // java.nio.Buffer
    public abstract boolean isDirect();

    public abstract ByteOrder order();

    public abstract ShortBuffer put(short s);

    public abstract ShortBuffer put(int i, short s);

    public abstract ShortBuffer slice();

    ShortBuffer() {
        throw new RuntimeException("Stub!");
    }

    public static ShortBuffer allocate(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public static ShortBuffer wrap(short[] array) {
        throw new RuntimeException("Stub!");
    }

    public static ShortBuffer wrap(short[] array, int start, int shortCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final short[] array() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final int arrayOffset() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(ShortBuffer otherBuffer) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public ShortBuffer get(short[] dst) {
        throw new RuntimeException("Stub!");
    }

    public ShortBuffer get(short[] dst, int dstOffset, int shortCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final boolean hasArray() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public final ShortBuffer put(short[] src) {
        throw new RuntimeException("Stub!");
    }

    public ShortBuffer put(short[] src, int srcOffset, int shortCount) {
        throw new RuntimeException("Stub!");
    }

    public ShortBuffer put(ShortBuffer src) {
        throw new RuntimeException("Stub!");
    }
}