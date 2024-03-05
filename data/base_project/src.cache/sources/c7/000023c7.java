package java.nio;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: DoubleBuffer.class */
public abstract class DoubleBuffer extends Buffer implements Comparable<DoubleBuffer> {
    public abstract DoubleBuffer asReadOnlyBuffer();

    public abstract DoubleBuffer compact();

    public abstract DoubleBuffer duplicate();

    public abstract double get();

    public abstract double get(int i);

    @Override // java.nio.Buffer
    public abstract boolean isDirect();

    public abstract ByteOrder order();

    public abstract DoubleBuffer put(double d);

    public abstract DoubleBuffer put(int i, double d);

    public abstract DoubleBuffer slice();

    DoubleBuffer() {
        throw new RuntimeException("Stub!");
    }

    public static DoubleBuffer allocate(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public static DoubleBuffer wrap(double[] array) {
        throw new RuntimeException("Stub!");
    }

    public static DoubleBuffer wrap(double[] array, int start, int doubleCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final double[] array() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final int arrayOffset() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(DoubleBuffer otherBuffer) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public DoubleBuffer get(double[] dst) {
        throw new RuntimeException("Stub!");
    }

    public DoubleBuffer get(double[] dst, int dstOffset, int doubleCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final boolean hasArray() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public final DoubleBuffer put(double[] src) {
        throw new RuntimeException("Stub!");
    }

    public DoubleBuffer put(double[] src, int srcOffset, int doubleCount) {
        throw new RuntimeException("Stub!");
    }

    public DoubleBuffer put(DoubleBuffer src) {
        throw new RuntimeException("Stub!");
    }
}