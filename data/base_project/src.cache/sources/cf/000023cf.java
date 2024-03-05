package java.nio;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: IntBuffer.class */
public abstract class IntBuffer extends Buffer implements Comparable<IntBuffer> {
    public abstract IntBuffer asReadOnlyBuffer();

    public abstract IntBuffer compact();

    public abstract IntBuffer duplicate();

    public abstract int get();

    public abstract int get(int i);

    @Override // java.nio.Buffer
    public abstract boolean isDirect();

    public abstract ByteOrder order();

    public abstract IntBuffer put(int i);

    public abstract IntBuffer put(int i, int i2);

    public abstract IntBuffer slice();

    IntBuffer() {
        throw new RuntimeException("Stub!");
    }

    public static IntBuffer allocate(int capacity) {
        throw new RuntimeException("Stub!");
    }

    public static IntBuffer wrap(int[] array) {
        throw new RuntimeException("Stub!");
    }

    public static IntBuffer wrap(int[] array, int start, int intCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final int[] array() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final int arrayOffset() {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(IntBuffer otherBuffer) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object other) {
        throw new RuntimeException("Stub!");
    }

    public IntBuffer get(int[] dst) {
        throw new RuntimeException("Stub!");
    }

    public IntBuffer get(int[] dst, int dstOffset, int intCount) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.nio.Buffer
    public final boolean hasArray() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public final IntBuffer put(int[] src) {
        throw new RuntimeException("Stub!");
    }

    public IntBuffer put(int[] src, int srcOffset, int intCount) {
        throw new RuntimeException("Stub!");
    }

    public IntBuffer put(IntBuffer src) {
        throw new RuntimeException("Stub!");
    }
}