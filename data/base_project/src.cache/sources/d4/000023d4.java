package java.nio;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: LongBuffer.class */
public abstract class LongBuffer extends Buffer implements Comparable<LongBuffer> {
    public abstract LongBuffer asReadOnlyBuffer();

    public abstract LongBuffer compact();

    public abstract LongBuffer duplicate();

    public abstract long get();

    public abstract long get(int i);

    @Override // java.nio.Buffer
    public abstract boolean isDirect();

    public abstract ByteOrder order();

    public abstract LongBuffer put(long j);

    public abstract LongBuffer put(int i, long j);

    public abstract LongBuffer slice();

    LongBuffer() {
        throw new RuntimeException("Stub!");
    }

    public static LongBuffer allocate(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public static LongBuffer wrap(long[] array) {
        throw new RuntimeException("Stub!");
    }

    public static LongBuffer wrap(long[] array, int start, int longCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final long[] array() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final int arrayOffset() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(LongBuffer otherBuffer) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public LongBuffer get(long[] dst) {
        throw new RuntimeException("Stub!");
    }

    public LongBuffer get(long[] dst, int dstOffset, int longCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final boolean hasArray() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public final LongBuffer put(long[] src) {
        throw new RuntimeException("Stub!");
    }

    public LongBuffer put(long[] src, int srcOffset, int longCount) {
        throw new RuntimeException("Stub!");
    }

    public LongBuffer put(LongBuffer src) {
        throw new RuntimeException("Stub!");
    }
}